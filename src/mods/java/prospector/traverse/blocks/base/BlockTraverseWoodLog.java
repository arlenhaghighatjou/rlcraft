package prospector.traverse.blocks.base;

import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import prospector.traverse.core.TraverseTab;
import prospector.traverse.shadow.shootingstar.ShootingStar;
import prospector.traverse.shadow.shootingstar.model.ModelCompound;

public class BlockTraverseWoodLog extends BlockLog {
   public BlockTraverseWoodLog(String name) {
      this.setRegistryName(new ResourceLocation("traverse", name + "_log"));
      this.setCreativeTab(TraverseTab.TAB);
      this.setUnlocalizedName(this.getRegistryName().toString());
      this.setDefaultState(this.blockState.getBaseState().withProperty(LOG_AXIS, EnumAxis.Y));
      ShootingStar.registerModel(new ModelCompound("traverse", this, "log", new IProperty[0]));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{LOG_AXIS});
   }

   public int getMetaFromState(IBlockState state) {
      return ((EnumAxis)state.getValue(LOG_AXIS)).ordinal();
   }

   public IBlockState getStateFromMeta(int meta) {
      for(EnumAxis axis : EnumAxis.values()) {
         if (axis.ordinal() == meta) {
            return this.getDefaultState().withProperty(LOG_AXIS, axis);
         }
      }

      return this.getDefaultState();
   }

   public int damageDropped(IBlockState state) {
      return 0;
   }
}
