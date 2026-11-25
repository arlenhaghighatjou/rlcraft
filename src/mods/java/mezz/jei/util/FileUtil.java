package mezz.jei.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class FileUtil {
   private FileUtil() {
   }

   public static boolean writeFileSafely(File file, FileUtil.FileOperation fileOperation) {
      File fileNew = new File(file.getAbsolutePath());
      File fileBackup = new File(file.getAbsolutePath() + ".bak");
      File fileTemp = new File(file.getAbsolutePath() + ".tmp");

      try {
         if (fileTemp.exists() && !fileTemp.delete()) {
            throw new IOException("Could not delete old temp file");
         } else {
            fileOperation.handle(fileTemp);
            if (file.exists()) {
               if (fileBackup.exists() && !fileBackup.delete()) {
                  throw new IOException("Could not delete old backup");
               }

               if (!file.renameTo(fileBackup)) {
                  throw new IOException("Could not backup file");
               }
            }

            if (!fileTemp.renameTo(fileNew)) {
               throw new IOException("Could not rename temp file");
            } else {
               if (fileBackup.exists() && !fileBackup.delete()) {
                  Log.get().error("Could not delete old backup file {}", fileBackup.getAbsoluteFile());
               }

               return true;
            }
         }
      } catch (IOException var6) {
         Log.get().error("Failed to save file {}.", fileNew, var6);
         if (fileBackup.exists()) {
            if ((!fileNew.exists() || fileNew.delete()) && fileBackup.renameTo(fileNew)) {
               Log.get().info("Restored file from backup.");
               return false;
            }

            Log.get().error("Failed to restore file from backup file {}", fileBackup.getAbsoluteFile());
         }

         return false;
      }
   }

   public static void readFileSafely(File file, FileUtil.FileOperation fileOperation) {
      if (!file.exists()) {
         File fileBackup = new File(file.getAbsolutePath() + ".bak");
         if (fileBackup.exists() && fileBackup.renameTo(file)) {
            Log.get().info("Restored file from backup.");
         }
      }

      if (file.exists()) {
         try {
            fileOperation.handle(file);
         } catch (IOException var3) {
            Log.get().error("Failed to read file {}.", file, var3);
         }
      }

   }

   public static void readZipFileSafely(File file, String zipEntryName, FileUtil.ZipInputFileOperation fileOperation) {
      readFileSafely(file, (file1) -> {
         ZipInputStream zipInput = new ZipInputStream(new FileInputStream(file1));
         if (getZipEntry(zipInput, zipEntryName)) {
            fileOperation.handle(zipInput);
            zipInput.close();
         }

      });
   }

   public static boolean writeZipFileSafely(File file, String zipEntryName, FileUtil.ZipOutputFileOperation fileOperation) {
      return writeFileSafely(file, (file1) -> {
         ZipOutputStream zipOutput = new ZipOutputStream(new FileOutputStream(file1));
         zipOutput.putNextEntry(new ZipEntry(zipEntryName));
         fileOperation.handle(zipOutput);
         zipOutput.closeEntry();
         zipOutput.close();
      });
   }

   public static boolean getZipEntry(ZipInputStream zipInputStream, String zipEntryName) throws IOException {
      while(true) {
         ZipEntry zipEntry = zipInputStream.getNextEntry();
         if (zipEntry != null) {
            if (!zipEntry.getName().equals(zipEntryName)) {
               continue;
            }

            return true;
         }

         return false;
      }
   }

   @FunctionalInterface
   public interface FileOperation {
      void handle(File var1) throws IOException;
   }

   @FunctionalInterface
   public interface ZipInputFileOperation {
      void handle(ZipInputStream var1);
   }

   @FunctionalInterface
   public interface ZipOutputFileOperation {
      void handle(ZipOutputStream var1);
   }
}
