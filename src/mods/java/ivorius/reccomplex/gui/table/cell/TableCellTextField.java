package ivorius.reccomplex.gui.table.cell;

import ivorius.reccomplex.gui.GuiValidityStateIndicator;
import ivorius.reccomplex.gui.table.Bounds;
import ivorius.reccomplex.gui.table.GuiTable;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class TableCellTextField<T> extends TableCellPropertyDefault<T> {
   @Nullable
   protected final GuiTextField textField = new GuiTextField(-1, this.getFontRenderer(), 0, 0, 0, 0);
   @Nullable
   protected final GuiValidityStateIndicator stateIndicator;
   protected boolean showsValidityState;
   protected boolean validDataType = true;
   protected GuiValidityStateIndicator.State validityState = GuiValidityStateIndicator.State.VALID;
   @Nullable
   protected Runnable changeListener;

   public TableCellTextField(String id, T value) {
      super(id, value);
      this.textField.setMaxStringLength(32500);
      this.stateIndicator = new GuiValidityStateIndicator(0, 0, GuiValidityStateIndicator.State.UNKNWON);
      this.setPropertyValue(value);
      this.updateValidityStateIndicator();
   }

   public int getMaxStringLength() {
      return this.textField.getMaxStringLength();
   }

   public void setMaxStringLength(int maxStringLength) {
      this.textField.setMaxStringLength(maxStringLength);
   }

   @Nullable
   public Runnable getChangeListener() {
      return this.changeListener;
   }

   public void setChangeListener(@Nullable Runnable changeListener) {
      this.changeListener = changeListener;
   }

   @Nullable
   public GuiTextField getTextField() {
      return this.textField;
   }

   @Nullable
   public GuiValidityStateIndicator getStateIndicator() {
      return this.stateIndicator;
   }

   public void setEnabled(boolean enabled) {
      super.setEnabled(enabled);
      this.textField.setEnabled(enabled);
   }

   protected abstract String serialize(T var1);

   @Nullable
   protected abstract T deserialize(String var1);

   public void draw(GuiTable screen, int mouseX, int mouseY, float partialTicks) {
      super.draw(screen, mouseX, mouseY, partialTicks);
      this.textField.drawTextBox();
      if (this.showsValidityState) {
         this.stateIndicator.draw();
      }

   }

   public void update(GuiTable screen) {
      super.update(screen);
      this.textField.updateCursorCounter();
   }

   public boolean keyTyped(char keyChar, int keyCode) {
      super.keyTyped(keyChar, keyCode);
      boolean used = this.textField.textboxKeyTyped(keyChar, keyCode);
      T cur = (T)this.deserialize(this.textField.getText());
      if (cur != null) {
         this.validDataType = true;
         T prev = (T)this.property;
         this.property = (P)cur;
         if (!cur.equals(prev)) {
            this.alertListenersOfChange();
         }

         if (this.changeListener != null) {
            this.changeListener.run();
         }
      } else {
         this.validDataType = false;
      }

      this.updateValidityStateIndicator();
      return used;
   }

   public void mouseClicked(int button, int x, int y) {
      super.mouseClicked(button, x, y);
      this.textField.mouseClicked(x, y, button);
      if (this.changeListener != null) {
         this.changeListener.run();
      }

   }

   public void setHidden(boolean hidden) {
      super.setHidden(hidden);
      this.textField.setVisible(!hidden);
      this.stateIndicator.setVisible(!hidden && this.showsValidityState);
   }

   public void setPropertyValue(T t) {
      super.setPropertyValue(t);
      if (this.textField != null) {
         this.textField.setText(this.serialize(t));
      }

      if (this.changeListener != null) {
         this.changeListener.run();
      }

   }

   public void setBounds(Bounds bounds) {
      super.setBounds(bounds);
      Bounds.set(this.textField, Bounds.fromSize(bounds.getMinX() + 2, bounds.getCenterY() - 9, bounds.getWidth() - (this.showsValidityState() ? 14 : 0) - 4, 18));
      this.stateIndicator.xPosition = bounds.getMaxX() - 12;
      this.stateIndicator.yPosition = bounds.getCenterY() - 5;
   }

   protected void updateValidityStateIndicator() {
      this.stateIndicator.setState(!this.validDataType ? GuiValidityStateIndicator.State.INVALID : this.validityState);
   }

   public GuiValidityStateIndicator.State getValidityState() {
      return this.validityState;
   }

   public void setValidityState(GuiValidityStateIndicator.State validityState) {
      this.validityState = validityState;
      this.updateValidityStateIndicator();
   }

   public boolean showsValidityState() {
      return this.showsValidityState;
   }

   public void setShowsValidityState(boolean showsValidityState) {
      this.showsValidityState = showsValidityState;
      this.setBounds(this.bounds());
      this.setHidden(this.isHidden());
   }
}
