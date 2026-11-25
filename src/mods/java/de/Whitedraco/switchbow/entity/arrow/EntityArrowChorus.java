package de.Whitedraco.switchbow.entity.arrow;

import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.Config.ArrowConfig;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowChorus extends EntityArrowBase implements IThrowableEntity {
   private double damagesetter = ArrowConfig.DamageArrowChorus.getDoub();

   public EntityArrowChorus(World worldIn) {
      super(worldIn);
      this.damage = this.damagesetter;
   }

   public EntityArrowChorus(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.damage = this.damagesetter;
   }

   public EntityArrowChorus(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
      this.damage = this.damagesetter;
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowChorus);
   }

   protected void arrowHit(EntityLivingBase living) {
      if (!living.world.isRemote) {
         double d0 = living.posX;
         double d1 = living.posY;
         double d2 = living.posZ;

         for(int i = 0; i < 16; ++i) {
            double d3 = living.posX + (living.getRNG().nextDouble() - 0.5D) * 16.0D;
            double d4 = MathHelper.clamp(living.posY + (double)(living.getRNG().nextInt(16) - 8), 0.0D, (double)(this.world.getActualHeight() - 1));
            double d5 = living.posZ + (living.getRNG().nextDouble() - 0.5D) * 16.0D;
            if (living.isRiding()) {
               living.dismountRidingEntity();
            }

            if (living.attemptTeleport(d3, d4, d5)) {
               this.world.playSound((EntityPlayer)null, d0, d1, d2, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
               living.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
               break;
            }
         }
      }

   }

   protected void arrowHitGround(BlockPos blockpos) {
   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }
}
