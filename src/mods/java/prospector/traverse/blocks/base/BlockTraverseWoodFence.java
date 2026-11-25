package prospector.traverse.blocks.base;

import net.minecraft.block.BlockFence;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.ResourceLocation;
import prospector.traverse.core.TraverseTab;
import prospector.traverse.shadow.shootingstar.ShootingStar;
import prospector.traverse.shadow.shootingstar.model.ModelCompound;

public class BlockTraverseWoodFence extends BlockFence {
   public BlockTraverseWoodFence(String name) {
      super(Material.WOOD, MapColor.WOOD);
      this.setRegistryName(new ResourceLocation("traverse", name + "_fence"));
      this.setUnlocalizedName(this.getRegistryName().toString());
      this.setCreativeTab(TraverseTab.TAB);
      this.setSoundType(SoundType.WOOD);
      this.setHardness(2.0F);
      this.setResistance(5.0F);
      ShootingStar.registerModel(new ModelCompound("traverse", this, "fence", new IProperty[0]));
   }
}
