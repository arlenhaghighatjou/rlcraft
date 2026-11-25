package mcjty.lostcities.api;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

public class LostCityEvent extends Event {
   private final World world;
   private final ILostChunkGenerator generator;
   private final int chunkX;
   private final int chunkZ;

   public LostCityEvent(World world, ILostChunkGenerator generator, int chunkX, int chunkZ) {
      this.world = world;
      this.generator = generator;
      this.chunkX = chunkX;
      this.chunkZ = chunkZ;
   }

   public World getWorld() {
      return this.world;
   }

   public ILostChunkGenerator getGenerator() {
      return this.generator;
   }

   public int getChunkX() {
      return this.chunkX;
   }

   public int getChunkZ() {
      return this.chunkZ;
   }

   public static class CharacteristicsEvent extends LostCityEvent {
      private final LostChunkCharacteristics characteristics;

      public CharacteristicsEvent(World world, ILostChunkGenerator generator, int chunkX, int chunkZ, LostChunkCharacteristics characteristics) {
         super(world, generator, chunkX, chunkZ);
         this.characteristics = characteristics;
      }

      public LostChunkCharacteristics getCharacteristics() {
         return this.characteristics;
      }
   }

   public static class PostGenCityChunkEvent extends LostCityEvent {
      private final ChunkPrimer primer;

      public PostGenCityChunkEvent(World world, ILostChunkGenerator generator, int chunkX, int chunkZ, ChunkPrimer primer) {
         super(world, generator, chunkX, chunkZ);
         this.primer = primer;
      }

      public ChunkPrimer getPrimer() {
         return this.primer;
      }
   }

   public static class PostGenOutsideChunkEvent extends LostCityEvent {
      private final ChunkPrimer primer;

      public PostGenOutsideChunkEvent(World world, ILostChunkGenerator generator, int chunkX, int chunkZ, ChunkPrimer primer) {
         super(world, generator, chunkX, chunkZ);
         this.primer = primer;
      }

      public ChunkPrimer getPrimer() {
         return this.primer;
      }
   }

   @Cancelable
   public static class PreExplosionEvent extends LostCityEvent {
      private final ChunkPrimer primer;

      public PreExplosionEvent(World world, ILostChunkGenerator generator, int chunkX, int chunkZ, ChunkPrimer primer) {
         super(world, generator, chunkX, chunkZ);
         this.primer = primer;
      }

      public ChunkPrimer getPrimer() {
         return this.primer;
      }
   }

   @Cancelable
   public static class PreGenCityChunkEvent extends LostCityEvent {
      private final ChunkPrimer primer;

      public PreGenCityChunkEvent(World world, ILostChunkGenerator generator, int chunkX, int chunkZ, ChunkPrimer primer) {
         super(world, generator, chunkX, chunkZ);
         this.primer = primer;
      }

      public ChunkPrimer getPrimer() {
         return this.primer;
      }
   }
}
