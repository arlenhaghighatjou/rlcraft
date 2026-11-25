package yalter.mousetweaks.forge;

import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import yalter.mousetweaks.Config;
import yalter.mousetweaks.Logger;
import yalter.mousetweaks.Main;
import yalter.mousetweaks.WheelScrollDirection;
import yalter.mousetweaks.WheelSearchOrder;

public class ConfigGuiFactory$ConfigGui extends GuiConfig {
   private static Property rmbTweak = new Property("RMB tweak", "true", Type.BOOLEAN);
   private static Property lmbTweakWithItem = new Property("LMB tweak with item", "true", Type.BOOLEAN);
   private static Property lmbTweakWithoutItem = new Property("LMB tweak without item", "true", Type.BOOLEAN);
   private static Property wheelTweak = new Property("Wheel tweak", "true", Type.BOOLEAN);
   private static Property wheelSearchOrder = new Property("Wheel tweak search order", "Last to first", Type.STRING, new String[]{"First to last", "Last to first"});
   private static Property wheelScrollDirection = new Property("Wheel tweak scroll direction", "Down to push, up to pull", Type.STRING, new String[]{"Down to push, up to pull", "Up to push, down to pull"});
   private static Property onTickMethodOrder = new Property("OnTick method order", "Forge, LiteLoader", Type.STRING);
   private static Property debug = new Property("Debug", "false", Type.BOOLEAN);
   private boolean is_open = false;

   public ConfigGuiFactory$ConfigGui(blk parentScreen) {
      super(parentScreen, getConfigElements(), "mousetweaks", false, false, ".minecraft/config/MouseTweaks.cfg");
   }

   private static List<IConfigElement> getConfigElements() {
      List<IConfigElement> list = new ArrayList();
      list.add(new ConfigElement(rmbTweak));
      list.add(new ConfigElement(lmbTweakWithItem));
      list.add(new ConfigElement(lmbTweakWithoutItem));
      list.add(new ConfigElement(wheelTweak));
      list.add(new ConfigElement(wheelSearchOrder));
      list.add(new ConfigElement(wheelScrollDirection));
      list.add(new ConfigElement(onTickMethodOrder));
      list.add(new ConfigElement(debug));
      return list;
   }

   public void b() {
      Logger.DebugLog("initGui()");
      if (!this.is_open) {
         this.is_open = true;
         Main.config.read();
         rmbTweak.set(Main.config.rmbTweak);
         lmbTweakWithItem.set(Main.config.lmbTweakWithItem);
         lmbTweakWithoutItem.set(Main.config.lmbTweakWithoutItem);
         wheelTweak.set(Main.config.wheelTweak);
         wheelSearchOrder.set(Main.config.wheelSearchOrder == WheelSearchOrder.FIRST_TO_LAST ? "First to last" : "Last to first");
         wheelScrollDirection.set(Main.config.wheelScrollDirection == WheelScrollDirection.NORMAL ? "Down to push, up to pull" : "Up to push, down to pull");
         onTickMethodOrder.set(Main.config.onTickMethodOrderString());
         debug.set(Config.debug);
      }

      super.b();
   }

   public void m() {
      Logger.DebugLog("onGuiClosed()");
      Main.config.rmbTweak = rmbTweak.getBoolean();
      Main.config.lmbTweakWithItem = lmbTweakWithItem.getBoolean();
      Main.config.lmbTweakWithoutItem = lmbTweakWithoutItem.getBoolean();
      Main.config.wheelTweak = wheelTweak.getBoolean();
      Main.config.wheelSearchOrder = wheelSearchOrder.getString().equals("First to last") ? WheelSearchOrder.FIRST_TO_LAST : WheelSearchOrder.LAST_TO_FIRST;
      Main.config.wheelScrollDirection = wheelScrollDirection.getString().equals("Down to push, up to pull") ? WheelScrollDirection.NORMAL : WheelScrollDirection.INVERTED;
      Main.config.onTickMethodOrderFromString(onTickMethodOrder.getString());
      Config.debug = debug.getBoolean();
      Main.config.save();
      Main.findOnTickMethod(true);
      this.is_open = false;
      super.m();
   }
}
