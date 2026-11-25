package com.endertech.minecraft.forge.entities;

import com.endertech.common.CommonTime;
import com.endertech.common.FloatBounds;
import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.ForgeConfig;
import com.endertech.minecraft.forge.configs.IHaveConfig;
import com.endertech.minecraft.forge.math.GameTime;
import com.endertech.minecraft.forge.world.ForgeWorld;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LivingHealthRegen<L extends EntityLivingBase> implements IHaveConfig {
   protected final GameTime updateInterval = GameTime.quaterSecond();
   private final ForgeConfig config;
   private final Class<L> livingClass;
   private final CommonTime.Interval afterHurtCooldown;
   private final float healOnEachUpdate;
   protected Map<L, CommonTime.Stamp> lastHurtsMap = new HashMap();

   public LivingHealthRegen(ForgeMod mod, ForgeConfig config, Class<L> livingClass, float cooldownInSeconds, float healOnEachUpdate) {
      this.config = config;
      this.livingClass = livingClass;
      cooldownInSeconds = ForgeConfig.getFloat(config, this.getClassCategory(), "afterHurtCooldown", cooldownInSeconds, FloatBounds.from(Float.valueOf(0.0F), Float.valueOf(360.0F)), "Defines the cooldown period of time (in seconds) after being hurt till regeneration start");
      this.afterHurtCooldown = CommonTime.Interval.seconds((double)cooldownInSeconds);
      this.healOnEachUpdate = ForgeConfig.getFloat(config, this.getClassCategory(), "healOnEachUpdate", healOnEachUpdate, FloatBounds.from(Float.valueOf(0.0F), Float.valueOf(5.0F)), "Defines the amount of health (in half-hearts) to be restored in each update (a quarter of second)");
      this.saveConfig();
      mod.getRegistrator().registerInEventBus(this);
   }

   @SubscribeEvent
   public void onLivingUpdate(LivingUpdateEvent event) {
      if (this.isProperLivingClass(event.getEntityLiving())) {
         L living = event.getEntityLiving();
         World world = living.getEntityWorld();
         if (ForgeWorld.isServerSide(world)) {
            CommonTime.Stamp lastHurt = (CommonTime.Stamp)this.lastHurtsMap.get(living);
            if ((lastHurt == null || CommonTime.Interval.passedFrom(lastHurt).moreThan(this.afterHurtCooldown)) && this.updateInterval.pastIn(world) && needsHealing(living)) {
               float healAmount = this.getHealAmount(living);
               living.heal(healAmount);
            }
         }
      }

   }

   @SubscribeEvent
   public void onLivingHurt(LivingHurtEvent event) {
      EntityLivingBase living = event.getEntityLiving();
      if (this.isProperLivingClass(living)) {
         this.lastHurtsMap.put(living, CommonTime.Stamp.now());
      }

   }

   protected boolean isProperLivingClass(EntityLivingBase living) {
      return this.livingClass.isAssignableFrom(living.getClass());
   }

   public ForgeConfig getConfig() {
      return this.config;
   }

   public float getHealAmount(L living) {
      return this.healOnEachUpdate;
   }

   public static boolean needsHealing(EntityLivingBase living) {
      return living.getHealth() > 0.0F && living.getHealth() < living.getMaxHealth();
   }
}
