package prospector.traverse.blocks.base;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import prospector.traverse.core.TraverseTab;
import prospector.traverse.shadow.shootingstar.ShootingStar;
import prospector.traverse.shadow.shootingstar.model.ModelCompound;

public class BlockTraverseStairs extends BlockStairs {
   public BlockTraverseStairs(IBlockState modelState, String name) {
      super(modelState);
      this.setRegistryName(new ResourceLocation("traverse", name + "_stairs"));
      this.setCreativeTab(TraverseTab.TAB);
      this.setUnlocalizedName(this.getRegistryName().toString());
      this.useNeighborBrightness = true;
      ShootingStar.registerModel(new ModelCompound("traverse", this, "stairs", new IProperty[0]));
   }
}
