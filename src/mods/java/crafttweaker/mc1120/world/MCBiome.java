package crafttweaker.mc1120.world;

import crafttweaker.api.world.IBiome;
import crafttweaker.api.world.IBiomeType;
import crafttweaker.mc1120.brackets.BracketHandlerBiomeType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class MCBiome implements IBiome {
   private final Biome biome;

   public MCBiome(Biome biome) {
      this.biome = biome;
   }

   public String getId() {
      return this.biome.getRegistryName().toString();
   }

   public String getName() {
      return this.biome.biomeName;
   }

   public boolean getCanRain() {
      return this.biome.canRain();
   }

   public boolean isSnowyBiome() {
      return this.biome.isSnowyBiome();
   }

   public boolean getIsHighHumidity() {
      return this.biome.isHighHumidity();
   }

   public float getSpawningChance() {
      return this.biome.getSpawningChance();
   }

   public float getBaseHeight() {
      return this.biome.getBaseHeight();
   }

   public float getRainfall() {
      return this.biome.getRainfall();
   }

   public int getWaterColorMultiplier() {
      return this.biome.getWaterColorMultiplier();
   }

   public boolean getIgnorePlayerSpawnSuitability() {
      return this.biome.ignorePlayerSpawnSuitability();
   }

   public float getHeightVariation() {
      return this.biome.getHeightVariation();
   }

   public float getTemperature() {
      return this.biome.getDefaultTemperature();
   }

   public List<IBiomeType> getTypes() {
      List<IBiomeType> types = new ArrayList();

      for(Type type : BiomeDictionary.getTypes(this.biome)) {
         types.add(BracketHandlerBiomeType.getBiomeType(type.getName()));
      }

      return types;
   }

   public Biome getInternal() {
      return this.biome;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCBiome mcBiome = (MCBiome)o;
         return Objects.equals(this.biome, mcBiome.biome);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.biome});
   }
}
