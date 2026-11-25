package vazkii.quark.building.block;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockSnowBricks extends BlockMod implements IQuarkBlock {
   public BlockSnowBricks() {
      super("snow_bricks", Material.CRAFTED_SNOW, new String[0]);
      this.setHardness(0.2F);
      this.setSoundType(SoundType.SNOW);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public boolean isToolEffective(String type, @Nonnull IBlockState state) {
      return type.equals("shovel");
   }

   public boolean canHarvestBlock(IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
      return true;
   }

   public int quantityDropped(Random random) {
      return 1;
   }

   @Nonnull
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Item.getItemFromBlock(this);
   }
}
