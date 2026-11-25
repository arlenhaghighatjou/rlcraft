package com.github.alexthe666.iceandfire.entity.tile;

import com.github.alexthe666.iceandfire.inventory.ContainerMyrmexCocoon;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootContext.Builder;

public class TileEntityMyrmexCocoon extends TileEntityLockableLoot {
   private NonNullList<ItemStack> chestContents = NonNullList.withSize(18, ItemStack.EMPTY);

   public TileEntityMyrmexCocoon() {
   }

   public int getSizeInventory() {
      return 18;
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
      Block block = this.world.getBlockState(this.pos).getBlock();
      return this.hasCustomName() ? this.customName : block.getUnlocalizedName() + ".name";
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
      return 64;
   }

   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      this.fillWithLoot(playerIn);
      return new ContainerMyrmexCocoon(playerInventory, this, playerIn);
   }

   public String getGuiID() {
      return "oldworldblues:storage";
   }

   protected NonNullList<ItemStack> getItems() {
      return this.chestContents;
   }

   public void openInventory(EntityPlayer player) {
      this.fillWithLoot((EntityPlayer)null);
      player.world.playSound((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
   }

   public void closeInventory(EntityPlayer player) {
      this.fillWithLoot((EntityPlayer)null);
      player.world.playSound((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound tag = new NBTTagCompound();
      this.writeToNBT(tag);
      return new SPacketUpdateTileEntity(this.pos, 0, tag);
   }

   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   public void fillWithLoot(@Nullable EntityPlayer player) {
      if (this.lootTable != null && this.world != null && this.world.getLootTableManager() != null) {
         LootTable loottable = this.world.getLootTableManager().getLootTableFromLocation(this.lootTable);
         this.lootTable = null;
         Random random;
         if (this.lootTableSeed == 0L) {
            random = new Random();
         } else {
            random = new Random(this.lootTableSeed);
         }

         Builder lootcontext$builder = new Builder((WorldServer)this.world);
         if (player != null) {
            lootcontext$builder.withLuck(player.getLuck()).withPlayer(player);
         }

         loottable.fillInventory(this, random, lootcontext$builder.build());
      }

   }

   public void onDataPacket(NetworkManager netManager, SPacketUpdateTileEntity packet) {
      this.readFromNBT(packet.getNbtCompound());
   }

   public boolean isFull(ItemStack heldStack) {
      for(ItemStack itemstack : this.chestContents) {
         if (itemstack.isEmpty() || heldStack != null && !heldStack.isEmpty() && itemstack.isItemEqual(heldStack) && itemstack.getCount() + heldStack.getCount() < itemstack.getMaxStackSize()) {
            return false;
         }
      }

      return true;
   }
}
