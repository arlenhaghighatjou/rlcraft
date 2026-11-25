package vazkii.quark.building.block.stairs;

import vazkii.arl.interf.IRecipeGrouped;
import vazkii.quark.base.block.BlockQuarkStairs;
import vazkii.quark.building.block.BlockStainedClayTiles;
import vazkii.quark.building.feature.HardenedClayTiles;

public class BlockStainedClayTilesStairs extends BlockQuarkStairs implements IRecipeGrouped {
   public BlockStainedClayTilesStairs(BlockStainedClayTiles.Variants variant) {
      super(variant.getName() + "_stairs", HardenedClayTiles.stained_clay_tiles.getDefaultState().withProperty(HardenedClayTiles.stained_clay_tiles.getVariantProp(), variant));
   }

   public String getRecipeGroup() {
      return "stained_clay_tiles_stairs";
   }
}
