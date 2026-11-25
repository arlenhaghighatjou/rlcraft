package prospector.traverse.blocks.base;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import prospector.traverse.core.TraverseTab;
import prospector.traverse.shadow.shootingstar.ShootingStar;
import prospector.traverse.shadow.shootingstar.model.ModelCompound;

public class BlockTraverseWoodPlanks extends Block {
   public BlockTraverseWoodPlanks(String name) {
      super(Material.WOOD);
      this.setRegistryName(new ResourceLocation("traverse", name + "_planks"));
      this.setUnlocalizedName(this.getRegistryName().toString());
      this.setCreativeTab(TraverseTab.TAB);
      this.setDefaultState(this.blockState.getBaseState());
      this.setHarvestLevel("axe", 0);
      this.setHardness(2.0F);
      this.setResistance(15.0F);
      this.setSoundType(SoundType.WOOD);
      ShootingStar.registerModel(new ModelCompound("traverse", this, "planks", new IProperty[0]));
   }

   public MapColor getMapColor(IBlockState state) {
      return MapColor.WOOD;
   }
}
