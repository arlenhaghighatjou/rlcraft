package xzeroair.trinkets.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.annotation.Nonnull;

public class Utils {
   public static Gson gson = (new GsonBuilder()).setPrettyPrinting().excludeFieldsWithModifiers(new int[]{2}).create();

   public Utils() {
   }

   public static File getFileLocation(String parent, String file) {
      File newInstance = new File(parent, file);
      File parentDirectory = newInstance.getParentFile();
      File fileReturn = parentDirectory.exists() ? newInstance : (parentDirectory.mkdirs() ? newInstance : null);
      return fileReturn;
   }

   public static Type getClassTypeToken(Class<?> object) {
      return TypeToken.get(object).getType();
   }

   public static <T> Type getClassTypeToken(T object) {
      return getClassTypeToken(object.getClass());
   }

   public static <T> Type getClassTypeTokenParameterized(Class<?> classObj, Class<?>... typeObj) {
      return TypeToken.getParameterized(classObj, typeObj).getType();
   }

   public static <T> T loadJson(File file, Type type) {
      try {
         FileReader reader = new FileReader(file);
         T loadedClass = (T)gson.fromJson(reader, type);
         reader.close();
         return loadedClass;
      } catch (FileNotFoundException var4) {
         System.out.println("Failed to find file: " + file.getPath());
      } catch (IOException var5) {
         var5.printStackTrace();
      }

      return (T)null;
   }

   public static <T> void saveJson(File file, T object, Type type) {
      try {
         FileWriter writer = new FileWriter(file);
         String jsonString = gson.toJson(object, type);
         writer.write(jsonString);
         writer.close();
      } catch (IOException var5) {
         var5.printStackTrace();
      }

   }

   public static <T> void saveJson(File file, T object) {
      Type type = getClassTypeToken(object);
      saveJson(file, object, type);
   }

   public static byte[] getDataFromURL(String url) {
      if (url != null && !url.isEmpty()) {
         try {
            HttpURLConnection http = (HttpURLConnection)(new URL(url)).openConnection();
            http.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            http.connect();
            InputStream stream = http.getInputStream();
            byte[] output = readAllBytes(stream);
            stream.close();
            http.disconnect();
            return output;
         } catch (FileNotFoundException var4) {
            var4.printStackTrace();
         } catch (IOException var5) {
            var5.printStackTrace();
         }
      }

      return new byte[0];
   }

   public static byte[] getDataFromFile(File file) {
      if (file != null) {
         try {
            InputStream stream = new FileInputStream(file);
            byte[] output = readAllBytes(stream);
            stream.close();
            return output;
         } catch (FileNotFoundException var3) {
            var3.printStackTrace();
         } catch (IOException var4) {
            var4.printStackTrace();
         }
      }

      return new byte[0];
   }

   @Nonnull
   public static byte[] readAllBytes(@Nonnull InputStream stream) throws IOException {
      int count = 0;
      int pos = 0;
      byte[] output = new byte[0];
      byte[] buf = new byte[1024];

      while((count = stream.read(buf)) > 0) {
         if (pos + count >= output.length) {
            byte[] tmp = output;
            output = new byte[pos + count];
            System.arraycopy(tmp, 0, output, 0, tmp.length);
         }

         for(int i = 0; i < count; ++i) {
            output[pos++] = buf[i];
         }
      }

      return output;
   }
}
