package org.orecruncher.dsurround.registry.biome;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.TempCategory;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;

@SideOnly(Side.CLIENT)
public class FakeBiome implements IBiome {
   private static int biomeIdCounter = -200;
   protected final int biomeId;
   protected final String name;
   protected final ResourceLocation key;
   protected BiomeInfo biomeData;

   public FakeBiome(@Nonnull String name) {
      this.biomeId = --biomeIdCounter;
      this.name = name;
      this.key = new ResourceLocation("dsurround", ("fake_" + name).replace(' ', '_'));
   }

   @Nullable
   public BiomeInfo getBiomeData() {
      return this.biomeData;
   }

   public void setBiomeData(@Nullable BiomeInfo data) {
      this.biomeData = data;
   }

   public int getId() {
      return this.biomeId;
   }

   public boolean canRain() {
      return getTrueBiome().canRain();
   }

   public boolean getEnableSnow() {
      return getTrueBiome().getEnableSnow();
   }

   public float getFloatTemperature(@Nonnull BlockPos pos) {
      return getTrueBiome().getFloatTemperature(pos);
   }

   public float getTemperature() {
      return getTrueBiome().getTemperature();
   }

   public TempCategory getTempCategory() {
      return getTrueBiome().getTempCategory();
   }

   public boolean isHighHumidity() {
      return getTrueBiome().isHighHumidity();
   }

   public float getRainfall() {
      BiomeInfo info = getTrueBiome();
      return info == null ? 0.0F : info.getRainfall();
   }

   public Biome getBiome() {
      return null;
   }

   public ResourceLocation getKey() {
      return this.key;
   }

   public String getName() {
      return this.name;
   }

   public Set<Type> getTypes() {
      return ImmutableSet.of();
   }

   public boolean isFake() {
      return true;
   }

   private static BiomeInfo getTrueBiome() {
      return EnvironStateHandler.EnvironState.getTruePlayerBiome();
   }
}
