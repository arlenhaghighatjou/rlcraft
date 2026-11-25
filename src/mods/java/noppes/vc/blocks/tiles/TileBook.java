package noppes.vc.blocks.tiles;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileBook extends TileBasicRotation {
   public ItemStack book = new ItemStack(Items.WRITABLE_BOOK);

   public TileBook() {
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.book = new ItemStack(compound.getCompoundTag("Items"));
      if (this.book == null) {
         this.book = new ItemStack(Items.WRITABLE_BOOK);
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      compound.setTag("Items", this.book.writeToNBT(new NBTTagCompound()));
      return super.writeToNBT(compound);
   }
}
