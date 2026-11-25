package net.ilexiconn.llibrary.client.gui.config;

import java.util.ArrayList;
import java.util.List;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.element.CheckboxElement;
import net.ilexiconn.llibrary.client.gui.element.ColorElement;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.server.property.IBooleanProperty;
import net.ilexiconn.llibrary.server.property.IIntProperty;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LLibraryConfigGUI extends ConfigGUI {
   private static final List<ConfigProperty> GENERAL_PROPERTIES = new ArrayList();
   private static final List<ConfigProperty> APPEARANCE_PROPERTIES = new ArrayList();

   public LLibraryConfigGUI(GuiScreen parent) {
      super(parent, LLibrary.INSTANCE, (Configuration)null);
      this.categories.add(new ConfigCategory("General", GENERAL_PROPERTIES));
      this.categories.add(new ConfigCategory("Appearance", APPEARANCE_PROPERTIES));
   }

   public void onGuiClosed() {
      LLibrary.CONFIG.save();
      super.onGuiClosed();
   }

   static {
      GENERAL_PROPERTIES.add(new ConfigProperty("Patreon Effects") {
         public Element<ConfigGUI> provideElement(ConfigGUI gui, float x, float y) {
            return new CheckboxElement<ConfigGUI>(gui, x, y, new IBooleanProperty() {
               public boolean getBoolean() {
                  return LLibrary.CONFIG.hasPatreonEffects();
               }

               public void setBoolean(boolean value) {
                  LLibrary.CONFIG.setPatreonEffects(value);
               }

               public boolean isValidBoolean(boolean value) {
                  return true;
               }
            });
         }
      });
      GENERAL_PROPERTIES.add(new ConfigProperty("Version Checker") {
         public Element<ConfigGUI> provideElement(ConfigGUI gui, float x, float y) {
            return new CheckboxElement<ConfigGUI>(gui, x, y, new IBooleanProperty() {
               public boolean getBoolean() {
                  return LLibrary.CONFIG.hasVersionCheck();
               }

               public void setBoolean(boolean value) {
                  LLibrary.CONFIG.setVersionCheck(value);
               }

               public boolean isValidBoolean(boolean value) {
                  return true;
               }
            });
         }
      });
      GENERAL_PROPERTIES.add(new ConfigProperty("Survival Tabs Always Visible") {
         public Element<ConfigGUI> provideElement(ConfigGUI gui, float x, float y) {
            return new CheckboxElement<ConfigGUI>(gui, x, y, new IBooleanProperty() {
               public boolean getBoolean() {
                  return LLibrary.CONFIG.areTabsAlwaysVisible();
               }

               public void setBoolean(boolean value) {
                  LLibrary.CONFIG.setTabsAlwaysVisible(value);
               }

               public boolean isValidBoolean(boolean value) {
                  return true;
               }
            });
         }
      });
      GENERAL_PROPERTIES.add(new ConfigProperty("Survival Tabs Left Side") {
         public Element<ConfigGUI> provideElement(ConfigGUI gui, float x, float y) {
            return new CheckboxElement<ConfigGUI>(gui, x, y, new IBooleanProperty() {
               public boolean getBoolean() {
                  return LLibrary.CONFIG.areTabsLeftSide();
               }

               public void setBoolean(boolean value) {
                  LLibrary.CONFIG.setTabsLeftSide(value);
               }

               public boolean isValidBoolean(boolean value) {
                  return true;
               }
            });
         }
      });
      APPEARANCE_PROPERTIES.add(new ConfigProperty("Accent Color") {
         public Element<ConfigGUI> provideElement(ConfigGUI gui, float x, float y) {
            return new ColorElement<ConfigGUI>(gui, x, y, 195, 149, new IIntProperty() {
               public int getInt() {
                  return LLibrary.CONFIG.getAccentColor();
               }

               public void setInt(int value) {
                  LLibrary.CONFIG.setAccentColor(value);
               }

               public boolean isValidInt(int value) {
                  return true;
               }
            });
         }
      });
      APPEARANCE_PROPERTIES.add(new ConfigProperty("Dark Mode") {
         public Element<ConfigGUI> provideElement(ConfigGUI gui, float x, float y) {
            return new CheckboxElement<ConfigGUI>(gui, x, y, new IBooleanProperty() {
               public boolean getBoolean() {
                  return LLibrary.CONFIG.getColorMode().equals("dark");
               }

               public void setBoolean(boolean value) {
                  LLibrary.CONFIG.setColorMode(value ? "dark" : "light");
               }

               public boolean isValidBoolean(boolean value) {
                  return true;
               }
            });
         }
      });
   }
}
