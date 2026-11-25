package com.ferreusveritas.dynamictrees.cells;

public class CellDarkOakLeaf extends CellMatrix {
   static final byte[] valMap = new byte[]{0, 1, 2, 3, 3, 0, 0, 0, 0, 1, 2, 3, 3, 0, 0, 0, 0, 1, 2, 3, 4, 0, 0, 0, 0, 1, 2, 3, 4, 0, 0, 0, 0, 1, 2, 3, 4, 0, 0, 0, 0, 1, 2, 3, 4, 0, 0, 0};

   public CellDarkOakLeaf(int value) {
      super(value, valMap);
   }
}
