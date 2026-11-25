package org.orecruncher.dsurround.registry.biome;

import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.TempCategory;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BiomeHandler implements IBiome {
   protected final Biome biome;
   protected final int id;
   protected String name;
   protected final Set<Type> types;

   public BiomeHandler(@Nonnull Biome biome) {
      this.biome = biome;
      this.id = Biome.getIdForBiome(this.biome);
      this.types = BiomeUtil.getBiomeTypes(this.biome);
      this.name = BiomeUtil.getBiomeName(this.biome);
   }

   public Biome getBiome() {
      return this.biome;
   }

   public int getId() {
      return this.id;
   }

   public ResourceLocation getKey() {
      return this.biome.getRegistryName();
   }

   public String getName() {
      return this.name;
   }

   public Set<Type> getTypes() {
      return this.types;
   }

   public boolean canRain() {
      return this.biome.canRain();
   }

   public boolean getEnableSnow() {
      return this.biome.getEnableSnow();
   }

   public float getFloatTemperature(@Nonnull BlockPos pos) {
      return this.biome.getTemperature(pos);
   }

   public float getTemperature() {
      return this.biome.getDefaultTemperature();
   }

   public TempCategory getTempCategory() {
      return this.biome.getTempCategory();
   }

   public boolean isHighHumidity() {
      return this.biome.isHighHumidity();
   }

   public float getRainfall() {
      return this.biome.getRainfall();
   }

   @Nonnull
   public static ResourceLocation getKey(@Nonnull Biome biome) {
      ResourceLocation res = biome.getRegistryName();
      if (res == null) {
         String name = biome.getClass().getName() + "_" + biome.getBiomeName().replace(' ', '_').toLowerCase();
         res = new ResourceLocation("dsurround", name);
      }

      return res;
   }
}
