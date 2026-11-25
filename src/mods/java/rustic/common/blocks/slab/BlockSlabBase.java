package rustic.common.blocks.slab;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSlabBase extends BlockSlab {
   public Item itemBlock = null;

   public BlockSlabBase(Material materialIn, String name, Block block) {
      super(materialIn);
      this.setRegistryName(name);
      this.setUnlocalizedName("rustic." + name);
      GameRegistry.findRegistry(Block.class).register(this);
      this.setHardness(2.0F);
      this.useNeighborBrightness = true;
   }

   public BlockSlabBase(Material materialIn, String name, Block block, SoundType sound) {
      super(materialIn);
      this.setRegistryName(name);
      this.setUnlocalizedName("rustic." + name);
      GameRegistry.findRegistry(Block.class).register(this);
      this.setHardness(2.0F);
      this.useNeighborBrightness = true;
      this.setSoundType(sound);
   }

   @SideOnly(Side.CLIENT)
   protected static boolean isHalfSlab(IBlockState state) {
      return true;
   }

   public int damageDropped(IBlockState state) {
      return this.getMetaFromState(state.withProperty(HALF, EnumBlockHalf.BOTTOM));
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public void initModel() {
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
   }

   public IBlockState getStateFromMeta(int meta) {
      IBlockState iblockstate = this.getDefaultState();
      if (!this.isDouble()) {
         iblockstate = iblockstate.withProperty(HALF, meta == 0 ? EnumBlockHalf.BOTTOM : EnumBlockHalf.TOP);
      }

      return iblockstate;
   }

   public int getMetaFromState(IBlockState state) {
      return state.getValue(HALF) == EnumBlockHalf.BOTTOM ? 0 : 1;
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{HALF});
   }

   public boolean isDouble() {
      return false;
   }

   public String getUnlocalizedName(int meta) {
      return null;
   }

   public IProperty getVariantProperty() {
      return HALF;
   }

   public Comparable<?> getTypeForItem(ItemStack stack) {
      return Integer.valueOf(0);
   }
}
