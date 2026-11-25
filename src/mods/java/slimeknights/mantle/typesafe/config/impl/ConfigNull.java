package slimeknights.mantle.typesafe.config.impl;

import java.io.ObjectStreamException;
import java.io.Serializable;
import slimeknights.mantle.typesafe.config.ConfigOrigin;
import slimeknights.mantle.typesafe.config.ConfigRenderOptions;
import slimeknights.mantle.typesafe.config.ConfigValueType;

final class ConfigNull extends AbstractConfigValue implements Serializable {
   private static final long serialVersionUID = 2L;

   ConfigNull(ConfigOrigin origin) {
      super(origin);
   }

   public ConfigValueType valueType() {
      return ConfigValueType.NULL;
   }

   public Object unwrapped() {
      return null;
   }

   String transformToString() {
      return "null";
   }

   protected void render(StringBuilder sb, int indent, boolean atRoot, ConfigRenderOptions options) {
      sb.append("null");
   }

   protected ConfigNull newCopy(ConfigOrigin origin) {
      return new ConfigNull(origin);
   }

   private Object writeReplace() throws ObjectStreamException {
      return new SerializedConfigValue(this);
   }
}
