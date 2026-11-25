package me.ichun.mods.ichunutil.common.core.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import me.ichun.mods.ichunutil.common.iChunUtil;

public class IOUtil {
   public static final int IDENTIFIER_LENGTH = 20;
   static final byte[] HEX_CHAR_TABLE = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};

   public IOUtil() {
   }

   public static boolean areBufferedImagesEqual(BufferedImage img1, BufferedImage img2) {
      if (img1 == null && img2 == null) {
         return true;
      } else if (img1 != null && img2 != null) {
         if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight()) {
            for(int x = 0; x < img1.getWidth(); ++x) {
               for(int y = 0; y < img1.getHeight(); ++y) {
                  if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                     return false;
                  }
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static void renameFilesToLowerCaseInDir(File dir) {
      File[] files = dir.listFiles();

      for(File file : files) {
         if (file.isDirectory()) {
            renameFilesToLowerCaseInDir(file);
         } else if (!file.getName().equals(file.getName().toLowerCase(Locale.ROOT))) {
            String name = file.getName().toLowerCase();
            File newFile = new File(dir, name);
            if (file.renameTo(newFile)) {
               System.out.println("Renaming " + file.getAbsolutePath() + " to " + newFile.getAbsolutePath());
            } else {
               System.out.println("Failed to rename " + file.getAbsolutePath() + " to " + newFile.getAbsolutePath());
            }
         }
      }

   }

   private static byte[] createChecksum(File filename) throws Exception {
      InputStream fis = new FileInputStream(filename);
      byte[] buffer = new byte[1024];
      MessageDigest complete = MessageDigest.getInstance("MD5");

      while(true) {
         int numRead = fis.read(buffer);
         if (numRead > 0) {
            complete.update(buffer, 0, numRead);
         }

         if (numRead == -1) {
            break;
         }
      }

      fis.close();
      return complete.digest();
   }

   public static String getMD5Checksum(File file) {
      try {
         byte[] b = createChecksum(file);
         String hex = getHexString(b);
         return hex;
      } catch (Exception var3) {
         iChunUtil.LOGGER.warn("Failed to generate MD5 checksum for " + file.getName());
         return null;
      }
   }

   private static String getHexString(byte[] raw) throws UnsupportedEncodingException {
      byte[] hex = new byte[2 * raw.length];
      int index = 0;

      for(byte b : raw) {
         int v = b & 255;
         hex[index++] = HEX_CHAR_TABLE[v >>> 4];
         hex[index++] = HEX_CHAR_TABLE[v & 15];
      }

      return new String(hex, "ASCII");
   }

   public static String readableFileSize(long size) {
      if (size <= 0L) {
         return "0";
      } else {
         String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
         int digitGroups = (int)(Math.log10((double)size) / Math.log10(1024.0D));
         return (new DecimalFormat("#,##0.#")).format((double)size / Math.pow(1024.0D, (double)digitGroups)) + " " + units[digitGroups];
      }
   }

   public static byte[] compress(String string) throws IOException {
      ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
      GZIPOutputStream gos = new GZIPOutputStream(os);
      gos.write(string.getBytes());
      gos.close();
      byte[] compressed = os.toByteArray();
      os.close();
      return compressed;
   }

   public static String decompress(byte[] compressed) throws IOException {
      int BUFFER_SIZE = 32;
      ByteArrayInputStream is = new ByteArrayInputStream(compressed);
      GZIPInputStream gis = new GZIPInputStream(is, 32);
      StringBuilder string = new StringBuilder();
      byte[] data = new byte[32];

      int bytesRead;
      while((bytesRead = gis.read(data)) != -1) {
         string.append(new String(data, 0, bytesRead));
      }

      gis.close();
      is.close();
      return string.toString();
   }
}
