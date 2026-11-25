package lykrast.defiledlands.common.item;

import lykrast.defiledlands.common.entity.projectile.EntityRavagerProjectile;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IPellet {
   EntityRavagerProjectile applyAttributes(EntityRavagerProjectile var1, ItemStack var2);

   default void onHit(EntityLivingBase target, EntityLivingBase source, ItemStack pellet) {
   }
}
