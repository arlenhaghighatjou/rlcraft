package fionathemortal.betterbiomeblend;

import java.util.HashMap;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraft.world.biome.BiomeColorHelper.ColorResolver;

public class ColorResolverCompatibility {
   public static final HashMap<ColorResolver, Integer> knownColorResolvers = new HashMap();
   public static int nextColorID = 3;

   public ColorResolverCompatibility() {
   }

   public static int addNewColorResolver(ColorResolver colorResolver) {
      int id = nextColorID++;
      knownColorResolvers.put(colorResolver, Integer.valueOf(id));
      return id;
   }

   public static int getColorResolverID(ColorResolver colorResolver) {
      Integer id = (Integer)knownColorResolvers.get(colorResolver);
      if (id == null) {
         id = addNewColorResolver(colorResolver);
      }

      return id.intValue();
   }

   static {
      knownColorResolvers.put(BiomeColorHelper.GRASS_COLOR, Integer.valueOf(0));
      knownColorResolvers.put(BiomeColorHelper.WATER_COLOR, Integer.valueOf(1));
      knownColorResolvers.put(BiomeColorHelper.FOLIAGE_COLOR, Integer.valueOf(2));
   }
}
