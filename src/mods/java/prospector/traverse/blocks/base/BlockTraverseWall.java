package prospector.traverse.blocks.base;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWall;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import prospector.traverse.core.TraverseTab;
import prospector.traverse.shadow.shootingstar.ShootingStar;
import prospector.traverse.shadow.shootingstar.model.ModelCompound;

public class BlockTraverseWall extends BlockWall {
   public BlockTraverseWall(Block modelBlock, String name) {
      super(modelBlock);
      this.setRegistryName(new ResourceLocation("traverse", name + "_wall"));
      this.setCreativeTab(TraverseTab.TAB);
      this.setUnlocalizedName(this.getRegistryName().toString());
      ShootingStar.registerModel(new ModelCompound("traverse", this, "wall", new IProperty[]{VARIANT}));
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
