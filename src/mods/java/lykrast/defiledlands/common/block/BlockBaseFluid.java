package lykrast.defiledlands.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBaseFluid extends BlockFluidClassic implements ICustomStateMapper {
   public BlockBaseFluid(Fluid fluid, Material material) {
      super(fluid, material);
   }

   @SideOnly(Side.CLIENT)
   public void setCustomStateMapper() {
      ModelLoader.setCustomStateMapper(this, (new Builder()).ignore(new IProperty[]{LEVEL}).build());
   }
}
