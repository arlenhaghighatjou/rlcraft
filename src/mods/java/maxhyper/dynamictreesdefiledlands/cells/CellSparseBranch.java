package maxhyper.dynamictreesdefiledlands.cells;

import com.ferreusveritas.dynamictrees.api.cells.ICell;
import net.minecraft.util.EnumFacing;

public class CellSparseBranch implements ICell {
   static final int[] map = new int[]{0, 2, 2, 2, 2, 2};

   public CellSparseBranch() {
   }

   public int getValue() {
      return 2;
   }

   public int getValueFromSide(EnumFacing side) {
      return map[side.ordinal()];
   }
}
