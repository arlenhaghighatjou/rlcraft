package com.dhanantry.scapeandrunparasites.entity.tile;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteCanister;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteCanisterC;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.SRPPacketParticle;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.ItemStackDataLists;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TileEntityCanister extends TileEntityLockableLoot implements ITickable {
   private NonNullList<ItemStack> chestContents = NonNullList.withSize(40, ItemStack.EMPTY);
   public int numPlayersUsing;
   private int ticksSinceSync;

   public TileEntityCanister() {
   }

   public int getSizeInventory() {
      return 40;
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.chestContents) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public String getName() {
      return this.hasCustomName() ? this.customName : "container.chest";
   }

   public static void registerFixesChest(DataFixer fixer) {
      fixer.registerWalker(FixTypes.BLOCK_ENTITY, new ItemStackDataLists(TileEntityCanister.class, new String[]{"Items"}));
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.chestContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      if (!this.checkLootAndRead(compound)) {
         ItemStackHelper.loadAllItems(compound, this.chestContents);
      }

      if (compound.hasKey("CustomName", 8)) {
         this.customName = compound.getString("CustomName");
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      if (!this.checkLootAndWrite(compound)) {
         ItemStackHelper.saveAllItems(compound, this.chestContents);
      }

      if (this.hasCustomName()) {
         compound.setString("CustomName", this.customName);
      }

      return compound;
   }

   public int getInventoryStackLimit() {
      return 100;
   }

   public void updateContainingBlockInfo() {
      super.updateContainingBlockInfo();
   }

   public void update() {
      int i = this.pos.getX();
      int j = this.pos.getY();
      int k = this.pos.getZ();
      ++this.ticksSinceSync;
      if (!this.world.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + i + j + k) % 200 == 0) {
         this.numPlayersUsing = 0;
         float f = 5.0F;

         for(EntityPlayer entityplayer : this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((double)((float)i - 5.0F), (double)((float)j - 5.0F), (double)((float)k - 5.0F), (double)((float)(i + 1) + 5.0F), (double)((float)(j + 1) + 5.0F), (double)((float)(k + 1) + 5.0F)))) {
            if (entityplayer.openContainer instanceof ContainerChest) {
               IInventory iinventory = ((ContainerChest)entityplayer.openContainer).getLowerChestInventory();
               if (iinventory == this || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest)iinventory).isPartOfLargeChest(this)) {
                  ++this.numPlayersUsing;
               }
            }
         }
      }

      if (!this.world.isRemote && this.ticksSinceSync % 20 == 0 && this.ticksSinceSync > 20 * SRPConfig.cystDelay) {
         int ccc = 0;

         for(int in = 0; in < this.getSizeInventory(); ++in) {
            if (((IItemHandler)this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null)).getStackInSlot(in).getItem() != Items.AIR) {
               ItemStack stack = ((IItemHandler)this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null)).getStackInSlot(in);
               if (!ParasiteEventEntity.checkName(stack.getItem().getRegistryName().toString(), SRPConfig.cystItemBlackList, false)) {
                  if (stack.isItemStackDamageable()) {
                     if (stack.attemptDamageItem(5, this.world.rand, (EntityPlayerMP)null)) {
                        stack.shrink(1);
                        if (SRPConfigSystems.useEvolution) {
                           SRPWorldData data = SRPWorldData.get(this.world);
                           data.setTotalKills(SRPConfig.cystConsumePoint, true, this.world, true);
                        }
                     }
                  } else {
                     stack.shrink(1);
                     if (SRPConfigSystems.useEvolution) {
                        SRPWorldData data = SRPWorldData.get(this.world);
                        data.setTotalKills(SRPConfig.cystConsumePoint, true, this.world, true);
                     }
                  }

                  this.world.playSound((EntityPlayer)null, (double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), SRPSounds.CYST_EATING, SoundCategory.HOSTILE, 0.25F, 1.0F);
                  SRPMain.network.sendToAll(new SRPPacketParticle((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.7D, (double)this.pos.getZ() + 0.5D, 0.5F, 0.5F, (byte)2));
                  return;
               }
            } else {
               ++ccc;
            }
         }

         if (ccc == this.getSizeInventory() && this.world.getBlockState(this.pos.down()).getBlock() != Blocks.AIR) {
            this.world.setBlockState(this.pos, SRPBlocks.ParasiteCanister.getDefaultState().withProperty(BlockParasiteCanister.VARIANT, BlockParasiteCanister.EnumType.CYST));
         }
      }

   }

   public boolean addStack(List<ItemStack> moblist2) {
      int p = 0;

      for(ItemStack mob : moblist2) {
         if (!mob.isEmpty()) {
            for(int in = 0; in <= 26; ++in) {
               ItemStack slot = ((IItemHandler)this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null)).getStackInSlot(in);
               if (ItemStack.areItemsEqual(slot, mob)) {
                  ItemStack atm = ((IItemHandler)this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null)).insertItem(in, mob, false);
                  ++p;
                  mob = atm;
               } else if (slot.getItem() == Items.AIR) {
                  ItemStack atm = ((IItemHandler)this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null)).insertItem(in, mob, false);
                  ++p;
                  mob = atm;
               }
            }
         }
      }

      return false;
   }

   public boolean receiveClientEvent(int id, int type) {
      if (id == 1) {
         this.numPlayersUsing = type;
         return true;
      } else {
         return super.receiveClientEvent(id, type);
      }
   }

   public void openInventory(EntityPlayer player) {
      if (player.isCreative()) {
         if (this.numPlayersUsing < 0) {
            this.numPlayersUsing = 0;
         }

         ++this.numPlayersUsing;
         this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
         this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
      }

   }

   public void closeInventory(EntityPlayer player) {
      if (player.isCreative() && this.getBlockType() instanceof BlockParasiteCanisterC) {
         --this.numPlayersUsing;
         this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
         this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
      }

   }

   @Nullable
   public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)super.getCapability(capability, facing);
   }

   public IItemHandler getSingleChestHandler() {
      return (IItemHandler)super.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
   }

   public void invalidate() {
      super.invalidate();
      this.updateContainingBlockInfo();
   }

   public String getGuiID() {
      return "srparasites:canister";
   }

   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      this.fillWithLoot(playerIn);
      return new ContainerChest(playerInventory, this, playerIn);
   }

   protected NonNullList<ItemStack> getItems() {
      return this.chestContents;
   }
}
