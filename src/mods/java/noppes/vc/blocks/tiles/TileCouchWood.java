package noppes.vc.blocks.tiles;

import net.minecraft.nbt.NBTTagCompound;

public class TileCouchWood extends TileBasicRotation {
   public boolean hasLeft = false;
   public boolean hasRight = false;

   public TileCouchWood() {
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.hasLeft = compound.getBoolean("CouchLeft");
      this.hasRight = compound.getBoolean("CouchRight");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      compound.setBoolean("CouchLeft", this.hasLeft);
      compound.setBoolean("CouchRight", this.hasRight);
      return super.writeToNBT(compound);
   }

   public static class TileCouchWood1 extends TileCouchWood {
      public TileCouchWood1() {
      }
   }

   public static class TileCouchWood2 extends TileCouchWood {
      public TileCouchWood2() {
      }
   }

   public static class TileCouchWood3 extends TileCouchWood {
      public TileCouchWood3() {
      }
   }

   public static class TileCouchWood4 extends TileCouchWood {
      public TileCouchWood4() {
      }
   }

   public static class TileCouchWood5 extends TileCouchWood {
      public TileCouchWood5() {
      }
   }
}
