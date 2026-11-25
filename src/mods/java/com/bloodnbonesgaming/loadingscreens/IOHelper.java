package com.bloodnbonesgaming.loadingscreens;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class IOHelper {
   public IOHelper() {
   }

   public static void outputImage(BufferedImage image, String name) {
      File folder = new File("./config/loadingscreens/output/");
      if (!folder.exists()) {
         folder.mkdirs();
      }

      File file = new File("./config/loadingscreens/output/" + name + ".png");
      file.getParentFile().mkdirs();

      try {
         ImageIO.write(image, "png", file);
      } catch (IOException var5) {
         var5.printStackTrace();
      }

   }
}
