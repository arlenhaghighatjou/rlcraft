package xzeroair.trinkets.util.compat.firstaid;

import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import xzeroair.trinkets.Trinkets;

public class FirstAidCompat {
   public FirstAidCompat() {
   }

   public static void rescale(EntityLivingBase entity) {
      if (Trinkets.FirstAid && entity instanceof EntityPlayer) {
         try {
            AbstractPlayerDamageModel cap = (AbstractPlayerDamageModel)entity.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null);
            cap.runScaleLogic((EntityPlayer)entity);
         } catch (Exception var2) {
            ;
         }
      }

   }
}
