package betterquesting.api2.client.gui.panels.lists;

import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.content.PanelFluidSlot;
import betterquesting.core.BetterQuesting;
import java.util.ArrayDeque;
import java.util.Iterator;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class CanvasFluidDatabase extends CanvasSearch<FluidStack, Fluid> {
   private final int btnId;

   public CanvasFluidDatabase(IGuiRect rect, int buttonId) {
      super(rect);
      this.btnId = buttonId;
   }

   protected Iterator<Fluid> getIterator() {
      return FluidRegistry.getRegisteredFluids().values().iterator();
   }

   protected void queryMatches(Fluid fluid, String query, ArrayDeque<FluidStack> results) {
      if (fluid != null && fluid.getName() != null) {
         try {
            FluidStack stack = new FluidStack(fluid, 1000);
            if (fluid.getUnlocalizedName().toLowerCase().contains(query) || fluid.getLocalizedName(stack).toLowerCase().contains(query) || fluid.getName().toLowerCase().contains(query)) {
               results.add(stack);
            }
         } catch (Exception var5) {
            BetterQuesting.logger.error("An error occured while searching fluid \"" + fluid.getName() + "\" (" + fluid.getClass().getName() + ")", var5);
         }

      }
   }

   protected boolean addResult(FluidStack stack, int index, int cachedWidth) {
      if (stack == null) {
         return false;
      } else {
         int x = index % (cachedWidth / 18) * 18;
         int y = index / (cachedWidth / 18) * 18;
         this.addPanel(new PanelFluidSlot(new GuiRectangle(x, y, 18, 18, 0), this.btnId, stack));
         return true;
      }
   }
}
