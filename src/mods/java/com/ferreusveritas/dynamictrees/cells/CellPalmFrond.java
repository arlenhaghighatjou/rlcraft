package com.ferreusveritas.dynamictrees.cells;

public class CellPalmFrond extends CellMatrix {
   static final byte[] valMap = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 1, 2, 3, 0, 0, 0, 0, 0, 1, 2, 3, 0, 0, 0, 0, 0, 1, 2, 3, 0, 0, 0, 0, 0, 1, 2, 3, 0, 0, 0, 0};

   public CellPalmFrond(int value) {
      super(value, valMap);
   }
}
