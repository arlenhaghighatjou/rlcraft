package rtg.api.world.deco.helper;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.util.math.ChunkPos;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.IRealisticBiome;
import rtg.api.world.deco.DecoBase;

public class DecoHelperRandomSplit extends DecoBase {
   public DecoBase[] decos = new DecoBase[0];
   public int[] chances = new int[0];

   public DecoHelperRandomSplit() {
   }

   public boolean properlyDefined() {
      for(int i = 0; i < this.decos.length; ++i) {
         if (!this.decos[i].properlyDefined()) {
            return false;
         }
      }

      return true;
   }

   public void generate(IRealisticBiome biome, RTGWorld rtgWorld, EaglercraftRandom rand, ChunkPos chunkPos, float river, boolean hasVillage) {
      if (this.decos.length >= 1 && this.chances.length >= 1 && this.decos.length == this.chances.length) {
         int totalChances = 0;

         for(int i = 0; i < this.decos.length; ++i) {
            totalChances += this.chances[i];
         }

         int chosen = rand.nextInt(totalChances);

         for(int i = 0; i < this.decos.length; ++i) {
            if (chosen < this.chances[i]) {
               this.decos[i].generate(biome, rtgWorld, rand, chunkPos, river, hasVillage);
            }

            chosen -= this.chances[i];
         }

      } else {
         throw new RuntimeException("DecoHelperRandomSplit is confused.");
      }
   }

   public DecoBase[] getDecos() {
      return this.decos;
   }

   public DecoHelperRandomSplit setDecos(DecoBase[] decos) {
      this.decos = decos;
      return this;
   }

   public int[] getChances() {
      return this.chances;
   }

   public DecoHelperRandomSplit setChances(int[] chances) {
      this.chances = chances;
      return this;
   }
}
