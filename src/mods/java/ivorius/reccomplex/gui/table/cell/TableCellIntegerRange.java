package ivorius.reccomplex.gui.table.cell;

import ivorius.ivtoolkit.gui.FloatRange;
import ivorius.ivtoolkit.gui.GuiControlListener;
import ivorius.ivtoolkit.gui.GuiSliderMultivalue;
import ivorius.ivtoolkit.gui.GuiSliderRange;
import ivorius.ivtoolkit.gui.IntegerRange;
import ivorius.ivtoolkit.gui.Ranges;
import ivorius.reccomplex.gui.table.Bounds;
import ivorius.reccomplex.gui.table.GuiTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableCellIntegerRange extends TableCellPropertyDefault<IntegerRange> implements GuiControlListener<GuiSliderMultivalue> {
   private GuiSliderRange slider;
   private boolean enabled = true;
   private int min;
   private int max;

   public TableCellIntegerRange(String id, IntegerRange value, int min, int max) {
      super(id, value);
      this.min = min;
      this.max = max;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
      if (this.slider != null) {
         this.slider.enabled = enabled;
      }

   }

   public void initGui(GuiTable screen) {
      super.initGui(screen);
      Bounds bounds = this.bounds();
      if (this.slider == null) {
         this.slider = new GuiSliderRange(-1, 0, 0, 0, 0, "");
         this.slider.addListener(this);
      }

      this.updateSliderBounds(bounds);
      this.updateSliderString();
      this.slider.setMinValue((float)this.min);
      this.slider.setMaxValue((float)this.max);
      this.slider.enabled = this.enabled;
      this.slider.setRange(new FloatRange((IntegerRange)this.property));
      this.slider.visible = !this.isHidden();
      screen.addButton(this, 0, this.slider);
   }

   public void setHidden(boolean hidden) {
      super.setHidden(hidden);
      if (this.slider != null) {
         this.slider.visible = !hidden;
      }

   }

   public void valueChanged(GuiSliderMultivalue gui) {
      this.property = (P)Ranges.roundedIntRange(this.slider.getRange());
      this.slider.setRange(new FloatRange((IntegerRange)this.property));
      this.updateSliderString();
      this.alertListenersOfChange();
   }

   public void setPropertyValue(IntegerRange value) {
      super.setPropertyValue(value);
      if (this.slider != null) {
         this.slider.setRange(new FloatRange(value));
         this.updateSliderString();
      }

   }

   protected void updateSliderString() {
      this.slider.displayString = ((IntegerRange)this.property).getMin() + " - " + ((IntegerRange)this.property).getMax();
   }

   protected void updateSliderBounds(Bounds bounds) {
      Bounds.set(this.slider, Bounds.fromSize(bounds.getMinX(), bounds.getMinY() + (bounds.getHeight() - 20) / 2, bounds.getWidth(), 20));
   }

   public void setBounds(Bounds bounds) {
      super.setBounds(bounds);
      if (this.slider != null) {
         this.updateSliderBounds(bounds);
      }

   }
}
