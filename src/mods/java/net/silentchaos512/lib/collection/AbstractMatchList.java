package net.silentchaos512.lib.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraftforge.common.config.Configuration;
import net.silentchaos512.lib.SilentLib;

public abstract class AbstractMatchList<T> implements IMatchList<T> {
   private static final Pattern WILDCARD_PATTERN = Pattern.compile("\\*");
   private boolean whitelist;
   private final List<String> list = new ArrayList();
   private final boolean allowUserToChangeType;
   private final String[] defaultValues;
   private final boolean defaultIsWhitelist;
   private static final String NAME_LIST_SUFFIX = " List";
   private static final String NAME_WHITELIST_SUFFIX = " IsWhitelist";
   private static final String COMMENT_WHITELIST = "If true, the list is a whitelist. Otherwise it is a blacklist.";

   AbstractMatchList(boolean whitelist, boolean allowUserToChangeType, String... defaultValues) {
      this.whitelist = this.defaultIsWhitelist = whitelist;
      this.allowUserToChangeType = allowUserToChangeType;
      this.defaultValues = (String[])defaultValues.clone();
   }

   public boolean matches(T t) {
      return this.contains(t) == this.whitelist;
   }

   protected abstract boolean contains(T var1);

   protected boolean containsKey(String key) {
      return this.list.stream().anyMatch((entry) -> {
         return entry.equalsIgnoreCase(key);
      });
   }

   protected static boolean keyMatches(String pattern, String key) {
      if (pattern.endsWith("*")) {
         String regex = WILDCARD_PATTERN.matcher(pattern).replaceAll(Matcher.quoteReplacement(".*"));
         SilentLib.LOGGER.debug("'{}' matches '{}' = {}", key, pattern, Boolean.valueOf(key.matches("(?i)" + regex)));
         return key.matches("(?i)" + regex);
      } else {
         return pattern.equalsIgnoreCase(key);
      }
   }

   public void loadConfig(Configuration config, String name, String category, String comment) {
      this.list.clear();
      Collections.addAll(this.list, config.getStringList(name + " List", category, this.defaultValues, comment));
      if (this.allowUserToChangeType) {
         this.whitelist = config.getBoolean(name + " IsWhitelist", category, this.defaultIsWhitelist, "If true, the list is a whitelist. Otherwise it is a blacklist.");
      }

   }

   protected boolean isWhitelist() {
      return this.whitelist;
   }

   List<String> getList() {
      return this.list;
   }
}
