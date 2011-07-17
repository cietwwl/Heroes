package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;

public class SkillSyphon extends TargettedSkill {

    private final static int maxHealth = 20;

    public SkillSyphon(Heroes plugin) {
        super(plugin);
        setName("Syphon");
        setDescription("Gives your health to the target");
        setUsage("/skill syphon [target] [health]");
        setMinArgs(0);
        setMaxArgs(2);
        getIdentifiers().add("skill syphon");
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("multiplier", 1d);
        node.setProperty("default-health", 4);
        return node;
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();

        int transferredHealth = getSetting(hero.getHeroClass(), "default-health", 4);
        if (args.length == 2) {
            try {
                transferredHealth = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("Sorry, that's an incorrect health value!");
                return false;
            }
        }
        int playerHealth = player.getHealth();
        transferredHealth = playerHealth < transferredHealth ? playerHealth : transferredHealth > maxHealth ? maxHealth : transferredHealth;
        player.setHealth(playerHealth - transferredHealth);
        int targetHealth = target.getHealth();
        transferredHealth *= getSetting(hero.getHeroClass(), "multiplier", 1d);
        transferredHealth = maxHealth - targetHealth < transferredHealth ? maxHealth - targetHealth : transferredHealth < 0 ? 0 : transferredHealth;
        target.setHealth(targetHealth + transferredHealth);

        broadcastExecuteText(hero, target);
        return true;
    }

}
