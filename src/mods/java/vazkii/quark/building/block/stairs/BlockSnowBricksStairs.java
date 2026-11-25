package vazkii.quark.building.block.stairs;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import vazkii.quark.base.block.BlockQuarkStairs;
import vazkii.quark.building.feature.SnowBricks;

public class BlockSnowBricksStairs extends BlockQuarkStairs {
   public BlockSnowBricksStairs() {
      super("snow_bricks_stairs", SnowBricks.snow_bricks.getDefaultState());
   }

   public boolean isToolEffective(String type, @Nonnull IBlockState state) {
      return type.equals("shovel");
   }

   public boolean canHarvestBlock(IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
      return true;
   }

   @Nonnull
   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return Item.getItemFromBlock(this);
   }
}
