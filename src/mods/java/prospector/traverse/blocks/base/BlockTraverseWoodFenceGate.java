package prospector.traverse.blocks.base;

import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.ResourceLocation;
import prospector.traverse.core.TraverseMod;
import prospector.traverse.core.TraverseTab;
import prospector.traverse.shadow.shootingstar.ShootingStar;
import prospector.traverse.shadow.shootingstar.model.ModelCompound;

public class BlockTraverseWoodFenceGate extends BlockFenceGate {
   public BlockTraverseWoodFenceGate(String name) {
      super(EnumType.OAK);
      this.setRegistryName(new ResourceLocation("traverse", name + "_fence_gate"));
      this.setUnlocalizedName(this.getRegistryName().toString());
      this.setCreativeTab(TraverseTab.TAB);
      this.setSoundType(SoundType.WOOD);
      this.setHardness(2.0F);
      this.setResistance(5.0F);
      TraverseMod.blockModelsToRegister.add(this);
      ShootingStar.registerModel(new ModelCompound("traverse", this, "fence", new IProperty[]{POWERED, IN_WALL}));
   }
}
