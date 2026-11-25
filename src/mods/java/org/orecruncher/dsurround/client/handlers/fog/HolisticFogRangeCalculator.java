package org.orecruncher.dsurround.client.handlers.fog;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.lib.collections.ObjectArray;

@SideOnly(Side.CLIENT)
public class HolisticFogRangeCalculator implements IFogRangeCalculator {
   protected final ObjectArray<IFogRangeCalculator> calculators = new ObjectArray(8);
   protected final FogResult cached = new FogResult();

   public HolisticFogRangeCalculator() {
   }

   public void add(@Nonnull IFogRangeCalculator calc) {
      this.calculators.add(calc);
   }

   @Nonnull
   public String getName() {
      return "HolisticFogRangeCalculator";
   }

   @Nonnull
   public FogResult calculate(@Nonnull RenderFogEvent event) {
      if (event.getFarPlaneDistance() < 0.0F) {
         ModBase.log().warn("Far plane distance in RenderFogEvent is negative: %d", new Object[]{event.getFarPlaneDistance()});
      }

      this.cached.set(event);
      float start = this.cached.getStart();
      float end = this.cached.getEnd();

      for(int i = 0; i < this.calculators.size(); ++i) {
         IFogRangeCalculator calc = (IFogRangeCalculator)this.calculators.get(i);
         FogResult result = calc.calculate(event);
         if (result.getStart() < result.getEnd() && result.getStart() >= 0.0F && result.getEnd() >= 0.0F) {
            start = Math.min(start, result.getStart());
            end = Math.min(end, result.getEnd());
         } else {
            ModBase.log().debug("Fog calculator '%s' has invalid results (start %f, end %f); ignoring", new Object[]{calc.getName(), result.getStart(), result.getEnd()});
         }
      }

      this.cached.set(start, end);
      return this.cached;
   }

   public void tick() {
      this.calculators.forEach(IFogRangeCalculator::tick);
   }

   @Nonnull
   public String toString() {
      return this.cached.toString();
   }
}
