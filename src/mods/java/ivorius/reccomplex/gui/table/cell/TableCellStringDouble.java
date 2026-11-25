package ivorius.reccomplex.gui.table.cell;

import com.google.common.primitives.Doubles;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableCellStringDouble extends TableCellTextField<Double> {
   public TableCellStringDouble(String id, Double value) {
      super(id, value);
      this.showsValidityState = true;
   }

   protected String serialize(Double aDouble) {
      return aDouble.toString();
   }

   @Nullable
   protected Double deserialize(String string) {
      return Doubles.tryParse(string);
   }
}
