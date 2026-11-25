package ivorius.reccomplex.gui.table.cell;

import ivorius.ivtoolkit.gui.FloatRange;
import ivorius.ivtoolkit.gui.GuiControlListener;
import ivorius.ivtoolkit.gui.GuiSliderMultivalue;
import ivorius.ivtoolkit.gui.GuiSliderRange;
import ivorius.reccomplex.gui.table.Bounds;
import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.utils.scale.Scale;
import ivorius.reccomplex.utils.scale.Scales;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableCellFloatRange extends TableCellPropertyDefault<FloatRange> implements GuiControlListener<GuiSliderMultivalue> {
   protected GuiSliderRange slider;
   protected boolean enabled = true;
   protected float min;
   protected float max;
   protected Scale scale = Scales.none();
   protected String titleFormat = "%.4f";

   public TableCellFloatRange(String id, FloatRange value, float min, float max, String titleFormat) {
      super(id, value);
      this.min = min;
      this.max = max;
      this.titleFormat = titleFormat;
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

   public float getMin() {
      return this.min;
   }

   public void setMin(float min) {
      this.min = min;
   }

   public float getMax() {
      return this.max;
   }

   public void setMax(float max) {
      this.max = max;
   }

   public Scale getScale() {
      return this.scale;
   }

   public void setScale(Scale scale) {
      this.scale = scale;
   }

   public String getTitleFormat() {
      return this.titleFormat;
   }

   public void setTitleFormat(String titleFormat) {
      this.titleFormat = titleFormat;
   }

   public void initGui(GuiTable screen) {
      super.initGui(screen);
      Bounds bounds = this.bounds();
      if (this.slider == null) {
         this.slider = new GuiSliderRange(-1, 0, 0, 0, 0, this.getRangeString());
         this.slider.addListener(this);
      }

      this.updateSliderBounds(bounds);
      this.slider.setMinValue(this.scale.out(this.min));
      this.slider.setMaxValue(this.scale.out(this.max));
      this.slider.enabled = this.enabled;
      this.slider.setRange(Scales.out(this.scale, (FloatRange)this.property));
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
      this.property = (P)Scales.in(this.scale, ((GuiSliderRange)gui).getRange());
      this.slider.displayString = this.getRangeString();
      this.alertListenersOfChange();
   }

   public void setPropertyValue(FloatRange value) {
      super.setPropertyValue(value);
      if (this.slider != null) {
         this.slider.setRange(Scales.out(this.scale, (FloatRange)this.property));
         this.slider.displayString = this.getRangeString();
      }

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

   private String getRangeString() {
      return String.format(this.titleFormat, ((FloatRange)this.property).getMin()) + " - " + String.format(this.titleFormat, ((FloatRange)this.property).getMax());
   }
}
