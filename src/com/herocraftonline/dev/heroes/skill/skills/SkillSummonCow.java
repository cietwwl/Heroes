package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.SkillResult;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Setting;
import com.herocraftonline.dev.heroes.util.Util;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.CreatureType;

public class SkillSummonCow extends ActiveSkill {

    public SkillSummonCow(Heroes plugin) {
        super(plugin, "Cow");
        setDescription("100% chance to spawn 1 cow, $1% for 2, and $2% for 3.");
        setUsage("/skill cow");
        setArgumentRange(0, 0);
        setIdentifiers("skill summoncow", "skill cow");
        setTypes(SkillType.SUMMON, SkillType.SILENCABLE, SkillType.EARTH);
    }

    @Override
    public String getDescription(Hero hero) {
        int chance2x = (int) (SkillConfigManager.getUseSetting(hero, this, "chance-2x", 0.2, false) * 100 + SkillConfigManager.getUseSetting(hero, this, "added-chance-2x-per-level", 0.0, false) * hero.getLevel());
        int chance3x = (int) (SkillConfigManager.getUseSetting(hero, this, "chance-3x", 0.1, false) * 100 + SkillConfigManager.getUseSetting(hero, this, "added-chance-3x-per-level", 0.0, false) * hero.getLevel());
        return getDescription().replace("$2", chance2x + "").replace("$3", chance3x + "");
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set("chance-2x", 0.2);
        node.set("chance-3x", 0.1);
        node.set(Setting.MAX_DISTANCE.node(), 20);
        node.set("chance-2x-per-level", 0.0);
        node.set("chance-3x-per-level", 0.0);
        return node;
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        broadcastExecuteText(hero);
        double chance2x = SkillConfigManager.getUseSetting(hero, this, "chance-2x", 0.2, false) + (int) SkillConfigManager.getUseSetting(hero, this, "chance-2x-per-level", 0.0, false) * hero.getSkillLevel(this);
        double chance3x = SkillConfigManager.getUseSetting(hero, this, "chance-3x", 0.1, false) + (int) SkillConfigManager.getUseSetting(hero, this, "chance-3x-per-level", 0.0, false) * hero.getSkillLevel(this);
        int distance = SkillConfigManager.getUseSetting(hero, this, Setting.MAX_DISTANCE, 20, false) + (int) SkillConfigManager.getUseSetting(hero, this, Setting.MAX_DISTANCE_INCREASE, 0.0, false) * hero.getSkillLevel(this);
        Block wTargetBlock = player.getTargetBlock(null, distance).getRelative(BlockFace.UP);
        CreatureType cType = wTargetBlock.getType() == Material.HUGE_MUSHROOM_1 || 
                wTargetBlock.getType() == Material.HUGE_MUSHROOM_2 || 
                wTargetBlock.getType() == Material.MYCEL ? CreatureType.MUSHROOM_COW : CreatureType.COW;
        
        player.getWorld().spawnCreature(wTargetBlock.getLocation(), cType);
        double chance = Util.rand.nextDouble();
        if (chance <= chance3x) {
            player.getWorld().spawnCreature(wTargetBlock.getLocation(), cType);
            player.getWorld().spawnCreature(wTargetBlock.getLocation(), cType);
        } else if (chance <= chance2x) {
            player.getWorld().spawnCreature(wTargetBlock.getLocation(), cType);
        }
        broadcastExecuteText(hero);
        return SkillResult.NORMAL;
    }


}