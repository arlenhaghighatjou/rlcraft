package mcjty.lostcities.dimensions.world.lost;

import javax.annotation.Nonnull;

public enum Direction {
   XMIN,
   XMAX,
   ZMIN,
   ZMAX;

   public static final Direction[] VALUES = new Direction[]{XMIN, XMAX, ZMIN, ZMAX};

   private Direction() {
   }

   public Orientation getOrientation() {
      return this != XMIN && this != XMAX ? Orientation.Z : Orientation.X;
   }

   public Transform getRotation() {
      switch(this) {
      case XMIN:
         return Transform.ROTATE_NONE;
      case XMAX:
         return Transform.ROTATE_180;
      case ZMIN:
         return Transform.ROTATE_90;
      case ZMAX:
         return Transform.ROTATE_270;
      default:
         throw new IllegalStateException("Cannot happen!");
      }
   }

   public Direction getOpposite() {
      switch(this) {
      case XMIN:
         return XMAX;
      case XMAX:
         return XMIN;
      case ZMIN:
         return ZMAX;
      case ZMAX:
         return ZMIN;
      default:
         throw new IllegalStateException("Cannot happen!");
      }
   }

   @Nonnull
   public BuildingInfo get(BuildingInfo info) {
      switch(this) {
      case XMIN:
         return info.getXmin();
      case XMAX:
         return info.getXmax();
      case ZMIN:
         return info.getZmin();
      case ZMAX:
         return info.getZmax();
      default:
         throw new IllegalStateException("Cannot happen!");
      }
   }

   public boolean atSide(int x, int z) {
      switch(this) {
      case XMIN:
         return x == 0;
      case XMAX:
         return x == 15;
      case ZMIN:
         return z == 0;
      case ZMAX:
         return z == 15;
      default:
         throw new IllegalStateException("Cannot happen!");
      }
   }
}
