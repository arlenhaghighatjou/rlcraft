package maxhyper.dynamictreesdefiledlands.worldgen;

import com.ferreusveritas.dynamictrees.api.worldgen.IBiomeDataBasePopulator;
import com.ferreusveritas.dynamictrees.worldgen.BiomeDataBase;
import com.ferreusveritas.dynamictrees.worldgen.BiomeDataBasePopulatorJson;
import net.minecraft.util.ResourceLocation;

public class BiomeDataBasePopulator implements IBiomeDataBasePopulator {
   public static final String RESOURCEPATH = "worldgen/default.json";
   private final BiomeDataBasePopulatorJson jsonPopulator = new BiomeDataBasePopulatorJson(new ResourceLocation("dynamictreesdefiledlands", "worldgen/default.json"));

   public BiomeDataBasePopulator() {
   }

   public void populate(BiomeDataBase dbase) {
      this.jsonPopulator.populate(dbase);
   }
}
