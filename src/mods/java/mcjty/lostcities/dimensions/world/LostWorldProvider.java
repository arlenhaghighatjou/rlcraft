package mcjty.lostcities.dimensions.world;

import javax.annotation.Nonnull;
import mcjty.lostcities.config.BiomeSelectionStrategy;
import mcjty.lostcities.config.LostCityConfiguration;
import mcjty.lostcities.config.LostCityProfile;
import mcjty.lostcities.dimensions.ModDimensions;
import mcjty.lostcities.setup.ModSetup;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.IChunkGenerator;

public class LostWorldProvider extends WorldProvider {
   public LostWorldProvider() {
   }

   @Nonnull
   public DimensionType getDimensionType() {
      return ModDimensions.lostDimensionType;
   }

   @Nonnull
   public String getSaveFolder() {
      return this.getDimension() == LostCityConfiguration.DIMENSION_ID ? "LOST" : "LOST" + this.getDimension();
   }

   @Nonnull
   public IChunkGenerator createChunkGenerator() {
      return new LostCityChunkGenerator(this.world, this.world.getSeed() >> 3 ^ 34328884229L);
   }

   private BiomeProvider getInternalBiomeProvider(World world) {
      if (this.biomeProvider == null) {
         for(WorldType type : WorldType.WORLD_TYPES) {
            if ("BIOMESOP".equals(type.getName())) {
               WorldType orig = world.getWorldInfo().getTerrainType();
               world.getWorldInfo().setTerrainType(type);
               this.biomeProvider = type.getBiomeProvider(world);
               world.getWorldInfo().setTerrainType(orig);
               break;
            }
         }
      }

      return this.biomeProvider;
   }

   protected void init() {
      super.init();
      String profileName = (String)ModDimensions.dimensionProfileMap.get(Integer.valueOf(this.world.provider.getDimension()));
      LostCityProfile profile = (LostCityProfile)LostCityConfiguration.profiles.get(profileName);
      if (profile == null) {
         profile = WorldTypeTools.getProfile(this.world);
      }

      BiomeProvider biomeProvider;
      if (ModSetup.biomesoplenty && LostCityConfiguration.DIMENSION_BOP) {
         biomeProvider = this.getInternalBiomeProvider(this.world);
      } else {
         biomeProvider = new BiomeProvider(this.world.getWorldInfo());
      }

      if (profile.ALLOWED_BIOME_FACTORS.length == 0) {
         this.biomeProvider = biomeProvider;
      } else {
         String[] outsideAllowedbiomeFactors = profile.ALLOWED_BIOME_FACTORS;
         String[] outsideManualBiomeMapping = profile.MANUAL_BIOME_MAPPINGS;
         BiomeSelectionStrategy outsideStrategy = null;
         if (profile.isSpace() && profile.CITYSPHERE_LANDSCAPE_OUTSIDE && !profile.CITYSPHERE_OUTSIDE_PROFILE.isEmpty()) {
            LostCityProfile outProfile = (LostCityProfile)LostCityConfiguration.profiles.get(profile.CITYSPHERE_OUTSIDE_PROFILE);
            outsideAllowedbiomeFactors = outProfile.ALLOWED_BIOME_FACTORS;
            outsideManualBiomeMapping = outProfile.MANUAL_BIOME_MAPPINGS;
            outsideStrategy = outProfile.BIOME_SELECTION_STRATEGY;
         }

         this.biomeProvider = new LostWorldFilteredBiomeProvider(this.world, biomeProvider, profile.ALLOWED_BIOME_FACTORS, profile.MANUAL_BIOME_MAPPINGS, profile.BIOME_SELECTION_STRATEGY, outsideAllowedbiomeFactors, outsideManualBiomeMapping, outsideStrategy);
      }

   }
}
