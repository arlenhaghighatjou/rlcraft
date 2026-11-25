package xzeroair.trinkets.traits.elements;

import xzeroair.trinkets.init.Elements;

public class IceElement extends Element {
   public IceElement() {
      super("Ice");
   }

   public Element[] getStrengths() {
      return new Element[]{Elements.ICE};
   }

   public Element[] getWeaknesses() {
      return new Element[]{Elements.FIRE};
   }
}
