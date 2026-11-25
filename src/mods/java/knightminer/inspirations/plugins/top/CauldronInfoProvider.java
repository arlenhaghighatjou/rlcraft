package knightminer.inspirations.plugins.top;

import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.NumberFormat;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class CauldronInfoProvider implements IProbeInfoProvider {
   public CauldronInfoProvider() {
   }

   public String getID() {
      return Util.resource("cauldron");
   }

   public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState state, IProbeHitData data) {
      if (state.getBlock() instanceof BlockEnhancedCauldron) {
         int level = ((BlockEnhancedCauldron)state.getBlock()).getLevel(state);
         int color = 221;
         String colorString = null;
         if (level > 0) {
            TileEntity te = world.getTileEntity(data.getPos());
            if (te instanceof TileCauldron) {
               ICauldronRecipe.CauldronState cauldron = ((TileCauldron)te).getState();
               if (cauldron.getFluid() != null) {
                  Fluid fluid = cauldron.getFluid();
                  probeInfo.horizontal().text(fluid.getLocalizedName(new FluidStack(fluid, 1000)));
                  if (fluid.getColor() != -1) {
                     color = fluid.getColor();
                  }
               } else if (cauldron.getPotion() != null) {
                  PotionType potion = cauldron.getPotion();
                  probeInfo.horizontal().text(Util.translate(potion.getNamePrefixed("potion.effect.")));
                  color = PotionUtils.getPotionColor(potion);
               } else if (cauldron.getColor() != -1) {
                  color = cauldron.getColor();
                  colorString = "#" + Integer.toHexString(color).toUpperCase();
                  EnumDyeColor dyeColor = Util.getDyeForColor(color);
                  if (dyeColor != null) {
                     probeInfo.horizontal().text(Util.translateFormatted("gui.jei.cauldron.color", Util.translate("item.fireworksCharge.%s", dyeColor.getUnlocalizedName())));
                  } else {
                     probeInfo.horizontal().text(Util.translate("gui.inspirations.cauldron.dye"));
                  }
               }
            }
         }

         probeInfo.horizontal().progress(level, InspirationsRegistry.getCauldronMax(), probeInfo.defaultProgressStyle().filledColor(color | -16777216).suffix(Util.translate("gui.top.inspirations.cauldron.bottles")).alternateFilledColor(-16777149).borderColor(-11184811).numberFormat(NumberFormat.COMPACT));
         if (colorString != null && mode == ProbeMode.EXTENDED) {
            probeInfo.horizontal().text(Util.translateFormatted("gui.inspirations.cauldron.color", colorString));
         }
      }

   }
}
