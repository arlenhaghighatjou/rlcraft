package ivorius.reccomplex.gui.table.cell;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.table.Bounds;
import ivorius.reccomplex.gui.table.GuiTable;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableCellBoolean extends TableCellPropertyDefault<Boolean> {
   private GuiButton button;
   protected String trueTitle;
   protected String falseTitle;
   protected boolean enabled;

   public TableCellBoolean(String id, boolean value) {
      this(id, Boolean.valueOf(value), IvTranslations.get("reccomplex.gui.true"), IvTranslations.get("reccomplex.gui.false"));
   }

   public TableCellBoolean(String id, Boolean value, String trueTitle, String falseTitle) {
      super(id, value);
      this.enabled = true;
      this.trueTitle = trueTitle;
      this.falseTitle = falseTitle;
   }

   public String getTrueTitle() {
      return this.trueTitle;
   }

   public void setTrueTitle(String trueTitle) {
      this.trueTitle = trueTitle;
   }

   public String getFalseTitle() {
      return this.falseTitle;
   }

   public void setFalseTitle(String falseTitle) {
      this.falseTitle = falseTitle;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
      if (this.button != null) {
         this.button.enabled = enabled;
      }

   }

   public void initGui(GuiTable screen) {
      super.initGui(screen);
      Bounds bounds = this.bounds();
      this.button = new GuiButton(-1, bounds.getMinX(), bounds.getMinY() + (bounds.getHeight() - 20) / 2, bounds.getWidth(), 20, this.getCurrentButtonTitle());
      this.button.enabled = this.enabled;
      this.button.visible = !this.isHidden();
      screen.addButton(this, 0, this.button);
   }

   public void setHidden(boolean hidden) {
      super.setHidden(hidden);
      if (this.button != null) {
         this.button.visible = !hidden;
      }

   }

   public void setPropertyValue(Boolean value) {
      super.setPropertyValue(value);
      if (this.button != null) {
         this.button.displayString = this.getCurrentButtonTitle();
      }

   }

   public void buttonClicked(int buttonID) {
      super.buttonClicked(buttonID);
      this.property = (P)!((Boolean)this.property).booleanValue();
      if (this.button != null) {
         this.button.displayString = this.getCurrentButtonTitle();
      }

      this.alertListenersOfChange();
   }

   public String getCurrentButtonTitle() {
      return ((Boolean)this.property).booleanValue() ? this.trueTitle : this.falseTitle;
   }
}
