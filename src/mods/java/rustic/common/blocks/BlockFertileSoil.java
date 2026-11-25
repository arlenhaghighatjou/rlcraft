package rustic.common.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import rustic.core.Rustic;

public class BlockFertileSoil extends BlockBase {
   public BlockFertileSoil() {
      super(Material.GROUND, "fertile_soil");
      this.setHardness(0.5F);
      this.setCreativeTab(Rustic.farmingTab);
      this.setSoundType(SoundType.GROUND);
   }

   public boolean isFertile(World world, BlockPos pos) {
      return true;
   }

   public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
      EnumPlantType plantType = plantable.getPlantType(world, pos.offset(direction));
      switch(plantType) {
      case Desert:
         return true;
      case Nether:
         return false;
      case Crop:
         return true;
      case Cave:
         return true;
      case Plains:
         return true;
      case Water:
         return false;
      case Beach:
         return true;
      default:
         return super.canSustainPlant(state, world, pos, direction, plantable);
      }
   }
}
