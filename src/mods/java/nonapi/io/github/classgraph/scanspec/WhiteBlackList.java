package nonapi.io.github.classgraph.scanspec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import nonapi.io.github.classgraph.utils.CollectionUtils;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.JarUtils;

public abstract class WhiteBlackList {
   protected Set<String> whitelist;
   protected Set<String> blacklist;
   protected Set<String> whitelistPrefixesSet;
   protected List<String> whitelistPrefixes;
   protected List<String> blacklistPrefixes;
   protected Set<String> whitelistGlobs;
   protected Set<String> blacklistGlobs;
   protected transient List<Pattern> whitelistPatterns;
   protected transient List<Pattern> blacklistPatterns;
   protected char separatorChar;

   public WhiteBlackList() {
   }

   public WhiteBlackList(char separatorChar) {
      this.separatorChar = separatorChar;
   }

   public abstract void addToWhitelist(String var1);

   public abstract void addToBlacklist(String var1);

   public abstract boolean isWhitelistedAndNotBlacklisted(String var1);

   public abstract boolean isWhitelisted(String var1);

   public abstract boolean whitelistHasPrefix(String var1);

   public abstract boolean isBlacklisted(String var1);

   public static String normalizePath(String path) {
      return FileUtils.sanitizeEntryPath(path, true);
   }

   public static String normalizePackageOrClassName(String packageOrClassName) {
      return normalizePath(packageOrClassName.replace('.', '/')).replace('/', '.');
   }

   public static String pathToPackageName(String path) {
      return path.replace('/', '.');
   }

   public static String packageNameToPath(String packageName) {
      return packageName.replace('.', '/');
   }

   public static String classNameToClassfilePath(String className) {
      return JarUtils.classNameToClassfilePath(className);
   }

   public static Pattern globToPattern(String glob) {
      return Pattern.compile("^" + glob.replace(".", "\\.").replace("*", ".*") + "$");
   }

   private static boolean matchesPatternList(String str, List<Pattern> patterns) {
      if (patterns != null) {
         for(Pattern pattern : patterns) {
            if (pattern.matcher(str).matches()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean whitelistIsEmpty() {
      return this.whitelist == null && this.whitelistPrefixes == null && this.whitelistGlobs == null;
   }

   public boolean blacklistIsEmpty() {
      return this.blacklist == null && this.blacklistPrefixes == null && this.blacklistGlobs == null;
   }

   public boolean whitelistAndBlacklistAreEmpty() {
      return this.whitelistIsEmpty() && this.blacklistIsEmpty();
   }

   public boolean isSpecificallyWhitelistedAndNotBlacklisted(String str) {
      return !this.whitelistIsEmpty() && this.isWhitelistedAndNotBlacklisted(str);
   }

   public boolean isSpecificallyWhitelisted(String str) {
      return !this.whitelistIsEmpty() && this.isWhitelisted(str);
   }

   void sortPrefixes() {
      if (this.whitelistPrefixesSet != null) {
         this.whitelistPrefixes = new ArrayList(this.whitelistPrefixesSet);
      }

      if (this.whitelistPrefixes != null) {
         CollectionUtils.sortIfNotEmpty(this.whitelistPrefixes);
      }

      if (this.blacklistPrefixes != null) {
         CollectionUtils.sortIfNotEmpty(this.blacklistPrefixes);
      }

   }

   private static void quoteList(Collection<String> coll, StringBuilder buf) {
      buf.append('[');
      boolean first = true;

      for(String item : coll) {
         if (first) {
            first = false;
         } else {
            buf.append(", ");
         }

         buf.append('"');

         for(int i = 0; i < item.length(); ++i) {
            char c = item.charAt(i);
            if (c == '"') {
               buf.append("\\\"");
            } else {
               buf.append(c);
            }
         }

         buf.append('"');
      }

      buf.append(']');
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      if (this.whitelist != null) {
         buf.append("whitelist: ");
         quoteList(this.whitelist, buf);
      }

      if (this.whitelistPrefixes != null) {
         if (buf.length() > 0) {
            buf.append("; ");
         }

         buf.append("whitelistPrefixes: ");
         quoteList(this.whitelistPrefixes, buf);
      }

      if (this.whitelistGlobs != null) {
         if (buf.length() > 0) {
            buf.append("; ");
         }

         buf.append("whitelistGlobs: ");
         quoteList(this.whitelistGlobs, buf);
      }

      if (this.blacklist != null) {
         if (buf.length() > 0) {
            buf.append("; ");
         }

         buf.append("blacklist: ");
         quoteList(this.blacklist, buf);
      }

      if (this.blacklistPrefixes != null) {
         if (buf.length() > 0) {
            buf.append("; ");
         }

         buf.append("blacklistPrefixes: ");
         quoteList(this.blacklistPrefixes, buf);
      }

      if (this.blacklistGlobs != null) {
         if (buf.length() > 0) {
            buf.append("; ");
         }

         buf.append("blacklistGlobs: ");
         quoteList(this.blacklistGlobs, buf);
      }

      return buf.toString();
   }

   public static class WhiteBlackListLeafname extends WhiteBlackList.WhiteBlackListWholeString {
      public WhiteBlackListLeafname() {
      }

      public WhiteBlackListLeafname(char separatorChar) {
         super(separatorChar);
      }

      public void addToWhitelist(String str) {
         super.addToWhitelist(JarUtils.leafName(str));
      }

      public void addToBlacklist(String str) {
         super.addToBlacklist(JarUtils.leafName(str));
      }

      public boolean isWhitelistedAndNotBlacklisted(String str) {
         return super.isWhitelistedAndNotBlacklisted(JarUtils.leafName(str));
      }

      public boolean isWhitelisted(String str) {
         return super.isWhitelisted(JarUtils.leafName(str));
      }

      public boolean whitelistHasPrefix(String str) {
         throw new IllegalArgumentException("Can only find prefixes of whole strings");
      }

      public boolean isBlacklisted(String str) {
         return super.isBlacklisted(JarUtils.leafName(str));
      }
   }

   public static class WhiteBlackListPrefix extends WhiteBlackList {
      public WhiteBlackListPrefix() {
      }

      public WhiteBlackListPrefix(char separatorChar) {
         super(separatorChar);
      }

      public void addToWhitelist(String str) {
         if (str.contains("*")) {
            throw new IllegalArgumentException("Cannot use a glob wildcard here: " + str);
         } else {
            if (this.whitelistPrefixesSet == null) {
               this.whitelistPrefixesSet = new HashSet();
            }

            this.whitelistPrefixesSet.add(str);
         }
      }

      public void addToBlacklist(String str) {
         if (str.contains("*")) {
            throw new IllegalArgumentException("Cannot use a glob wildcard here: " + str);
         } else {
            if (this.blacklistPrefixes == null) {
               this.blacklistPrefixes = new ArrayList();
            }

            this.blacklistPrefixes.add(str);
         }
      }

      public boolean isWhitelistedAndNotBlacklisted(String str) {
         boolean isWhitelisted = this.whitelistPrefixes == null;
         if (!isWhitelisted) {
            for(String prefix : this.whitelistPrefixes) {
               if (str.startsWith(prefix)) {
                  isWhitelisted = true;
                  break;
               }
            }
         }

         if (!isWhitelisted) {
            return false;
         } else {
            if (this.blacklistPrefixes != null) {
               for(String prefix : this.blacklistPrefixes) {
                  if (str.startsWith(prefix)) {
                     return false;
                  }
               }
            }

            return true;
         }
      }

      public boolean isWhitelisted(String str) {
         boolean isWhitelisted = this.whitelistPrefixes == null;
         if (!isWhitelisted) {
            for(String prefix : this.whitelistPrefixes) {
               if (str.startsWith(prefix)) {
                  isWhitelisted = true;
                  break;
               }
            }
         }

         return isWhitelisted;
      }

      public boolean whitelistHasPrefix(String str) {
         throw new IllegalArgumentException("Can only find prefixes of whole strings");
      }

      public boolean isBlacklisted(String str) {
         if (this.blacklistPrefixes != null) {
            for(String prefix : this.blacklistPrefixes) {
               if (str.startsWith(prefix)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public static class WhiteBlackListWholeString extends WhiteBlackList {
      public WhiteBlackListWholeString() {
      }

      public WhiteBlackListWholeString(char separatorChar) {
         super(separatorChar);
      }

      public void addToWhitelist(String str) {
         if (str.contains("*")) {
            if (this.whitelistGlobs == null) {
               this.whitelistGlobs = new HashSet();
               this.whitelistPatterns = new ArrayList();
            }

            this.whitelistGlobs.add(str);
            this.whitelistPatterns.add(globToPattern(str));
         } else {
            if (this.whitelist == null) {
               this.whitelist = new HashSet();
            }

            this.whitelist.add(str);
         }

         if (this.whitelistPrefixesSet == null) {
            this.whitelistPrefixesSet = new HashSet();
            this.whitelistPrefixesSet.add("");
            this.whitelistPrefixesSet.add("/");
         }

         String separator = Character.toString(this.separatorChar);
         String prefix = str;
         if (str.contains("*")) {
            prefix = str.substring(0, str.indexOf(42));
            int sepIdx = prefix.lastIndexOf(this.separatorChar);
            if (sepIdx < 0) {
               prefix = "";
            } else {
               prefix = prefix.substring(0, prefix.lastIndexOf(this.separatorChar));
            }
         }

         while(prefix.endsWith(separator)) {
            prefix = prefix.substring(0, prefix.length() - 1);
         }

         while(!prefix.isEmpty()) {
            this.whitelistPrefixesSet.add(prefix + this.separatorChar);
            prefix = FileUtils.getParentDirPath(prefix, this.separatorChar);
         }

      }

      public void addToBlacklist(String str) {
         if (str.contains("*")) {
            if (this.blacklistGlobs == null) {
               this.blacklistGlobs = new HashSet();
               this.blacklistPatterns = new ArrayList();
            }

            this.blacklistGlobs.add(str);
            this.blacklistPatterns.add(globToPattern(str));
         } else {
            if (this.blacklist == null) {
               this.blacklist = new HashSet();
            }

            this.blacklist.add(str);
         }

      }

      public boolean isWhitelistedAndNotBlacklisted(String str) {
         return this.isWhitelisted(str) && !this.isBlacklisted(str);
      }

      public boolean isWhitelisted(String str) {
         return this.whitelist == null && this.whitelistPatterns == null || this.whitelist != null && this.whitelist.contains(str) || WhiteBlackList.matchesPatternList(str, this.whitelistPatterns);
      }

      public boolean whitelistHasPrefix(String str) {
         return this.whitelistPrefixesSet == null ? false : this.whitelistPrefixesSet.contains(str);
      }

      public boolean isBlacklisted(String str) {
         return this.blacklist != null && this.blacklist.contains(str) || WhiteBlackList.matchesPatternList(str, this.blacklistPatterns);
      }
   }
}
