package nonapi.io.github.classgraph.utils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FastPathResolver {
   private static final Pattern percentMatcher = Pattern.compile("([%][0-9a-fA-F][0-9a-fA-F])+");
   private static final boolean WINDOWS = File.separatorChar == '\\';

   private FastPathResolver() {
   }

   private static void translateSeparator(String path, int startIdx, int endIdx, boolean stripFinalSeparator, StringBuilder buf) {
      for(int i = startIdx; i < endIdx; ++i) {
         char c = path.charAt(i);
         if (c != '\\' && c != '/') {
            buf.append(c);
         } else if (i < endIdx - 1 || !stripFinalSeparator) {
            char prevChar = buf.length() == 0 ? 0 : buf.charAt(buf.length() - 1);
            if (prevChar != '/') {
               buf.append('/');
            }
         }
      }

   }

   private static int hexCharToInt(char c) {
      return c >= '0' && c <= '9' ? c - 48 : (c >= 'a' && c <= 'f' ? c - 97 + 10 : c - 65 + 10);
   }

   private static void unescapePercentEncoding(String path, int startIdx, int endIdx, StringBuilder buf) {
      if (endIdx - startIdx == 3 && path.charAt(startIdx + 1) == '2' && path.charAt(startIdx + 2) == '0') {
         buf.append(' ');
      } else {
         byte[] bytes = new byte[(endIdx - startIdx) / 3];
         int i = startIdx;

         for(int j = 0; i < endIdx; ++j) {
            char c1 = path.charAt(i + 1);
            char c2 = path.charAt(i + 2);
            int digit1 = hexCharToInt(c1);
            int digit2 = hexCharToInt(c2);
            bytes[j] = (byte)(digit1 << 4 | digit2);
            i += 3;
         }

         String str = new String(bytes, StandardCharsets.UTF_8);
         str = str.replace("/", "%2F").replace("\\", "%5C");
         buf.append(str);
      }

   }

   public static String normalizePath(String path, boolean isFileOrJarURL) {
      boolean hasPercent = path.indexOf(37) >= 0;
      if (!hasPercent && path.indexOf(92) < 0 && !path.endsWith("/")) {
         return path;
      } else {
         int len = path.length();
         StringBuilder buf = new StringBuilder();
         if (hasPercent && isFileOrJarURL) {
            int prevEndMatchIdx = 0;

            int endMatchIdx;
            for(Matcher matcher = percentMatcher.matcher(path); matcher.find(); prevEndMatchIdx = endMatchIdx) {
               int startMatchIdx = matcher.start();
               endMatchIdx = matcher.end();
               translateSeparator(path, prevEndMatchIdx, startMatchIdx, false, buf);
               unescapePercentEncoding(path, startMatchIdx, endMatchIdx, buf);
            }

            translateSeparator(path, prevEndMatchIdx, len, true, buf);
            return buf.toString();
         } else {
            translateSeparator(path, 0, len, true, buf);
            return buf.toString();
         }
      }
   }

   public static String resolve(String resolveBasePath, String relativePath) {
      if (relativePath != null && !relativePath.isEmpty()) {
         String prefix = "";
         boolean isAbsolutePath = false;
         boolean isFileOrJarURL = false;
         int startIdx = 0;
         if (relativePath.regionMatches(true, startIdx, "jar:", 0, 4)) {
            startIdx += 4;
            isFileOrJarURL = true;
         }

         if (relativePath.regionMatches(true, startIdx, "http://", 0, 7)) {
            startIdx += 7;
            prefix = "http://";
            isAbsolutePath = true;
         } else if (relativePath.regionMatches(true, startIdx, "https://", 0, 8)) {
            startIdx += 8;
            prefix = "https://";
            isAbsolutePath = true;
         } else if (relativePath.regionMatches(true, startIdx, "jrt:", 0, 5)) {
            startIdx += 4;
            prefix = "jrt:";
            isAbsolutePath = true;
         } else if (relativePath.regionMatches(true, startIdx, "file:", 0, 5)) {
            startIdx += 5;
            if (WINDOWS) {
               if (!relativePath.startsWith("\\\\\\\\", startIdx) && !relativePath.startsWith("////", startIdx)) {
                  if (relativePath.startsWith("\\\\", startIdx)) {
                     startIdx += 2;
                  }
               } else {
                  startIdx += 4;
                  prefix = "//";
                  isAbsolutePath = true;
               }
            }

            if (relativePath.startsWith("//", startIdx)) {
               startIdx += 2;
            }

            isFileOrJarURL = true;
         } else if (WINDOWS && (relativePath.startsWith("//") || relativePath.startsWith("\\\\"))) {
            startIdx += 2;
            prefix = "//";
            isAbsolutePath = true;
         }

         if (WINDOWS) {
            if (relativePath.length() - startIdx > 2 && Character.isLetter(relativePath.charAt(startIdx)) && relativePath.charAt(startIdx + 1) == ':') {
               isAbsolutePath = true;
            } else if (relativePath.length() - startIdx > 3 && (relativePath.charAt(startIdx) == '/' || relativePath.charAt(startIdx) == '\\') && Character.isLetter(relativePath.charAt(startIdx + 1)) && relativePath.charAt(startIdx + 2) == ':') {
               isAbsolutePath = true;
               ++startIdx;
            }
         }

         if (relativePath.length() - startIdx > 1 && (relativePath.charAt(startIdx) == '/' || relativePath.charAt(startIdx) == '\\')) {
            isAbsolutePath = true;
         }

         String pathStr = normalizePath(startIdx == 0 ? relativePath : relativePath.substring(startIdx), isFileOrJarURL);
         if (!pathStr.equals("/")) {
            if (pathStr.endsWith("/")) {
               pathStr = pathStr.substring(0, pathStr.length() - 1);
            }

            if (pathStr.endsWith("!")) {
               pathStr = pathStr.substring(0, pathStr.length() - 1);
            }

            if (pathStr.endsWith("/")) {
               pathStr = pathStr.substring(0, pathStr.length() - 1);
            }

            if (pathStr.isEmpty()) {
               pathStr = "/";
            }
         }

         String pathResolved;
         if (!isAbsolutePath && resolveBasePath != null && !resolveBasePath.isEmpty()) {
            pathResolved = FileUtils.sanitizeEntryPath(resolveBasePath + (resolveBasePath.endsWith("/") ? "" : "/") + pathStr, false);
         } else {
            pathResolved = FileUtils.sanitizeEntryPath(pathStr, false);
         }

         return prefix.isEmpty() ? pathResolved : prefix + pathResolved;
      } else {
         return resolveBasePath == null ? "" : resolveBasePath;
      }
   }

   public static String resolve(String pathStr) {
      return resolve((String)null, pathStr);
   }
}
