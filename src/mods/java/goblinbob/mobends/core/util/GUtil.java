package goblinbob.mobends.core.util;

import goblinbob.mobends.core.math.Quaternion;
import goblinbob.mobends.core.math.QuaternionUtils;
import goblinbob.mobends.core.math.vector.IVec3f;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;

public class GUtil {
   public static final float PI = 3.1415927F;
   public static final float TWO_PI = 6.2831855F;
   public static final float RAD_TO_DEG = 57.295776F;

   public GUtil() {
   }

   public static float clamp(float value, float min, float max) {
      return Math.min(Math.max(value, min), max);
   }

   public static int clamp(int value, int min, int max) {
      return Math.min(Math.max(value, min), max);
   }

   public static double angleFromCoordinates(double x, double z) {
      return Math.atan2(x, z) / 3.141592653589793D * 180.0D;
   }

   public static double getRadianDifference(double a, double b) {
      a = wrapRadians(a);
      b = wrapRadians(b);
      double d = Math.abs(a - b);
      d = d > 3.141592653589793D ? 6.283185307179586D - d : d;
      return d;
   }

   public static double wrapRadians(double a) {
      a = a % 3.141592653589793D;
      if (a >= 3.141592653589793D) {
         a -= 6.283185307179586D;
      } else if (a < -3.141592653589793D) {
         a += 6.283185307179586D;
      }

      return a;
   }

   public static IVec3f translate(IVec3f vector, float x, float y, float z) {
      vector.add(x, y, z);
      return vector;
   }

   public static IVec3f scale(IVec3f vector, float x, float y, float z) {
      vector.scale(x, y, z);
      return vector;
   }

   public static void rotate(IVec3f[] points, Quaternion rotation) {
      for(IVec3f point : points) {
         QuaternionUtils.multiply(point, rotation, point);
      }

   }

   public static float lerp(float a, float b, float slide) {
      return a + (b - a) * slide;
   }

   public static float interpolateRotation(float a, float b, float partialTicks) {
      float f;
      for(f = b - a; f < -180.0F; f += 360.0F) {
         ;
      }

      while(f >= 180.0F) {
         f -= 360.0F;
      }

      return a + partialTicks * f;
   }

   public static float interpolateRadians(float a, float b, float partialTicks) {
      float f;
      for(f = b - a; f < -3.1415927F; f += 6.2831855F) {
         ;
      }

      while(f >= 3.1415927F) {
         f -= 6.2831855F;
      }

      return a + partialTicks * f;
   }

   public static IVec3f[] translate(IVec3f[] vectors, float x, float y, float z) {
      for(IVec3f vector : vectors) {
         translate(vector, x, y, z);
      }

      return vectors;
   }

   public static IVec3f[] scale(IVec3f[] vectors, float x, float y, float z) {
      for(IVec3f vector : vectors) {
         scale(vector, x, y, z);
      }

      return vectors;
   }

   public static String[] wrapText(FontRenderer fontRenderer, String text, int maxWidth) {
      if (maxWidth <= 0) {
         return new String[0];
      } else if (!text.contains(" ")) {
         return new String[]{text};
      } else {
         List<String> lines = new ArrayList();
         String leftover = text + "";
         String line = "";
         boolean endOfString = false;

         while(true) {
            String leftoverToNextSpace;
            if (leftover.contains(" ")) {
               leftoverToNextSpace = leftover.substring(0, leftover.indexOf(" "));
            } else {
               leftoverToNextSpace = leftover;
               endOfString = true;
            }

            int currentWidth = fontRenderer.getStringWidth(line + leftoverToNextSpace);
            if (currentWidth > maxWidth) {
               lines.add(line.trim());
               line = leftoverToNextSpace + " ";
            } else {
               line = line + leftoverToNextSpace + " ";
            }

            if (!endOfString) {
               leftover = leftover.substring(leftover.indexOf(" ") + 1);
            } else {
               lines.add(line.trim());
            }

            if (endOfString) {
               break;
            }
         }

         return (String[])lines.toArray(new String[0]);
      }
   }

   public static String[] readLines(BufferedReader reader) {
      try {
         List<String> lines = new ArrayList();

         for(String line = reader.readLine(); line != null; line = reader.readLine()) {
            lines.add(line);
         }

         reader.close();
         return (String[])lines.toArray(new String[0]);
      } catch (FileNotFoundException var3) {
         var3.printStackTrace();
      } catch (IOException var4) {
         var4.printStackTrace();
      }

      return null;
   }

   public static String[] readLines(File file) {
      try {
         return readLines(new BufferedReader(new FileReader(file)));
      } catch (FileNotFoundException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static String readFile(BufferedReader reader) {
      try {
         String content;
         String line;
         for(content = ""; (line = reader.readLine()) != null; content = content + line + "\n") {
            ;
         }

         reader.close();
         return content;
      } catch (FileNotFoundException var3) {
         var3.printStackTrace();
      } catch (IOException var4) {
         var4.printStackTrace();
      }

      return null;
   }

   public static String readFile(File file) {
      try {
         return readFile(new BufferedReader(new FileReader(file)));
      } catch (FileNotFoundException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static void writeLines(File file, String[] lines) {
      try {
         BufferedWriter os = new BufferedWriter(new FileWriter(file));

         for(String line : lines) {
            os.write(line);
            os.newLine();
         }

         os.close();
      } catch (IOException var7) {
         var7.printStackTrace();
      }

   }
}
