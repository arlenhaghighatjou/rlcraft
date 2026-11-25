package org.orecruncher.dsurround.mixins;

import net.minecraft.world.biome.Biome;
import org.orecruncher.dsurround.registry.IDataAccessor;
import org.orecruncher.dsurround.registry.biome.BiomeInfo;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({Biome.class})
public abstract class MixinBiome implements IDataAccessor<BiomeInfo> {
   private BiomeInfo dsurround_biome_info = null;

   public MixinBiome() {
   }

   public BiomeInfo getData() {
      return this.dsurround_biome_info;
   }

   public void setData(BiomeInfo data) {
      this.dsurround_biome_info = data;
   }
}
