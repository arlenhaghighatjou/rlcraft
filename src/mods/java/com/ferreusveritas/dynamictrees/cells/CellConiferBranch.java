package com.ferreusveritas.dynamictrees.cells;

import com.ferreusveritas.dynamictrees.api.cells.ICell;
import net.minecraft.util.EnumFacing;

public class CellConiferBranch implements ICell {
   static final int[] map = new int[]{2, 2, 3, 3, 3, 3};

   public CellConiferBranch() {
   }

   public int getValue() {
      return 5;
   }

   public int getValueFromSide(EnumFacing side) {
      return map[side.ordinal()];
   }
}
