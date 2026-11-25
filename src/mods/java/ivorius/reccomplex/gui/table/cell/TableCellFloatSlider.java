package ivorius.reccomplex.gui.table.cell;

import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableCellFloatSlider extends TableCellSlider<Float> {
   protected String titleFormat;

   public TableCellFloatSlider(String id, float value, float min, float max) {
      super(id, Float.valueOf(value), Float.valueOf(min), Float.valueOf(max));
      this.setTitleFormat("%.4f");
   }

   public String getTitleFormat() {
      return this.titleFormat;
   }

   public void setTitleFormat(String titleFormat) {
      this.titleFormat = titleFormat;
      this.slider.displayString = this.displayString();
   }

   protected float serialize(Float aFloat) {
      return aFloat.floatValue();
   }

   @Nullable
   protected Float deserialize(float val) {
      return val;
   }

   protected String displayString() {
      return String.format(this.titleFormat != null ? this.titleFormat : "%f", this.getPropertyValue());
   }
}
