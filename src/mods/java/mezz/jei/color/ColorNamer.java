package mezz.jei.color;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.arlenh7.eaglercraft.awt.Color;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import mezz.jei.util.Translator;

public class ColorNamer {
   private final ImmutableMap<Color, String> colorNames;

   public ColorNamer(ImmutableMap<Color, String> colorNames) {
      this.colorNames = colorNames;
   }

   public Collection<String> getColorNames(Iterable<Color> colors, boolean lowercase) {
      Set<String> allColorNames = new LinkedHashSet();

      for(Color color : colors) {
         String colorName = this.getClosestColorName(color);
         if (colorName != null) {
            if (lowercase) {
               allColorNames.add(Translator.toLowercaseWithLocale(colorName));
            } else {
               allColorNames.add(colorName);
            }
         }
      }

      return allColorNames;
   }

   @Nullable
   private String getClosestColorName(Color color) {
      if (this.colorNames.isEmpty()) {
         return null;
      } else {
         String closestColorName = null;
         Double closestColorDistance = Double.MAX_VALUE;
         UnmodifiableIterator var4 = this.colorNames.entrySet().iterator();

         while(var4.hasNext()) {
            Entry<Color, String> entry = (Entry)var4.next();
            Color namedColor = (Color)entry.getKey();
            Double distance = ColorUtil.slowPerceptualColorDistanceSquared(namedColor, color);
            if (distance.doubleValue() < closestColorDistance.doubleValue()) {
               closestColorDistance = distance;
               closestColorName = (String)entry.getValue();
            }
         }

         return closestColorName;
      }
   }
}
