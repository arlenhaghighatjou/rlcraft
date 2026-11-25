package com.yungnickyoung.minecraft.bettermineshafts.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class BoxUtil {
   public BoxUtil() {
   }

   public static StructureBoundingBox boxFromCoordsWithRotation(int x, int y, int z, int secondaryAxisLen, int yLen, int mainAxisLen, EnumFacing mainAxis) {
      StructureBoundingBox blockBox = new StructureBoundingBox(x, y, z, x, y + yLen - 1, z);
      switch(mainAxis) {
      case NORTH:
      default:
         blockBox.maxX = x + (secondaryAxisLen - 1);
         blockBox.minZ = z - (mainAxisLen - 1);
         break;
      case SOUTH:
         blockBox.minX = x - (secondaryAxisLen - 1);
         blockBox.maxZ = z + (mainAxisLen - 1);
         break;
      case WEST:
         blockBox.minX = x - (mainAxisLen - 1);
         blockBox.minZ = z - (secondaryAxisLen - 1);
         break;
      case EAST:
         blockBox.maxX = x + (mainAxisLen - 1);
         blockBox.maxZ = z + (secondaryAxisLen - 1);
      }

      return blockBox;
   }
}
