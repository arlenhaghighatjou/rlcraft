package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class EntityPStationary extends EntityPMalleable {
   protected double buried;
   protected double buriedT;
   protected boolean up = false;
   protected boolean onlyPeek;
   protected int peeking;
   protected boolean relocate;

   public EntityPStationary(World worldIn) {
      super(worldIn);
      this.tasks.removeTask(this.aiWander);
      this.tasks.removeTask(this.folow);
      this.tasks.removeTask(this.jumpT);
      if (SRPConfig.mobattacking) {
         this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityPlayer.class, 0, true, true, (Predicate)null, 1.0D, 1.0F));
         this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityLiving.class, 0, true, true, new Predicate<EntityLiving>() {
            public boolean apply(@Nullable EntityLiving entity) {
               return !(entity instanceof EntityWaterMob) && !(entity instanceof EntityAnimal) && !(entity instanceof EntityVillager) && !ParasiteEventEntity.checkEntity(entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
            }
         }, 1.0D, 1.0F));
      } else {
         this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityPlayer.class, 0, true, true, (Predicate)null, 1.0D, 1.0F));
      }

      this.onlyPeek = false;
      this.relocate = false;
      this.valueEvDeath = SRPConfig.turretLoosingEPValue;
   }

   public void onLivingUpdate() {
      if (!this.isAIDisabled()) {
         if (!this.world.isRemote) {
            this.posX = this.prevPosX;
            this.posZ = this.prevPosZ;
            if (!this.onGround) {
               this.motionY -= 0.5D;
            }
         }

         this.buried();
         super.onLivingUpdate();
         if (SRPConfigSystems.useEvolution && this.getRSChance(this.phaseCreated) == 0.0D) {
            this.attackEntityFrom(DamageSource.OUT_OF_WORLD, 1.0F);
         }

         if (this.onlyPeek && this.ticksExisted > 100) {
            if (this.getAttackTarget() != null) {
               if (!this.getAttackTarget().isEntityAlive()) {
                  this.setAttackTarget((EntityLivingBase)null);
               } else {
                  this.peeking = 0;
               }
            } else {
               ++this.peeking;
               if (this.peeking > 100) {
                  this.world.setEntityState(this, (byte)51);
                  this.up = true;
               }
            }
         }

         this.retreat(true);
      }
   }

   public void setPeek(boolean in) {
      this.onlyPeek = in;
   }

   public void setAttackTarget(EntityLivingBase entitylivingbaseIn) {
      if (this.buried <= 0.0D && this.peeking <= 50) {
         super.setAttackTarget(entitylivingbaseIn);
      }
   }

   public boolean buried() {
      if (this.buried <= 0.0D) {
         if (this.getParasiteStatus() == 3) {
            this.setParasiteStatus(0);
         } else if (!this.up) {
            this.buried = -0.1D;
         }

         return false;
      } else {
         IBlockState state = this.world.getBlockState(this.getPosition().down());
         if (state.getBlock() != Blocks.AIR) {
            int id = Block.getStateId(state);

            for(int i = 0; i < 10; ++i) {
               this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY, this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, new int[]{id});
            }
         }

         if (!this.up) {
            this.buried -= 0.08D;
         }

         return true;
      }
   }

   public void setBuried() {
      this.setParasiteStatus(3);
      this.buried = this.buriedT;
   }

   protected void retreat(boolean dead) {
      if (this.up) {
         this.buried += 0.08D;
         this.setParasiteStatus(3);
         if (this.buried > this.buriedT && !this.world.isRemote) {
            if (dead) {
               this.setDead();
            } else if (this.getAttackTarget() != null && ParasiteEventEntity.teleportDigging(this, 10.0F, this.getAttackTarget().getPosition(), 5, 2)) {
               this.world.setEntityState(this, (byte)52);
               this.up = false;
            }
         }
      }

   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      boolean flag = super.attackEntityFrom(source, amount);
      if (flag && source == DamageSource.IN_WALL && this.blockH != 0.0F) {
         this.skillBreakBlocks();
      }

      return flag;
   }

   @SideOnly(Side.CLIENT)
   public double getFloorTimer() {
      return this.isAIDisabled() ? -0.1D : this.buried;
   }

   public double getRSChance(byte in) {
      switch(in) {
      case 1:
         return SRPConfigSystems.reinforcementSystemChanceOne;
      case 2:
         return SRPConfigSystems.reinforcementSystemChanceTwo;
      case 3:
         return SRPConfigSystems.reinforcementSystemChanceThree;
      case 4:
         return SRPConfigSystems.reinforcementSystemChanceFour;
      case 5:
         return SRPConfigSystems.reinforcementSystemChanceFive;
      case 6:
         return SRPConfigSystems.reinforcementSystemChanceSix;
      case 7:
         return SRPConfigSystems.reinforcementSystemChanceSeven;
      case 8:
         return SRPConfigSystems.reinforcementSystemChanceEight;
      default:
         return 0.0D;
      }
   }

   public void applyEntityCollision(Entity entityIn) {
   }

   protected void collideWithEntity(Entity entityIn) {
   }

   public void knockBack(Entity entityIn, float strength, double xRatio, double zRatio) {
   }

   public boolean isPushedByWater() {
      return false;
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setBoolean("parasitedepeek", this.onlyPeek);
      compound.setBoolean("parasiteuppp", this.up);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      if (compound.hasKey("parasitedepeek", 99)) {
         this.setPeek(compound.getBoolean("parasitedepeek"));
      }

      if (compound.hasKey("parasiteuppp", 99)) {
         this.up = compound.getBoolean("parasiteuppp");
      }

   }

   @SideOnly(Side.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 50) {
         this.buried = this.buriedT;
      } else if (id == 51) {
         this.up = true;
      } else if (id == 52) {
         this.onlyPeek = true;
      } else {
         super.handleStatusUpdate(id);
      }

   }

   public void skillBreakBlocks() {
      if (this.blockH != 0.0F) {
         int blocksbroke = 0;
         int i1 = MathHelper.floor(this.posY + 0.1D);
         double l1 = this.posX;
         double i2 = this.posZ;
         boolean flag = false;
         int Brangeatm = this.BGrange;
         int offsetT = -1;

         for(int k2 = -1 * this.BGrange; k2 <= 1 * this.BGrange; ++k2) {
            for(int l2 = -1 * this.BGrange; l2 <= 1 * this.BGrange; ++l2) {
               for(int j = 1 + offsetT; j <= this.BGheight + offsetT; ++j) {
                  double i3 = l1 + (double)k2;
                  double k = (double)(i1 + j);
                  double l = i2 + (double)l2;
                  BlockPos blockpos = new BlockPos(i3, k, l);
                  IBlockState iblockstate = this.world.getBlockState(blockpos);
                  Block block = iblockstate.getBlock();
                  float bHard = iblockstate.getBlockHardness(this.world, blockpos);
                  if (bHard <= this.blockH && bHard > 0.0F && (!(block instanceof IMetaName) || block == SRPBlocks.ParasiteCanister) && block != SRPBlocks.BiomeHeart && block != SRPBlocks.ColonyHeart && block != SRPBlocks.ParasiteRubbleDense && block != SRPBlocks.ParasiteCanisterActive && block != SRPBlocks.dodN) {
                     String name = block.getRegistryName().toString();
                     if (!this.blockException(name) && block != Blocks.AIR && block.canEntityDestroy(iblockstate, this.world, blockpos, this) && ForgeEventFactory.onEntityDestroyBlock(this, blockpos, iblockstate)) {
                        if (SRPConfig.cystActive) {
                           int meta = block.getMetaFromState(iblockstate);
                           flag = this.destroyBlockPos(blockpos, false) || flag;
                           if (SRPConfig.doTileDrops) {
                              this.addToBlockInv(name + ";" + meta);
                           }
                        } else {
                           this.destroyBlockPos(blockpos, SRPConfig.doTileDrops);
                        }

                        ++blocksbroke;
                     }
                  }
               }
            }
         }

         this.BGrange = Brangeatm;
         this.SkillBGflag = true;
      }
   }
}
