package org.orecruncher.dsurround.client.aurora;

import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.Color;

@SideOnly(Side.CLIENT)
public final class AuroraColor {
   public final Color baseColor;
   public final Color fadeColor;
   public final Color middleColor;
   private static final List<AuroraColor> COLOR_SETS = new ArrayList();
   private static final float WARMER = 0.3F;
   private static final float COOLER = -0.3F;

   private AuroraColor(@Nonnull Color base, @Nonnull Color fade) {
      this(base, fade, base);
   }

   private AuroraColor(@Nonnull Color base, @Nonnull Color fade, @Nonnull Color mid) {
      this.baseColor = base;
      this.fadeColor = fade;
      this.middleColor = mid;
   }

   @Nonnull
   public static AuroraColor get(@Nonnull EaglercraftRandom random) {
      int idx = random.nextInt(COLOR_SETS.size());
      return (AuroraColor)COLOR_SETS.get(idx);
   }

   public static int testId() {
      return COLOR_SETS.size() - 1;
   }

   static {
      COLOR_SETS.add(new AuroraColor(new Color(0, 255, 153), new Color(51, 255, 0)));
      COLOR_SETS.add(new AuroraColor(Color.BLUE, Color.GREEN));
      COLOR_SETS.add(new AuroraColor(Color.MAGENTA, Color.GREEN));
      COLOR_SETS.add(new AuroraColor(Color.INDIGO, Color.GREEN));
      COLOR_SETS.add(new AuroraColor(Color.TURQOISE, Color.LGREEN));
      COLOR_SETS.add(new AuroraColor(Color.YELLOW, Color.RED));
      COLOR_SETS.add(new AuroraColor(Color.GREEN, Color.RED));
      COLOR_SETS.add(new AuroraColor(Color.GREEN, Color.YELLOW));
      COLOR_SETS.add(new AuroraColor(Color.RED, Color.YELLOW));
      COLOR_SETS.add(new AuroraColor(Color.NAVY, Color.INDIGO));
      COLOR_SETS.add(new AuroraColor(Color.CYAN, Color.MAGENTA));
      COLOR_SETS.add(new AuroraColor(Color.AURORA_GREEN, Color.AURORA_RED, Color.AURORA_BLUE));
      COLOR_SETS.add(new AuroraColor(Color.YELLOW.luminance(0.3F).asImmutable(), Color.RED.luminance(0.3F).asImmutable()));
      COLOR_SETS.add(new AuroraColor(Color.GREEN.luminance(0.3F).asImmutable(), Color.RED.luminance(0.3F).asImmutable()));
      COLOR_SETS.add(new AuroraColor(Color.GREEN.luminance(0.3F).asImmutable(), Color.YELLOW.luminance(0.3F).asImmutable()));
      COLOR_SETS.add(new AuroraColor(Color.BLUE.luminance(0.3F).asImmutable(), Color.GREEN.luminance(0.3F).asImmutable()));
      COLOR_SETS.add(new AuroraColor(Color.INDIGO.luminance(0.3F).asImmutable(), Color.GREEN.luminance(0.3F).asImmutable()));
      COLOR_SETS.add(new AuroraColor(Color.AURORA_GREEN.luminance(0.3F).asImmutable(), Color.AURORA_RED.luminance(0.3F).asImmutable(), Color.AURORA_BLUE.luminance(0.3F).asImmutable()));
      COLOR_SETS.add(new AuroraColor(Color.YELLOW.luminance(-0.3F).asImmutable(), Color.RED.luminance(-0.3F).asImmutable()));
      COLOR_SETS.add(new AuroraColor(Color.GREEN.luminance(-0.3F).asImmutable(), Color.RED.luminance(-0.3F).asImmutable()));
      COLOR_SETS.add(new AuroraColor(Color.GREEN.luminance(-0.3F).asImmutable(), Color.YELLOW.luminance(-0.3F).asImmutable()));
      COLOR_SETS.add(new AuroraColor(Color.BLUE.luminance(-0.3F).asImmutable(), Color.GREEN.luminance(-0.3F).asImmutable()));
      COLOR_SETS.add(new AuroraColor(Color.INDIGO.luminance(-0.3F).asImmutable(), Color.GREEN.luminance(-0.3F).asImmutable()));
      COLOR_SETS.add(new AuroraColor(Color.AURORA_GREEN.luminance(-0.3F).asImmutable(), Color.AURORA_RED.luminance(-0.3F).asImmutable(), Color.AURORA_BLUE.luminance(-0.3F).asImmutable()));
   }
}
