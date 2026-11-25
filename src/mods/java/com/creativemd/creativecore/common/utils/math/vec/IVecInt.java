package com.creativemd.creativecore.common.utils.math.vec;

import net.minecraft.util.EnumFacing.Axis;

public interface IVecInt {
   int getX();

   int getY();

   int getZ();

   void setX(int var1);

   void setY(int var1);

   void setZ(int var1);

   int get(Axis var1);

   void set(Axis var1, int var2);
}
