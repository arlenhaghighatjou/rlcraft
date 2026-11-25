package yalter.mousetweaks.liteloader;

import com.mumfrey.liteloader.client.gui.GuiCheckbox;
import com.mumfrey.liteloader.modconfig.AbstractConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;
import com.mumfrey.liteloader.modconfig.AbstractConfigPanel.ConfigOptionListener;
import yalter.mousetweaks.Config;
import yalter.mousetweaks.Logger;
import yalter.mousetweaks.Main;

public class MouseTweaksConfigPanel extends AbstractConfigPanel {
   public MouseTweaksConfigPanel() {
   }

   public String getPanelTitle() {
      return "Mouse Tweaks Configuration";
   }

   protected void addOptions(ConfigPanelHost host) {
      Logger.DebugLog("addOptions()");
      Main.config.read();
      int y = -16;
      y = y + 16;
      ((GuiCheckbox)this.addControl(new GuiCheckbox(0, 0, y, "Enable RMB tweak"), new ConfigOptionListener<GuiCheckbox>() {
         public void actionPerformed(GuiCheckbox control) {
            Main.config.rmbTweak = control.checked = !control.checked;
         }
      })).checked = Main.config.rmbTweak;
      y = y + 16;
      ((GuiCheckbox)this.addControl(new GuiCheckbox(0, 0, y, "Enable LMB tweak with item"), new ConfigOptionListener<GuiCheckbox>() {
         public void actionPerformed(GuiCheckbox control) {
            Main.config.lmbTweakWithItem = control.checked = !control.checked;
         }
      })).checked = Main.config.lmbTweakWithItem;
      y = y + 16;
      ((GuiCheckbox)this.addControl(new GuiCheckbox(0, 0, y, "Enable LMB tweak without item"), new ConfigOptionListener<GuiCheckbox>() {
         public void actionPerformed(GuiCheckbox control) {
            Main.config.lmbTweakWithoutItem = control.checked = !control.checked;
         }
      })).checked = Main.config.lmbTweakWithoutItem;
      y = y + 16;
      ((GuiCheckbox)this.addControl(new GuiCheckbox(0, 0, y, "Enable wheel tweak"), new ConfigOptionListener<GuiCheckbox>() {
         public void actionPerformed(GuiCheckbox control) {
            Main.config.wheelTweak = control.checked = !control.checked;
         }
      })).checked = Main.config.wheelTweak;
      y = y + 16;
      ((GuiCheckbox)this.addControl(new GuiCheckbox(0, 0, y, "Enable debug mode"), new ConfigOptionListener<GuiCheckbox>() {
         public void actionPerformed(GuiCheckbox control) {
            Config.debug = control.checked = !control.checked;
         }
      })).checked = Config.debug;
   }

   public void onPanelHidden() {
      Logger.DebugLog("onPanelHidden()");
      Main.config.save();
      Main.findOnTickMethod(true);
   }
}
