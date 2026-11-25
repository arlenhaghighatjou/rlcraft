package com.ferreusveritas.dynamictrees.api.cells;

import net.minecraft.util.EnumFacing;

public interface ICell {
   int getValue();

   int getValueFromSide(EnumFacing var1);
}
