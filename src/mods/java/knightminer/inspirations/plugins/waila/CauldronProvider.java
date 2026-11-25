package knightminer.inspirations.plugins.waila;

import java.util.List;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class CauldronProvider implements IWailaDataProvider {
   public CauldronProvider() {
   }

   public List<String> getWailaBody(ItemStack itemStack, List<String> current, IWailaDataAccessor accessor, IWailaConfigHandler config) {
      if (!config.getConfig(WailaRegistrar.CONFIG_CAULDRON)) {
         return current;
      } else {
         IBlockState state = accessor.getBlockState();
         if (state.getBlock() instanceof BlockEnhancedCauldron) {
            int level = ((BlockEnhancedCauldron)state.getBlock()).getLevel(state);
            String colorString = null;
            if (level > 0 && accessor.getTileEntity() instanceof TileCauldron) {
               ICauldronRecipe.CauldronState cauldron = ((TileCauldron)accessor.getTileEntity()).getState();
               if (cauldron.getFluid() != null) {
                  Fluid fluid = cauldron.getFluid();
                  current.add(fluid.getLocalizedName(new FluidStack(fluid, 1000)));
               } else if (cauldron.getPotion() != null) {
                  current.add(Util.translate(cauldron.getPotion().getNamePrefixed("potion.effect.")));
               } else if (cauldron.getColor() != -1) {
                  int color = cauldron.getColor();
                  colorString = "#" + Integer.toHexString(color).toUpperCase();
                  EnumDyeColor dyeColor = Util.getDyeForColor(color);
                  if (dyeColor != null) {
                     current.add(Util.translateFormatted("gui.jei.cauldron.color", Util.translate("item.fireworksCharge.%s", dyeColor.getUnlocalizedName())));
                  } else {
                     current.add(Util.translate("gui.inspirations.cauldron.dye"));
                  }
               }
            }

            current.add(Util.translateFormatted("gui.waila.inspirations.cauldron.bottles", level, InspirationsRegistry.getCauldronMax()));
            if (colorString != null && accessor.getPlayer().isSneaking()) {
               current.add(Util.translateFormatted("gui.inspirations.cauldron.color", colorString));
            }
         }

         return current;
      }
   }
}
