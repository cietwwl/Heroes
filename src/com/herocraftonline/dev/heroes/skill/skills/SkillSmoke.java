package com.herocraftonline.dev.heroes.skill.skills;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillSmoke extends ActiveSkill {

    private String applyText;
    private String expireText;

    public SkillSmoke(Heroes plugin) {
        super(plugin, "Smoke");
        setDescription("You completely disappear from view");
        setUsage("/skill smoke");
        setArgumentRange(0, 0);
        setIdentifiers("skill smoke");
        setNotes("Note: Taking damage removes the effect");
        setTypes(SkillType.ILLUSION, SkillType.BUFF);

        registerEvent(Type.ENTITY_DAMAGE, new SkillEntityListener(), Priority.Normal);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty(Setting.DURATION.node(), 20000);
        node.setProperty(Setting.APPLY_TEXT.node(), "%hero% vanished in a cloud of smoke!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%hero% reappeared!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%hero% vanished in a cloud of smoke!").replace("%hero%", "$1");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%hero% reappeard!").replace("%hero%", "$1");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        broadcastExecuteText(hero);

        long duration = getSetting(hero.getHeroClass(), Setting.DURATION.node(), 20000);
        Player player = hero.getPlayer();
        player.getWorld().playEffect(player.getLocation(), org.bukkit.Effect.SMOKE, 4);
        hero.addEffect(new SmokeEffect(this, duration));

        return true;
    }

    public class SkillEntityListener extends EntityListener {

        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                Hero hero = plugin.getHeroManager().getHero(player);
                if (hero.hasEffect("Smoke")) {
                    hero.removeEffect(hero.getEffect("Smoke"));
                }
            }
        }
    }

    public class SkillPlayerListener extends PlayerListener {

        @Override
        public void onPlayerInteract(PlayerInteractEvent event) {
            if (event.getAction() != Action.PHYSICAL) {
                Hero hero = plugin.getHeroManager().getHero(event.getPlayer());
                if (hero.hasEffect("Smoke")) {
                    hero.removeEffect(hero.getEffect("Smoke"));
                }
            }
        }
    }

    public class SmokeEffect extends ExpirableEffect {

        public SmokeEffect(Skill skill, long duration) {
            super(skill, "Smoke", duration);
            this.types.add(EffectType.BENEFICIAL);
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);
            CraftPlayer craftPlayer = (CraftPlayer) hero.getPlayer();
            // Tell all the logged in Clients to Destroy the Entity - Appears Invisible.
            final Player[] players = plugin.getServer().getOnlinePlayers();
            for (Player onlinePlayer : players) {
                CraftPlayer hostilePlayer = (CraftPlayer) onlinePlayer;
                hostilePlayer.getHandle().netServerHandler.sendPacket(new Packet29DestroyEntity(craftPlayer.getEntityId()));
            }

            broadcast(craftPlayer.getLocation(), applyText, craftPlayer.getDisplayName());
        }

        @Override
        public void remove(Hero hero) {
            super.remove(hero);
            Player player = hero.getPlayer();
            EntityHuman entity = ((CraftPlayer) player).getHandle();
            final Player[] players = plugin.getServer().getOnlinePlayers();
            for (Player onlinePlayer : players) {
                if (onlinePlayer.equals(player)) {
                    continue;
                }
                ((CraftPlayer) onlinePlayer).getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(entity));
            }

            broadcast(player.getLocation(), expireText, player.getDisplayName());
        }

    }
}
