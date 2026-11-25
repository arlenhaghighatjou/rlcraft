package nonapi.io.github.classgraph.utils;

import io.github.classgraph.ClassGraphException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JarUtils {
   private static final Pattern DASH_VERSION = Pattern.compile("-(\\d+(\\.|$))");
   private static final Pattern NON_ALPHANUM = Pattern.compile("[^A-Za-z0-9]");
   private static final Pattern REPEATING_DOTS = Pattern.compile("(\\.)(\\1)+");
   private static final Pattern LEADING_DOTS = Pattern.compile("^\\.");
   private static final Pattern TRAILING_DOTS = Pattern.compile("\\.$");
   private static final String[] UNIX_NON_PATH_SEPARATORS = new String[]{"jar:", "file:", "http://", "https://", "\\:"};
   private static final int[] UNIX_NON_PATH_SEPARATOR_COLON_POSITIONS = new int[UNIX_NON_PATH_SEPARATORS.length];

   private JarUtils() {
   }

   public static String[] smartPathSplit(String pathStr) {
      return smartPathSplit(pathStr, File.pathSeparatorChar);
   }

   public static String[] smartPathSplit(String pathStr, char separatorChar) {
      if (pathStr != null && !pathStr.isEmpty()) {
         if (separatorChar != ':') {
            List<String> partsFiltered = new ArrayList();

            for(String part : pathStr.split(String.valueOf(separatorChar))) {
               String partFiltered = part.trim();
               if (!partFiltered.isEmpty()) {
                  partsFiltered.add(partFiltered);
               }
            }

            return (String[])partsFiltered.toArray(new String[0]);
         } else {
            Set<Integer> splitPoints = new HashSet();
            int i = -1;

            while(true) {
               boolean foundNonPathSeparator = false;

               for(int j = 0; j < UNIX_NON_PATH_SEPARATORS.length; ++j) {
                  int startIdx = i - UNIX_NON_PATH_SEPARATOR_COLON_POSITIONS[j];
                  if (pathStr.regionMatches(true, startIdx, UNIX_NON_PATH_SEPARATORS[j], 0, UNIX_NON_PATH_SEPARATORS[j].length()) && (startIdx == 0 || pathStr.charAt(startIdx - 1) == ':')) {
                     foundNonPathSeparator = true;
                     break;
                  }
               }

               if (!foundNonPathSeparator) {
                  splitPoints.add(Integer.valueOf(i));
               }

               i = pathStr.indexOf(58, i + 1);
               if (i < 0) {
                  break;
               }
            }

            splitPoints.add(Integer.valueOf(pathStr.length()));
            Object var10 = new ArrayList(splitPoints);
            CollectionUtils.sortIfNotEmpty(splitPointsSorted);
            ArrayList var12 = new ArrayList();

            for(int var14 = 1; var14 < ((List)var10).size(); ++var14) {
               int idx0 = ((Integer)((List)var10).get(var14 - 1)).intValue();
               int idx1 = ((Integer)((List)var10).get(var14)).intValue();
               String part = pathStr.substring(idx0 + 1, idx1).trim();
               part = part.replaceAll("\\\\:", ":");
               if (!part.isEmpty()) {
                  var12.add(part);
               }
            }

            return (String[])var12.toArray(new String[0]);
         }
      } else {
         return new String[0];
      }
   }

   private static void appendPathElt(Object pathElt, StringBuilder buf) {
      if (buf.length() > 0) {
         buf.append(File.pathSeparatorChar);
      }

      String path = File.separatorChar == '\\' ? pathElt.toString() : pathElt.toString().replaceAll(File.pathSeparator, "\\" + File.pathSeparator);
      buf.append(path);
   }

   public static String pathElementsToPathStr(Object... pathElts) {
      StringBuilder buf = new StringBuilder();

      for(Object pathElt : pathElts) {
         appendPathElt(pathElt, buf);
      }

      return buf.toString();
   }

   public static String pathElementsToPathStr(Iterable<?> pathElts) {
      StringBuilder buf = new StringBuilder();

      for(Object pathElt : pathElts) {
         appendPathElt(pathElt, buf);
      }

      return buf.toString();
   }

   public static String leafName(String path) {
      int bangIdx = path.indexOf(33);
      int endIdx = bangIdx >= 0 ? bangIdx : path.length();
      int leafStartIdx = 1 + (File.separatorChar == '/' ? path.lastIndexOf(47, endIdx) : Math.max(path.lastIndexOf(47, endIdx), path.lastIndexOf(File.separatorChar, endIdx)));
      int sepIdx = path.indexOf("---");
      if (sepIdx >= 0) {
         sepIdx += "---".length();
      }

      leafStartIdx = Math.max(leafStartIdx, sepIdx);
      leafStartIdx = Math.min(leafStartIdx, endIdx);
      return path.substring(leafStartIdx, endIdx);
   }

   public static String classfilePathToClassName(String classfilePath) {
      if (!classfilePath.endsWith(".class")) {
         throw new IllegalArgumentException("Classfile path does not end with \".class\": " + classfilePath);
      } else {
         return classfilePath.substring(0, classfilePath.length() - 6).replace('/', '.');
      }
   }

   public static String classNameToClassfilePath(String className) {
      return className.replace('.', '/') + ".class";
   }

   public static String derivedAutomaticModuleName(String jarPath) {
      int endIdx = jarPath.length();
      int lastPlingIdx = jarPath.lastIndexOf(33);
      if (lastPlingIdx > 0 && jarPath.lastIndexOf(46) <= Math.max(lastPlingIdx, jarPath.lastIndexOf(47))) {
         endIdx = lastPlingIdx;
      }

      int secondToLastPlingIdx = endIdx == 0 ? -1 : jarPath.lastIndexOf("!", endIdx - 1);
      int startIdx = Math.max(secondToLastPlingIdx, jarPath.lastIndexOf(47, endIdx - 1)) + 1;
      int lastDotBeforeLastPlingIdx = jarPath.lastIndexOf(46, endIdx - 1);
      if (lastDotBeforeLastPlingIdx > startIdx) {
         endIdx = lastDotBeforeLastPlingIdx;
      }

      String moduleName = jarPath.substring(startIdx, endIdx);
      Matcher matcher = DASH_VERSION.matcher(moduleName);
      if (matcher.find()) {
         moduleName = moduleName.substring(0, matcher.start());
      }

      moduleName = NON_ALPHANUM.matcher(moduleName).replaceAll(".");
      moduleName = REPEATING_DOTS.matcher(moduleName).replaceAll(".");
      if (moduleName.length() > 0 && moduleName.charAt(0) == '.') {
         moduleName = LEADING_DOTS.matcher(moduleName).replaceAll("");
      }

      int len = moduleName.length();
      if (len > 0 && moduleName.charAt(len - 1) == '.') {
         moduleName = TRAILING_DOTS.matcher(moduleName).replaceAll("");
      }

      return moduleName;
   }

   static {
      for(int i = 0; i < UNIX_NON_PATH_SEPARATORS.length; ++i) {
         UNIX_NON_PATH_SEPARATOR_COLON_POSITIONS[i] = UNIX_NON_PATH_SEPARATORS[i].indexOf(58);
         if (UNIX_NON_PATH_SEPARATOR_COLON_POSITIONS[i] < 0) {
            throw ClassGraphException.newClassGraphException("Could not find ':' in \"" + UNIX_NON_PATH_SEPARATORS[i] + "\"");
         }
      }

   }
}
