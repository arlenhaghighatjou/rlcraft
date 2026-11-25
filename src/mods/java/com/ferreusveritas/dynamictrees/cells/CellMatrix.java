package com.ferreusveritas.dynamictrees.cells;

import com.ferreusveritas.dynamictrees.api.cells.ICell;
import net.minecraft.util.EnumFacing;

public class CellMatrix implements ICell {
   private final int value;
   private final byte[] valMap;

   public CellMatrix(int value, byte[] valMap) {
      this.value = value;
      this.valMap = valMap;
   }

   public int getValue() {
      return this.value;
   }

   public int getValueFromSide(EnumFacing side) {
      return this.valMap[(side.ordinal() << 3) + this.value];
   }
}
