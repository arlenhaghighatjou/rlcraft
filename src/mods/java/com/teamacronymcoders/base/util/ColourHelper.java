package com.teamacronymcoders.base.util;

import com.teamacronymcoders.base.Base;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class ColourHelper {
   public ColourHelper() {
   }

   public static int getColour(InputStream stream) {
      try {
         ImageInputStream is = ImageIO.createImageInputStream(stream);
         Iterator<ImageReader> iter = ImageIO.getImageReaders(is);
         if (!iter.hasNext()) {
            Base.instance.getLogger().error("Cannot load the specified stream: " + stream);
         }

         ImageReader imageReader = (ImageReader)iter.next();
         imageReader.setInput(is);
         Image img = imageReader.read(0).getScaledInstance(1, 1, 16);
         BufferedImage image = new BufferedImage(img.getWidth((ImageObserver)null), img.getHeight((ImageObserver)null), 2);
         Graphics2D bGr = image.createGraphics();
         bGr.drawImage(img, 0, 0, (ImageObserver)null);
         bGr.dispose();
         int height = image.getHeight();
         int width = image.getWidth();
         HashMap m = new HashMap();

         for(int i = 0; i < width; ++i) {
            for(int j = 0; j < height; ++j) {
               int rgb = image.getRGB(i, j);
               Integer counter = (Integer)m.get(Integer.valueOf(rgb));
               if (counter == null) {
                  counter = Integer.valueOf(0);
               }

               m.put(Integer.valueOf(rgb), Integer.valueOf(counter.intValue() + 1));
            }
         }

         return Integer.decode("0x" + getMostCommonColour(m)).intValue();
      } catch (Exception var14) {
         var14.printStackTrace();
         return 16777215;
      }
   }

   public static String getMostCommonColour(Map map) {
      List list = new LinkedList(map.entrySet());
      Collections.sort(list, (o1, o2) -> {
         return ((Comparable)((Entry)((Entry)o1)).getValue()).compareTo(((Entry)((Entry)o2)).getValue());
      });
      if (list.size() == 0) {
         return "FFFFFF";
      } else {
         Entry me = (Entry)list.get(list.size() - 1);
         int[] rgb = getRGBArr(((Integer)me.getKey()).intValue());
         return String.format("%s%s%s", Integer.toHexString(rgb[0]), Integer.toHexString(rgb[1]), Integer.toHexString(rgb[2]));
      }
   }

   public static int[] getRGBArr(int pixel) {
      int red = pixel >> 16 & 255;
      int green = pixel >> 8 & 255;
      int blue = pixel & 255;
      return new int[]{red, green, blue};
   }
}
