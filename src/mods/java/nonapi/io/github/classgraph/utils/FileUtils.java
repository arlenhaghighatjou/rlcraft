package nonapi.io.github.classgraph.utils;

import io.github.classgraph.ClassGraphException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

public final class FileUtils {
   private static Method cleanMethod;
   private static Method attachmentMethod;
   private static Object theUnsafe;
   public static final int FILECHANNEL_FILE_SIZE_THRESHOLD;
   public static final String CURR_DIR_PATH;
   private static final int DEFAULT_BUFFER_SIZE = 16384;
   public static final int MAX_BUFFER_SIZE = 2147483639;
   private static final int MAX_INITIAL_BUFFER_SIZE = 16777216;

   private FileUtils() {
   }

   private static SimpleEntry<byte[], Integer> readAllBytes(InputStream inputStream, long fileSizeHint) throws IOException {
      if (fileSizeHint > 2147483639L) {
         throw new IOException("InputStream is too large to read");
      } else {
         int bufferSize = fileSizeHint < 1L ? 16384 : Math.min((int)fileSizeHint, 16777216);
         byte[] buf = new byte[bufferSize];
         int bufLength = buf.length;
         int totBytesRead = 0;

         while(true) {
            int bytesRead;
            for(; (bytesRead = inputStream.read(buf, totBytesRead, bufLength - totBytesRead)) <= 0; buf = Arrays.copyOf(buf, bufLength)) {
               if (bytesRead < 0) {
                  return new SimpleEntry(bufLength == totBytesRead ? buf : Arrays.copyOf(buf, totBytesRead), totBytesRead);
               }

               if (bufLength <= 2147483639 - bufLength) {
                  bufLength <<= 1;
               } else {
                  if (bufLength == 2147483639) {
                     throw new IOException("InputStream too large to read");
                  }

                  bufLength = 2147483639;
               }
            }

            totBytesRead += bytesRead;
         }
      }
   }

   public static byte[] readAllBytesAsArray(InputStream inputStream, long fileSize) throws IOException {
      SimpleEntry<byte[], Integer> ent = readAllBytes(inputStream, fileSize);
      byte[] buf = (byte[])ent.getKey();
      int bufBytesUsed = ((Integer)ent.getValue()).intValue();
      return buf.length == bufBytesUsed ? buf : Arrays.copyOf(buf, bufBytesUsed);
   }

   public static ByteBuffer readAllBytesAsByteBuffer(InputStream inputStream, long fileSizeHint) throws IOException {
      return ByteBuffer.wrap(readAllBytesAsArray(inputStream, fileSizeHint));
   }

   public static String readAllBytesAsString(InputStream inputStream, long fileSizeHint) throws IOException {
      SimpleEntry<byte[], Integer> ent = readAllBytes(inputStream, fileSizeHint);
      byte[] buf = (byte[])ent.getKey();
      int bufBytesUsed = ((Integer)ent.getValue()).intValue();
      return new String(buf, 0, bufBytesUsed, StandardCharsets.UTF_8);
   }

   public static InputStream byteBufferToInputStream(final ByteBuffer byteBuffer) {
      return new InputStream() {
         final ByteBuffer buf = byteBuffer;

         public int read() {
            return !this.buf.hasRemaining() ? -1 : this.buf.get() & 255;
         }

         public int read(byte[] bytes, int off, int len) {
            if (!this.buf.hasRemaining()) {
               return -1;
            } else {
               int bytesRead = Math.min(len, this.buf.remaining());
               this.buf.get(bytes, off, bytesRead);
               return bytesRead;
            }
         }
      };
   }

   public static String sanitizeEntryPath(String path, boolean removeInitialSlash) {
      if (path.isEmpty()) {
         return "";
      } else {
         boolean foundSegmentToSanitize = false;
         int lastSepIdx = -1;
         char prevC = 0;

         for(int i = 0; i < path.length() + 1; ++i) {
            char c = i == path.length() ? 0 : path.charAt(i);
            if (c == '/' || c == '!' || c == 0) {
               int segmentLength = i - (lastSepIdx + 1);
               if (segmentLength == 0 && prevC == c || segmentLength == 1 && path.charAt(i - 1) == '.' || segmentLength == 2 && path.charAt(i - 2) == '.' && path.charAt(i - 1) == '.') {
                  foundSegmentToSanitize = true;
               }

               lastSepIdx = i;
            }

            prevC = c;
         }

         String pathSanitized = path;
         if (foundSegmentToSanitize) {
            List<List<String>> allSectionSegments = new ArrayList();
            List<String> currSectionSegments = new ArrayList();
            allSectionSegments.add(currSectionSegments);
            int lastSepIdx = -1;

            for(int i = 0; i < path.length() + 1; ++i) {
               char c = i == path.length() ? 0 : path.charAt(i);
               if (c == '/' || c == '!' || c == 0) {
                  String segment = path.substring(lastSepIdx + 1, i);
                  if (!segment.equals(".") && !segment.isEmpty()) {
                     if (segment.equals("..")) {
                        if (!currSectionSegments.isEmpty()) {
                           currSectionSegments.remove(currSectionSegments.size() - 1);
                        }
                     } else {
                        currSectionSegments.add(segment);
                     }
                  }

                  if (c == '!' && !currSectionSegments.isEmpty()) {
                     currSectionSegments = new ArrayList();
                     allSectionSegments.add(currSectionSegments);
                  }

                  lastSepIdx = i;
               }
            }

            StringBuilder buf = new StringBuilder();

            for(List<String> sectionSegments : allSectionSegments) {
               if (!sectionSegments.isEmpty()) {
                  if (buf.length() > 0) {
                     buf.append('!');
                  }

                  for(String sectionSegment : sectionSegments) {
                     buf.append('/');
                     buf.append(sectionSegment);
                  }
               }
            }

            pathSanitized = buf.toString();
            if (pathSanitized.isEmpty() && path.startsWith("/")) {
               pathSanitized = "/";
            }
         }

         if (removeInitialSlash || !path.startsWith("/")) {
            while(pathSanitized.startsWith("/")) {
               pathSanitized = pathSanitized.substring(1);
            }
         }

         return pathSanitized;
      }
   }

   public static boolean isClassfile(String path) {
      int len = path.length();
      return len > 6 && path.regionMatches(true, len - 6, ".class", 0, 6);
   }

   public static boolean canRead(File file) {
      try {
         return file.canRead();
      } catch (SecurityException var2) {
         return false;
      }
   }

   public static boolean canReadAndIsFile(File file) {
      try {
         if (!file.canRead()) {
            return false;
         }
      } catch (SecurityException var2) {
         return false;
      }

      return file.isFile();
   }

   public static void checkCanReadAndIsFile(File file) throws IOException {
      try {
         if (!file.canRead()) {
            throw new FileNotFoundException("File does not exist or cannot be read: " + file);
         }
      } catch (SecurityException var2) {
         throw new FileNotFoundException("File " + file + " cannot be accessed: " + var2);
      }

      if (!file.isFile()) {
         throw new IOException("Not a regular file: " + file);
      }
   }

   public static boolean canReadAndIsDir(File file) {
      try {
         if (!file.canRead()) {
            return false;
         }
      } catch (SecurityException var2) {
         return false;
      }

      return file.isDirectory();
   }

   public static void checkCanReadAndIsDir(File file) throws IOException {
      try {
         if (!file.canRead()) {
            throw new FileNotFoundException("Directory does not exist or cannot be read: " + file);
         }
      } catch (SecurityException var2) {
         throw new FileNotFoundException("File " + file + " cannot be accessed: " + var2);
      }

      if (!file.isDirectory()) {
         throw new IOException("Not a directory: " + file);
      }
   }

   public static String getParentDirPath(String path, char separator) {
      int lastSlashIdx = path.lastIndexOf(separator);
      return lastSlashIdx <= 0 ? "" : path.substring(0, lastSlashIdx);
   }

   public static String getParentDirPath(String path) {
      return getParentDirPath(path, '/');
   }

   private static void lookupCleanMethodPrivileged() {
      if (VersionFinder.JAVA_MAJOR_VERSION < 9) {
         try {
            cleanMethod = Class.forName("sun.misc.Cleaner").getMethod("clean");
            cleanMethod.setAccessible(true);
            Class<?> directByteBufferClass = Class.forName("sun.nio.ch.DirectBuffer");
            attachmentMethod = directByteBufferClass.getMethod("attachment");
            attachmentMethod.setAccessible(true);
         } catch (SecurityException var5) {
            throw ClassGraphException.newClassGraphException("You need to grant classgraph RuntimePermission(\"accessClassInPackage.sun.misc\") and ReflectPermission(\"suppressAccessChecks\")");
         } catch (LinkageError | ReflectiveOperationException var6) {
            ;
         }
      } else {
         try {
            Class<?> unsafeClass;
            try {
               unsafeClass = Class.forName("sun.misc.Unsafe");
            } catch (LinkageError | ReflectiveOperationException var2) {
               unsafeClass = Class.forName("jdk.internal.misc.Unsafe");
            }

            cleanMethod = unsafeClass.getMethod("invokeCleaner", ByteBuffer.class);
            cleanMethod.setAccessible(true);
            Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
            theUnsafeField.setAccessible(true);
            theUnsafe = theUnsafeField.get((Object)null);
         } catch (SecurityException var3) {
            throw ClassGraphException.newClassGraphException("You need to grant classgraph RuntimePermission(\"accessClassInPackage.sun.misc\"), RuntimePermission(\"accessClassInPackage.jdk.internal.misc\") and ReflectPermission(\"suppressAccessChecks\")");
         } catch (LinkageError | ReflectiveOperationException var4) {
            ;
         }
      }

   }

   private static boolean closeDirectByteBufferPrivileged(ByteBuffer byteBuffer, LogNode log) {
      try {
         if (cleanMethod == null) {
            if (log != null) {
               log.log("Could not unmap ByteBuffer, cleanMethod == null");
            }

            return false;
         } else if (VersionFinder.JAVA_MAJOR_VERSION >= 9) {
            if (theUnsafe == null) {
               if (log != null) {
                  log.log("Could not unmap ByteBuffer, theUnsafe == null");
               }

               return false;
            } else {
               try {
                  cleanMethod.invoke(theUnsafe, byteBuffer);
                  return true;
               } catch (IllegalArgumentException var5) {
                  return false;
               }
            }
         } else if (attachmentMethod == null) {
            if (log != null) {
               log.log("Could not unmap ByteBuffer, attachmentMethod == null");
            }

            return false;
         } else if (attachmentMethod.invoke(byteBuffer) != null) {
            return false;
         } else {
            Method cleaner = byteBuffer.getClass().getMethod("cleaner");
            if (cleaner == null) {
               if (log != null) {
                  log.log("Could not unmap ByteBuffer, cleaner == null");
               }

               return false;
            } else {
               try {
                  cleaner.setAccessible(true);
               } catch (Exception var7) {
                  if (log != null) {
                     log.log("Could not unmap ByteBuffer, cleaner.setAccessible(true) failed");
                  }

                  return false;
               }

               Object cleanerResult = cleaner.invoke(byteBuffer);
               if (cleanerResult == null) {
                  if (log != null) {
                     log.log("Could not unmap ByteBuffer, cleanerResult == null");
                  }

                  return false;
               } else {
                  try {
                     cleanMethod.invoke(cleaner.invoke(byteBuffer));
                     return true;
                  } catch (Exception var6) {
                     if (log != null) {
                        log.log("Could not unmap ByteBuffer, cleanMethod.invoke(cleanerResult) failed: " + var6);
                     }

                     return false;
                  }
               }
            }
         }
      } catch (SecurityException | ReflectiveOperationException var8) {
         if (log != null) {
            log.log("Could not unmap ByteBuffer: " + var8);
         }

         return false;
      }
   }

   public static boolean closeDirectByteBuffer(final ByteBuffer byteBuffer, final LogNode log) {
      return byteBuffer != null && byteBuffer.isDirect() ? ((Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            return FileUtils.closeDirectByteBufferPrivileged(byteBuffer, log);
         }
      })).booleanValue() : false;
   }

   static {
      String currDirPathStr = "";

      try {
         Path currDirPath = Paths.get("").toAbsolutePath();
         currDirPathStr = currDirPath.toString();
         currDirPath = currDirPath.normalize();
         currDirPathStr = currDirPath.toString();
         currDirPath = currDirPath.toRealPath(LinkOption.NOFOLLOW_LINKS);
         currDirPathStr = currDirPath.toString();
         currDirPathStr = FastPathResolver.resolve(currDirPathStr);
      } catch (IOException var2) {
         throw ClassGraphException.newClassGraphException("Could not resolve current directory: " + currDirPathStr, var2);
      }

      CURR_DIR_PATH = currDirPathStr;
      switch(VersionFinder.OS) {
      case Linux:
      case MacOSX:
      case Solaris:
      case BSD:
      case Unix:
         FILECHANNEL_FILE_SIZE_THRESHOLD = 16384;
         break;
      case Windows:
         FILECHANNEL_FILE_SIZE_THRESHOLD = -1;
         break;
      case Unknown:
      default:
         FILECHANNEL_FILE_SIZE_THRESHOLD = 16384;
      }

      AccessController.doPrivileged(new PrivilegedAction<Object>() {
         public Object run() {
            FileUtils.lookupCleanMethodPrivileged();
            return null;
         }
      });
   }
}
