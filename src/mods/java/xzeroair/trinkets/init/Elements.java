package xzeroair.trinkets.init;

import net.minecraft.util.ResourceLocation;
import xzeroair.trinkets.traits.elements.Element;

public class Elements {
   public static final Element[] EMPTY = new Element[0];
   public static final Element NEUTRAL = getRegisteredElement("Neutral");
   public static final Element ICE = getRegisteredElement("Ice");
   public static final Element FIRE = getRegisteredElement("Fire");
   public static final Element WATER = getRegisteredElement("Water");

   public Elements() {
   }

   private static Element getRegisteredElement(String name) {
      return getRegisteredElement("xat", name);
   }

   private static Element getRegisteredElement(String modid, String name) {
      Element element = (Element)Element.Registry.getValue(new ResourceLocation(modid, name));
      if (element == null) {
         throw new IllegalStateException("Invalid Race requested: " + name);
      } else {
         return element;
      }
   }
}
