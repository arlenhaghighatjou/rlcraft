package com.creativemd.creativecore.common.config.gui;

import com.creativemd.creativecore.common.config.holder.ConfigKey;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.google.gson.JsonElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiConfigControl extends GuiParent {
   public final ConfigKey.ConfigKeyField field;
   public final Side side;
   private GuiButton resetButton;

   public GuiConfigControl(ConfigKey.ConfigKeyField field, int x, int y, int width, int height, Side side) {
      super(field.name, x, y, width, height);
      this.field = field;
      this.borderWidth = this.marginWidth = 0;
      this.side = side;
   }

   public void setResetButton(GuiButton resetButton) {
      this.resetButton = resetButton;
      this.updateButton();
   }

   public void updateButton() {
      this.resetButton.enabled = !this.field.isDefault(this.field.converation.save(this, this.field.getType(), this.field), this.side);
   }

   public boolean hasBorder() {
      return false;
   }

   public boolean hasBackground() {
      return false;
   }

   public boolean hasMouseOverEffect() {
      return false;
   }

   public void init(JsonElement initalValue) {
      this.field.converation.createControls(this, this.field, this.field.getType(), this.width - 20);
      this.field.converation.loadValue(initalValue != null ? this.field.converation.readElement(this.field.getDefault(), false, false, initalValue, this.side, this.field) : this.field.get(), this, this.field);
   }

   public void reset() {
      this.field.converation.loadValue(this.field.getDefault(), this, this.field);
      this.updateButton();
   }

   public void changed() {
      this.updateButton();
   }

   public JsonElement save() {
      Object value = this.field.converation.save(this, this.field.getType(), this.field);
      return !this.field.get().equals(value) ? this.field.converation.writeElement(value, this.field.getDefault(), true, false, this.side, this.field) : null;
   }
}
