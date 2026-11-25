package com.ferreusveritas.dynamictrees.cells;

public class CellAcaciaLeaf extends CellMatrix {
   static final byte[] valMap = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 0, 0, 0, 0, 1, 2, 3, 4, 0, 0, 0, 0, 1, 2, 3, 4, 0, 0, 0, 0, 1, 2, 3, 4, 0, 0, 0, 0, 1, 2, 3, 4, 0, 0, 0};

   public CellAcaciaLeaf(int value) {
      super(value, valMap);
   }
}
