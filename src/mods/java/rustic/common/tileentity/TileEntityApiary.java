package rustic.common.tileentity;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import rustic.common.Config;
import rustic.common.items.ModItems;

public class TileEntityApiary extends TileEntity implements ITickable {
   private ItemStackHandler beeItemStackHandler = new ItemStackHandler(1) {
      protected void onContentsChanged(int slot) {
         TileEntityApiary.this.markDirty();
      }

      @Nonnull
      public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
         return stack.getItem().equals(ModItems.BEE) ? super.insertItem(slot, stack, simulate) : stack;
      }
   };
   private ItemStackHandler honeyCombItemStackHandler = new ItemStackHandler(1) {
      protected void onContentsChanged(int slot) {
         TileEntityApiary.this.markDirty();
      }

      @Nonnull
      public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
         return stack.getItem().equals(ModItems.HONEYCOMB) ? super.insertItem(slot, stack, simulate) : stack;
      }
   };
   private int reproductionTime = 1200;
   private int reproductionTimer = 0;
   private int productionTime = 600;
   private int productionTimer = 0;
   private EaglercraftRandom random = new EaglercraftRandom();

   public TileEntityApiary() {
   }

   public ITextComponent getDisplayName() {
      return new TextComponentTranslation(this.getName(), new Object[0]);
   }

   public String getName() {
      return "container.rustic.apiary";
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      if (compound.hasKey("items")) {
         ItemStackHandler tempHandler = new ItemStackHandler(2);
         tempHandler.deserializeNBT((NBTTagCompound)compound.getTag("items"));
         this.beeItemStackHandler.setStackInSlot(0, tempHandler.getStackInSlot(0));
         this.honeyCombItemStackHandler.setStackInSlot(0, tempHandler.getStackInSlot(1));
      }

      if (compound.hasKey("bees")) {
         this.beeItemStackHandler.deserializeNBT((NBTTagCompound)compound.getTag("bees"));
      }

      if (compound.hasKey("honeyComb")) {
         this.honeyCombItemStackHandler.deserializeNBT((NBTTagCompound)compound.getTag("honeyComb"));
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      compound.setTag("bees", this.beeItemStackHandler.serializeNBT());
      compound.setTag("honeyComb", this.honeyCombItemStackHandler.serializeNBT());
      return compound;
   }

   public boolean canInteractWith(EntityPlayer playerIn) {
      return !this.isInvalid() && playerIn.getDistanceSq(this.pos.add(0.5D, 0.5D, 0.5D)) <= 64.0D;
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? true : super.hasCapability(capability, facing);
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
         return (T)(facing == EnumFacing.DOWN ? this.honeyCombItemStackHandler : this.beeItemStackHandler);
      } else {
         return (T)super.getCapability(capability, facing);
      }
   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      this.invalidate();
      if (this.beeItemStackHandler != null && !world.isRemote) {
         for(int i = 0; i < this.beeItemStackHandler.getSlots(); ++i) {
            if (this.beeItemStackHandler.getStackInSlot(i) != null) {
               Block.spawnAsEntity(world, pos, this.beeItemStackHandler.getStackInSlot(i));
            }
         }
      }

      if (this.honeyCombItemStackHandler != null && !world.isRemote) {
         for(int i = 0; i < this.honeyCombItemStackHandler.getSlots(); ++i) {
            if (this.honeyCombItemStackHandler.getStackInSlot(i) != null) {
               Block.spawnAsEntity(world, pos, this.honeyCombItemStackHandler.getStackInSlot(i));
            }
         }
      }

      world.setTileEntity(pos, (TileEntity)null);
   }

   public void update() {
      int numBees = 0;
      if (this.beeItemStackHandler.getStackInSlot(0).getItem().equals(ModItems.BEE)) {
         numBees = this.beeItemStackHandler.getStackInSlot(0).getCount();
      }

      int numHoneycomb = 0;
      if (this.honeyCombItemStackHandler.getStackInSlot(0).getItem().equals(ModItems.HONEYCOMB)) {
         numHoneycomb = this.honeyCombItemStackHandler.getStackInSlot(0).getCount();
      }

      if (numBees > 0 && !this.getWorld().isRemote) {
         this.reproductionTime = (int)(Config.BEE_REPRODUCTION_MULTIPLIER * (1600.0F / ((float)numBees / 20.0F + 1.0F)));
         this.productionTime = (int)(Config.BEE_HONEYCOMB_MULTIPLIER * (800.0F / ((float)numBees / 20.0F + 1.0F)));
         ++this.reproductionTimer;
         ++this.productionTimer;
         if (this.reproductionTimer >= this.reproductionTime) {
            this.reproductionTimer = 0;
            if (numBees < 64) {
               this.beeItemStackHandler.getStackInSlot(0).grow(1);
            }
         }

         if (this.productionTimer >= this.productionTime) {
            this.productionTimer = 0;
            if (numHoneycomb == 0) {
               this.honeyCombItemStackHandler.setStackInSlot(0, new ItemStack(ModItems.HONEYCOMB, 1));
            } else if (numHoneycomb < 64) {
               this.honeyCombItemStackHandler.getStackInSlot(0).grow(1);
            }
         }

         if (Config.BEE_GROWTH_MULTIPLIER != 0.0F && this.random.nextInt((int)Math.ceil((double)(2048.0F / ((float)numBees * Config.BEE_GROWTH_MULTIPLIER)))) == 0) {
            int randX = this.random.nextInt(9) - 4;
            int randZ = this.random.nextInt(9) - 4;
            int x = this.getPos().getX();
            int y = this.getPos().getY();
            int z = this.getPos().getZ();

            for(int i = 0; i < 3; ++i) {
               BlockPos pos = new BlockPos(x + randX, y + 1 - i, z + randZ);
               Block block = this.getWorld().getBlockState(pos).getBlock();
               if (block instanceof IGrowable || block instanceof IPlantable) {
                  block.updateTick(this.world, pos, this.world.getBlockState(pos), this.world.rand);
                  this.world.updateBlockTick(pos, block, 0, 1);
                  break;
               }
            }
         }

         this.markDirty();
      }

   }
}
