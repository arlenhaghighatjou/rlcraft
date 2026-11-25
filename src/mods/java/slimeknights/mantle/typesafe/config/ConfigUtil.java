package slimeknights.mantle.typesafe.config;

import java.util.List;
import slimeknights.mantle.typesafe.config.impl.ConfigImplUtil;

public final class ConfigUtil {
   private ConfigUtil() {
   }

   public static String quoteString(String s) {
      return ConfigImplUtil.renderJsonString(s);
   }

   public static String joinPath(String... elements) {
      return ConfigImplUtil.joinPath(elements);
   }

   public static String joinPath(List<String> elements) {
      return ConfigImplUtil.joinPath(elements);
   }

   public static List<String> splitPath(String path) {
      return ConfigImplUtil.splitPath(path);
   }
}
