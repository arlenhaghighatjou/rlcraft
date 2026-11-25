package svenhjol.meson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootContext.Builder;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public abstract class MesonTileInventory extends MesonTile {
   protected String lootTable;
   protected int lootSize;

   public MesonTileInventory() {
   }

   public abstract ItemStackHandler getInventory();

   public abstract int getInventorySize();

   public void generateLoot(@Nullable EntityPlayer player) {
      if (!this.world.isRemote && this.hasLootTable()) {
         ResourceLocation res = new ResourceLocation(this.lootTable);
         LootTable table = this.world.getLootTableManager().getLootTableFromLocation(res);
         Builder builder = new Builder((WorldServer)this.world);
         if (player != null) {
            builder.withLuck(player.getLuck());
         }

         LootContext context = builder.build();
         List<ItemStack> list = this.generateLootForPools(table, this.world.rand, context);
         List<Integer> slots = new ArrayList();

         for(int i = 0; i < this.getInventorySize(); ++i) {
            slots.add(Integer.valueOf(i));
         }

         Collections.shuffle(slots);
         int i = 0;

         for(ItemStack item : list) {
            if (i < this.getInventorySize()) {
               this.getInventory().setStackInSlot(((Integer)slots.get(i++)).intValue(), item);
            }
         }

         this.lootTable = null;
         this.lootSize = 0;
      }

   }

   protected List<ItemStack> generateLootForPools(LootTable table, EaglercraftRandom rand, LootContext context) {
      List<ItemStack> loot = table.generateLootForPools(rand, context);
      if (this.lootSize > 0) {
         int i = 0;

         while(loot.size() < this.lootSize && i++ < 10) {
            loot.addAll(table.generateLootForPools(rand, context));
         }

         if (loot.size() > this.lootSize) {
            loot = loot.subList(0, this.lootSize);
         }
      }

      return loot;
   }

   public int getComparatorOutput() {
      float f = 0.0F;
      int i = 0;
      ItemStackHandler inventory = this.getInventory();

      for(int slot = 0; slot < this.getInventorySize(); ++slot) {
         ItemStack stack = inventory.getStackInSlot(slot);
         if (!stack.isEmpty()) {
            f += (float)stack.getCount() / (float)Math.min(inventory.getSlotLimit(slot), stack.getMaxStackSize());
            ++i;
         }
      }

      f = f / (float)this.getInventorySize();
      return MathHelper.floor((double)f * 14.0D) + (i > 0 ? 1 : 0);
   }

   public void readTag(NBTTagCompound tag) {
      super.readTag(tag);
      this.getInventory().deserializeNBT(tag.getCompoundTag("inventory"));
      this.setLootTable(tag.getString("lootTable"));
      this.setLootSize(tag.getInteger("lootSize"));
   }

   public void writeTag(NBTTagCompound tag) {
      super.writeTag(tag);
      tag.setTag("inventory", this.getInventory().serializeNBT());
      tag.setInteger("lootSize", this.getLootSize());
      tag.setString("lootTable", this.getLootTable());
   }

   public boolean hasLootTable() {
      return this.lootTable != null && !this.lootTable.isEmpty();
   }

   public String getLootTable() {
      return this.hasLootTable() ? this.lootTable : "";
   }

   public void setLootTable(ResourceLocation lootTable) {
      this.lootTable = lootTable.toString();
   }

   public void setLootTable(ResourceLocation lootTable, int lootSize) {
      this.setLootTable(lootTable.toString(), lootSize);
   }

   public void setLootTable(ResourceLocation lootTable, long seed) {
      this.setLootTable(lootTable.toString());
   }

   public void setLootTable(String lootTable) {
      this.lootTable = lootTable;
   }

   public void setLootTable(String lootTable, int lootSize) {
      this.setLootTable(lootTable);
      this.setLootSize(lootSize);
   }

   public int getLootSize() {
      return this.lootSize;
   }

   public void setLootSize(int size) {
      this.lootSize = size;
   }

   public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
   }

   public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? this.getInventory() : super.getCapability(capability, facing));
   }
}
