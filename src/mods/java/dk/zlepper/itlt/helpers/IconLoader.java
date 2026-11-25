package dk.zlepper.itlt.helpers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;

public class IconLoader {
   public IconLoader() {
   }

   public static ByteBuffer[] load(String filepath) {
      return load(new File(filepath));
   }

   public static ByteBuffer[] load(File fil) {
      BufferedImage image = null;

      try {
         image = ImageIO.read(fil);
      } catch (IOException var4) {
         var4.printStackTrace();
      }

      ByteBuffer[] buffers = null;
      String OS = System.getProperty("os.name").toUpperCase();
      if (OS.contains("WIN")) {
         buffers = new ByteBuffer[]{loadInstance(image, 16), loadInstance(image, 32)};
      } else if (OS.contains("MAC")) {
         buffers = new ByteBuffer[]{loadInstance(image, 128)};
      } else {
         buffers = new ByteBuffer[]{loadInstance(image, 32)};
      }

      return buffers;
   }

   private static ByteBuffer loadInstance(BufferedImage image, int dimension) {
      BufferedImage scaledIcon = new BufferedImage(dimension, dimension, 3);
      Graphics2D g = scaledIcon.createGraphics();
      double ratio = getIconRatio(image, scaledIcon);
      double width = (double)image.getWidth() * ratio;
      double height = (double)image.getHeight() * ratio;
      g.drawImage(image, (int)(((double)scaledIcon.getWidth() - width) / 2.0D), (int)(((double)scaledIcon.getHeight() - height) / 2.0D), (int)width, (int)height, (ImageObserver)null);
      g.dispose();
      return convertToByteBuffer(scaledIcon);
   }

   private static double getIconRatio(BufferedImage src, BufferedImage icon) {
      double ratio = 1.0D;
      if (src.getWidth() > icon.getWidth()) {
         ratio = (double)icon.getWidth() / (double)src.getWidth();
      } else {
         ratio = (double)(icon.getWidth() / src.getWidth());
      }

      if (src.getHeight() > icon.getHeight()) {
         double r2 = (double)icon.getHeight() / (double)src.getHeight();
         if (r2 < ratio) {
            ratio = r2;
         }
      } else {
         double r2 = (double)(icon.getHeight() / src.getHeight());
         if (r2 < ratio) {
            ratio = r2;
         }
      }

      return ratio;
   }

   public static ByteBuffer convertToByteBuffer(BufferedImage image) {
      byte[] buffer = new byte[image.getWidth() * image.getHeight() * 4];
      int counter = 0;

      for(int i = 0; i < image.getHeight(); ++i) {
         for(int j = 0; j < image.getWidth(); ++j) {
            int colorSpace = image.getRGB(j, i);
            buffer[counter + 0] = (byte)(colorSpace << 8 >> 24);
            buffer[counter + 1] = (byte)(colorSpace << 16 >> 24);
            buffer[counter + 2] = (byte)(colorSpace << 24 >> 24);
            buffer[counter + 3] = (byte)(colorSpace >> 24);
            counter += 4;
         }
      }

      return ByteBuffer.wrap(buffer);
   }
}
