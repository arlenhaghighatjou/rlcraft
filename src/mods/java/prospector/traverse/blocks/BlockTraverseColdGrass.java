package prospector.traverse.blocks;

import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import prospector.traverse.core.TraverseTab;
import prospector.traverse.shadow.shootingstar.ShootingStar;
import prospector.traverse.shadow.shootingstar.model.ModelCompound;
import prospector.traverse.util.TUtils;

public class BlockTraverseColdGrass extends BlockTallGrass {
   public BlockTraverseColdGrass() {
      this.setRegistryName(new ResourceLocation("traverse", "cold_grass"));
      this.setUnlocalizedName(this.getRegistryName().toString());
      this.setCreativeTab(TraverseTab.TAB);
      this.setSoundType(SoundType.PLANT);
      this.useNeighborBrightness = true;
      ShootingStar.registerModel(new ModelCompound("traverse", this, "plant", new IProperty[]{TYPE}));
   }

   public boolean canSustainBush(IBlockState state) {
      return state.getBlock() == TUtils.getBlock("blue_rock") || state.getBlock() == Blocks.ICE || state.getBlock() == Blocks.FROSTED_ICE || state.getBlock() == Blocks.PACKED_ICE;
   }

   @SideOnly(Side.CLIENT)
   public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
      items.add(new ItemStack(this, 1, 0));
   }

   public int damageDropped(IBlockState state) {
      return 0;
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState();
   }

   public int getMetaFromState(IBlockState state) {
      return 0;
   }
}
