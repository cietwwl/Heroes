package com.herocraftonline.dev.heroes.skill.skills;

import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.getspout.spoutapi.event.inventory.InventoryEnchantEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.PassiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillEnchant extends PassiveSkill {
    
    public SkillEnchant(Heroes plugin) {
        super(plugin, "Enchant");
        
        setDescription("You are able to enchant items!");
        setArgumentRange(0, 0);
        setTypes(SkillType.KNOWLEDGE, SkillType.ITEM);
        setEffectTypes(EffectType.BENEFICIAL);
        
        if (Heroes.useSpout) {
            registerEvent(Type.CUSTOM_EVENT, new SkillEnchantListener(this), Priority.Lowest);
        }
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection section = super.getDefaultConfig();
        section.set("PROTECTION_ENVIRONMENTAL", 200);
        section.set("PROTECTION_FIRE", 1);
        section.set("PROTECTION_FALL", 1);
        section.set("PROTECTION_EXPLOSIONS", 200);
        section.set("PROTECTION_PROJECTILE", 200);
        section.set("OXYGEN", 1);
        section.set("WATER_WORKER", 1);
        section.set("DAMAGE_ALL", 200);
        section.set("DAMAGE_UNDEAD", 200);
        section.set("DAMAGE_ARTHROPODS", 200);
        section.set("KNOCKBACK", 1);
        section.set("FIRE_ASPECT", 1);
        section.set("LOOT_BONUS_MOBS", 1);
        section.set("DIG_SPEED", 1);
        section.set("SILK_TOUCH", 200);
        section.set("DURABILITY", 1);
        section.set("LOOT_BONUS_BLOCKS", 1);
        section.set(Setting.APPLY_TEXT.node(), "");
        return section;
    }
    
    public class SkillEnchantListener extends InventoryListener {

        private final Skill skill;
        
        public SkillEnchantListener(Skill skill) {
            this.skill = skill;
        }
        
        @Override
        public void onInventoryEnchant(InventoryEnchantEvent event) {
            if (event.isCancelled())
                return;
            
            Hero hero = plugin.getHeroManager().getHero(event.getPlayer());
            if (!hero.hasEffect(getName())) {
                event.setCancelled(true);
                return;
            }
            
            List<String> enchants = SkillConfigManager.getUseSettingKeys(hero, skill);
            Map<Enchantment, Integer> newEnchants = event.getResult().getEnchantments();
            for (Enchantment enchant : newEnchants.keySet()) {
                if (event.getBefore().containsEnchantment(enchant)) {
                    continue;
                }
                if (!enchants.contains(enchant.getName())) {
                    event.setCancelled(true);
                    return;
                }
                int level = SkillConfigManager.getUseSetting(hero, skill, enchant.getName(), 1, true);
                if (hero.getLevel(hero.getEnchantingClass()) < level) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}