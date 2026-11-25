package dev.satyrn.wolfarmor.mixin;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({EntityLiving.class})
public abstract class MixinEntityLiving extends MixinEntityLivingBase {
   public MixinEntityLiving() {
   }

   @Shadow
   public abstract PathNavigate getNavigator();

   @Shadow
   public abstract void setItemStackToSlot(EntityEquipmentSlot var1, ItemStack var2);

   @Shadow
   public abstract void setAttackTarget(@Nullable EntityLivingBase var1);
}
