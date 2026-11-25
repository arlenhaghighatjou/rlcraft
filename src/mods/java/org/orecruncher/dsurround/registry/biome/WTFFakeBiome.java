package org.orecruncher.dsurround.registry.biome;

import javax.annotation.Nonnull;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome.TempCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WTFFakeBiome extends FakeBiome {
   public WTFFakeBiome() {
      super("WTFJustHappened");
   }

   public boolean canRain() {
      return false;
   }

   public boolean getEnableSnow() {
      return false;
   }

   public float getFloatTemperature(@Nonnull BlockPos pos) {
      return 0.0F;
   }

   public float getTemperature() {
      return 0.0F;
   }

   public TempCategory getTempCategory() {
      return TempCategory.COLD;
   }

   public boolean isHighHumidity() {
      return false;
   }

   public float getRainfall() {
      return 0.0F;
   }
}
