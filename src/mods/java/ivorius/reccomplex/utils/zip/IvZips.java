package ivorius.reccomplex.utils.zip;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class IvZips {
   public IvZips() {
   }

   public static void walkStreams(ZipInputStream zipInputStream, IvZips.Consumer<InputStream> consumer) throws IOException {
      ZipEntry zipEntry;
      while((zipEntry = zipInputStream.getNextEntry()) != null) {
         consumer.accept(zipEntry.getName(), zipInputStream);
         zipInputStream.closeEntry();
      }

      zipInputStream.close();
   }

   public static void addZipEntry(ZipOutputStream zip, String path, byte[] bytes) throws IOException {
      ZipEntry jsonEntry = new ZipEntry(path);
      zip.putNextEntry(jsonEntry);
      jsonEntry.setSize((long)bytes.length);
      zip.write(bytes);
      zip.closeEntry();
   }

   @FunctionalInterface
   public interface Consumer<T> {
      void accept(String var1, T var2) throws IOException;
   }
}
