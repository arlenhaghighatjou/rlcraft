package paulevs.betternether.blocks;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.BetterNether;

public class BlockEyeBase extends Block {
   public BlockEyeBase() {
      super(Material.WOOD, MapColor.BROWN);
      this.setSoundType(SoundType.SLIME);
      this.setHardness(0.5F);
      this.setResistance(0.5F);
      this.setCreativeTab(BetterNether.BN_TAB);
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      Block up = worldIn.getBlockState(pos.up()).getBlock();
      if (up != BlocksRegister.BLOCK_EYE_VINE) {
         worldIn.destroyBlock(pos, true);
         this.spawnSeeds(worldIn, pos, worldIn.rand);
      }

   }

   private void spawnSeeds(World world, BlockPos pos, Random random) {
      if (!world.isRemote) {
         ItemStack drop = new ItemStack(BlocksRegister.BLOCK_EYE_SEED, 1 + random.nextInt(3));
         EntityItem itemEntity = new EntityItem(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, drop);
         world.spawnEntity(itemEntity);
      }

   }

   public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
      this.spawnSeeds(worldIn, pos, worldIn.rand);
      worldIn.destroyBlock(pos, true);
   }

   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.SLIME_BALL;
   }
}
