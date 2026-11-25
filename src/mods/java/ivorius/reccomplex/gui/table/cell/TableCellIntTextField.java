package ivorius.reccomplex.gui.table.cell;

import com.google.common.primitives.Ints;
import java.util.function.IntPredicate;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableCellIntTextField extends TableCellTextField<Integer> {
   @Nullable
   protected IntPredicate predicate;

   public TableCellIntTextField(String id, int value) {
      super(id, Integer.valueOf(value));
      this.showsValidityState = true;
   }

   public TableCellIntTextField(String id, int value, IntPredicate predicate) {
      super(id, Integer.valueOf(value));
      this.predicate = predicate;
   }

   public IntPredicate getPredicate() {
      return this.predicate;
   }

   public void setPredicate(IntPredicate predicate) {
      this.predicate = predicate;
   }

   protected String serialize(Integer integer) {
      return integer.toString();
   }

   @Nullable
   protected Integer deserialize(String string) {
      Integer i = Ints.tryParse(string);
      return i != null && (this.predicate == null || this.predicate.test(i.intValue())) ? i : null;
   }
}
