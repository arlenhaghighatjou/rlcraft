package de.impelon.disenchanter.tileentity;

import de.impelon.disenchanter.inventory.ContainerDisenchantmentBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityEnchantmentTable;

public class TileEntityDisenchantmentTable extends TileEntityEnchantmentTable {
   public TileEntityDisenchantmentTable() {
   }

   public String getName() {
      return this.hasCustomName() ? super.getName() : "container.disenchant";
   }

   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return ContainerDisenchantmentBase.create(playerInventory, this.world, this.pos);
   }

   public String getGuiID() {
      return null;
   }

   public NBTTagCompound getUpdateTag() {
      NBTTagCompound updateTag = super.getUpdateTag();
      return this.writeToNBT(updateTag);
   }

   public void handleUpdateTag(NBTTagCompound tag) {
      super.handleUpdateTag(tag);
   }
}
