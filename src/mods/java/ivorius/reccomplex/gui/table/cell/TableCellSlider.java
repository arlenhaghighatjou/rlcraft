package ivorius.reccomplex.gui.table.cell;

import ivorius.ivtoolkit.gui.GuiControlListener;
import ivorius.ivtoolkit.gui.GuiSlider;
import ivorius.reccomplex.gui.table.Bounds;
import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.utils.scale.Scale;
import ivorius.reccomplex.utils.scale.Scales;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class TableCellSlider<T> extends TableCellPropertyDefault<T> implements GuiControlListener<GuiSlider> {
   protected GuiSlider slider = new GuiSlider(-1, 0, 0, 0, 0, "");
   protected T min;
   protected T max;
   protected Scale scale;

   public TableCellSlider(String id, T value, T min, T max) {
      super(id, value);
      this.slider.addListener(this);
      this.scale = Scales.none();
      this.setMin(min);
      this.setMax(max);
      this.setPropertyValue(value);
   }

   public T getMin() {
      return this.min;
   }

   public void setMin(T min) {
      this.min = min;
      this.slider.setMinValue(this.scale.out(this.serialize(min)));
   }

   public T getMax() {
      return this.max;
   }

   public void setMax(T max) {
      this.max = max;
      this.slider.setMaxValue(this.scale.out(this.serialize(max)));
   }

   public Scale getScale() {
      return this.scale;
   }

   public void setScale(Scale scale) {
      this.scale = scale;
      this.setMin(this.min);
      this.setMax(this.max);
      this.slider.setValue(scale.out(this.serialize(this.property)));
   }

   public void setEnabled(boolean enabled) {
      super.setEnabled(enabled);
      this.slider.enabled = enabled;
   }

   protected abstract float serialize(T var1);

   @Nullable
   protected abstract T deserialize(float var1);

   protected abstract String displayString();

   public void initGui(GuiTable screen) {
      super.initGui(screen);
      screen.addButton(this, 0, this.slider);
   }

   public void setHidden(boolean hidden) {
      super.setHidden(hidden);
      if (this.slider != null) {
         this.slider.visible = !hidden;
      }

   }

   public void valueChanged(GuiSlider gui) {
      this.property = (P)this.deserialize(this.scale.in(gui.getValue()));
      gui.displayString = this.displayString();
      this.alertListenersOfChange();
   }

   public void setPropertyValue(T value) {
      super.setPropertyValue(value);
      if (this.slider != null) {
         this.slider.setValue(this.scale.out(this.serialize(this.property)));
         this.slider.displayString = this.displayString();
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
}
