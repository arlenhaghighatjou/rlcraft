package moe.plushie.armourers_workshop.api.common.skin.entity;

import net.minecraft.entity.EntityLivingBase;

public interface ISkinnableEntityRegisty {
   void registerEntity(ISkinnableEntity var1);

   ISkinnableEntity getSkinnableEntity(EntityLivingBase var1);

   boolean isValidEntity(EntityLivingBase var1);
}
