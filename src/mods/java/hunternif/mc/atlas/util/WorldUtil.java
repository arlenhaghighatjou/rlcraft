package hunternif.mc.atlas.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;
import net.minecraft.world.chunk.Chunk;

class WorldUtil {
   WorldUtil() {
   }

   public static Village getVillageInChunk(Chunk chunk) {
      int centerX = (chunk.x << 4) + 8;
      int centerZ = (chunk.z << 4) + 8;

      for(Village village : chunk.getWorld().villageCollection.getVillageList()) {
         BlockPos coords = village.getCenter();
         if ((centerX - coords.getX()) * (centerX - coords.getX()) + (centerZ - coords.getZ()) * (centerZ - coords.getZ()) <= village.getVillageRadius() * village.getVillageRadius()) {
            return village;
         }
      }

      return null;
   }

   public static boolean isVillageDoorInChunk(Village village, Chunk chunk) {
      int centerX = (chunk.x << 4) + 8;
      int centerZ = (chunk.z << 4) + 8;
      if (village.isAnnihilated()) {
         return true;
      } else {
         for(Object doorInfo : village.getVillageDoorInfoList()) {
            BlockPos door = ((VillageDoorInfo)doorInfo).getDoorBlockPos();
            if ((centerX - door.getX()) * (centerX - door.getX()) + (centerZ - door.getZ()) * (centerZ - door.getZ()) <= 100) {
               return true;
            }
         }

         return false;
      }
   }
}
