package rustic.common.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.client.renderer.EvaporatingBasinRenderer;
import rustic.common.tileentity.TileEntityEvaporatingBasin;
import rustic.core.Rustic;

public class BlockEvaporatingBasin extends BlockBase implements ITileEntityProvider {
   protected static final AxisAlignedBB BASIN_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.25D, 0.9375D);

   public BlockEvaporatingBasin() {
      super(Material.ROCK, "evaporating_basin");
      this.setHardness(1.25F);
      this.setCreativeTab(Rustic.farmingTab);
      this.setSoundType(SoundType.STONE);
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityEvaporatingBasin();
   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof TileEntityEvaporatingBasin) {
         ((TileEntityEvaporatingBasin)tileentity).breakBlock(worldIn, pos, state);
      }

      worldIn.removeTileEntity(pos);
      super.breakBlock(worldIn, pos, state);
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      return ((TileEntityEvaporatingBasin)world.getTileEntity(pos)).activate(world, pos, state, player, hand, side, hitX, hitY, hitZ);
   }

   public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
      super.eventReceived(state, worldIn, pos, id, param);
      TileEntity tileentity = worldIn.getTileEntity(pos);
      return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return BASIN_AABB;
   }

   @SideOnly(Side.CLIENT)
   public void initModel() {
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
      ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEvaporatingBasin.class, new EvaporatingBasinRenderer());
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
      return BlockFaceShape.UNDEFINED;
   }
}
