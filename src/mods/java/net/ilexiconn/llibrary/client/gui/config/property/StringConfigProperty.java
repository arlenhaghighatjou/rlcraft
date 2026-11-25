package net.ilexiconn.llibrary.client.gui.config.property;

import net.ilexiconn.llibrary.client.gui.config.ConfigGUI;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.client.gui.element.PropertyInputElement;
import net.minecraftforge.common.config.Property;

public class StringConfigProperty extends StringConfigPropertyBase {
   public StringConfigProperty(Property property) {
      super(property);
   }

   public Element<ConfigGUI> provideElement(ConfigGUI gui, float x, float y) {
      return new PropertyInputElement<ConfigGUI>(gui, x, y, 192, this);
   }

   public boolean isValidString(String value) {
      return false;
   }
}
