package svenhjol.charm.crafting.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.items.ItemStackHandler;
import svenhjol.charm.crafting.feature.Crate;
import svenhjol.meson.MesonTileInventory;
import vazkii.quark.api.IDropoffManager;

@Interface(
   iface = "vazkii.quark.api.IDropoffManager",
   modid = "quark"
)
public class TileCrate extends MesonTileInventory implements IDropoffManager {
   public static final int SIZE = 9;
   protected boolean showName;
   public ItemStackHandler inventory = new ItemStackHandler(this.getInventorySize()) {
      protected int getStackLimit(int slot, ItemStack stack) {
         return Crate.canInsertItem(stack) ? 64 : 0;
      }

      protected void onContentsChanged(int slot) {
         TileCrate.this.markDirty();
      }
   };

   public TileCrate() {
   }

   public boolean acceptsDropoff(EntityPlayer player) {
      return true;
   }

   public ItemStackHandler getInventory() {
      return this.inventory;
   }

   public String getModId() {
      return "charm";
   }

   public void readTag(NBTTagCompound tag) {
      this.showName = tag.getBoolean("showname");
      super.readTag(tag);
   }

   public void writeTag(NBTTagCompound tag) {
      tag.setBoolean("showname", this.showName);
      super.writeTag(tag);
   }

   public void setShowName(boolean show) {
      this.showName = show;
   }

   public ITextComponent getDisplayName() {
      return this.showName ? new TextComponentString(this.getName()) : null;
   }

   public int getInventorySize() {
      return 9;
   }
}
