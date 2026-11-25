package com.ferreusveritas.dynamictrees.api.cells;

import net.minecraft.util.EnumFacing;

public class CellNull implements ICell {
   public static final CellNull NULLCELL = new CellNull();

   public CellNull() {
   }

   public int getValue() {
      return 0;
   }

   public int getValueFromSide(EnumFacing side) {
      return 0;
   }
}
