package dev.satyrn.wolfarmor.mixin;

import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({Entity.class})
public abstract class MixinEntity {
   @Shadow
   public boolean isDead;
   @Shadow
   public boolean onGround;
   @Shadow
   public double posX;
   @Shadow
   public double posY;
   @Shadow
   public double posZ;
   @Shadow
   public double motionZ;
   @Shadow
   public double motionX;
   @Shadow
   public float rotationYaw;
   @Shadow
   public World world;
   @Shadow
   protected EntityDataManager dataManager;

   public MixinEntity() {
   }

   @Shadow
   public abstract AxisAlignedBB getEntityBoundingBox();

   @Shadow
   @Nullable
   public abstract Entity getRidingEntity();

   @Shadow
   public abstract boolean isSprinting();

   @Shadow
   public abstract boolean isInWater();

   @Shadow
   public abstract boolean isInsideOfMaterial(Material var1);

   @Shadow
   public abstract boolean isRiding();

   @Shadow
   protected abstract void applyEnchantments(EntityLivingBase var1, Entity var2);

   @Shadow
   @Nullable
   public abstract EntityItem entityDropItem(ItemStack var1, float var2);

   @Shadow
   public abstract String getCustomNameTag();

   @Shadow
   public abstract World getEntityWorld();

   @Shadow
   public abstract void playSound(SoundEvent var1, float var2, float var3);

   @Shadow
   public abstract int getEntityId();
}
