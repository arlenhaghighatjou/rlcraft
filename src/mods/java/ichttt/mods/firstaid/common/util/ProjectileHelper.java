package ichttt.mods.firstaid.common.util;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.math.AxisAlignedBB;

public class ProjectileHelper {
   private static final AxisAlignedBB HEAD_AABB = new AxisAlignedBB(0.0D, 1.5D, 0.0D, 1.0D, 2.0D, 1.0D);
   private static final AxisAlignedBB MAIN_AABB = new AxisAlignedBB(0.0D, 0.8D, 0.0D, 1.0D, 1.5D, 1.0D);
   private static final AxisAlignedBB LEG_AABB = new AxisAlignedBB(0.0D, 0.4D, 0.0D, 1.0D, 0.8D, 1.0D);
   private static final AxisAlignedBB FEET_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.4D, 1.0D);

   public ProjectileHelper() {
   }

   @Nullable
   public static EntityEquipmentSlot getPartByPosition(Entity hittingObject, EntityPlayer toTrack) {
      if (testAABB(hittingObject, toTrack, HEAD_AABB)) {
         return EntityEquipmentSlot.HEAD;
      } else if (testAABB(hittingObject, toTrack, MAIN_AABB)) {
         return EntityEquipmentSlot.CHEST;
      } else if (testAABB(hittingObject, toTrack, LEG_AABB)) {
         return EntityEquipmentSlot.LEGS;
      } else {
         return testAABB(hittingObject, toTrack, FEET_AABB) ? EntityEquipmentSlot.FEET : null;
      }
   }

   private static boolean testAABB(Entity hittingObject, EntityPlayer toTest, AxisAlignedBB aabb) {
      AxisAlignedBB toTestAABB = hittingObject.getEntityBoundingBox();
      return toTestAABB.minY - toTest.posY < aabb.maxY && toTestAABB.maxY - toTest.posY > aabb.minY;
   }
}
