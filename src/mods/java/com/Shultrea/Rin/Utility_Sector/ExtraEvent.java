package com.Shultrea.Rin.Utility_Sector;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import com.Shultrea.Rin.Prop_Sector.ArrowPropertiesProvider;
import com.Shultrea.Rin.Prop_Sector.IArrowProperties;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ExtraEvent {
   public ExtraEvent() {
   }

   @SubscribeEvent
   public void onArrowHit(LivingDamageEvent fEvent) {
      if (fEvent.getSource().damageType == "arrow") {
         if (fEvent.getSource().getImmediateSource() instanceof EntityArrow) {
            EntityArrow cause = (EntityArrow)fEvent.getSource().getImmediateSource();
            if (cause.hasCapability(ArrowPropertiesProvider.ARROWPROPERTIES_CAP, (EnumFacing)null)) {
               if (!EnchantmentBase.isOffensivePetDisallowed(fEvent.getSource().getImmediateSource(), fEvent.getSource().getTrueSource())) {
                  IArrowProperties properties = (IArrowProperties)cause.getCapability(ArrowPropertiesProvider.ARROWPROPERTIES_CAP, (EnumFacing)null);
                  int flameLevel = properties.getFlameLevel();
                  switch(flameLevel) {
                  case 1:
                     if (ModConfig.enabled.LesserFlame) {
                        fEvent.getEntityLiving().setFire(2);
                     }
                     break;
                  case 2:
                     if (ModConfig.enabled.AdvancedFlame) {
                        fEvent.getEntityLiving().setFire(15);
                     }
                     break;
                  case 3:
                     if (ModConfig.enabled.SupremeFlame) {
                        fEvent.getEntityLiving().setFire(30);
                     }
                  }

                  float pullPower = properties.getPullPower();
                  if (pullPower > 0.0F && ModConfig.enabled.Pulling) {
                     pullPower = pullPower * -1.0F;
                     float distance = MathHelper.sqrt(cause.motionX * cause.motionX + cause.motionZ * cause.motionZ);
                     fEvent.getEntityLiving().addVelocity(cause.motionX * (double)pullPower * 0.6000000238418579D / (double)distance, 0.1D, cause.motionZ * (double)pullPower * 0.6000000238418579D / (double)distance);
                     fEvent.getEntityLiving().velocityChanged = true;
                  }

               }
            }
         }
      }
   }
}
