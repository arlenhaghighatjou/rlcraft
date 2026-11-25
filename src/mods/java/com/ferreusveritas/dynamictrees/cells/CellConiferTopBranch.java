package com.ferreusveritas.dynamictrees.cells;

import com.ferreusveritas.dynamictrees.api.cells.ICell;
import net.minecraft.util.EnumFacing;

public class CellConiferTopBranch implements ICell {
   static final int[] map = new int[]{2, 5, 3, 3, 3, 3};

   public CellConiferTopBranch() {
   }

   public int getValue() {
      return 5;
   }

   public int getValueFromSide(EnumFacing side) {
      return map[side.ordinal()];
   }
}
