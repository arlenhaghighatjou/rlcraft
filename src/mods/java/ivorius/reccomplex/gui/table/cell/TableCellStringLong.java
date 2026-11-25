package ivorius.reccomplex.gui.table.cell;

import com.google.common.primitives.Longs;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableCellStringLong extends TableCellTextField<Long> {
   public TableCellStringLong(String id, Long value) {
      super(id, value);
      this.showsValidityState = true;
   }

   protected String serialize(Long aLong) {
      return aLong.toString();
   }

   @Nullable
   protected Long deserialize(String string) {
      return Longs.tryParse(string);
   }
}
