package nonapi.io.github.classgraph.utils;

import java.nio.charset.StandardCharsets;

public final class URLPathEncoder {
   private static boolean[] safe = new boolean[256];
   private static final char[] HEXADECIMAL;
   private static final String[] SCHEME_PREFIXES;

   private URLPathEncoder() {
   }

   public static String encodePath(String path) {
      int validColonPrefixLen = 0;

      for(String scheme : SCHEME_PREFIXES) {
         if (path.startsWith(scheme)) {
            validColonPrefixLen = scheme.length();
            break;
         }
      }

      byte[] pathBytes = path.getBytes(StandardCharsets.UTF_8);
      StringBuilder encodedPath = new StringBuilder(pathBytes.length * 3);

      for(int i = 0; i < pathBytes.length; ++i) {
         byte pathByte = pathBytes[i];
         int b = pathByte & 255;
         if (!safe[b] && (b != 58 || i >= validColonPrefixLen)) {
            encodedPath.append('%');
            encodedPath.append(HEXADECIMAL[(b & 240) >> 4]);
            encodedPath.append(HEXADECIMAL[b & 15]);
         } else {
            encodedPath.append((char)b);
         }
      }

      return encodedPath.toString();
   }

   public static String normalizeURLPath(String urlPath) {
      String urlPathNormalized = urlPath;
      if (!urlPath.startsWith("jrt:") && !urlPath.startsWith("http://") && !urlPath.startsWith("https://")) {
         urlPathNormalized = urlPath.replace("/!", "!").replace("!/", "!").replace("!", "!/");
         if (!urlPathNormalized.startsWith("file:") && !urlPathNormalized.startsWith("jar:")) {
            urlPathNormalized = "file:" + urlPathNormalized;
         }

         if (urlPathNormalized.contains("!") && !urlPathNormalized.startsWith("jar:")) {
            urlPathNormalized = "jar:" + urlPathNormalized;
         }
      }

      return encodePath(urlPathNormalized);
   }

   static {
      for(int i = 97; i <= 122; ++i) {
         safe[i] = true;
      }

      for(int i = 65; i <= 90; ++i) {
         safe[i] = true;
      }

      for(int i = 48; i <= 57; ++i) {
         safe[i] = true;
      }

      safe[36] = safe[45] = safe[95] = safe[46] = safe[43] = true;
      safe[33] = safe[42] = safe[39] = safe[40] = safe[41] = safe[44] = true;
      safe[47] = true;
      HEXADECIMAL = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
      SCHEME_PREFIXES = new String[]{"jrt:", "file:", "jar:file:", "jar:", "http:", "https:"};
   }
}
