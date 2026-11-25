package com.bloodnbonesgaming.lib.util;

import com.bloodnbonesgaming.lib.BNBGamingLib;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.Stream;
import org.apache.commons.lang3.SystemUtils;

public class FileHelper {
   public FileHelper() {
   }

   public static BufferedReader openReader(File file) throws FileNotFoundException {
      return new BufferedReader(new FileReader(file));
   }

   public static BufferedWriter openWriter(File file) throws IOException {
      return new BufferedWriter(new FileWriter(file));
   }

   public static void writeText(String fileName, String text) {
      writeText(new File(fileName), text);
   }

   public static void writeText(File file, String text) {
      file.getParentFile().mkdirs();

      try {
         BufferedWriter writer = openWriter(file);
         Throwable var3 = null;

         try {
            writer.write(text);
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (writer != null) {
               if (var3 != null) {
                  try {
                     writer.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  writer.close();
               }
            }

         }
      } catch (Exception var15) {
         BNBGamingLib.libInstance.getLog().error("Failed to write file " + file.getName());
         var15.printStackTrace();
      }

   }

   public static void copyDirectoryFromJar(Class<?> classInJar, String jarDirectory, String destinationFolder) {
      String path = classInJar.getProtectionDomain().getCodeSource().getLocation().getPath();

      try {
         String decodedPath = URLDecoder.decode(path, "UTF-8");
         String[] split = decodedPath.split("!");

         try {
            FileSystem fileSystem = FileSystems.newFileSystem(Paths.get(split[0].substring(SystemUtils.IS_OS_WINDOWS ? 6 : 5)), classInJar.getClassLoader());
            Throwable var7 = null;

            try {
               Path jarPath = fileSystem.getPath(jarDirectory);
               iteratePath(classInJar, jarPath, jarDirectory, destinationFolder);
            } catch (Throwable var18) {
               var7 = var18;
               throw var18;
            } finally {
               if (fileSystem != null) {
                  if (var7 != null) {
                     try {
                        fileSystem.close();
                     } catch (Throwable var17) {
                        var7.addSuppressed(var17);
                     }
                  } else {
                     fileSystem.close();
                  }
               }

            }
         } catch (IOException var20) {
            var20.printStackTrace();
         }
      } catch (UnsupportedEncodingException var21) {
         var21.printStackTrace();
      }

   }

   private static void iteratePath(Class<?> classInJar, Path jarPath, String jarDirectory, String destinationFolder) {
      try {
         Stream<Path> walk = Files.walk(jarPath, Integer.MAX_VALUE, new FileVisitOption[0]);
         Throwable var5 = null;

         try {
            Iterator<Path> it = walk.iterator();

            while(it.hasNext()) {
               Path path = (Path)it.next();
               if (!Files.isDirectory(path, new LinkOption[0])) {
                  readWrite(classInJar, path.toString(), jarDirectory, destinationFolder);
               }
            }
         } catch (Throwable var16) {
            var5 = var16;
            throw var16;
         } finally {
            if (walk != null) {
               if (var5 != null) {
                  try {
                     walk.close();
                  } catch (Throwable var15) {
                     var5.addSuppressed(var15);
                  }
               } else {
                  walk.close();
               }
            }

         }
      } catch (IOException var18) {
         var18.printStackTrace();
      }

   }

   private static void readWrite(Class<?> classInJar, String jarPosition, String jarDirectory, String destinationFolder) {
      File end = new File(destinationFolder.concat(jarPosition.substring(jarDirectory.length())));

      try {
         end.getParentFile().mkdirs();
         end.createNewFile();
         byte[] buffer = new byte[4096];
         InputStream stream = classInJar.getResourceAsStream(jarPosition);
         Throwable var8 = null;

         try {
            OutputStream outStream = new FileOutputStream(end);
            Throwable var10 = null;

            try {
               int readBytes;
               try {
                  while((readBytes = stream.read(buffer)) > 0) {
                     outStream.write(buffer, 0, readBytes);
                  }
               } catch (Throwable var35) {
                  var10 = var35;
                  throw var35;
               }
            } finally {
               if (outStream != null) {
                  if (var10 != null) {
                     try {
                        outStream.close();
                     } catch (Throwable var34) {
                        var10.addSuppressed(var34);
                     }
                  } else {
                     outStream.close();
                  }
               }

            }
         } catch (Throwable var37) {
            var8 = var37;
            throw var37;
         } finally {
            if (stream != null) {
               if (var8 != null) {
                  try {
                     stream.close();
                  } catch (Throwable var33) {
                     var8.addSuppressed(var33);
                  }
               } else {
                  stream.close();
               }
            }

         }
      } catch (IOException var39) {
         var39.printStackTrace();
      }

   }
}
