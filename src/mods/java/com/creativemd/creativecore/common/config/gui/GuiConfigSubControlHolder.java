package com.creativemd.creativecore.common.config.gui;

import com.creativemd.creativecore.common.config.holder.ConfigKey;
import com.creativemd.creativecore.common.config.holder.ICreativeConfigHolder;
import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiLabel;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraftforge.fml.relauncher.Side;

public class GuiConfigSubControlHolder extends GuiConfigSubControl {
   public final ICreativeConfigHolder holder;
   public final Object value;

   public GuiConfigSubControlHolder(String name, int x, int y, int width, int height, ICreativeConfigHolder holder, Object value) {
      super(name, x, y, width, height);
      this.holder = holder;
      this.value = value;
   }

   public void createControls() {
      int offsetX = 1;
      int offsetY = 1;

      for(ConfigKey key : this.holder.fields()) {
         if (!key.requiresRestart) {
            Object value = key.get();
            String caption = translateOrDefault("config." + String.join(".", this.holder.path()) + "." + key.name + ".name", key.name);
            String comment = "config." + String.join(".", this.holder.path()) + "." + key.name + ".comment";
            if (!(value instanceof ICreativeConfigHolder)) {
               GuiLabel label = new GuiLabel(caption + ":", offsetX, offsetY + 2);
               final GuiConfigControl config = new GuiConfigControl((ConfigKey.ConfigKeyField)key, 0, offsetY, 100, 14, Side.SERVER);
               GuiButton resetButton = new GuiButton("r", offsetX + 390, offsetY, 14) {
                  public void onClicked(int x, int y, int button) {
                     config.reset();
                  }
               };
               int labelWidth = label.width;
               config.posX = label.posX + labelWidth + 2;
               config.width = this.width - 25 - config.posX;
               config.init((JsonElement)null);
               this.addControl(label.setLangTooltip(comment));
               this.addControl(config);
               this.addControl(resetButton.setCustomTooltip(new String[]{"reset to default"}));
               config.setResetButton(resetButton);
               offsetY += config.height + 1;
            }
         }
      }

      this.height = offsetY;
   }

   public void save() {
      JsonObject json = new JsonObject();

      for(GuiControl control : this.controls) {
         if (control instanceof GuiConfigControl) {
            JsonElement element = ((GuiConfigControl)control).save();
            if (element != null) {
               json.add(((GuiConfigControl)control).field.name, element);
            }
         }
      }

      this.holder.load(false, true, json, Side.SERVER);
   }
}
