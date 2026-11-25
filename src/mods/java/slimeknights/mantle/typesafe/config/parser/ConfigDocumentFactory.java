package slimeknights.mantle.typesafe.config.parser;

import java.io.File;
import java.io.Reader;
import slimeknights.mantle.typesafe.config.ConfigParseOptions;
import slimeknights.mantle.typesafe.config.impl.Parseable;

public final class ConfigDocumentFactory {
   public ConfigDocumentFactory() {
   }

   public static ConfigDocument parseReader(Reader reader, ConfigParseOptions options) {
      return Parseable.newReader(reader, options).parseConfigDocument();
   }

   public static ConfigDocument parseReader(Reader reader) {
      return parseReader(reader, ConfigParseOptions.defaults());
   }

   public static ConfigDocument parseFile(File file, ConfigParseOptions options) {
      return Parseable.newFile(file, options).parseConfigDocument();
   }

   public static ConfigDocument parseFile(File file) {
      return parseFile(file, ConfigParseOptions.defaults());
   }

   public static ConfigDocument parseString(String s, ConfigParseOptions options) {
      return Parseable.newString(s, options).parseConfigDocument();
   }

   public static ConfigDocument parseString(String s) {
      return parseString(s, ConfigParseOptions.defaults());
   }
}
