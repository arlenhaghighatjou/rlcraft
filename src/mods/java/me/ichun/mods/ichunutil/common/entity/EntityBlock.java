package me.ichun.mods.ichunutil.common.entity;

import java.util.Set;
import javax.annotation.Nullable;
import me.ichun.mods.ichunutil.api.event.BlockEntityEvent;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.grab.GrabHandler;
import me.ichun.mods.ichunutil.common.packet.mod.PacketRequestBlockEntityData;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

public class EntityBlock extends Entity {
   private static final DataParameter<Float> ROT_YAW = EntityDataManager.createKey(EntityBlock.class, DataSerializers.FLOAT);
   private static final DataParameter<Float> ROT_PITCH = EntityDataManager.createKey(EntityBlock.class, DataSerializers.FLOAT);
   private static final DataParameter<Boolean> CAN_ROTATE = EntityDataManager.createKey(EntityBlock.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> BEHAVIOUR = EntityDataManager.createKey(EntityBlock.class, DataSerializers.VARINT);
   private static final DataParameter<BlockPos> ORIGIN = EntityDataManager.createKey(EntityBlock.class, DataSerializers.BLOCK_POS);
   public EntityLivingBase creator;
   public float rotYaw;
   public float rotPitch;
   public float prevRotYaw;
   public float prevRotPitch;
   public double prevMotionX;
   public double prevMotionY;
   public double prevMotionZ;
   public int timeOnGround;
   public int timeExisting;
   public int timeout;
   public boolean canDropItems;
   public boolean requestedSetupPacket;
   public boolean setup;
   public IBlockState[][][] blocks;
   public NBTTagCompound[][][] tileEntityNBTs;
   public TileEntityMobSpawner[][][] mobSpawners;
   @SideOnly(Side.CLIENT)
   public TileEntity[][][] renderingTileEntities;
   public static float maxRotFac = 25.0F;

   public EntityBlock(World world) {
      super(world);
      this.setSize(0.95F, 0.95F);
      this.preventEntitySpawning = true;
      this.isImmuneToFire = true;
      this.canDropItems = true;
      this.blocks = new IBlockState[][][]{{{Blocks.AIR.getDefaultState()}}};
      this.tileEntityNBTs = new NBTTagCompound[1][1][1];
   }

   @Nullable
   public static EntityBlock createEntityBlock(World world, EntityLivingBase living, Set<BlockPos> poses) {
      return !MinecraftForge.EVENT_BUS.post(new BlockEntityEvent.Pickup(world, living, poses)) && !poses.isEmpty() ? new EntityBlock(world, living, poses) : null;
   }

   private EntityBlock(World world, EntityLivingBase living, Set<BlockPos> poses) {
      this(world);
      this.creator = living;
      int lowX = Integer.MAX_VALUE;
      int lowY = Integer.MAX_VALUE;
      int lowZ = Integer.MAX_VALUE;
      int highX = Integer.MIN_VALUE;
      int highY = Integer.MIN_VALUE;
      int highZ = Integer.MIN_VALUE;

      for(BlockPos pos : poses) {
         if (pos.getX() < lowX) {
            lowX = pos.getX();
         }

         if (pos.getY() < lowY) {
            lowY = pos.getY();
         }

         if (pos.getZ() < lowZ) {
            lowZ = pos.getZ();
         }

         if (pos.getX() > highX) {
            highX = pos.getX();
         }

         if (pos.getY() > highY) {
            highY = pos.getY();
         }

         if (pos.getZ() > highZ) {
            highZ = pos.getZ();
         }
      }

      int countX = highX - lowX + 1;
      int countY = highY - lowY + 1;
      int countZ = highZ - lowZ + 1;
      this.blocks = new IBlockState[countX][countY][countZ];
      this.tileEntityNBTs = new NBTTagCompound[countX][countY][countZ];
      this.mobSpawners = new TileEntityMobSpawner[countX][countY][countZ];
      world.restoringBlockSnapshots = true;

      for(BlockPos pos : poses) {
         IBlockState state = world.getBlockState(pos);
         if (state.getBlock() != Blocks.AIR) {
            this.blocks[highX - pos.getX()][highY - pos.getY()][highZ - pos.getZ()] = state;
            TileEntity te = world.getTileEntity(pos);
            if (te != null && state.getBlock().hasTileEntity(state)) {
               world.setTileEntity(pos, state.getBlock().createTileEntity(world, state));
               this.tileEntityNBTs[highX - pos.getX()][highY - pos.getY()][highZ - pos.getZ()] = new NBTTagCompound();
               te.writeToNBT(this.tileEntityNBTs[highX - pos.getX()][highY - pos.getY()][highZ - pos.getZ()]);
               te.invalidate();
               TileEntity te1 = state.getBlock().createTileEntity(world, state);
               if (te instanceof TileEntityMobSpawner && te1 instanceof TileEntityMobSpawner) {
                  te1.readFromNBT(this.tileEntityNBTs[highX - pos.getX()][highY - pos.getY()][highZ - pos.getZ()]);
                  this.mobSpawners[highX - pos.getX()][highY - pos.getY()][highZ - pos.getZ()] = (TileEntityMobSpawner)te1;
               }
            }
         }

         world.setBlockState(pos, Blocks.DIRT.getDefaultState(), 2);
      }

      world.restoringBlockSnapshots = false;

      for(BlockPos pos : poses) {
         world.setBlockToAir(pos);
      }

      this.setSize();
      this.setLocationAndAngles((double)((float)lowX + (float)countX / 2.0F), (double)lowY + 0.025D, (double)((float)lowZ + (float)countZ / 2.0F), 0.0F, 0.0F);
      this.getDataManager().set(ORIGIN, new BlockPos(lowX, lowY, lowZ));
   }

   public void setSize() {
      this.setSize((float)Math.max(this.blocks.length, this.blocks[0][0].length) - 0.05F, (float)this.blocks[0].length - 0.05F);
   }

   public void setSize(float width, float height) {
      float f2 = this.width;
      this.width = width;
      this.height = height;
      if (this.blocks == null) {
         this.setEntityBoundingBox(new AxisAlignedBB(this.getEntityBoundingBox().minX, this.getEntityBoundingBox().minY, this.getEntityBoundingBox().minZ, this.getEntityBoundingBox().minX + (double)this.width, this.getEntityBoundingBox().minY + (double)this.height, this.getEntityBoundingBox().minZ + (double)this.width));
      } else {
         double var10003 = this.getEntityBoundingBox().minX;
         double var10004 = this.getEntityBoundingBox().minY;
         double var10005 = this.getEntityBoundingBox().minZ;
         float var10007 = (float)this.blocks.length;
         double var10006 = this.getEntityBoundingBox().minX + (double)(var10007 - 0.05F);
         float var10008 = (float)this.blocks[0].length;
         this.setEntityBoundingBox(new AxisAlignedBB(var10003, var10004, var10005, var10006, this.getEntityBoundingBox().minY + (double)(var10008 - 0.05F), this.getEntityBoundingBox().minZ + (double)((float)this.blocks[0][0].length - 0.05F)));
      }

      if (this.width > f2 && !this.firstUpdate && !this.world.isRemote) {
         this.move(MoverType.SELF, (double)(f2 - this.width), 0.0D, (double)(f2 - this.width));
      }

   }

   @SideOnly(Side.CLIENT)
   public boolean isInRangeToRenderDist(double distance) {
      double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 20.0D;
      if (Double.isNaN(d0)) {
         d0 = 1.0D;
      }

      d0 = d0 * 64.0D * getRenderDistanceWeight();
      return distance < d0 * d0;
   }

   public void setPosition(double x, double y, double z) {
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      if (this.blocks == null) {
         float f = this.width / 2.0F;
         float f1 = this.height;
         this.setEntityBoundingBox(new AxisAlignedBB(x - (double)f, y, z - (double)f, x + (double)f, y + (double)f1, z + (double)f));
      } else {
         float fx = ((float)this.blocks.length - 0.05F) / 2.0F;
         float f1 = (float)this.blocks[0].length - 0.05F;
         float fz = ((float)this.blocks[0][0].length - 0.05F) / 2.0F;
         this.setEntityBoundingBox(new AxisAlignedBB(x - (double)fx, y, z - (double)fz, x + (double)fx, y + (double)f1, z + (double)fz));
      }

   }

   protected void entityInit() {
      this.getDataManager().register(ROT_YAW, Float.valueOf(this.rand.nextFloat() * 2.0F * maxRotFac - maxRotFac));
      this.getDataManager().register(ROT_PITCH, Float.valueOf(this.rand.nextFloat() * 2.0F * maxRotFac - maxRotFac));
      this.getDataManager().register(CAN_ROTATE, Boolean.valueOf(true));
      this.getDataManager().register(BEHAVIOUR, Integer.valueOf(0));
      this.getDataManager().register(ORIGIN, BlockPos.ORIGIN);
   }

   public void setRotFacYaw(float f) {
      this.getDataManager().set(ROT_YAW, Float.valueOf(f));
   }

   public float getRotFacYaw() {
      return ((Float)this.getDataManager().get(ROT_YAW)).floatValue();
   }

   public void setRotFacPitch(float f) {
      this.getDataManager().set(ROT_PITCH, Float.valueOf(f));
   }

   public float getRotFacPitch() {
      return ((Float)this.getDataManager().get(ROT_PITCH)).floatValue();
   }

   public void setCanRotate(boolean flag) {
      this.getDataManager().set(CAN_ROTATE, Boolean.valueOf(flag));
   }

   public boolean getCanRotate() {
      return ((Boolean)this.getDataManager().get(CAN_ROTATE)).booleanValue();
   }

   public void setBehaviour(int i) {
      this.getDataManager().set(BEHAVIOUR, Integer.valueOf(1));
   }

   public int getBehaviour() {
      return ((Integer)this.getDataManager().get(BEHAVIOUR)).intValue();
   }

   public BlockPos getOrigin() {
      return (BlockPos)this.getDataManager().get(ORIGIN);
   }

   public void onUpdate() {
      if (this.world.isRemote && !this.setup) {
         if (!this.requestedSetupPacket) {
            this.requestedSetupPacket = true;
            iChunUtil.channel.sendToServer(new PacketRequestBlockEntityData(this));
         }

      } else {
         ++this.timeExisting;
         if (!this.world.isRemote && this.posY < -500.0D) {
            this.setDead();
         } else {
            this.noClip = this.timeExisting < this.timeout;
            this.prevRotYaw = this.rotYaw;
            this.prevRotPitch = this.rotPitch;
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            if (!GrabHandler.isGrabbed(this, FMLCommonHandler.instance().getEffectiveSide())) {
               this.rotYaw += this.getRotFacYaw();
               this.rotPitch += this.getRotFacPitch();
            }

            this.prevMotionX = this.motionX;
            this.prevMotionY = this.motionY;
            this.prevMotionZ = this.motionZ;
            this.motionY -= 0.06D;
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            boolean setBlock = false;
            if (!GrabHandler.isGrabbed(this, FMLCommonHandler.instance().getEffectiveSide())) {
               if (this.onGround && this.ticksExisted > 2) {
                  ++this.timeOnGround;
                  if (this.motionY == 0.0D) {
                     if (this.prevMotionY < -0.1D) {
                        double minBounceFactor = Math.sqrt(1.3333333333333333D);
                        float blockHardness = this.getAverageBlockHardness();
                        double bounceFactor = (double)blockHardness < minBounceFactor ? minBounceFactor : (double)blockHardness;
                        this.motionY = this.prevMotionY * -(1.0D / (2.0D * bounceFactor));
                        this.setRotFacYaw(this.rand.nextFloat() * 2.0F * maxRotFac - maxRotFac);
                        this.setRotFacPitch(this.rand.nextFloat() * 2.0F * maxRotFac - maxRotFac);
                     }

                     if (this.timeOnGround > 3) {
                        this.setRotFacYaw(this.getRotFacYaw() * 0.6F);
                        this.setRotFacPitch(this.getRotFacPitch() * 0.6F);
                     }

                     this.motionX *= 0.7D;
                     this.motionZ *= 0.7D;
                     if (Math.abs(this.prevMotionX) < 0.05D && Math.abs(this.prevMotionZ) < 0.05D && Math.abs(this.prevMotionY) < 0.05D && (double)Math.abs(this.getRotFacYaw()) < 0.05D && (double)Math.abs(this.getRotFacPitch()) < 0.05D) {
                        setBlock = true;
                     }
                  }
               } else {
                  this.timeOnGround = 0;
               }

               if (this.motionX == 0.0D && this.prevMotionX != this.motionX) {
                  double minBounceFactor = Math.sqrt(1.3333333333333333D);
                  float blockHardness = this.getAverageBlockHardness();
                  double bounceFactor = 2.0D * ((double)blockHardness < minBounceFactor ? minBounceFactor : (double)blockHardness);
                  this.motionX = this.prevMotionX * -(1.0D / (bounceFactor * bounceFactor));
                  this.setRotFacYaw(this.rand.nextFloat() * 2.0F * maxRotFac - maxRotFac);
                  this.setRotFacPitch(this.rand.nextFloat() * 2.0F * maxRotFac - maxRotFac);
               }

               if (this.motionZ == 0.0D && this.prevMotionZ != this.motionZ) {
                  double minBounceFactor = Math.sqrt(1.3333333333333333D);
                  float blockHardness = this.getAverageBlockHardness();
                  double bounceFactor = 2.0D * ((double)blockHardness < minBounceFactor ? minBounceFactor : (double)blockHardness);
                  this.motionZ = this.prevMotionZ * -(1.0D / (bounceFactor * bounceFactor));
                  this.setRotFacYaw(this.rand.nextFloat() * 2.0F * maxRotFac - maxRotFac);
                  this.setRotFacPitch(this.rand.nextFloat() * 2.0F * maxRotFac - maxRotFac);
               }
            } else if (!this.world.isRemote) {
               for(int i = 0; i < this.mobSpawners.length; ++i) {
                  for(int j = this.mobSpawners[i].length - 1; j >= 0; --j) {
                     for(int k = 0; k < this.mobSpawners[i][j].length; ++k) {
                        if (this.mobSpawners[i][j][k] != null) {
                           BlockPos pos = new BlockPos(this.posX - (this.getEntityBoundingBox().maxX - this.getEntityBoundingBox().minX + 0.05D) / 2.0D + (double)this.blocks.length - (double)i - 0.5D, this.posY + (double)this.blocks[i].length - (double)j - 0.5D, this.posZ - (this.getEntityBoundingBox().maxZ - this.getEntityBoundingBox().minZ + 0.05D) / 2.0D + (double)this.blocks[i][j].length - (double)k - 0.5D);
                           TileEntityMobSpawner spawner = this.mobSpawners[i][j][k];
                           spawner.setWorld(this.world);
                           spawner.setPos(pos);
                           spawner.update();
                           spawner.update();
                        }
                     }
                  }
               }
            }

            if (!this.world.isRemote && (setBlock || this.timeExisting > 6000)) {
               this.setDead();

               for(int i = 0; i < this.blocks.length; ++i) {
                  for(int j = this.blocks[i].length - 1; j >= 0; --j) {
                     for(int k = 0; k < this.blocks[i][j].length; ++k) {
                        if (this.blocks[i][j][k] != null) {
                           BlockPos pos = new BlockPos(this.posX - (this.getEntityBoundingBox().maxX - this.getEntityBoundingBox().minX + 0.05D) / 2.0D + (double)this.blocks.length - (double)i - 0.5D, this.posY + (double)this.blocks[i].length - (double)j - 0.5D, this.posZ - (this.getEntityBoundingBox().maxZ - this.getEntityBoundingBox().minZ + 0.05D) / 2.0D + (double)this.blocks[i][j].length - (double)k - 0.5D);
                           if (!MinecraftForge.EVENT_BUS.post(new BlockEntityEvent.Place(this.world, this, this.creator, this.blocks[i][j][k], pos)) && (this.world.setBlockState(pos, this.blocks[i][j][k], 2) || !this.canDropItems)) {
                              if (this.tileEntityNBTs[i][j][k] != null && this.blocks[i][j][k].getBlock().hasTileEntity(this.blocks[i][j][k])) {
                                 TileEntity te = this.world.getTileEntity(pos);
                                 if (te != null) {
                                    NBTTagCompound nbttagcompound = new NBTTagCompound();
                                    te.writeToNBT(nbttagcompound);

                                    for(String s : this.tileEntityNBTs[i][j][k].getKeySet()) {
                                       NBTBase nbtbase = this.tileEntityNBTs[i][j][k].getTag(s);
                                       if (!s.equals("x") && !s.equals("y") && !s.equals("z")) {
                                          nbttagcompound.setTag(s, nbtbase.copy());
                                       }
                                    }

                                    te.readFromNBT(nbttagcompound);
                                    te.markDirty();
                                 }
                              }
                           } else {
                              this.blocks[i][j][k].getBlock().dropBlockAsItem(this.world, pos, this.blocks[i][j][k], 0);
                              if (this.tileEntityNBTs[i][j][k] != null && this.blocks[i][j][k].getBlock().hasTileEntity(this.blocks[i][j][k])) {
                                 TileEntity te = this.blocks[i][j][k].getBlock().createTileEntity(this.world, this.blocks[i][j][k]);
                                 if (te instanceof IInventory) {
                                    te.readFromNBT(this.tileEntityNBTs[i][j][k]);
                                    this.handleIInventoryBreak((IInventory)te);
                                 } else if (te instanceof IItemHandler) {
                                    te.readFromNBT(this.tileEntityNBTs[i][j][k]);
                                    this.handleIItemHandlerBreak((IItemHandler)te);
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               for(int i = 0; i < this.blocks.length; ++i) {
                  for(int j = this.blocks[i].length - 1; j >= 0; --j) {
                     for(int k = 0; k < this.blocks[i][j].length; ++k) {
                        if (this.blocks[i][j][k] != null) {
                           BlockPos pos = new BlockPos(this.posX - (this.getEntityBoundingBox().maxX - this.getEntityBoundingBox().minX + 0.05D) / 2.0D + (double)this.blocks.length - (double)i - 0.5D, this.posY + (double)this.blocks[i].length - (double)j - 0.5D, this.posZ - (this.getEntityBoundingBox().maxZ - this.getEntityBoundingBox().minZ + 0.05D) / 2.0D + (double)this.blocks[i][j].length - (double)k - 0.5D);
                           this.world.notifyNeighborsRespectDebug(pos, Blocks.AIR, false);
                        }
                     }
                  }
               }
            }

            this.motionX *= 0.95D;
            this.motionY *= 0.95D;
            this.motionZ *= 0.95D;
         }
      }
   }

   public float getAverageBlockHardness() {
      float hardness = 0.0F;
      int count = 0;

      for(int i = 0; i < this.blocks.length; ++i) {
         for(int j = 0; j < this.blocks[i].length; ++j) {
            for(int k = 0; k < this.blocks[i][j].length; ++k) {
               if (this.blocks[i][j][k] != null) {
                  hardness += this.blocks[i][j][k].getBlock().blockHardness;
                  ++count;
               }
            }
         }
      }

      return hardness / (float)count;
   }

   public void handleIItemHandlerBreak(IItemHandler var7) {
      if (var7 != null) {
         EntityItem var14;
         for(int var8 = 0; var8 < var7.getSlots(); ++var8) {
            for(int tries = 0; tries < 200; this.world.spawnEntity(var14)) {
               ++tries;
               ItemStack var9 = var7.extractItem(var8, 32767, false);
               if (var9.isEmpty() || var9.getCount() <= 0) {
                  break;
               }

               float var10 = this.rand.nextFloat() * 0.8F + 0.1F;
               float var11 = this.rand.nextFloat() * 0.8F + 0.1F;
               float var12 = this.rand.nextFloat() * 0.8F + 0.1F;
               var14 = new EntityItem(this.world, (double)((float)this.posX + var10), (double)((float)this.posY + var11), (double)((float)this.posZ + var12), var9);
               float var15 = 0.05F;
               var14.motionX = (double)((float)this.rand.nextGaussian() * var15);
               var14.motionY = (double)((float)this.rand.nextGaussian() * var15 + 0.2F);
               var14.motionZ = (double)((float)this.rand.nextGaussian() * var15);
               if (var9.hasTagCompound()) {
                  var14.getItem().setTagCompound(var9.getTagCompound().copy());
               }
            }
         }
      }

   }

   public void handleIInventoryBreak(IInventory var7) {
      if (var7 != null) {
         for(int var8 = 0; var8 < var7.getSizeInventory(); ++var8) {
            ItemStack var9 = var7.getStackInSlot(var8);
            if (!var9.isEmpty()) {
               float var10 = this.rand.nextFloat() * 0.8F + 0.1F;
               float var11 = this.rand.nextFloat() * 0.8F + 0.1F;

               EntityItem var14;
               for(float var12 = this.rand.nextFloat() * 0.8F + 0.1F; var9.getCount() > 0; this.world.spawnEntity(var14)) {
                  int var13 = this.rand.nextInt(21) + 10;
                  if (var13 > var9.getCount()) {
                     var13 = var9.getCount();
                  }

                  var9.shrink(var13);
                  var14 = new EntityItem(this.world, (double)((float)this.posX + var10), (double)((float)this.posY + var11), (double)((float)this.posZ + var12), new ItemStack(var9.getItem(), var13, var9.getItemDamage()));
                  float var15 = 0.05F;
                  var14.motionX = (double)((float)this.rand.nextGaussian() * var15);
                  var14.motionY = (double)((float)this.rand.nextGaussian() * var15 + 0.2F);
                  var14.motionZ = (double)((float)this.rand.nextGaussian() * var15);
                  if (var9.hasTagCompound()) {
                     var14.getItem().setTagCompound(var9.getTagCompound().copy());
                  }
               }
            }
         }
      }

   }

   public void shatter() {
      if ((this.blocks.length != 1 || this.blocks[0].length != 1 || this.blocks[0][0].length != 1) && !this.isDead) {
         this.setDead();

         for(int i = 0; i < this.blocks.length; ++i) {
            for(int j = 0; j < this.blocks[i].length; ++j) {
               for(int k = 0; k < this.blocks[i][j].length; ++k) {
                  if (this.blocks[i][j][k] != null) {
                     EntityBlock block = new EntityBlock(this.world);
                     block.creator = this.creator;
                     block.blocks = new IBlockState[1][1][1];
                     block.tileEntityNBTs = new NBTTagCompound[1][1][1];
                     block.blocks[0][0][0] = this.blocks[i][j][k];
                     block.tileEntityNBTs[0][0][0] = this.tileEntityNBTs[i][j][k];
                     block.setSize();
                     block.setLocationAndAngles(this.posX - (double)(this.width / 2.0F) + (double)i, this.posY + (double)j, this.posZ - (double)(this.width / 2.0F) + (double)k, 0.0F, 0.0F);
                     block.setCanRotate(block.getCanRotate());
                     block.setBehaviour(block.getBehaviour());
                     float randMag = 0.1F;
                     block.motionX = this.motionX + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * randMag);
                     block.motionY = this.motionY + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * randMag);
                     block.motionZ = this.motionZ + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * randMag);
                     this.world.spawnEntity(block);
                  }
               }
            }
         }
      }

   }

   public boolean canBeCollidedWith() {
      return this.timeExisting > this.timeout && !this.isDead;
   }

   public boolean canBePushed() {
      return this.timeExisting > this.timeout && !this.isDead;
   }

   protected void dealFireDamage(int par1) {
   }

   public AxisAlignedBB getCollisionBox(Entity entity) {
      return entity.getEntityBoundingBox();
   }

   public AxisAlignedBB getCollisionBoundingBox() {
      return this.getEntityBoundingBox();
   }

   public void readEntityFromNBT(NBTTagCompound tag) {
      this.timeExisting = tag.getInteger("timeExisting");
      this.setCanRotate(tag.getBoolean("canRotate"));
      this.setBehaviour(tag.getInteger("behaviour"));
      if (tag.getInteger("lengthX") > 0 && tag.getInteger("lengthY") > 0 && tag.getInteger("lengthZ") > 0) {
         this.blocks = new IBlockState[tag.getInteger("lengthX")][tag.getInteger("lengthY")][tag.getInteger("lengthZ")];
         this.tileEntityNBTs = new NBTTagCompound[tag.getInteger("lengthX")][tag.getInteger("lengthY")][tag.getInteger("lengthZ")];
      } else {
         this.blocks = new IBlockState[][][]{{{Blocks.AIR.getDefaultState()}}};
         this.tileEntityNBTs = new NBTTagCompound[1][1][1];
      }

      for(int i = 0; i < this.blocks.length; ++i) {
         for(int j = 0; j < this.blocks[i].length; ++j) {
            for(int k = 0; k < this.blocks[i][j].length; ++k) {
               if (tag.hasKey("state_" + Integer.toString(i) + "_" + Integer.toString(j) + "_" + Integer.toString(k))) {
                  this.blocks[i][j][k] = Block.getBlockFromName(tag.getString("state_" + Integer.toString(i) + "_" + Integer.toString(j) + "_" + Integer.toString(k))).getStateFromMeta(tag.getByte("meta_" + Integer.toString(i) + "_" + Integer.toString(j) + "_" + Integer.toString(k)) & 255);
                  if (this.blocks[i][j][k] != null && tag.hasKey("tileEnt_" + Integer.toString(i) + "_" + Integer.toString(j) + "_" + Integer.toString(k))) {
                     this.tileEntityNBTs[i][j][k] = tag.getCompoundTag("tileEnt_" + Integer.toString(i) + "_" + Integer.toString(j) + "_" + Integer.toString(k));
                  }
               }
            }
         }
      }

      this.setSize();
   }

   public void writeEntityToNBT(NBTTagCompound tag) {
      tag.setInteger("timeExisting", this.timeExisting);
      tag.setBoolean("canRotate", this.getCanRotate());
      tag.setInteger("behaviour", this.getBehaviour());
      tag.setInteger("lengthX", this.blocks.length);
      tag.setInteger("lengthY", this.blocks[0].length);
      tag.setInteger("lengthZ", this.blocks[0][0].length);

      for(int i = 0; i < this.blocks.length; ++i) {
         for(int j = 0; j < this.blocks[i].length; ++j) {
            for(int k = 0; k < this.blocks[i][j].length; ++k) {
               if (this.blocks[i][j][k] != null) {
                  Block block = this.blocks[i][j][k].getBlock();
                  ResourceLocation resourcelocation = (ResourceLocation)Block.REGISTRY.getNameForObject(block);
                  tag.setString("state_" + Integer.toString(i) + "_" + Integer.toString(j) + "_" + Integer.toString(k), resourcelocation.toString());
                  tag.setByte("meta_" + Integer.toString(i) + "_" + Integer.toString(j) + "_" + Integer.toString(k), (byte)block.getMetaFromState(this.blocks[i][j][k]));
                  if (this.tileEntityNBTs[i][j][k] != null) {
                     tag.setTag("tileEnt_" + Integer.toString(i) + "_" + Integer.toString(j) + "_" + Integer.toString(k), this.tileEntityNBTs[i][j][k]);
                  }
               }
            }
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public boolean canRenderOnFire() {
      return false;
   }
}
