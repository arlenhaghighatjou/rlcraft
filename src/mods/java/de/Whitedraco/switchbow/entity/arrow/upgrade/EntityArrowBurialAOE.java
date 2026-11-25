package de.Whitedraco.switchbow.entity.arrow.upgrade;

import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.Config.ArrowConfig;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import de.Whitedraco.switchbow.entity.EntityBurial;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowBurialAOE extends EntityArrowBase implements IThrowableEntity {
   private double damagesetter = ArrowConfig.DamageArrowBurialAOE.getDoub();

   public EntityArrowBurialAOE(World worldIn) {
      super(worldIn);
      this.damage = this.damagesetter;
   }

   public EntityArrowBurialAOE(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.damage = this.damagesetter;
   }

   public EntityArrowBurialAOE(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
      this.damage = this.damagesetter;
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowBurialAOE);
   }

   protected void arrowHit(EntityLivingBase living) {
      if (!living.world.isRemote) {
         this.AOEEffekt(living.posX, living.posY, living.posZ);
      }

   }

   protected void arrowHitGround(BlockPos blockpos) {
      if (!this.world.isRemote) {
         this.AOEEffekt((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
      }

   }

   private void AOEEffekt(double posx, double posy, double posz) {
      double Umkreis = 1.5D;
      List Mobliste = this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(posx - Umkreis, posy - 1.5D, posz - Umkreis, posx + Umkreis, posy + 1.5D, posz + Umkreis));
      if (Mobliste.size() > 0) {
         for(int a = 0; a < Mobliste.size(); ++a) {
            EntityLivingBase Mob = (EntityLivingBase)Mobliste.get(a);
            this.world.spawnEntity(new EntityBurial(Mob.world, Mob.posX, Mob.posY, Mob.posZ, Mob));
         }
      }

      this.setDead();
   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }

   protected void onHit(RayTraceResult raytraceResultIn) {
      Entity entity = raytraceResultIn.entityHit;
      if (entity != null) {
         float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
         int i = MathHelper.ceil((double)f * this.damage);
         if (this.getIsCritical()) {
            int var10000 = i + this.rand.nextInt(i / 2 + 2);
         }

         if (this.shootingEntity == null) {
            DamageSource damagesource = DamageSource.causeArrowDamage(this, this);
         } else {
            DamageSource damagesource = DamageSource.causeArrowDamage(this, this.shootingEntity);
         }

         if (this.isBurning() && !(entity instanceof EntityEnderman)) {
            entity.setFire(5);
         }

         if (entity instanceof EntityLivingBase) {
            this.arrowHit((EntityLivingBase)entity);
            if (!this.world.isRemote) {
               ((EntityLivingBase)entity).setArrowCountInEntity(((EntityLivingBase)entity).getArrowCountInEntity() + 1);
            }

            this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
            if (!(entity instanceof EntityEnderman)) {
               this.setDead();
            }
         }
      } else {
         BlockPos blockpos = raytraceResultIn.getBlockPos();
         this.xTile = blockpos.getX();
         this.yTile = blockpos.getY();
         this.zTile = blockpos.getZ();
         IBlockState iblockstate = this.world.getBlockState(blockpos);
         this.inTile = iblockstate.getBlock();
         this.inData = this.inTile.getMetaFromState(iblockstate);
         this.motionX = (double)((float)(raytraceResultIn.hitVec.x - this.posX));
         this.motionY = (double)((float)(raytraceResultIn.hitVec.y - this.posY));
         this.motionZ = (double)((float)(raytraceResultIn.hitVec.z - this.posZ));
         float f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
         this.posX -= this.motionX / (double)f2 * 0.05000000074505806D;
         this.posY -= this.motionY / (double)f2 * 0.05000000074505806D;
         this.posZ -= this.motionZ / (double)f2 * 0.05000000074505806D;
         this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
         this.inGround = true;
         this.arrowShake = 7;
         this.setIsCritical(false);
         if (iblockstate.getMaterial() != Material.AIR) {
            this.inTile.onEntityCollidedWithBlock(this.world, blockpos, iblockstate, this);
         }
      }

   }
}
