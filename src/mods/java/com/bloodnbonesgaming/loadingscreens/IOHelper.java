package com.bloodnbonesgaming.loadingscreens;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.opengl.ImageData;
import com.arlenh7.eaglercraft.image.PngWriter;
import java.io.IOException;

public class IOHelper {
   public IOHelper() {
   }

   public static void outputImage(ImageData image, String name) {
      VFile2 file = new VFile2("config/loadingscreens/output/" + name + ".png");

      try {
         PngWriter writer = new PngWriter(image.width, image.height);

         for(int y = 0; y < image.height; ++y) {
            writer.writeRow(image.pixels, y * image.width);
         }

         file.setAllBytes(writer.finish());
      } catch (IOException var5) {
         var5.printStackTrace();
      }

   }
}
