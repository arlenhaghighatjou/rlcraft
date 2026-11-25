package net.ilexiconn.llibrary.client.gui.config.property;

import net.ilexiconn.llibrary.client.gui.config.ConfigGUI;
import net.ilexiconn.llibrary.client.gui.element.ColorElement;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.server.property.IIntProperty;
import net.minecraftforge.common.config.Property;

public class ColorConfigProperty extends ForgeConfigProperty implements IIntProperty {
   public ColorConfigProperty(Property configProperty) {
      super(configProperty);
   }

   public Element<ConfigGUI> provideElement(ConfigGUI gui, float x, float y) {
      return new ColorElement<ConfigGUI>(gui, x, y, 195, 149, this);
   }

   public int getInt() {
      return this.property.getInt();
   }

   public void setInt(int value) {
      this.property.set(value);
   }

   public boolean isValidInt(int value) {
      return true;
   }
}
