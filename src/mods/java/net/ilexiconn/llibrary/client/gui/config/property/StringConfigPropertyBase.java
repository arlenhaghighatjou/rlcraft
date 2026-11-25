package net.ilexiconn.llibrary.client.gui.config.property;

import net.ilexiconn.llibrary.server.property.IStringProperty;
import net.minecraftforge.common.config.Property;

public abstract class StringConfigPropertyBase extends ForgeConfigProperty implements IStringProperty {
   public StringConfigPropertyBase(Property property) {
      super(property);
   }

   public String getString() {
      return this.property.getString();
   }

   public void setString(String value) {
      this.property.set(value);
   }

   public static StringConfigPropertyBase factory(Property property) {
      return (StringConfigPropertyBase)(property.getValidValues().length > 0 ? new StringSelectionConfigProperty(property) : new StringConfigProperty(property));
   }
}
