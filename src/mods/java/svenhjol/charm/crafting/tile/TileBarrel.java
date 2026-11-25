package svenhjol.charm.crafting.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.items.ItemStackHandler;
import svenhjol.meson.MesonTileInventory;
import vazkii.quark.api.IDropoffManager;

@Interface(
   iface = "vazkii.quark.api.IDropoffManager",
   modid = "quark"
)
public class TileBarrel extends MesonTileInventory implements IDropoffManager {
   public static final int SIZE = 27;
   public int facing;
   public ItemStackHandler inventory = new ItemStackHandler(this.getInventorySize()) {
      protected void onContentsChanged(int slot) {
         TileBarrel.this.markDirty();
      }
   };

   public TileBarrel() {
   }

   public void readTag(NBTTagCompound tag) {
      this.facing = tag.getInteger("facing");
      super.readTag(tag);
   }

   public void writeTag(NBTTagCompound tag) {
      tag.setInteger("facing", this.facing);
      super.writeTag(tag);
   }

   public String getModId() {
      return "charm";
   }

   public ItemStackHandler getInventory() {
      return this.inventory;
   }

   public int getInventorySize() {
      return 27;
   }

   public boolean acceptsDropoff(EntityPlayer player) {
      return true;
   }

   public String getDefaultName() {
      return I18n.translateToLocal("tile.charm:barrel.name");
   }

   public void setFacing(EnumFacing facing) {
      this.facing = facing.getIndex();
      this.markDirty();
   }

   public EnumFacing getFacing() {
      return EnumFacing.getFront(this.facing);
   }
}
