package com.herocraftonline.dev.heroes.effects;

import org.bukkit.entity.LivingEntity;

import com.herocraftonline.dev.heroes.hero.Hero;

public interface Periodic {

    /**
     * Returns the last time the effect ticked
     * 
     * @return
     */
    public long getLastTickTime();

    /**
     * @return the period
     */
    public long getPeriod();

    /**
     * Returns whether the effect is ready for ticking
     * 
     * @return
     */
    public boolean isReady();

    /**
     * runs the effect on the specified creature
     * 
     * @param creature
     */
    public void tick(LivingEntity lEntity);

    /**
     * runs the effect on the specified hero
     * 
     * @param hero
     */
    public void tick(Hero hero);
}
