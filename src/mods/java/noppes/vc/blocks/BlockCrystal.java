package noppes.vc.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.vc.VCTabs;

public class BlockCrystal extends BlockBreakable {
   public static final PropertyInteger DAMAGE = PropertyInteger.create("damage", 0, 15);

   public BlockCrystal() {
      super(Material.GLASS, false);
      this.setSoundType(SoundType.GLASS);
      this.setLightLevel(0.8F);
      this.setHardness(5.0F);
      this.setResistance(10.0F);
      this.setCreativeTab(VCTabs.BLOCKS);
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isTopSolid(IBlockState state) {
      return true;
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.TRANSLUCENT;
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

   @SideOnly(Side.CLIENT)
   public void getSubBlocks(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
      for(int i = 0; i < 16; ++i) {
         par3List.add(new ItemStack(this, 1, i));
      }

   }

   public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return MapColor.getBlockColor(EnumDyeColor.byDyeDamage(this.damageDropped(state)));
   }

   public String getUnlocalizedName() {
      return "item.crystal";
   }

   public Block register(String name) {
      this.setUnlocalizedName(name);
      this.setRegistryName("variedcommodities", name);
      return this;
   }
}
