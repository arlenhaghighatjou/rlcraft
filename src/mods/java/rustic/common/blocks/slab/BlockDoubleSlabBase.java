package rustic.common.blocks.slab;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDoubleSlabBase extends BlockSlab {
   public Item itemBlock = null;
   private Block slab;

   public BlockDoubleSlabBase(Material materialIn, String name) {
      super(materialIn);
      this.setRegistryName(name);
      this.setUnlocalizedName("rustic." + name);
      GameRegistry.findRegistry(Block.class).register(this);
      this.setHardness(2.0F);
   }

   public BlockDoubleSlabBase(Material materialIn, String name, SoundType sound) {
      super(materialIn);
      this.setRegistryName(name);
      this.setUnlocalizedName("rustic." + name);
      GameRegistry.findRegistry(Block.class).register(this);
      this.setHardness(2.0F);
      this.setSoundType(sound);
   }

   public void setSlab(Block slab) {
      this.slab = slab;
   }

   public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      ArrayList<ItemStack> drops = new ArrayList();
      drops.add(new ItemStack(Item.getItemFromBlock(this.slab), 1));
      drops.add(new ItemStack(Item.getItemFromBlock(this.slab), 1));
      return drops;
   }

   public void initModel() {
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName().toString(), "inventory"));
   }

   public String getUnlocalizedName(int meta) {
      return null;
   }

   public boolean isDouble() {
      return true;
   }

   @SideOnly(Side.CLIENT)
   protected static boolean isHalfSlab(IBlockState state) {
      return true;
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return new ItemStack(Item.getItemFromBlock(this.slab));
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

   public IProperty<?> getVariantProperty() {
      return HALF;
   }

   public Comparable<?> getTypeForItem(ItemStack stack) {
      return Integer.valueOf(0);
   }
}
