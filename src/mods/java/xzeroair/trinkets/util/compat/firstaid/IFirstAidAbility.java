package xzeroair.trinkets.util.compat.firstaid;

import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.Optional.Method;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;

public interface IFirstAidAbility extends IAbilityInterface {
   @Method(
      modid = "firstaid"
   )
   boolean firstAidHit(EntityLivingBase var1, DamageSource var2, float var3, AbstractPlayerDamageModel var4, AbstractPlayerDamageModel var5);
}
