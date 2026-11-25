package de.Whitedraco.switchbow.entity.arrow.upgrade;

import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.Config.ArrowConfig;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowBoneAOE extends EntityArrowBase implements IThrowableEntity {
   private int spawnbonemeal = 0;
   private boolean wasbonemeal = false;
   private double damagesetter = ArrowConfig.DamageArrowBoneAOE.getDoub();

   public EntityArrowBoneAOE(World worldIn) {
      super(worldIn);
      this.damage = this.damagesetter;
   }

   public EntityArrowBoneAOE(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.damage = this.damagesetter;
   }

   public EntityArrowBoneAOE(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
      this.damage = this.damagesetter;
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowBoneAOE);
   }

   protected void arrowHit(EntityLivingBase living) {
      if (!living.world.isRemote) {
         ;
      }

   }

   protected void arrowHitGround(BlockPos blockpos) {
      if (!this.world.isRemote) {
         if (this.spawnbonemeal < 4) {
            if (applyBonemeal(this.world, blockpos.getX() + 2, blockpos.getY() + 1, blockpos.getZ())) {
               this.wasbonemeal = true;
               this.pickupStatus = PickupStatus.DISALLOWED;
            } else if (applyBonemeal(this.world, blockpos.getX() + 2, blockpos.getY(), blockpos.getZ())) {
               this.wasbonemeal = true;
               this.pickupStatus = PickupStatus.DISALLOWED;
            }

            if (applyBonemeal(this.world, blockpos.getX() - 2, blockpos.getY() + 1, blockpos.getZ())) {
               this.wasbonemeal = true;
               this.pickupStatus = PickupStatus.DISALLOWED;
            } else if (applyBonemeal(this.world, blockpos.getX() - 2, blockpos.getY(), blockpos.getZ())) {
               this.wasbonemeal = true;
               this.pickupStatus = PickupStatus.DISALLOWED;
            }

            if (applyBonemeal(this.world, blockpos.getX(), blockpos.getY() + 1, blockpos.getZ() - 2)) {
               this.wasbonemeal = true;
               this.pickupStatus = PickupStatus.DISALLOWED;
            } else if (applyBonemeal(this.world, blockpos.getX(), blockpos.getY(), blockpos.getZ() - 2)) {
               this.wasbonemeal = true;
               this.pickupStatus = PickupStatus.DISALLOWED;
            }

            if (applyBonemeal(this.world, blockpos.getX(), blockpos.getY() + 1, blockpos.getZ() + 2)) {
               this.wasbonemeal = true;
               this.pickupStatus = PickupStatus.DISALLOWED;
            } else if (applyBonemeal(this.world, blockpos.getX(), blockpos.getY(), blockpos.getZ() + 2)) {
               this.wasbonemeal = true;
               this.pickupStatus = PickupStatus.DISALLOWED;
            }

            blockpos = blockpos.add(-1, 0, -1);

            for(int i = 0; i < 3; ++i) {
               for(int p = 0; p < 3; ++p) {
                  if (applyBonemeal(this.world, blockpos.getX() + i, blockpos.getY() + 1, blockpos.getZ() + p)) {
                     this.wasbonemeal = true;
                     this.pickupStatus = PickupStatus.DISALLOWED;
                  } else if (applyBonemeal(this.world, blockpos.getX() + i, blockpos.getY(), blockpos.getZ() + p)) {
                     this.wasbonemeal = true;
                     this.pickupStatus = PickupStatus.DISALLOWED;
                  }
               }
            }

            ++this.spawnbonemeal;
         } else if (this.wasbonemeal) {
            this.setDead();
         }
      }

   }

   public static boolean applyBonemeal(World worldIn, int p_150919_2_, int p_150919_3_, int p_150919_4_) {
      BlockPos target = new BlockPos(p_150919_2_, p_150919_3_, p_150919_4_);
      IBlockState iblockstate = worldIn.getBlockState(target);
      boolean returner = false;
      if (iblockstate.getBlock() instanceof IGrowable) {
         IGrowable igrowable = (IGrowable)iblockstate.getBlock();
         if (igrowable.canGrow(worldIn, target, iblockstate, worldIn.isRemote) && !worldIn.isRemote && igrowable.canUseBonemeal(worldIn, worldIn.rand, target, iblockstate)) {
            igrowable.grow(worldIn, worldIn.rand, target, iblockstate);
            return true;
         }
      }

      return false;
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setInteger("spawnbonemeal", this.spawnbonemeal);
      compound.setBoolean("wasbonemeal", this.wasbonemeal);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.spawnbonemeal = compound.getInteger("spawnbonemeal");
      this.wasbonemeal = compound.getBoolean("wasbonemeal");
   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }
}
