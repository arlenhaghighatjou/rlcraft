package com.endertech.minecraft.forge.api;

import com.endertech.minecraft.forge.math.Vect3d;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;

public interface IWind {
   Vect3d getMotion();

   default float getStrengthIn(EnumFacing direction) {
      Axis axis = direction.getAxis();
      int signum = direction.getAxisDirection().getOffset();
      switch(axis) {
      case X:
         return (float)this.getMotion().x * (float)signum;
      case Y:
         return (float)this.getMotion().y * (float)signum;
      case Z:
         return (float)this.getMotion().z * (float)signum;
      default:
         return 0.0F;
      }
   }

   default void sortDirections(List<EnumFacing> directions) {
      Collections.sort(directions, (dir1, dir2) -> {
         float stren1 = this.getStrengthIn(dir1);
         float stren2 = this.getStrengthIn(dir2);
         return (void)Float.compare(stren2, stren1);
      });
   }
}
