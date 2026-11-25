package rtg.api.world.gen.feature;

import java.util.ArrayList;
import java.util.Arrays;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenLayers extends WorldGenerator {
   private IBlockState layerBlock;
   private PropertyInteger layerProperty;
   private int dropHeight;
   private int layerRange;
   private int scatter;
   private ArrayList<IBlockState> validGroundBlocks;

   public WorldGenLayers(IBlockState block, PropertyInteger prop, int dropHeight, int range, int scatter) {
      super(false);
      this.layerBlock = block;
      this.layerProperty = prop;
      this.dropHeight = dropHeight;
      this.layerRange = range;
      this.setScatter(scatter < 1 ? 1 : scatter);
      this.validGroundBlocks = new ArrayList(Arrays.asList(Blocks.GRASS.getDefaultState(), Blocks.DIRT.getDefaultState(), Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.PODZOL), Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.COARSE_DIRT)));
   }

   public WorldGenLayers(IBlockState block, PropertyInteger prop, int dropHeight, int range, int scatter, ArrayList<IBlockState> validGroundBlocks) {
      this(block, prop, dropHeight, range, scatter);
      this.setValidGroundBlocks(validGroundBlocks);
   }

   public boolean generate(World world, EaglercraftRandom rand, BlockPos pos) {
      MutableBlockPos blockpos = new MutableBlockPos();

      for(int x = pos.getX() - this.layerRange; x <= pos.getX() + this.layerRange; ++x) {
         for(int z = pos.getZ() - this.layerRange; z <= pos.getZ() + this.layerRange; ++z) {
            blockpos.setPos(x, pos.getY() + this.dropHeight, z);
            if (world.isAirBlock(blockpos)) {
               blockpos.move(EnumFacing.DOWN);

               while(blockpos.getY() > 0 && world.isAirBlock(blockpos)) {
                  blockpos.move(EnumFacing.DOWN);
               }

               blockpos.move(EnumFacing.UP);
               if (this.layerBlock.getBlock().canPlaceBlockAt(world, blockpos) && rand.nextInt(this.scatter) == 0) {
                  IBlockState state = world.getBlockState(new BlockPos(blockpos.getX(), blockpos.getY() - 1, blockpos.getZ()));
                  if (this.validGroundBlocks.contains(state)) {
                     this.setBlockAndNotifyAdequately(world, blockpos, this.layerBlock);
                  }
               } else {
                  blockpos.move(EnumFacing.DOWN);
                  IBlockState state = world.getBlockState(new BlockPos(blockpos));
                  if (state.getBlock() == this.layerBlock.getBlock()) {
                     int layers = ((Integer)state.getValue(this.layerProperty)).intValue();
                     this.setBlockAndNotifyAdequately(world, blockpos, this.layerBlock.withProperty(this.layerProperty, Integer.valueOf((layers & 7) + 1)));
                  }
               }
            }
         }
      }

      return true;
   }

   public IBlockState getLayerBlock() {
      return this.layerBlock;
   }

   public WorldGenLayers setLayerBlock(IBlockState layerBlock) {
      this.layerBlock = layerBlock;
      return this;
   }

   public PropertyInteger getLayerProperty() {
      return this.layerProperty;
   }

   public WorldGenLayers setLayerProperty(PropertyInteger layerProperty) {
      this.layerProperty = layerProperty;
      return this;
   }

   public int getDropHeight() {
      return this.dropHeight;
   }

   public WorldGenLayers setDropHeight(int dropHeight) {
      this.dropHeight = dropHeight;
      return this;
   }

   public int getLayerRange() {
      return this.layerRange;
   }

   public WorldGenLayers setLayerRange(int layerRange) {
      this.layerRange = layerRange;
      return this;
   }

   public int getScatter() {
      return this.scatter;
   }

   public WorldGenLayers setScatter(int scatter) {
      this.scatter = scatter;
      return this;
   }

   public ArrayList<IBlockState> getValidGroundBlocks() {
      return this.validGroundBlocks;
   }

   public WorldGenLayers setValidGroundBlocks(ArrayList<IBlockState> validGroundBlocks) {
      this.validGroundBlocks = validGroundBlocks;
      return this;
   }
}
