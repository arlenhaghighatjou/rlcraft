package com.ferreusveritas.dynamictrees.cells;

import com.ferreusveritas.dynamictrees.api.cells.ICell;
import net.minecraft.util.EnumFacing;

public class CellNormal implements ICell {
   private final int value;

   public CellNormal(int value) {
      this.value = value;
   }

   public int getValue() {
      return this.value;
   }

   public int getValueFromSide(EnumFacing side) {
      return this.value;
   }
}
