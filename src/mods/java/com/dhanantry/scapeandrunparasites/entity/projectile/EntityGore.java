package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.tile.TileEntityCanister;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityGore extends Entity {
   private static final DataParameter<Integer> SKIN = EntityDataManager.createKey(EntityGore.class, DataSerializers.VARINT);
   private int groun;
   private byte type;
   public ArrayList<String> inbBlockName;
   public ArrayList<Integer> inbBlockNumber;

   public EntityGore(World worldIn) {
      super(worldIn);
      this.isImmuneToFire = true;
      this.setSize(0.4F, 0.4F);
   }

   public EntityGore(World worldIn, double x, double y, double z, EntityLivingBase igniter, float stren) {
      this(worldIn);
      this.setPosition(x, y, z);
      float f = (float)(Math.random() * 6.283185307179586D);
      this.motionX = (double)(-((float)Math.sin((double)f)) * 0.02F);
      this.motionY = 0.20000000298023224D;
      this.motionZ = (double)(-((float)Math.cos((double)f)) * 0.02F);
      this.prevPosX = x;
      this.prevPosY = y;
      this.prevPosZ = z;
      this.inbBlockName = new ArrayList();
      this.inbBlockNumber = new ArrayList();
   }

   protected void entityInit() {
      this.dataManager.register(SKIN, Integer.valueOf(0));
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (!this.hasNoGravity()) {
         this.motionY -= 0.03999999910593033D;
      }

      this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.9800000190734863D;
      this.motionY *= 0.9800000190734863D;
      this.motionZ *= 0.9800000190734863D;
      if (this.onGround) {
         this.motionX *= 0.699999988079071D;
         this.motionZ *= 0.699999988079071D;
      }

      this.handleWaterMovement();
      if (this.world.isRemote && this.ticksExisted % 5 == 0 && !this.onGround) {
         label172:
         switch(this.getSkin()) {
         case 1:
            int i = 0;

            while(true) {
               if (i > 0) {
                  break label172;
               }

               this.spawnParticles(SRPEnumParticle.GSPLASH, 0, 0, 0);
               this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
               ++i;
            }
         case 2:
            int i = 0;

            while(true) {
               if (i > 0) {
                  break label172;
               }

               this.spawnParticles(SRPEnumParticle.GCLOUD, 150, 0, 0);
               this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
               ++i;
            }
         case 3:
            int i = 0;

            while(true) {
               if (i > 0) {
                  break label172;
               }

               this.spawnParticles(SRPEnumParticle.GCLOUD, 200, 200, 0);
               this.spawnParticles(SRPEnumParticle.GSPLASH, 3, -1, -1);
               ++i;
            }
         case 4:
            for(int i = 0; i <= 0; ++i) {
               this.spawnParticles(SRPEnumParticle.GSPLASH, 0, 0, 0);
            }
         }
      }

      if (this.onGround) {
         ++this.groun;
      }

      if (this.ticksExisted >= 200) {
         this.setDead();
      }

      if (this.groun >= 1) {
         if (!this.world.isRemote) {
            label140:
            switch(this.type) {
            case 1:
               this.placeBlood(SRPBlocks.Gore.getDefaultState().withProperty(BlockGore.VARIANT, BlockGore.EnumType.SIM_SMALL));
               break;
            case 2:
               this.placeBlood(SRPBlocks.Gore.getDefaultState().withProperty(BlockGore.VARIANT, BlockGore.EnumType.PRI_SMALL));
               break;
            case 3:
               this.placeBlood(SRPBlocks.Gore.getDefaultState().withProperty(BlockGore.VARIANT, BlockGore.EnumType.ADA_SMALL));
               break;
            case 4:
               this.placeBlood(SRPBlocks.Gore.getDefaultState().withProperty(BlockGore.VARIANT, BlockGore.EnumType.PURE_SMALL));
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            default:
               break;
            case 10:
               Block block = this.world.getBlockState(this.getPosition()).getBlock();
               if (block != Blocks.AIR && block != Blocks.GRASS) {
                  BlockPos helper = this.getPosition();
                  int i = 4;

                  for(int rand = this.rand.nextInt(4); i >= 0; rand = (rand + 1) % 4) {
                     --i;
                     helper = directionToSpread(this.getPosition(), rand);
                     block = this.world.getBlockState(helper).getBlock();
                     if (block == Blocks.AIR || block == Blocks.GRASS) {
                        this.world.setBlockState(helper, SRPBlocks.ParasiteCanisterActive.getDefaultState(), 3);
                        this.addCystItems(helper);
                        this.setDead();
                        return;
                     }
                  }

                  this.world.setBlockState(this.getPosition(), SRPBlocks.ParasiteCanisterActive.getDefaultState(), 3);
                  this.addCystItems(this.getPosition());
               } else {
                  this.world.setBlockState(this.getPosition(), SRPBlocks.ParasiteCanisterActive.getDefaultState(), 3);
                  this.addCystItems(this.getPosition());
               }
               break;
            case 11:
               Block block = this.world.getBlockState(this.getPosition()).getBlock();
               if (block != Blocks.AIR && block != Blocks.GRASS) {
                  BlockPos helper = this.getPosition();
                  int i = 4;
                  int rand = this.rand.nextInt(4);

                  while(true) {
                     if (i < 0) {
                        break label140;
                     }

                     --i;
                     helper = directionToSpread(this.getPosition(), rand);
                     block = this.world.getBlockState(helper).getBlock();
                     if (block == Blocks.AIR || block == Blocks.GRASS) {
                        this.world.setBlockState(helper, SRPBlocks.dodN.getDefaultState(), 3);
                        this.setDead();
                        return;
                     }

                     rand = (rand + 1) % 4;
                  }
               } else {
                  this.world.setBlockState(this.getPosition(), SRPBlocks.dodN.getDefaultState(), 3);
                  break;
               }
            case 12:
               IBlockState state = SRPBlocks.InfestRemain.getStateFromMeta(1);
               Block block22 = this.world.getBlockState(this.getPosition()).getBlock();
               if ((block22 == Blocks.AIR || block22 instanceof BlockBush) && block22 != SRPBlocks.Gore) {
                  this.world.setBlockState(this.getPosition(), state);
               } else {
                  BlockPos helper = this.getPosition();
                  int i = 4;
                  int rand = this.rand.nextInt(4);
                  int ccc = 0;

                  while(i >= 0) {
                     --i;
                     helper = directionToSpread(this.getPosition(), rand);
                     block22 = this.world.getBlockState(helper).getBlock();
                     if ((block22 == Blocks.AIR || block22 instanceof BlockBush) && block22 != SRPBlocks.Gore) {
                        this.world.setBlockState(helper, state);
                        ++ccc;
                        if (ccc > 2) {
                           return;
                        }

                        rand = (rand + 1) % 4;
                     } else {
                        rand = (rand + 1) % 4;
                     }
                  }
               }
            }
         }

         this.setDead();
         if (this.world.isRemote) {
            for(int k = 0; k < 10; ++k) {
               this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
            }
         }
      }

   }

   private void addCystItems(BlockPos pos) {
      if (this.inbBlockName != null) {
         if (!this.inbBlockName.isEmpty()) {
            TileEntity tileentity = this.world.getTileEntity(pos);
            if (tileentity instanceof TileEntityCanister) {
               TileEntityCanister cyst = (TileEntityCanister)tileentity;
               String[] here = new String[2];
               List<ItemStack> preDrops = new ArrayList();
               int loop = 0;

               for(int i = 0; i < this.inbBlockName.size(); ++i) {
                  here = ((String)this.inbBlockName.get(i)).split(";");
                  String name = here[0];
                  int meta = 0;

                  try {
                     meta = Integer.parseInt(here[1]);
                  } catch (Exception var14) {
                     ;
                  }

                  for(Block block = Block.getBlockFromName(name); loop < ((Integer)this.inbBlockNumber.get(i)).intValue(); ++loop) {
                     boolean fat = true;

                     try {
                        preDrops.addAll(block.getDrops(this.world, pos, block.getStateFromMeta(meta), 1));
                        fat = false;
                     } catch (Exception var13) {
                        ;
                     }

                     if (fat) {
                        preDrops.add(new ItemStack(block));
                     }
                  }

                  cyst.addStack(preDrops);
                  loop = 0;
                  preDrops = new ArrayList();
               }
            }

         }
      }
   }

   private void placeBlood(IBlockState state) {
      Block block = this.world.getBlockState(this.getPosition()).getBlock();
      if ((block == Blocks.AIR || block instanceof BlockBush) && block != SRPBlocks.Gore) {
         this.world.setBlockState(this.getPosition(), state);
      } else {
         BlockPos helper = this.getPosition();
         int i = 4;

         for(int rand = this.rand.nextInt(4); i >= 0; rand = (rand + 1) % 4) {
            --i;
            helper = directionToSpread(this.getPosition(), rand);
            block = this.world.getBlockState(helper).getBlock();
            if ((block == Blocks.AIR || block instanceof BlockBush) && block != SRPBlocks.Gore) {
               this.world.setBlockState(helper, state);
               return;
            }
         }
      }

   }

   private static BlockPos directionToSpread(BlockPos atm, int choice) {
      switch(choice) {
      case 0:
         atm = atm.north();
         break;
      case 1:
         atm = atm.east();
         break;
      case 2:
         atm = atm.west();
         break;
      default:
         atm = atm.south();
      }

      return atm;
   }

   public void setType(byte in) {
      this.type = in;
      if (this.type == 12) {
         in = 4;
      }

      this.setSkin(in);
   }

   public void onLivingUpdate() {
   }

   protected void collideWithNearbyEntities() {
   }

   protected void collideWithEntity(Entity entityIn) {
      entityIn.applyEntityCollision(this);
   }

   public void onEntityUpdate() {
      super.onEntityUpdate();
   }

   public void setMotion(double xSpeedIn, double ySpeedIn, double zSpeedIn, double capX, double capY) {
      xSpeedIn = Math.min(xSpeedIn, capX);
      ySpeedIn = Math.min(ySpeedIn, capY);
      zSpeedIn = Math.min(zSpeedIn, capX);
      this.motionX = xSpeedIn * (Math.random() * 2.0D - 1.0D);
      this.motionY = ySpeedIn;
      this.motionZ = zSpeedIn * (Math.random() * 2.0D - 1.0D);
   }

   protected void writeEntityToNBT(NBTTagCompound compound) {
      compound.setInteger("parasitetype", this.getSkin());
      compound.setInteger("bloodtype", this.type);
      NBTTagList allResS = new NBTTagList();
      NBTTagList allResI = new NBTTagList();
      if (this.inbBlockName != null) {
         for(int i = 0; i < this.inbBlockName.size(); ++i) {
            String res = (String)this.inbBlockName.get(i);
            NBTTagCompound resT = new NBTTagCompound();
            resT.setString("block" + i, res);
            allResS.appendTag(resT);
            int resi = ((Integer)this.inbBlockNumber.get(i)).intValue();
            NBTTagCompound resU = new NBTTagCompound();
            resU.setInteger("block" + i, resi);
            allResI.appendTag(resU);
         }

         compound.setTag("srpinvblocksname", allResS);
         compound.setTag("srpinvblocksnumber", allResI);
      }
   }

   protected void readEntityFromNBT(NBTTagCompound compound) {
      if (compound.hasKey("parasitetype", 99)) {
         this.setSkin(compound.getInteger("parasitetype"));
      }

      if (compound.hasKey("bloodtype", 99)) {
         this.type = (byte)compound.getInteger("bloodtype");
      }

      if (compound.hasKey("srpinvblocks")) {
         NBTTagList allResS = compound.getTagList("srpinvblocksname", 10);
         NBTTagList allResI = compound.getTagList("srpinvblocksnumber", 10);
         if (allResS.tagCount() != allResI.tagCount()) {
            return;
         }

         for(int i = 0; i < allResS.tagCount(); ++i) {
            NBTTagCompound resT = allResS.getCompoundTagAt(i);
            String res = resT.getString("block" + i);
            this.inbBlockName.add(i, res);
            NBTTagCompound resU = allResI.getCompoundTagAt(i);
            int resi = resU.getInteger("block" + i);
            this.inbBlockNumber.add(i, Integer.valueOf(resi));
         }
      }

   }

   public float getEyeHeight() {
      return 0.0F;
   }

   public int getSkin() {
      return ((Integer)this.dataManager.get(SKIN)).intValue();
   }

   public void setSkin(int texture) {
      this.dataManager.set(SKIN, Integer.valueOf(texture));
   }

   @SideOnly(Side.CLIENT)
   public void spawnParticles(SRPEnumParticle particleType, int r, int g, int b) {
      double d0 = this.rand.nextGaussian() * 0.02D;
      double d1 = this.rand.nextGaussian() * 0.02D;
      double d2 = this.rand.nextGaussian() * 0.02D;
      ParticleSpawner.spawnParticle(particleType, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2, r, g, b);
   }
}
