package com.ferreusveritas.dynamictrees.cells;

public class CellConiferLeaf extends CellMatrix {
   static final byte[] valMap = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 4, 0, 0, 0, 0, 1, 2, 0, 2, 0, 0, 0, 0, 1, 2, 0, 2, 0, 0, 0, 0, 1, 2, 0, 2, 0, 0, 0, 0, 1, 2, 0, 2, 0, 0, 0};

   public CellConiferLeaf(int value) {
      super(value, valMap);
   }
}
