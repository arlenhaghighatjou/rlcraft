package org.orecruncher.dsurround.client.aurora;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.shader.Shaders;

@SideOnly(Side.CLIENT)
public final class AuroraFactory {
   public AuroraFactory() {
   }

   @Nonnull
   public static IAurora produce(long seed) {
      return (IAurora)(ModOptions.aurora.auroraUseShader && Shaders.areShadersSupported() ? new AuroraShaderBand(seed) : new AuroraClassic(seed));
   }

   public static final class AuroraGeometry {
      public final int length;
      public final float nodeLength;
      public final float nodeWidth;
      public final int alphaLimit;
      private static final List<AuroraFactory.AuroraGeometry> PRESET = new ArrayList();

      private AuroraGeometry(int length, float nodeLength, float nodeWidth, int alphaLimit) {
         this.length = length;
         this.nodeLength = nodeLength;
         this.nodeWidth = nodeWidth;
         this.alphaLimit = alphaLimit;
      }

      @Nonnull
      public static AuroraFactory.AuroraGeometry get(@Nonnull Random random) {
         int idx = random.nextInt(PRESET.size());
         return (AuroraFactory.AuroraGeometry)PRESET.get(idx);
      }

      @Nonnull
      public String toString() {
         StringBuilder builder = new StringBuilder();
         builder.append("bandLength:").append(this.length);
         builder.append(";nodeLength:").append(this.nodeLength);
         builder.append(";nodeWidth:").append(this.nodeWidth);
         builder.append(";alphaLimit:").append(this.alphaLimit);
         return builder.toString();
      }

      static {
         PRESET.add(new AuroraFactory.AuroraGeometry(128, 30.0F, 2.0F, 96));
         PRESET.add(new AuroraFactory.AuroraGeometry(128, 15.0F, 2.0F, 96));
         PRESET.add(new AuroraFactory.AuroraGeometry(64, 30.0F, 2.0F, 96));
         PRESET.add(new AuroraFactory.AuroraGeometry(64, 15.0F, 2.0F, 96));
         PRESET.add(new AuroraFactory.AuroraGeometry(128, 30.0F, 2.0F, 80));
         PRESET.add(new AuroraFactory.AuroraGeometry(128, 15.0F, 2.0F, 80));
         PRESET.add(new AuroraFactory.AuroraGeometry(64, 30.0F, 2.0F, 80));
         PRESET.add(new AuroraFactory.AuroraGeometry(64, 15.0F, 2.0F, 80));
         PRESET.add(new AuroraFactory.AuroraGeometry(128, 30.0F, 2.0F, 64));
         PRESET.add(new AuroraFactory.AuroraGeometry(128, 15.0F, 2.0F, 64));
         PRESET.add(new AuroraFactory.AuroraGeometry(64, 30.0F, 2.0F, 64));
         PRESET.add(new AuroraFactory.AuroraGeometry(64, 15.0F, 2.0F, 64));
      }
   }
}
