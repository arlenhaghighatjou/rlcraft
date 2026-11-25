package slimeknights.mantle.typesafe.config.impl;

import slimeknights.mantle.typesafe.config.ConfigIncludeContext;
import slimeknights.mantle.typesafe.config.ConfigParseOptions;
import slimeknights.mantle.typesafe.config.ConfigParseable;
import slimeknights.mantle.typesafe.config.ConfigSyntax;

class SimpleIncludeContext implements ConfigIncludeContext {
   private final Parseable parseable;
   private final ConfigParseOptions options;

   SimpleIncludeContext(Parseable parseable) {
      this.parseable = parseable;
      this.options = SimpleIncluder.clearForInclude(parseable.options());
   }

   private SimpleIncludeContext(Parseable parseable, ConfigParseOptions options) {
      this.parseable = parseable;
      this.options = options;
   }

   SimpleIncludeContext withParseable(Parseable parseable) {
      return parseable == this.parseable ? this : new SimpleIncludeContext(parseable);
   }

   public ConfigParseable relativeTo(String filename) {
      if (ConfigImpl.traceLoadsEnabled()) {
         ConfigImpl.trace("Looking for '" + filename + "' relative to " + this.parseable);
      }

      return this.parseable != null ? this.parseable.relativeTo(filename) : null;
   }

   public ConfigParseOptions parseOptions() {
      return this.options;
   }

   public ConfigIncludeContext setParseOptions(ConfigParseOptions options) {
      return new SimpleIncludeContext(this.parseable, options.setSyntax((ConfigSyntax)null).setOriginDescription((String)null));
   }
}
