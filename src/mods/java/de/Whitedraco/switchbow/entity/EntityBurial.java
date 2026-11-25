package de.Whitedraco.switchbow.entity;

import de.Whitedraco.switchbow.Config.BlackListBurialBlockConfig;
import de.Whitedraco.switchbow.Config.BlackListConfig;
import de.Whitedraco.switchbow.Config.SwitchBowConfig;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.Vector;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityBurial extends Entity {
   Vector<PosBlockSave> PosBlockData = new Vector();
   boolean setPos = true;
   private EaglercraftUUID targetMobUUID;
   private int targetMobNetworkId;
   int Speed = 6;
   int SpeedCount = 0;
   int Count = 0;
   int MaxCount = 0;
   String noBurial = "noBurial";
   boolean noBurialFlag = false;
   long time;
   int whichBurialEffect = 2;

   public EntityBurial(World worldIn) {
      super(worldIn);
   }

   public EntityBurial(World world, double posX, double posY, double posZ, EntityLivingBase mob) {
      super(world);
      this.whichBurialEffect = SwitchBowConfig.whichBurialEffect.getInteger();
      this.setPosition(posX, posY, posZ);
      if (mob.getTags().contains(this.noBurial)) {
         this.noBurialFlag = true;
      }

      if (this.whichBurialEffect < 3 && this.whichBurialEffect > 0 && !BlackListConfig.isBlacklistBurial(mob) && !this.noBurialFlag) {
         this.getBlock(mob, posX, posY, posZ);
      }

      this.setTargetMob(mob);
      mob.setPosition(this.posX, this.posY, this.posZ);
   }

   private void getBlock(EntityLivingBase entitylivingbase, double posx, double posy, double posz) {
      float Mobheight = entitylivingbase.height;
      float Mobradius = entitylivingbase.width / 2.0F;
      posx = entitylivingbase.posX;
      posy = entitylivingbase.posY;
      posz = entitylivingbase.posZ;
      int minx = (int)Math.floor(posx - (double)Mobradius);
      int maxx = (int)Math.floor(posx + (double)Mobradius);
      int maxy = (int)posy;
      int miny = (int)(posy - (double)Mobheight - 1.0D);
      int minz = (int)Math.floor(posz - (double)Mobradius);
      int maxz = (int)Math.floor(posz + (double)Mobradius);

      for(int y = miny; y <= maxy; ++y) {
         for(int x = minx; x <= maxx; ++x) {
            for(int z = minz; z <= maxz; ++z) {
               if (this.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.BEDROCK && this.world.getTileEntity(new BlockPos(x, y, z)) == null && this.world.getBlockState(new BlockPos(x, y, z)).getBlock() != Blocks.AIR && this.world.getBlockState(new BlockPos(x, y, z)).getBlock() != null && this.isUnbreakable(this.world, new BlockPos(x, y, z))) {
                  this.PosBlockData.add(new PosBlockSave(this.world.getBlockState(new BlockPos(x, y, z)), new BlockPos(x, y, z)));
                  this.world.setBlockToAir(new BlockPos(x, y, z));
               }
            }
         }
      }

      this.MaxCount = this.PosBlockData.size();
   }

   private boolean isUnbreakable(World worldIn, BlockPos pos) {
      return this.world.getBlockState(pos).getBlockHardness(worldIn, pos) >= 0.0F && this.world.getBlockState(pos).getBlockHardness(worldIn, pos) < 6000000.0F && !BlackListBurialBlockConfig.isBlacklistBlock(this.world.getBlockState(pos).getBlock());
   }

   public void onUpdate() {
      super.onEntityUpdate();
      Entity mob = this.getTargetMob();
      if (mob != null && !BlackListConfig.isBlacklistBurial(mob) && !this.noBurialFlag) {
         if (!this.world.isRemote && !mob.getTags().contains(this.noBurial)) {
            mob.addTag(this.noBurial);
         }

         if (this.whichBurialEffect == 1 || this.whichBurialEffect == 2) {
            ++this.SpeedCount;
            if (this.SpeedCount >= this.Speed) {
               if (!this.world.isRemote) {
                  if (this.whichBurialEffect == 2) {
                     int actualY = ((PosBlockSave)this.PosBlockData.get(this.Count)).i.getY();

                     while(true) {
                        BlockPos pos = ((PosBlockSave)this.PosBlockData.get(this.Count)).i;
                        IBlockState state = ((PosBlockSave)this.PosBlockData.get(this.Count)).s;
                        EntityFallingBlock fallingBlock = new EntityFallingBlock(this.world, (double)pos.getX() + 0.5D, this.posY + 0.5D, (double)pos.getZ() + 0.5D, state);
                        fallingBlock.fallTime = 1;
                        this.world.spawnEntity(fallingBlock);
                        ++this.Count;
                        if (this.Count >= this.MaxCount) {
                           this.setDead();
                           this.removeTagBurial();
                           return;
                        }

                        if (this.PosBlockData.size() < this.Count || ((PosBlockSave)this.PosBlockData.get(this.Count)).i.getY() != actualY) {
                           break;
                        }
                     }

                     --this.Count;
                  } else if (this.whichBurialEffect == 1) {
                     int actualY = ((PosBlockSave)this.PosBlockData.get(this.Count)).i.getY();

                     while(true) {
                        if (this.world.getBlockState(((PosBlockSave)this.PosBlockData.get(this.PosBlockData.size() - 1 - this.Count)).i).getBlock() == Blocks.AIR) {
                           this.world.setBlockState(((PosBlockSave)this.PosBlockData.get(this.PosBlockData.size() - 1 - this.Count)).i, ((PosBlockSave)this.PosBlockData.get(this.PosBlockData.size() - 1 - this.Count)).s);
                        }

                        ++this.Count;
                        if (this.Count >= this.MaxCount) {
                           this.setDead();
                           this.removeTagBurial();
                           return;
                        }

                        if (this.PosBlockData.size() < this.Count || ((PosBlockSave)this.PosBlockData.get(this.Count)).i.getY() != actualY) {
                           break;
                        }
                     }

                     --this.Count;
                  }
               }

               this.SpeedCount = 0;
               ++this.Count;
            }

            if (this.Count >= this.MaxCount) {
               this.setDead();
               this.removeTagBurial();
            }

            if (!this.world.isRemote && mob != null) {
               mob.motionY = -1.0D;
            }
         }

         if (this.whichBurialEffect >= 3 || this.whichBurialEffect <= 0) {
            double posiY = mob.posY + (double)mob.height + 0.2D;
            if (posiY < this.posY || this.ticksExisted > 200) {
               this.removeTagBurial();
               this.setDead();
               return;
            }

            if (posiY >= this.posY && !this.world.isRemote && mob != null) {
               mob.setPositionAndUpdate(this.posX, mob.posY - 0.075D, this.posZ);
            }
         }

      } else {
         this.setDead();
      }
   }

   protected void entityInit() {
   }

   protected void readEntityFromNBT(NBTTagCompound compound) {
      this.SpeedCount = compound.getInteger("speedcount");
      this.Count = compound.getInteger("count");
      this.MaxCount = compound.getInteger("list");

      for(int x = 0; x < this.MaxCount; ++x) {
         this.PosBlockData.add(x, new PosBlockSave(NBTUtil.readBlockState(compound.getCompoundTag("BlockState" + x)), NBTUtil.getPosFromTag(compound.getCompoundTag("BlockPos" + x))));
      }

      if (compound.hasUniqueId("targetMob")) {
         this.targetMobUUID = compound.getUniqueId("targetMob");
      }

      this.whichBurialEffect = compound.getInteger("whichBurial");
   }

   protected void writeEntityToNBT(NBTTagCompound compound) {
      compound.setInteger("speedcount", this.SpeedCount);
      compound.setInteger("count", this.Count);

      for(int x = 0; x < this.PosBlockData.size(); ++x) {
         compound.setTag("BlockPos" + x, NBTUtil.createPosTag(((PosBlockSave)this.PosBlockData.get(x)).i));
         compound.setTag("BlockState" + x, NBTUtil.writeBlockState(new NBTTagCompound(), ((PosBlockSave)this.PosBlockData.get(x)).s));
      }

      this.MaxCount = this.PosBlockData.size();
      compound.setInteger("list", this.MaxCount);
      if (this.targetMobUUID != null) {
         compound.setUniqueId("targetMob", this.targetMobUUID);
      }

      compound.setInteger("whichBurial", this.whichBurialEffect);
   }

   public void setTargetMob(@Nullable Entity p_212361_1_) {
      if (p_212361_1_ != null) {
         this.targetMobUUID = p_212361_1_.getUniqueID();
         this.targetMobNetworkId = p_212361_1_.getEntityId();
      }

   }

   @Nullable
   public Entity getTargetMob() {
      if (this.targetMobUUID != null && this.world instanceof WorldServer) {
         return ((WorldServer)this.world).getEntityFromUuid(this.targetMobUUID);
      } else {
         return this.targetMobNetworkId != 0 ? this.world.getEntityByID(this.targetMobNetworkId) : null;
      }
   }

   private void removeTagBurial() {
      if (!this.world.isRemote) {
         this.getTargetMob().removeTag(this.noBurial);
      }

   }
}
