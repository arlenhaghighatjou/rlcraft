package com.dhanantry.scapeandrunparasites.entity.monster.inborn;

import com.dhanantry.scapeandrunparasites.block.BlockColonyStructure;
import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityKol extends EntityParasiteBase {
   EntityKol.EntityAIFindingSpotBlock build;

   public EntityKol(World worldIn) {
      super(worldIn);
      this.setSize(0.85F, 1.0F);
      this.experienceValue = SRPAttributes.XP_LiTTLE;
      this.tasks.removeTask(this.folow);
      this.type = 7;
      this.killcount = -10.0D;
   }

   public int getParasiteIDRegister() {
      return 36;
   }

   public EntityKol(World worldIn, BlockPos origin, int distanceBuilding) {
      this(worldIn);
      this.setTask(origin, distanceBuilding);
   }

   public void setTask(BlockPos origin, int distanceBuilding) {
      this.build = new EntityKol.EntityAIFindingSpotBlock(this, distanceBuilding);
      this.tasks.addTask(3, this.build);
      this.setOrigin(origin);
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(8, new EntityAILookIdle(this));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.LODO_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.LODO_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.LODO_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.LODO_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
   }

   public float getEyeHeight() {
      return 0.8F;
   }

   public static void registerFixesLodo(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityKol.class);
   }

   public void setOrigin(BlockPos pos) {
      this.build.setOrigin(pos.getX(), pos.getY(), pos.getZ());
   }

   public void setOrigin(int x, int y, int z) {
      this.build.setOrigin(x, y, z);
   }

   public boolean getCanSpawnHere() {
      IBlockState iblockstate = this.world.getBlockState((new BlockPos(this)).down());
      return iblockstate.canEntitySpawn(this) && this.world.getDifficulty() != EnumDifficulty.PEACEFUL && this.isValidLightLevelTwo() && SRPConfig.spawnDays <= (int)this.world.getWorldTime();
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      if (this.build != null) {
         compound.setInteger("parasiteoriginx", this.build.getOrigin(1));
         compound.setInteger("parasiteoriginy", this.build.getOrigin(2));
         compound.setInteger("parasiteoriginz", this.build.getOrigin(3));
      }

   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      int x = 0;
      int y = 0;
      int z = 0;
      if (compound.hasKey("parasiteoriginx", 99)) {
         x = compound.getInteger("parasiteoriginx");
      }

      if (compound.hasKey("parasiteoriginy", 99)) {
         y = compound.getInteger("parasiteoriginy");
      }

      if (compound.hasKey("parasiteoriginz", 99)) {
         z = compound.getInteger("parasiteoriginz");
      }

      this.build = new EntityKol.EntityAIFindingSpotBlock(this, 10);
      this.tasks.addTask(3, this.build);
      this.setOrigin(x, y, z);
   }

   public class EntityAIFindingSpotBlock extends EntityAIBase {
      protected final EntityParasiteBase entity;
      private int building;
      private int defence;
      private int tiick;
      private int originX;
      private int originY;
      private int originZ;
      private int maxDistance;

      public EntityAIFindingSpotBlock(EntityKol creatureIn, int maxDistance) {
         this.entity = creatureIn;
         this.building = 26;
         this.defence = this.building / 2;
         this.tiick = 0;
         this.maxDistance = maxDistance * maxDistance;
         this.setMutexBits(1);
      }

      public boolean shouldExecute() {
         ++this.tiick;
         return this.tiick >= 200 && this.entity.getParasiteStatus() == 0;
      }

      public boolean shouldContinueExecuting() {
         return this.entity.getNavigator().noPath() && this.entity.getParasiteStatus() == 0 && this.tiick != 0;
      }

      public void resetTask() {
         this.tiick = 0;
      }

      public void updateTask() {
         BlockPos flag = this.entity.getPosition();
         int range = 25;

         for(int x = flag.getX() - range; x <= flag.getX() + range; ++x) {
            for(int z = flag.getZ() - range; z <= flag.getZ() + range; ++z) {
               if (this.checkPosition(x, z) == this.defence) {
                  BlockPos spot = this.checkBlock(EntityKol.this.world, new BlockPos(x, flag.getY(), z));
                  if (spot != null) {
                     this.entity.world.setBlockState(spot.down(), SRPBlocks.ParasiteStructure.getDefaultState().withProperty(BlockColonyStructure.ACTIVE, Integer.valueOf(2)));
                     this.resetTask();
                     return;
                  }
               } else if (this.checkPosition(x, z) == this.building) {
                  BlockPos spot = this.checkBlock(EntityKol.this.world, new BlockPos(x, flag.getY(), z));
                  if (spot != null) {
                     this.entity.world.setBlockState(spot.down(), SRPBlocks.ParasiteStructure.getDefaultState().withProperty(BlockColonyStructure.ACTIVE, Integer.valueOf(1)));
                     this.resetTask();
                     return;
                  }
               }
            }
         }

         this.resetTask();
      }

      private BlockPos checkBlock(World world, BlockPos pos) {
         if (this.getDistanceSqFromOrigin(pos)) {
            return null;
         } else {
            pos = ParasiteEventEntity.getFloor(world, pos, 5);
            if (pos != null) {
               if (this.checkArea(world, pos)) {
                  return null;
               }

               Block blockL = world.getBlockState(pos.down()).getBlock();
               if (blockL != SRPBlocks.ParasiteStructure && blockL != SRPBlocks.ColonyHeart && blockL != SRPBlocks.BiomeHeart && blockL != SRPBlocks.ParasiteRubbleDense && !(blockL instanceof IMetaName)) {
                  return pos;
               }
            }

            return null;
         }
      }

      private boolean checkArea(World world, BlockPos pos) {
         int a = 20;
         int yy = pos.getY();

         for(int i = yy - a; i <= yy + a; ++i) {
            if (world.getBlockState(new BlockPos(pos.getX(), i, pos.getZ())).getBlock() == SRPBlocks.ParasiteStructure) {
               return true;
            }
         }

         return false;
      }

      private int checkPosition(int posX, int posZ) {
         if (posX % this.defence == 0 && posX % this.building != 0 && posZ % this.defence == 0 && posZ % this.building != 0) {
            return this.defence;
         } else {
            return posX % this.building == 0 && posZ % this.building == 0 ? this.building : 0;
         }
      }

      private boolean getDistanceSqFromOrigin(BlockPos pos) {
         double d0 = (double)(this.originX - pos.getX());
         double d1 = (double)(this.originY - pos.getY());
         double d2 = (double)(this.originZ - pos.getZ());
         return d0 * d0 + d1 * d1 + d2 * d2 > (double)this.maxDistance;
      }

      public void setOrigin(int x, int y, int z) {
         this.originX = x;
         this.originY = y;
         this.originZ = z;
      }

      public int getOrigin(int in) {
         switch(in) {
         case 1:
            return this.originX;
         case 2:
            return this.originY;
         case 3:
            return this.originZ;
         default:
            return 0;
         }
      }
   }
}
