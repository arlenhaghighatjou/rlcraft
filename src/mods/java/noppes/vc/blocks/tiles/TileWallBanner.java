package noppes.vc.blocks.tiles;

import net.minecraft.util.math.AxisAlignedBB;

public class TileWallBanner extends TileBanner {
   public TileWallBanner() {
   }

   public AxisAlignedBB getRenderBoundingBox() {
      return new AxisAlignedBB((double)this.pos.getX(), (double)(this.pos.getY() - 1), (double)this.pos.getZ(), (double)(this.pos.getX() + 1), (double)(this.pos.getY() + 1), (double)(this.pos.getZ() + 1));
   }

   public static class TileWallBanner1 extends TileWallBanner {
      public TileWallBanner1() {
      }
   }

   public static class TileWallBanner2 extends TileWallBanner {
      public TileWallBanner2() {
      }
   }

   public static class TileWallBanner3 extends TileWallBanner {
      public TileWallBanner3() {
      }
   }

   public static class TileWallBanner4 extends TileWallBanner {
      public TileWallBanner4() {
      }
   }
}
