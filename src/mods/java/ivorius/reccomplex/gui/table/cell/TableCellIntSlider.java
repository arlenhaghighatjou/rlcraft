package ivorius.reccomplex.gui.table.cell;

import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableCellIntSlider extends TableCellSlider<Integer> {
   public TableCellIntSlider(String id, int value, int min, int max) {
      super(id, Integer.valueOf(value), Integer.valueOf(min), Integer.valueOf(max));
   }

   protected float serialize(Integer integer) {
      return (float)integer.intValue();
   }

   @Nullable
   protected Integer deserialize(float val) {
      return (int)(val + 0.5F);
   }

   protected String displayString() {
      return ((Integer)this.getPropertyValue()).toString();
   }
}
