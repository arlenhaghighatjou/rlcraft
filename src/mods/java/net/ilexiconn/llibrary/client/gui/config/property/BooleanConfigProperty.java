package net.ilexiconn.llibrary.client.gui.config.property;

import net.ilexiconn.llibrary.client.gui.config.ConfigGUI;
import net.ilexiconn.llibrary.client.gui.element.CheckboxElement;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.server.property.IBooleanProperty;
import net.minecraftforge.common.config.Property;

public class BooleanConfigProperty extends ForgeConfigProperty implements IBooleanProperty {
   public BooleanConfigProperty(Property configProperty) {
      super(configProperty);
   }

   public Element<ConfigGUI> provideElement(ConfigGUI gui, float x, float y) {
      return new CheckboxElement<ConfigGUI>(gui, x, y, this);
   }

   public boolean getBoolean() {
      return this.property.getBoolean();
   }

   public void setBoolean(boolean value) {
      this.property.set(value);
   }

   public boolean isValidBoolean(boolean value) {
      return true;
   }
}
