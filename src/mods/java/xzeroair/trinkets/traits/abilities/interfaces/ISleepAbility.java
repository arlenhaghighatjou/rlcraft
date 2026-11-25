package xzeroair.trinkets.traits.abilities.interfaces;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.util.math.BlockPos;

public interface ISleepAbility extends IAbilityInterface {
   default SleepResult onStartSleeping(EntityLivingBase entity, BlockPos pos, SleepResult result) {
      return result;
   }

   void onWakeUp(EntityLivingBase var1, boolean var2, boolean var3, boolean var4);
}
