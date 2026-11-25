package mcjty.lostcities.dimensions.world.lost;

import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.util.Rotation;

public enum Transform {
   ROTATE_NONE(Rotation.NONE),
   ROTATE_90(Rotation.CLOCKWISE_90),
   ROTATE_180(Rotation.CLOCKWISE_180),
   ROTATE_270(Rotation.COUNTERCLOCKWISE_90),
   MIRROR_X(Rotation.CLOCKWISE_180),
   MIRROR_Z(Rotation.CLOCKWISE_180),
   MIRROR_90_X(Rotation.CLOCKWISE_90);

   private final Rotation mcRotation;

   private Transform(Rotation mcRotation) {
      this.mcRotation = mcRotation;
   }

   public Rotation getMcRotation() {
      return this.mcRotation;
   }

   public Transform getOpposite() {
      switch(this) {
      case ROTATE_NONE:
         return ROTATE_NONE;
      case ROTATE_270:
         return ROTATE_90;
      case ROTATE_180:
         return ROTATE_180;
      case ROTATE_90:
         return ROTATE_270;
      case MIRROR_X:
         return MIRROR_X;
      case MIRROR_Z:
         return MIRROR_Z;
      case MIRROR_90_X:
         return MIRROR_90_X;
      default:
         throw new IllegalStateException("Cannot happen!");
      }
   }

   public int rotateX(int x, int z) {
      switch(this) {
      case ROTATE_NONE:
         return x;
      case ROTATE_270:
         return z;
      case ROTATE_180:
         return 15 - x;
      case ROTATE_90:
         return 15 - z;
      case MIRROR_X:
         return 15 - x;
      case MIRROR_Z:
         return x;
      case MIRROR_90_X:
         return z;
      default:
         throw new IllegalStateException("Cannot happen!");
      }
   }

   public int rotateZ(int x, int z) {
      switch(this) {
      case ROTATE_NONE:
         return z;
      case ROTATE_270:
         return 15 - x;
      case ROTATE_180:
         return 15 - z;
      case ROTATE_90:
         return x;
      case MIRROR_X:
         return z;
      case MIRROR_Z:
         return 15 - z;
      case MIRROR_90_X:
         return x;
      default:
         throw new IllegalStateException("Cannot happen!");
      }
   }

   public EnumRailDirection transform(EnumRailDirection shape) {
      if (this == ROTATE_NONE) {
         return shape;
      } else {
         switch(shape) {
         case NORTH_SOUTH:
            return this != ROTATE_90 && this != ROTATE_270 && this != MIRROR_90_X ? shape : EnumRailDirection.EAST_WEST;
         case EAST_WEST:
            return this != ROTATE_90 && this != ROTATE_270 ? shape : EnumRailDirection.NORTH_SOUTH;
         case ASCENDING_EAST:
            switch(this) {
            case ROTATE_270:
               return EnumRailDirection.ASCENDING_NORTH;
            case ROTATE_180:
               return EnumRailDirection.ASCENDING_WEST;
            case ROTATE_90:
               return EnumRailDirection.ASCENDING_SOUTH;
            case MIRROR_X:
               return EnumRailDirection.ASCENDING_WEST;
            case MIRROR_Z:
            default:
               throw new IllegalStateException("Cannot happen!");
            case MIRROR_90_X:
               return EnumRailDirection.ASCENDING_NORTH;
            }
         case ASCENDING_WEST:
            switch(this) {
            case ROTATE_270:
               return EnumRailDirection.ASCENDING_SOUTH;
            case ROTATE_180:
               return EnumRailDirection.ASCENDING_EAST;
            case ROTATE_90:
               return EnumRailDirection.ASCENDING_NORTH;
            case MIRROR_X:
               return EnumRailDirection.ASCENDING_EAST;
            case MIRROR_Z:
            default:
               throw new IllegalStateException("Cannot happen!");
            case MIRROR_90_X:
               return EnumRailDirection.ASCENDING_SOUTH;
            }
         case ASCENDING_NORTH:
            switch(this) {
            case ROTATE_270:
               return EnumRailDirection.ASCENDING_WEST;
            case ROTATE_180:
               return EnumRailDirection.ASCENDING_SOUTH;
            case ROTATE_90:
               return EnumRailDirection.ASCENDING_EAST;
            case MIRROR_X:
               return EnumRailDirection.ASCENDING_SOUTH;
            case MIRROR_Z:
               return EnumRailDirection.ASCENDING_SOUTH;
            case MIRROR_90_X:
               return EnumRailDirection.ASCENDING_WEST;
            default:
               throw new IllegalStateException("Cannot happen!");
            }
         case ASCENDING_SOUTH:
            switch(this) {
            case ROTATE_270:
               return EnumRailDirection.ASCENDING_EAST;
            case ROTATE_180:
               return EnumRailDirection.ASCENDING_NORTH;
            case ROTATE_90:
               return EnumRailDirection.ASCENDING_WEST;
            case MIRROR_X:
               return EnumRailDirection.ASCENDING_NORTH;
            case MIRROR_Z:
               return EnumRailDirection.ASCENDING_NORTH;
            case MIRROR_90_X:
               return EnumRailDirection.ASCENDING_EAST;
            default:
               throw new IllegalStateException("Cannot happen!");
            }
         case SOUTH_EAST:
            switch(this) {
            case ROTATE_270:
               return EnumRailDirection.NORTH_EAST;
            case ROTATE_180:
               return EnumRailDirection.NORTH_WEST;
            case ROTATE_90:
               return EnumRailDirection.SOUTH_WEST;
            case MIRROR_X:
               return EnumRailDirection.SOUTH_WEST;
            case MIRROR_Z:
               return EnumRailDirection.NORTH_EAST;
            case MIRROR_90_X:
               return EnumRailDirection.NORTH_EAST;
            default:
               throw new IllegalStateException("Cannot happen!");
            }
         case SOUTH_WEST:
            switch(this) {
            case ROTATE_270:
               return EnumRailDirection.SOUTH_EAST;
            case ROTATE_180:
               return EnumRailDirection.NORTH_EAST;
            case ROTATE_90:
               return EnumRailDirection.NORTH_WEST;
            case MIRROR_X:
               return EnumRailDirection.SOUTH_EAST;
            case MIRROR_Z:
               return EnumRailDirection.NORTH_WEST;
            case MIRROR_90_X:
               return EnumRailDirection.SOUTH_EAST;
            default:
               throw new IllegalStateException("Cannot happen!");
            }
         case NORTH_WEST:
            switch(this) {
            case ROTATE_270:
               return EnumRailDirection.SOUTH_WEST;
            case ROTATE_180:
               return EnumRailDirection.SOUTH_EAST;
            case ROTATE_90:
               return EnumRailDirection.NORTH_EAST;
            case MIRROR_X:
               return EnumRailDirection.NORTH_EAST;
            case MIRROR_Z:
               return EnumRailDirection.SOUTH_WEST;
            case MIRROR_90_X:
               return EnumRailDirection.SOUTH_WEST;
            default:
               throw new IllegalStateException("Cannot happen!");
            }
         case NORTH_EAST:
            switch(this) {
            case ROTATE_270:
               return EnumRailDirection.NORTH_WEST;
            case ROTATE_180:
               return EnumRailDirection.SOUTH_WEST;
            case ROTATE_90:
               return EnumRailDirection.SOUTH_EAST;
            case MIRROR_X:
               return EnumRailDirection.NORTH_WEST;
            case MIRROR_Z:
               return EnumRailDirection.SOUTH_EAST;
            case MIRROR_90_X:
               return EnumRailDirection.NORTH_WEST;
            }
         }

         throw new IllegalStateException("Cannot happen!");
      }
   }

   public static void main(String[] args) {
      int x = 4;
      int z = 4;
      System.out.println("x,z = " + x + "," + z + " -> " + ROTATE_90.rotateX(x, z) + "," + ROTATE_90.rotateZ(x, z));
   }
}
