package com.teamacronymcoders.base.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressionHelper {
   public CompressionHelper() {
   }

   public static byte[] compressStringToByteArray(String uncompressedString) {
      try {
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
         gzipOutputStream.write(uncompressedString.getBytes("UTF-8"));
         gzipOutputStream.close();
         return byteArrayOutputStream.toByteArray();
      } catch (Exception var3) {
         var3.printStackTrace();
         return new byte[0];
      }
   }

   public static String decompressStringFromByteArray(byte[] compressedString) {
      try {
         StringBuilder stringBuilder = new StringBuilder();
         GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(compressedString));
         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gzipInputStream, "UTF-8"));

         String line;
         while((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
         }

         return stringBuilder.toString();
      } catch (Exception var5) {
         var5.printStackTrace();
         return "";
      }
   }
}
