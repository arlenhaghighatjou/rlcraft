package sereneseasons.handler.season;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockReed;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.BlockEvent.CropGrowEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sereneseasons.api.SSBlocks;
import sereneseasons.config.FertilityConfig;
import sereneseasons.init.ModFertility;

public class SeasonalCropGrowthHandler {
   public SeasonalCropGrowthHandler() {
   }

   public void register(EventBus bus) {
      bus.register(ItemTooltipEvent.class, this::onItemTooltipAdded);
      bus.register(CropGrowEvent.class, this::onCropGrowth);
      bus.register(BonemealEvent.class, this::onApplyBonemeal);
   }

   @SideOnly(Side.CLIENT)
   public void onItemTooltipAdded(ItemTooltipEvent event) {
      ModFertility.setupTooltips(event);
   }

   public void onCropGrowth(CropGrowEvent event) {
      Block plant = event.getState().getBlock();
      boolean isFertile = ModFertility.isCropFertile(plant.getRegistryName().toString(), event.getWorld(), event.getPos());
      if (FertilityConfig.general_category.seasonal_crops && !isFertile && !this.isGreenhouseGlassAboveBlock(event.getWorld(), event.getPos())) {
         if (FertilityConfig.general_category.crops_break && !(plant instanceof BlockGrass) && !(plant instanceof BlockReed)) {
            event.getWorld().destroyBlock(event.getPos(), true);
         } else {
            event.setResult(Result.DENY);
         }
      }

   }

   public void onApplyBonemeal(BonemealEvent event) {
      Block plant = event.getBlock().getBlock();
      boolean isFertile = ModFertility.isCropFertile(plant.getRegistryName().toString(), event.getWorld(), event.getPos());
      if (FertilityConfig.general_category.seasonal_crops && !isFertile && !this.isGreenhouseGlassAboveBlock(event.getWorld(), event.getPos())) {
         if (FertilityConfig.general_category.crops_break && !(plant instanceof BlockGrass) && !(plant instanceof BlockReed)) {
            event.getWorld().destroyBlock(event.getPos(), true);
         }

         event.setCanceled(true);
      }

   }

   private boolean isGreenhouseGlassAboveBlock(World world, BlockPos cropPos) {
      for(int i = 0; i < FertilityConfig.general_category.greenhouse_glass_max_height; ++i) {
         if (world.getBlockState(cropPos.add(0, i + 1, 0)).getBlock().equals(SSBlocks.greenhouse_glass)) {
            return true;
         }
      }

      return false;
   }
}
