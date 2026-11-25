package ivorius.reccomplex.gui.table.cell;

import ivorius.reccomplex.gui.table.Bounds;
import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.utils.scale.Scale;
import java.util.function.Consumer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableCellFloatNullable extends TableCellPropertyDefault<Float> {
   protected TableCellFloatSlider floatCell;
   protected TableCellButton nullButton;
   protected float defaultValue;
   protected String buttonTitleNull;
   protected String buttonTitleCustom;
   protected float nullButtonWidth = 0.08F;

   public TableCellFloatNullable(String id, Float value, float defaultValue, float min, float max, String buttonTitleNull, String buttonTitleCustom) {
      super(id, value);
      this.defaultValue = defaultValue;
      this.buttonTitleNull = buttonTitleNull;
      this.buttonTitleCustom = buttonTitleCustom;
      this.floatCell = new TableCellFloatSlider((String)null, 0.0F, 0.0F, 0.0F);
      this.floatCell.addListener((value1) -> {
         this.setPropertyValue(value1);
         this.alertListenersOfChange();
      });
      this.nullButton = new TableCellButton((String)null, (String)null, "");
      this.nullButton.addAction(() -> {
         this.setPropertyValue(this.getPropertyValue() == null ? defaultValue : null);
         this.alertListenersOfChange();
      });
      this.setMin(Float.valueOf(min));
      this.setMax(Float.valueOf(max));
      this.setPropertyValue(value);
   }

   public Float getMin() {
      return (Float)this.floatCell.getMin();
   }

   public void setMin(Float min) {
      this.floatCell.setMin(min);
   }

   public Float getMax() {
      return (Float)this.floatCell.getMax();
   }

   public void setMax(Float max) {
      this.floatCell.setMax(max);
   }

   public Scale getScale() {
      return this.floatCell.getScale();
   }

   public void setScale(Scale scale) {
      this.floatCell.setScale(scale);
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
      this.floatCell.setEnabled(enabled && this.property != null);
      this.nullButton.setEnabled(enabled);
   }

   public float getNullButtonWidth() {
      return this.nullButtonWidth;
   }

   public void setNullButtonWidth(float nullButtonWidth) {
      this.nullButtonWidth = nullButtonWidth;
      this.setBounds(this.bounds());
   }

   protected float getActiveValue() {
      return this.property != null ? ((Float)this.property).floatValue() : this.defaultValue;
   }

   public void setPropertyValue(Float value) {
      super.setPropertyValue(value);
      if (this.floatCell != null && this.nullButton != null) {
         this.floatCell.setPropertyValue(Float.valueOf(this.getActiveValue()));
         this.nullButton.setTitle(value != null ? this.buttonTitleCustom : this.buttonTitleNull);
         this.setEnabled(this.enabled);
      }

   }

   public void setHidden(boolean hidden) {
      super.setHidden(hidden);
      this.floatCell.setHidden(hidden);
      this.nullButton.setHidden(hidden);
   }

   public void initGui(GuiTable screen) {
      super.initGui(screen);
      this.floatCell.initGui(screen);
      this.nullButton.initGui(screen);
   }

   public void setBounds(Bounds bounds) {
      super.setBounds(bounds);
      int nullButtonIntWidth = (int)((float)bounds.getWidth() * this.nullButtonWidth);
      this.floatCell.setBounds(Bounds.fromSize(bounds.getMinX(), bounds.getMinY(), bounds.getWidth() - nullButtonIntWidth - 4, bounds.getHeight()));
      this.nullButton.setBounds(Bounds.fromSize(bounds.getMaxX() - nullButtonIntWidth, bounds.getMinY(), nullButtonIntWidth, bounds.getHeight()));
   }
}
