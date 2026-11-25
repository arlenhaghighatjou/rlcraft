package noppes.vc.blocks.tiles;

import net.minecraft.util.math.AxisAlignedBB;

public class TileTallLamp extends TileBasicRotation {
   public TileTallLamp() {
   }

   public AxisAlignedBB getRenderBoundingBox() {
      return new AxisAlignedBB((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (double)(this.pos.getX() + 1), (double)(this.pos.getY() + 2), (double)(this.pos.getZ() + 1));
   }

   public static class TileTallLamp1 extends TileTallLamp {
      public TileTallLamp1() {
      }
   }

   public static class TileTallLamp2 extends TileTallLamp {
      public TileTallLamp2() {
      }
   }

   public static class TileTallLamp3 extends TileTallLamp {
      public TileTallLamp3() {
      }
   }

   public static class TileTallLamp4 extends TileTallLamp {
      public TileTallLamp4() {
      }
   }
}
