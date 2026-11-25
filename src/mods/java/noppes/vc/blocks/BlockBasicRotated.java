package noppes.vc.blocks;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import noppes.vc.blocks.tiles.TileBasicRotation;

public abstract class BlockBasicRotated extends BlockBasicContainer {
   private Block block;
   public static final PropertyInteger DAMAGE = PropertyInteger.create("damage", 0, 6);

   protected BlockBasicRotated(Block block) {
      super(block.getDefaultState().getMaterial());
      this.block = block;
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      int l = (MathHelper.floor((double)(placer.rotationYaw * (float)this.maxRotation() / 360.0F) + 0.5D) & this.maxRotation() - 1) % this.maxRotation();
      TileBasicRotation tile = (TileBasicRotation)world.getTileEntity(pos);
      Optional.ofNullable(tile).ifPresent((t) -> {
         t.rotation = l;
      });
   }

   public int maxRotation() {
      return 4;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.INVISIBLE;
   }

   public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
      return true;
   }

   public int getMetaFromState(IBlockState state) {
      return this.damageDropped(state);
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(DAMAGE, Integer.valueOf(meta));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{DAMAGE});
   }

   public int damageDropped(IBlockState state) {
      return ((Integer)state.getValue(DAMAGE)).intValue();
   }
}
