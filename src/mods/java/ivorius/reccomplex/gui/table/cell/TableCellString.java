package ivorius.reccomplex.gui.table.cell;

import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableCellString extends TableCellTextField<String> {
   public TableCellString(String id, String value) {
      super(id, value);
   }

   protected String serialize(String s) {
      return s;
   }

   @Nullable
   protected String deserialize(String string) {
      return string;
   }
}
