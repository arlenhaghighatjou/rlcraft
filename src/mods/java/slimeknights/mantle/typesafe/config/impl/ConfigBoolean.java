package slimeknights.mantle.typesafe.config.impl;

import java.io.ObjectStreamException;
import java.io.Serializable;
import slimeknights.mantle.typesafe.config.ConfigOrigin;
import slimeknights.mantle.typesafe.config.ConfigValueType;

final class ConfigBoolean extends AbstractConfigValue implements Serializable {
   private static final long serialVersionUID = 2L;
   private final boolean value;

   ConfigBoolean(ConfigOrigin origin, boolean value) {
      super(origin);
      this.value = value;
   }

   public ConfigValueType valueType() {
      return ConfigValueType.BOOLEAN;
   }

   public Boolean unwrapped() {
      return this.value;
   }

   String transformToString() {
      return this.value ? "true" : "false";
   }

   protected ConfigBoolean newCopy(ConfigOrigin origin) {
      return new ConfigBoolean(origin, this.value);
   }

   private Object writeReplace() throws ObjectStreamException {
      return new SerializedConfigValue(this);
   }
}
