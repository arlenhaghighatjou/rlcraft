package slimeknights.mantle.typesafe.config.impl;

import java.io.ObjectStreamException;
import java.io.Serializable;
import slimeknights.mantle.typesafe.config.ConfigOrigin;
import slimeknights.mantle.typesafe.config.ConfigRenderOptions;
import slimeknights.mantle.typesafe.config.ConfigValueType;

abstract class ConfigString extends AbstractConfigValue implements Serializable {
   private static final long serialVersionUID = 2L;
   protected final String value;

   protected ConfigString(ConfigOrigin origin, String value) {
      super(origin);
      this.value = value;
   }

   boolean wasQuoted() {
      return this instanceof ConfigString.Quoted;
   }

   public ConfigValueType valueType() {
      return ConfigValueType.STRING;
   }

   public String unwrapped() {
      return this.value;
   }

   String transformToString() {
      return this.value;
   }

   protected void render(StringBuilder sb, int indent, boolean atRoot, ConfigRenderOptions options) {
      String rendered;
      if (options.getJson()) {
         rendered = ConfigImplUtil.renderJsonString(this.value);
      } else {
         rendered = ConfigImplUtil.renderStringUnquotedIfPossible(this.value);
      }

      sb.append(rendered);
   }

   static final class Quoted extends ConfigString {
      Quoted(ConfigOrigin origin, String value) {
         super(origin, value);
      }

      protected ConfigString.Quoted newCopy(ConfigOrigin origin) {
         return new ConfigString.Quoted(origin, this.value);
      }

      private Object writeReplace() throws ObjectStreamException {
         return new SerializedConfigValue(this);
      }
   }

   static final class Unquoted extends ConfigString {
      Unquoted(ConfigOrigin origin, String value) {
         super(origin, value);
      }

      protected ConfigString.Unquoted newCopy(ConfigOrigin origin) {
         return new ConfigString.Unquoted(origin, this.value);
      }

      private Object writeReplace() throws ObjectStreamException {
         return new SerializedConfigValue(this);
      }
   }
}
