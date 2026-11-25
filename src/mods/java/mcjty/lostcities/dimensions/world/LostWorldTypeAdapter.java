package mcjty.lostcities.dimensions.world;

import mcjty.lostcities.api.IChunkPrimerFactory;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.IChunkGenerator;

public class LostWorldTypeAdapter extends LostWorldType {
   private final String otherWorldtype;
   private BiomeProvider biomeProvider = null;
   private IChunkGenerator otherGenerator = null;
   private IChunkPrimerFactory factory = null;

   public LostWorldTypeAdapter(String other) {
      super("lc_" + other);
      this.otherWorldtype = other;
   }

   public IChunkGenerator getChunkGenerator(World world, String generatorOptions) {
      if (this.otherGenerator == null) {
         for(WorldType type : WorldType.WORLD_TYPES) {
            if (this.otherWorldtype.equals(type.getName())) {
               WorldType orig = world.getWorldInfo().getTerrainType();
               world.getWorldInfo().setTerrainType(type);
               this.otherGenerator = type.getChunkGenerator(world, generatorOptions);
               world.getWorldInfo().setTerrainType(orig);
               if (this.otherGenerator instanceof IChunkPrimerFactory) {
                  this.factory = (IChunkPrimerFactory)this.otherGenerator;
               }
               break;
            }
         }
      }

      return new LostCityChunkGenerator(world, this.factory);
   }

   protected BiomeProvider getInternalBiomeProvider(World world) {
      if (this.biomeProvider == null) {
         for(WorldType type : WorldType.WORLD_TYPES) {
            if (this.otherWorldtype.equals(type.getName())) {
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
}
