package com.ferreusveritas.dynamictrees.api.treedata;

import com.ferreusveritas.dynamictrees.trees.Species;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public interface IBiomeSuitabilityDecider {
   IBiomeSuitabilityDecider.Decision getBiomeSuitability(World var1, Biome var2, Species var3, BlockPos var4);

   public static class Decision {
      private final boolean handled;
      private float suitability;

      public Decision() {
         this.handled = false;
      }

      public Decision(float suitability) {
         this.suitability = MathHelper.clamp(suitability, 0.0F, 1.0F);
         this.handled = true;
      }

      public boolean isHandled() {
         return this.handled;
      }

      public float getSuitability() {
         return this.suitability;
      }
   }
}
