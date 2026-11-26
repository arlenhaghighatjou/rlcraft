package de.Whitedraco.switchbow.tileentity;

import de.Whitedraco.switchbow.Initial;
import de.Whitedraco.switchbow.Config.SwitchBowConfig;
import de.Whitedraco.switchbow.proxy.packets.ParticleFreezePacket;
import de.Whitedraco.switchbow.proxy.packets.TEPodestParticlePacket;
import de.Whitedraco.switchbow.proxy.packets.TEPodestUpdatePacket;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import com.arlenh7.eaglercraft.network.INetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class TileEntityPodest extends TileEntityLockableLoot implements IInventory, ITickable {
   private static final EaglercraftRandom RNG = new EaglercraftRandom();
   private static final double particleShowWight = 40.0D;
   private NonNullList<ItemStack> stacks = NonNullList.withSize(1, ItemStack.EMPTY);
   protected String customName;
   private static int maxTicksTransformationproItem = SwitchBowConfig.maxTicksTransformationproItem.getInteger();
   private int ticksTransformation = 0;
   private boolean transforming = false;
   private boolean isAltar = false;
   private boolean startTransformation = false;

   public TileEntityPodest() {
   }

   public int getSizeInventory() {
      return 1;
   }

   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return this.getItemTransformingOutput(stack.getItem()) == null ? false : super.isItemValidForSlot(index, stack);
   }

   public int addItemStack(ItemStack stack) {
      for(int i = 0; i < this.stacks.size(); ++i) {
         if (((ItemStack)this.stacks.get(i)).isEmpty()) {
            this.setInventorySlotContents(i, stack);
            return i;
         }
      }

      return -1;
   }

   public String getName() {
      return this.hasCustomName() ? this.customName : "tile.name.Podest";
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.stacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      if (!this.checkLootAndRead(compound)) {
         ItemStackHelper.loadAllItems(compound, this.stacks);
      }

      if (compound.hasKey("CustomName", 8)) {
         this.customName = compound.getString("CustomName");
      }

      this.isAltar = compound.getBoolean("isStructur");
      this.startTransformation = compound.getBoolean("startTransformation");
      this.ticksTransformation = compound.getInteger("ticksTransformation");
      this.transforming = compound.getBoolean("transforming");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      if (!this.checkLootAndWrite(compound)) {
         ItemStackHelper.saveAllItems(compound, this.stacks);
      }

      if (this.hasCustomName()) {
         compound.setString("CustomName", this.customName);
      }

      compound.setBoolean("isStructur", this.isAltar);
      compound.setBoolean("startTransformation", this.startTransformation);
      compound.setInteger("ticksTransformation", this.ticksTransformation);
      compound.setBoolean("transforming", this.transforming);
      return compound;
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public String getGuiID() {
      return "minecraft:dispenser";
   }

   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return null;
   }

   public void onDataPacket(INetworkManager net, SPacketUpdateTileEntity pkt) {
      NBTTagCompound tag = pkt.getNbtCompound();
      this.readUpdateTag(tag);
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound tag = new NBTTagCompound();
      this.writeUpdateTag(tag);
      return new SPacketUpdateTileEntity(this.pos, this.getBlockMetadata(), tag);
   }

   public NBTTagCompound getUpdateTag() {
      NBTTagCompound tag = super.getUpdateTag();
      this.writeUpdateTag(tag);
      return tag;
   }

   public void writeUpdateTag(NBTTagCompound tag) {
      tag.setBoolean("isStructur", this.isAltar);
      tag.setBoolean("startTransformation", this.startTransformation);
      tag.setInteger("ticksTransformation", this.ticksTransformation);
      tag.setBoolean("transforming", this.transforming);
   }

   public void readUpdateTag(NBTTagCompound tag) {
      this.isAltar = tag.getBoolean("isStructur");
      this.startTransformation = tag.getBoolean("startTransformation");
      this.ticksTransformation = tag.getInteger("ticksTransformation");
      this.transforming = tag.getBoolean("transforming");
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.stacks) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public void setStartTransformation(boolean startTransformation) {
      this.startTransformation = startTransformation;
      this.markDirty();
   }

   protected NonNullList<ItemStack> getItems() {
      return this.stacks;
   }

   public void update() {
      if (!this.world.isRemote) {
         this.isAltar = this.isStructur();
         Initial.network.sendToAll(new TEPodestUpdatePacket(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.getStackInSlot(0)));
         if (this.isAltar) {
            if (this.startTransformation) {
               ItemStack stack = this.getStackInSlot(0);
               this.ticksTransformation = stack.getCount() * maxTicksTransformationproItem;
               this.startTransformation = false;
               this.transforming = true;
            }

            if (this.getStackInSlot(0) == ItemStack.EMPTY) {
               this.ticksTransformation = 0;
               this.transforming = false;
            }

            if (this.ticksTransformation > 0) {
               --this.ticksTransformation;
               Initial.network.sendToAllAround(new TEPodestParticlePacket((double)this.pos.getX() + 1.5D, (double)(this.pos.getY() + 1), (double)this.pos.getZ() + 1.5D, 0.5D, 0.3D, 0.5D), new TargetPoint(this.getWorld().provider.getDimension(), (double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), 40.0D));
               Initial.network.sendToAllAround(new TEPodestParticlePacket((double)this.pos.getX() - 0.5D, (double)(this.pos.getY() + 1), (double)this.pos.getZ() - 0.5D, -0.5D, 0.3D, -0.5D), new TargetPoint(this.getWorld().provider.getDimension(), (double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), 40.0D));
               Initial.network.sendToAllAround(new TEPodestParticlePacket((double)this.pos.getX() - 0.5D, (double)(this.pos.getY() + 1), (double)this.pos.getZ() + 1.5D, -0.5D, 0.3D, 0.5D), new TargetPoint(this.getWorld().provider.getDimension(), (double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), 40.0D));
               Initial.network.sendToAllAround(new TEPodestParticlePacket((double)this.pos.getX() + 1.5D, (double)(this.pos.getY() + 1), (double)this.pos.getZ() - 0.5D, 0.5D, 0.3D, -0.5D), new TargetPoint(this.getWorld().provider.getDimension(), (double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), 40.0D));
            } else if (this.ticksTransformation <= 0 && this.transforming && this.getStackInSlot(0) != ItemStack.EMPTY) {
               if (this.getItemTransformingOutput(this.getStackInSlot(0).getItem()) != null) {
                  this.setInventorySlotContents(0, new ItemStack(this.getItemTransformingOutput(this.getStackInSlot(0).getItem()), this.getStackInSlot(0).getCount()));
               }

               double d0 = 3.0D;
               double d1 = 10.0D;
               double d2 = 50.0D;
               Initial.network.sendToAllAround(new ParticleFreezePacket((double)this.getPos().getX(), (double)this.getPos().getY(), (double)this.getPos().getZ(), d0 + 10.0D, d1, d2), new TargetPoint(this.getWorld().provider.getDimension(), (double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), 40.0D));
               Initial.network.sendToAllAround(new ParticleFreezePacket((double)(this.getPos().getX() + 1), (double)this.getPos().getY(), (double)this.getPos().getZ(), d0 + 10.0D, d1, d2), new TargetPoint(this.getWorld().provider.getDimension(), (double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), 40.0D));
               Initial.network.sendToAllAround(new ParticleFreezePacket((double)this.getPos().getX(), (double)this.getPos().getY(), (double)(this.getPos().getZ() + 1), d0 + 10.0D, d1, d2), new TargetPoint(this.getWorld().provider.getDimension(), (double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), 40.0D));
               Initial.network.sendToAllAround(new ParticleFreezePacket((double)(this.getPos().getX() + 1), (double)this.getPos().getY(), (double)(this.getPos().getZ() + 1), d0 + 10.0D, d1, d2), new TargetPoint(this.getWorld().provider.getDimension(), (double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), 40.0D));
            }
         }
      }

   }

   public Item getItemTransformingOutput(Item input) {
      return Initial.hashItemTransforming.get(input) != null ? (Item)Initial.hashItemTransforming.get(input) : null;
   }

   public boolean isStructur() {
      int posX = this.getPos().getX();
      int posY = this.getPos().getY();
      int posZ = this.getPos().getZ();
      boolean next = false;
      boolean next2 = false;
      if (this.world.getBlockState(new BlockPos(posX, posY - 1, posZ)).getBlock() == Blocks.OBSIDIAN) {
         if (this.world.getBlockState(new BlockPos(posX, posY - 2, posZ)).getBlock() == Blocks.QUARTZ_BLOCK && Blocks.QUARTZ_BLOCK.getMetaFromState(this.world.getBlockState(new BlockPos(posX, posY - 2, posZ))) == 0) {
            BlockPos pos = new BlockPos(posX - 1, posY - 2, posZ + 1);
            if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_BLOCK && Blocks.QUARTZ_BLOCK.getMetaFromState(this.world.getBlockState(pos)) == 0) {
               pos = new BlockPos(posX, posY - 2, posZ);
               if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_BLOCK && Blocks.QUARTZ_BLOCK.getMetaFromState(this.world.getBlockState(pos)) == 0) {
                  pos = new BlockPos(posX - 1, posY - 2, posZ);
                  if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_BLOCK && Blocks.QUARTZ_BLOCK.getMetaFromState(this.world.getBlockState(pos)) == 0) {
                     pos = new BlockPos(posX - 1, posY - 2, posZ - 1);
                     if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_BLOCK && Blocks.QUARTZ_BLOCK.getMetaFromState(this.world.getBlockState(pos)) == 0) {
                        pos = new BlockPos(posX, posY - 2, posZ + 1);
                        if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_BLOCK && Blocks.QUARTZ_BLOCK.getMetaFromState(this.world.getBlockState(pos)) == 0) {
                           pos = new BlockPos(posX, posY - 2, posZ - 1);
                           if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_BLOCK && Blocks.QUARTZ_BLOCK.getMetaFromState(this.world.getBlockState(pos)) == 0) {
                              pos = new BlockPos(posX + 1, posY - 2, posZ + 1);
                              if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_BLOCK && Blocks.QUARTZ_BLOCK.getMetaFromState(this.world.getBlockState(pos)) == 0) {
                                 pos = new BlockPos(posX + 1, posY - 2, posZ);
                                 if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_BLOCK && Blocks.QUARTZ_BLOCK.getMetaFromState(this.world.getBlockState(pos)) == 0) {
                                    pos = new BlockPos(posX + 1, posY - 2, posZ - 1);
                                    if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_BLOCK && Blocks.QUARTZ_BLOCK.getMetaFromState(this.world.getBlockState(pos)) == 0) {
                                       next = true;
                                       if (next) {
                                          pos = new BlockPos(posX - 2, posY - 2, posZ + 1);
                                          if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_STAIRS) {
                                             pos = new BlockPos(posX - 2, posY - 2, posZ);
                                             if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_STAIRS) {
                                                pos = new BlockPos(posX - 2, posY - 2, posZ - 1);
                                                if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_STAIRS) {
                                                   pos = new BlockPos(posX + 1, posY - 2, posZ + 2);
                                                   if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_STAIRS) {
                                                      pos = new BlockPos(posX, posY - 2, posZ + 2);
                                                      if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_STAIRS) {
                                                         pos = new BlockPos(posX - 1, posY - 2, posZ + 2);
                                                         if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_STAIRS) {
                                                            pos = new BlockPos(posX + 2, posY - 2, posZ + 1);
                                                            if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_STAIRS) {
                                                               pos = new BlockPos(posX + 2, posY - 2, posZ);
                                                               if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_STAIRS) {
                                                                  pos = new BlockPos(posX + 2, posY - 2, posZ - 1);
                                                                  if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_STAIRS) {
                                                                     pos = new BlockPos(posX + 1, posY - 2, posZ - 2);
                                                                     if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_STAIRS) {
                                                                        pos = new BlockPos(posX, posY - 2, posZ - 2);
                                                                        if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_STAIRS) {
                                                                           pos = new BlockPos(posX - 1, posY - 2, posZ - 2);
                                                                           if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_STAIRS) {
                                                                              next2 = true;
                                                                              if (next2) {
                                                                                 pos = new BlockPos(posX + 2, posY - 2, posZ + 2);
                                                                                 if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_BLOCK && Blocks.QUARTZ_BLOCK.getMetaFromState(this.world.getBlockState(pos)) == 2) {
                                                                                    pos = new BlockPos(posX + 2, posY - 2, posZ - 2);
                                                                                    if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_BLOCK && Blocks.QUARTZ_BLOCK.getMetaFromState(this.world.getBlockState(pos)) == 2) {
                                                                                       pos = new BlockPos(posX - 2, posY - 2, posZ + 2);
                                                                                       if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_BLOCK && Blocks.QUARTZ_BLOCK.getMetaFromState(this.world.getBlockState(pos)) == 2) {
                                                                                          pos = new BlockPos(posX - 2, posY - 2, posZ - 2);
                                                                                          if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_BLOCK && Blocks.QUARTZ_BLOCK.getMetaFromState(this.world.getBlockState(pos)) == 2) {
                                                                                             pos = new BlockPos(posX + 2, posY - 1, posZ + 2);
                                                                                             if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_BLOCK && Blocks.QUARTZ_BLOCK.getMetaFromState(this.world.getBlockState(pos)) == 2) {
                                                                                                pos = new BlockPos(posX + 2, posY - 1, posZ - 2);
                                                                                                if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_BLOCK && Blocks.QUARTZ_BLOCK.getMetaFromState(this.world.getBlockState(pos)) == 2) {
                                                                                                   pos = new BlockPos(posX - 2, posY - 1, posZ + 2);
                                                                                                   if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_BLOCK && Blocks.QUARTZ_BLOCK.getMetaFromState(this.world.getBlockState(pos)) == 2) {
                                                                                                      pos = new BlockPos(posX - 2, posY - 1, posZ - 2);
                                                                                                      if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_BLOCK && Blocks.QUARTZ_BLOCK.getMetaFromState(this.world.getBlockState(pos)) == 2) {
                                                                                                         pos = new BlockPos(posX + 2, posY, posZ + 2);
                                                                                                         if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_BLOCK && Blocks.QUARTZ_BLOCK.getMetaFromState(this.world.getBlockState(pos)) == 2) {
                                                                                                            pos = new BlockPos(posX + 2, posY, posZ - 2);
                                                                                                            if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_BLOCK && Blocks.QUARTZ_BLOCK.getMetaFromState(this.world.getBlockState(pos)) == 2) {
                                                                                                               pos = new BlockPos(posX - 2, posY, posZ + 2);
                                                                                                               if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_BLOCK && Blocks.QUARTZ_BLOCK.getMetaFromState(this.world.getBlockState(pos)) == 2) {
                                                                                                                  pos = new BlockPos(posX - 2, posY, posZ - 2);
                                                                                                                  if (this.world.getBlockState(pos).getBlock() == Blocks.QUARTZ_BLOCK && Blocks.QUARTZ_BLOCK.getMetaFromState(this.world.getBlockState(pos)) == 2) {
                                                                                                                     pos = new BlockPos(posX + 2, posY + 1, posZ + 2);
                                                                                                                     if (this.world.getBlockState(pos).getBlock() == Blocks.MAGMA) {
                                                                                                                        pos = new BlockPos(posX + 2, posY + 1, posZ - 2);
                                                                                                                        if (this.world.getBlockState(pos).getBlock() == Blocks.MAGMA) {
                                                                                                                           pos = new BlockPos(posX - 2, posY + 1, posZ + 2);
                                                                                                                           if (this.world.getBlockState(pos).getBlock() == Blocks.MAGMA) {
                                                                                                                              pos = new BlockPos(posX - 2, posY + 1, posZ - 2);
                                                                                                                              return this.world.getBlockState(pos).getBlock() == Blocks.MAGMA;
                                                                                                                           } else {
                                                                                                                              return false;
                                                                                                                           }
                                                                                                                        } else {
                                                                                                                           return false;
                                                                                                                        }
                                                                                                                     } else {
                                                                                                                        return false;
                                                                                                                     }
                                                                                                                  } else {
                                                                                                                     return false;
                                                                                                                  }
                                                                                                               } else {
                                                                                                                  return false;
                                                                                                               }
                                                                                                            } else {
                                                                                                               return false;
                                                                                                            }
                                                                                                         } else {
                                                                                                            return false;
                                                                                                         }
                                                                                                      } else {
                                                                                                         return false;
                                                                                                      }
                                                                                                   } else {
                                                                                                      return false;
                                                                                                   }
                                                                                                } else {
                                                                                                   return false;
                                                                                                }
                                                                                             } else {
                                                                                                return false;
                                                                                             }
                                                                                          } else {
                                                                                             return false;
                                                                                          }
                                                                                       } else {
                                                                                          return false;
                                                                                       }
                                                                                    } else {
                                                                                       return false;
                                                                                    }
                                                                                 } else {
                                                                                    return false;
                                                                                 }
                                                                              } else {
                                                                                 return false;
                                                                              }
                                                                           } else {
                                                                              return false;
                                                                           }
                                                                        } else {
                                                                           return false;
                                                                        }
                                                                     } else {
                                                                        return false;
                                                                     }
                                                                  } else {
                                                                     return false;
                                                                  }
                                                               } else {
                                                                  return false;
                                                               }
                                                            } else {
                                                               return false;
                                                            }
                                                         } else {
                                                            return false;
                                                         }
                                                      } else {
                                                         return false;
                                                      }
                                                   } else {
                                                      return false;
                                                   }
                                                } else {
                                                   return false;
                                                }
                                             } else {
                                                return false;
                                             }
                                          } else {
                                             return false;
                                          }
                                       } else {
                                          return false;
                                       }
                                    } else {
                                       return false;
                                    }
                                 } else {
                                    return false;
                                 }
                              } else {
                                 return false;
                              }
                           } else {
                              return false;
                           }
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
