package net.ilexiconn.llibrary.client.gui.config.property;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.ilexiconn.llibrary.client.gui.config.ConfigGUI;
import net.ilexiconn.llibrary.client.gui.element.DropdownButtonElement;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.server.property.IStringSelectionProperty;
import net.minecraftforge.common.config.Property;

public class StringSelectionConfigProperty extends StringConfigPropertyBase implements IStringSelectionProperty {
   private final Set<String> validStringValues = new HashSet();

   public StringSelectionConfigProperty(Property property) {
      super(property);
      Collections.addAll(this.validStringValues, property.getValidValues());
   }

   public Element<ConfigGUI> provideElement(ConfigGUI gui, float x, float y) {
      return new DropdownButtonElement<ConfigGUI>(gui, x, y, 192, 12, this);
   }

   public Set<String> getValidStringValues() {
      return this.validStringValues;
   }
}
