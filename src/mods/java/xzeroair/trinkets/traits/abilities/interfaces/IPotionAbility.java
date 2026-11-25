package xzeroair.trinkets.traits.abilities.interfaces;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

public interface IPotionAbility extends IAbilityInterface {
   boolean potionApplied(EntityLivingBase var1, PotionEffect var2, boolean var3);
}
