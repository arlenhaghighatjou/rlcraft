package noppes.vc.blocks.tiles;

import net.minecraft.nbt.NBTTagCompound;

public class TileCouchWool extends TileBasicRotation {
   public boolean hasLeft = false;
   public boolean hasRight = false;
   public boolean hasCornerLeft = false;
   public boolean hasCornerRight = false;

   public TileCouchWool() {
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.hasLeft = compound.getBoolean("CouchLeft");
      this.hasRight = compound.getBoolean("CouchRight");
      this.hasCornerLeft = compound.getBoolean("CouchCornerLeft");
      this.hasCornerRight = compound.getBoolean("CouchCornerRight");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      compound.setBoolean("CouchLeft", this.hasLeft);
      compound.setBoolean("CouchRight", this.hasRight);
      compound.setBoolean("CouchCornerLeft", this.hasCornerLeft);
      compound.setBoolean("CouchCornerRight", this.hasCornerRight);
      return super.writeToNBT(compound);
   }

   public static class TileCouchWool1 extends TileCouchWool {
      public TileCouchWool1() {
      }
   }

   public static class TileCouchWool2 extends TileCouchWool {
      public TileCouchWool2() {
      }
   }

   public static class TileCouchWool3 extends TileCouchWool {
      public TileCouchWool3() {
      }
   }

   public static class TileCouchWool4 extends TileCouchWool {
      public TileCouchWool4() {
      }
   }

   public static class TileCouchWool5 extends TileCouchWool {
      public TileCouchWool5() {
      }
   }
}
