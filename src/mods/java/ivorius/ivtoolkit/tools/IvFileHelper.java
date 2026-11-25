package ivorius.ivtoolkit.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import net.minecraft.util.ResourceLocation;

public class IvFileHelper {
   public IvFileHelper() {
   }

   public static InputStream inputStreamFromResourceLocation(ResourceLocation resourceLocation) {
      return IvFileHelper.class.getResourceAsStream("/assets/" + resourceLocation.getResourceDomain() + "/" + resourceLocation.getResourcePath());
   }

   public static void setContentsOfFile(OutputStream outputStream, byte[] contents) {
      try {
         outputStream.write(contents);
         outputStream.close();
      } catch (IOException var3) {
         var3.printStackTrace();
      }

   }

   public static void setContentsOfFile(File file, byte[] contents) {
      FileOutputStream output = null;

      try {
         output = new FileOutputStream(file);
      } catch (IOException var4) {
         var4.printStackTrace();
      }

      setContentsOfFile(output, contents);
   }

   public static void setContentsOfFileAsString(File file, String contents) {
      char[] charArray = contents.toCharArray();
      byte[] byteArray = new byte[charArray.length];

      for(int i = 0; i < byteArray.length; ++i) {
         byteArray[i] = (byte)charArray[i];
      }

      setContentsOfFile(file, byteArray);
   }

   public static byte[] getContentsOfFile(InputStream inputStream) {
      ArrayList<Byte> array = new ArrayList();

      try {
         while(true) {
            int i = inputStream.read();
            if (i < 0) {
               break;
            }

            array.add(Byte.valueOf((byte)i));
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      byte[] byteArray = new byte[array.size()];

      for(int i = 0; i < array.size(); ++i) {
         byteArray[i] = ((Byte)array.get(i)).byteValue();
      }

      return byteArray;
   }

   public static byte[] getContentsOfFile(File file) {
      FileInputStream input = null;

      try {
         input = new FileInputStream(file);
      } catch (FileNotFoundException var3) {
         var3.printStackTrace();
      }

      return getContentsOfFile(input);
   }

   public static String getContentsOfFileAsString(File file) {
      return getStringFromByteArray(getContentsOfFile(file));
   }

   public static byte[] getContentsOfFileInJar(String path) {
      return getContentsOfFile(IvFileHelper.class.getResourceAsStream(path));
   }

   public static String getContentsOfFileInJarAsString(String path) {
      return getStringFromByteArray(getContentsOfFileInJar(path));
   }

   public static String getStringFromByteArray(byte[] byteArray) {
      char[] charArray = new char[byteArray.length];

      for(int i = 0; i < charArray.length; ++i) {
         charArray[i] = (char)byteArray[i];
      }

      return String.valueOf(charArray);
   }

   public static File getValidatedFolder(File file) {
      if (!file.exists() && !file.mkdir()) {
         System.out.println("Could not create " + file.getName() + " folder");
      }

      return file.exists() ? file : null;
   }

   public static File getValidatedFolder(File parent, String child) {
      return getValidatedFolder(new File(parent, child));
   }
}
