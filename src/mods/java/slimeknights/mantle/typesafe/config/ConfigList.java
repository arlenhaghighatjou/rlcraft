package slimeknights.mantle.typesafe.config;

import java.util.List;

public interface ConfigList extends List<ConfigValue>, ConfigValue {
   List<Object> unwrapped();

   ConfigList withOrigin(ConfigOrigin var1);
}
