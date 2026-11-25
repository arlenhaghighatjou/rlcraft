package vazkii.quark.building.block.wall;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import vazkii.quark.base.block.BlockQuarkWall;

public class BlockSnowBricksWall extends BlockQuarkWall {
   public BlockSnowBricksWall(String name, IBlockState state) {
      super(name, state);
   }

   public boolean isToolEffective(String type, @Nonnull IBlockState state) {
      return type.equals("shovel");
   }

   public boolean canHarvestBlock(IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
      return true;
   }

   @Nonnull
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Item.getItemFromBlock(this);
   }
}
