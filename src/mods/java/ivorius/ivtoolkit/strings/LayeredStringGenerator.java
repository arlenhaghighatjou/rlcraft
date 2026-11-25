package ivorius.ivtoolkit.strings;

import ivorius.ivtoolkit.random.WeightedSelector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class LayeredStringGenerator {
   public LayeredStringGenerator.Layer baseLayer;

   public LayeredStringGenerator(LayeredStringGenerator.Layer baseLayer) {
      this.baseLayer = baseLayer;
   }

   public String randomString(EaglercraftRandom random) {
      return this.baseLayer.randomString(random);
   }

   private static <O> O getRandomElementFrom(List<O> list, EaglercraftRandom random) {
      return (O)list.get(random.nextInt(list.size()));
   }

   public interface Layer {
      String randomString(EaglercraftRandom var1);
   }

   public static class LayerSimple implements LayeredStringGenerator.Layer {
      private Map<Character, LayeredStringGenerator.Layer> subLayers;
      private List<WeightedSelector.SimpleItem<String>> baseStrings = new ArrayList();

      public LayerSimple(Map<Character, LayeredStringGenerator.Layer> subLayers) {
         this.subLayers = subLayers;
      }

      public LayerSimple(Object... layersForCharacters) {
         this.subLayers = new HashMap();

         for(int i = 0; i < layersForCharacters.length / 2; ++i) {
            this.subLayers.put((Character)layersForCharacters[i * 2], (LayeredStringGenerator.Layer)layersForCharacters[i * 2 + 1]);
         }

      }

      /** @deprecated */
      @Deprecated
      public void addStrings(int weight, String... strings) {
         for(String s : strings) {
            this.baseStrings.add(new WeightedSelector.SimpleItem((double)weight, s));
         }

      }

      public void addStrings(double weight, String... strings) {
         for(String s : strings) {
            this.baseStrings.add(new WeightedSelector.SimpleItem(weight, s));
         }

      }

      public String randomString(EaglercraftRandom random) {
         StringBuilder stringBuilder = new StringBuilder();
         String component = (String)WeightedSelector.select(random, this.baseStrings);

         for(char aChar : component.toCharArray()) {
            LayeredStringGenerator.Layer subLayer = (LayeredStringGenerator.Layer)this.subLayers.get(Character.valueOf(aChar));
            if (subLayer == null) {
               stringBuilder.append(aChar);
            } else {
               stringBuilder.append(subLayer.randomString(random));
            }
         }

         return stringBuilder.toString();
      }
   }

   public static class LayerStatic implements LayeredStringGenerator.Layer {
      private List<WeightedSelector.SimpleItem<String>> baseStrings = new ArrayList();

      public LayerStatic(String... baseStrings) {
         this.addStrings(1, baseStrings);
      }

      /** @deprecated */
      @Deprecated
      public void addStrings(int weight, String... strings) {
         for(String s : strings) {
            this.baseStrings.add(new WeightedSelector.SimpleItem((double)weight, s));
         }

      }

      public void addStrings(double weight, String... strings) {
         for(String s : strings) {
            this.baseStrings.add(new WeightedSelector.SimpleItem(weight, s));
         }

      }

      public String randomString(EaglercraftRandom random) {
         return (String)WeightedSelector.select(random, this.baseStrings);
      }
   }

   public static class LayerUppercase implements LayeredStringGenerator.Layer {
      public LayeredStringGenerator.Layer parent;

      public LayerUppercase(LayeredStringGenerator.Layer parent) {
         this.parent = parent;
      }

      public String randomString(EaglercraftRandom random) {
         return firstCharUppercase(this.parent.randomString(random));
      }

      private static String firstCharUppercase(String name) {
         return Character.toString(name.charAt(0)).toUpperCase() + name.substring(1);
      }
   }
}
