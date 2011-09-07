package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.HeroRegainHealthEvent;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillChant extends TargettedSkill {

    public SkillChant(Heroes plugin) {
        super(plugin, "Chant");
        setDescription("Heals the target");
        setUsage("/skill chant <target>");
        setArgumentRange(0, 1);
        setIdentifiers(new String[] { "skill chant" });
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("health", 5);
        return node;
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        if (!(target instanceof Player)) {
            Messaging.send(player, "Invalid target!");
            return false;
        }
        
        Hero targetHero = plugin.getHeroManager().getHero((Player) target);
        int hpPlus = getSetting(hero.getHeroClass(), "health", 5);
        double targetHealth = targetHero.getHealth();

        if (targetHealth >= targetHero.getMaxHealth()) {
            if (player.equals(targetHero.getPlayer())) {
                Messaging.send(player, "You are already at full health.");  
            } else {
                Messaging.send(player, "Target is already fully healed.");
            }
            return false;
        }

        HeroRegainHealthEvent hrhEvent = new HeroRegainHealthEvent(targetHero, hpPlus, this);
        plugin.getServer().getPluginManager().callEvent(hrhEvent);
        if (hrhEvent.isCancelled()) {
            Messaging.send(player, "Unable to heal the target at this time!");
            return false;
        }

        targetHero.setHealth(targetHealth + hrhEvent.getAmount());
        targetHero.syncHealth();

        broadcastExecuteText(hero, target);
        return true;
    }
}