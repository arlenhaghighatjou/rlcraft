package org.orecruncher.dsurround.client.handlers.fog;

import javax.annotation.Nonnull;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.lib.math.MathStuff;
import org.orecruncher.lib.random.XorShiftRandom;

@SideOnly(Side.CLIENT)
public class MorningFogRangeCalculator extends VanillaFogRangeCalculator {
   protected static final float START = 0.63F;
   protected static final float END = 0.83F;
   protected static final float RESERVE = 10.0F;
   protected int fogDay = -1;
   protected boolean doFog = false;
   protected MorningFogRangeCalculator.FogType type = MorningFogRangeCalculator.FogType.NORMAL;
   protected final FogResult cache = new FogResult();

   public MorningFogRangeCalculator() {
   }

   @Nonnull
   public String getName() {
      return "MorningFogRangeCalculator";
   }

   @Nonnull
   public FogResult calculate(@Nonnull RenderFogEvent event) {
      this.cache.set(event);
      if (this.type != MorningFogRangeCalculator.FogType.NONE && this.cache.getStart() > this.type.getReserve()) {
         float ca = EnvironStateHandler.EnvironState.getWorld().getCelestialAngle((float)event.getRenderPartialTicks());
         if (ca >= this.type.getStart() && ca <= this.type.getEnd()) {
            float mid = (this.type.getStart() + this.type.getEnd()) / 2.0F;
            float factor = 1.0F - MathStuff.abs(ca - mid) / (mid - this.type.getStart());
            float shift = this.cache.getStart() * factor;
            float newEnd = this.cache.getEnd() - shift;
            float newStart = MathStuff.clamp(this.cache.getStart() - shift * 2.0F, this.type.getReserve() + 1.0F, newEnd);
            this.cache.set(newStart, newEnd);
         }
      }

      return this.cache;
   }

   public void tick() {
      int day = EnvironStateHandler.EnvironState.getClock().getDay();
      if (this.fogDay != day) {
         int dim = EnvironStateHandler.EnvironState.getDimensionId();
         this.fogDay = day;
         boolean doFog = dim != -1 && dim != 1 && (ModOptions.fog.morningFogChance < 2 || XorShiftRandom.current().nextInt(ModOptions.fog.morningFogChance) == 0);
         this.type = doFog ? this.getFogType() : MorningFogRangeCalculator.FogType.NONE;
      }

   }

   protected MorningFogRangeCalculator.FogType getFogType() {
      return MorningFogRangeCalculator.FogType.NORMAL;
   }

   public static enum FogType {
      NONE(0.0F, 0.0F, 0.0F),
      NORMAL(0.63F, 0.83F, 10.0F),
      LIGHT(0.73F, 0.72999996F, 15.0F),
      MEDIUM(0.53F, 0.93F, 10.0F),
      HEAVY(0.53F, 1.03F, 5.0F);

      private final float start;
      private final float end;
      private final float reserve;

      private FogType(float start, float end, float reserve) {
         this.start = start;
         this.end = end;
         this.reserve = reserve;
      }

      public float getStart() {
         return this.start;
      }

      public float getEnd() {
         return this.end;
      }

      public float getReserve() {
         return this.reserve;
      }
   }
}
