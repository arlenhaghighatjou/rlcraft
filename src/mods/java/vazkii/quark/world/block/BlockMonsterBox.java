package vazkii.quark.world.block;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockModContainer;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.world.tile.TileMonsterBox;

public class BlockMonsterBox extends BlockModContainer implements IQuarkBlock {
   public BlockMonsterBox() {
      super("monster_box", Material.IRON, new String[0]);
      this.setHardness(25.0F);
      this.setSoundType(SoundType.METAL);
   }

   public int quantityDropped(Random random) {
      return 0;
   }

   @Nonnull
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.AIR;
   }

   @Nonnull
   public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
      return ItemStack.EMPTY;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   @Nonnull
   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
      return new TileMonsterBox();
   }
}
