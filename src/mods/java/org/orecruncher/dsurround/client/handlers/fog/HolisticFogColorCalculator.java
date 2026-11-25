package org.orecruncher.dsurround.client.handlers.fog;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.Color;
import org.orecruncher.lib.collections.ObjectArray;

@SideOnly(Side.CLIENT)
public class HolisticFogColorCalculator implements IFogColorCalculator {
   protected ObjectArray<IFogColorCalculator> calculators = new ObjectArray(4);
   protected Color cached;

   public HolisticFogColorCalculator() {
   }

   public void add(@Nonnull IFogColorCalculator calc) {
      this.calculators.add(calc);
   }

   @Nonnull
   public Color calculate(@Nonnull FogColors event) {
      Color result = null;

      for(int i = 0; i < this.calculators.size(); ++i) {
         Color color = ((IFogColorCalculator)this.calculators.get(i)).calculate(event);
         if (result == null) {
            result = color;
         } else {
            result = result.mix(color);
         }
      }

      if (result == null) {
         result = new Color(event.getRed(), event.getGreen(), event.getBlue());
      }

      return this.cached = result;
   }

   public void tick() {
      this.calculators.forEach(IFogColorCalculator::tick);
   }

   public String toString() {
      return this.cached != null ? this.cached.toString() : "<NOT SET>";
   }
}
