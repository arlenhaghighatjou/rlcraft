package rtg.api.world.gen.feature;

import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Collections;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import rtg.api.util.BlockUtil;
import rtg.api.util.Logger;

public class WorldGenFlowersRTG extends WorldGenerator {
   private Collection<EnumFlowerType> flowers;

   public WorldGenFlowersRTG(Collection<EnumFlowerType> flowers) {
      this.flowers = Collections.unmodifiableCollection(flowers);
   }

   public boolean generate(World world, EaglercraftRandom rand, BlockPos pos) {
      if (this.flowers != null && !this.flowers.isEmpty()) {
         IBlockState flower = BlockUtil.getStateFlower((EnumFlowerType)Iterables.get(this.flowers, (new EaglercraftRandom()).nextInt(this.flowers.size())));
         BlockFlower block = (BlockFlower)flower.getBlock();
         if (world.isAirBlock(pos) && pos.getY() < 255 && block.canPlaceBlockAt(world, pos) && block.canBlockStay(world, pos, flower)) {
            world.setBlockState(pos, flower, 2);
            return true;
         } else {
            return false;
         }
      } else {
         Logger.error("Tried to generate a flower from an null or empty collection in Dim: {}, at coords {}", world.provider.getDimension(), pos);
         return false;
      }
   }
}
