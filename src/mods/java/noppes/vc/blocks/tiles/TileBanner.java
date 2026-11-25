package noppes.vc.blocks.tiles;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;

public class TileBanner extends TileBasicRotation {
   public ItemStack icon;
   public long time = 0L;

   public TileBanner() {
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.icon = new ItemStack(compound.getCompoundTag("BannerIcon"));
      this.time = compound.getLong("EditTime");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      if (this.icon != null) {
         compound.setTag("BannerIcon", this.icon.writeToNBT(new NBTTagCompound()));
      }

      compound.setLong("EditTime", this.time);
      return super.writeToNBT(compound);
   }

   public AxisAlignedBB getRenderBoundingBox() {
      return new AxisAlignedBB((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (double)(this.pos.getX() + 1), (double)(this.pos.getY() + 2), (double)(this.pos.getZ() + 1));
   }

   public boolean canEdit() {
      return System.currentTimeMillis() - this.time < 10000L;
   }

   public static class TileBanner1 extends TileBanner {
      public TileBanner1() {
      }
   }

   public static class TileBanner2 extends TileBanner {
      public TileBanner2() {
      }
   }

   public static class TileBanner3 extends TileBanner {
      public TileBanner3() {
      }
   }

   public static class TileBanner4 extends TileBanner {
      public TileBanner4() {
      }
   }
}
