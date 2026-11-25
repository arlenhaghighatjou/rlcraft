package rtg.api.world.gen.feature;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenVinesRTG extends WorldGenerator {
   protected Block vineBlock;
   protected int maxY;
   protected PropertyBool propNorth;
   protected PropertyBool propEast;
   protected PropertyBool propSouth;
   protected PropertyBool propWest;

   public WorldGenVinesRTG() {
      this.vineBlock = Blocks.VINE;
      this.setMaxY(254);
      this.propNorth = BlockVine.NORTH;
      this.propEast = BlockVine.EAST;
      this.propSouth = BlockVine.SOUTH;
      this.propWest = BlockVine.WEST;
   }

   public WorldGenVinesRTG(Block vineBlock, int maxY, PropertyBool propNorth, PropertyBool propEast, PropertyBool propSouth, PropertyBool propWest) {
      this();
      this.vineBlock = vineBlock;
      this.maxY = maxY;
      this.propNorth = propNorth;
      this.propEast = propEast;
      this.propSouth = propSouth;
      this.propWest = propWest;
   }

   public boolean generate(World worldIn, EaglercraftRandom rand, BlockPos position) {
      for(; position.getY() < this.maxY; position = position.up()) {
         if (worldIn.isAirBlock(position)) {
            Block north = worldIn.getBlockState(position.north()).getBlock();
            Block south = worldIn.getBlockState(position.south()).getBlock();
            Block east = worldIn.getBlockState(position.east()).getBlock();
            Block west = worldIn.getBlockState(position.west()).getBlock();

            for(EnumFacing enumfacing : Plane.HORIZONTAL.facings()) {
               if (this.vineBlock.canPlaceBlockOnSide(worldIn, position, enumfacing)) {
                  this.addVine(worldIn, rand, position, enumfacing);
                  break;
               }
            }
         } else {
            position = position.add(rand.nextInt(4) - rand.nextInt(4), 0, rand.nextInt(4) - rand.nextInt(4));
         }
      }

      return true;
   }

   protected void addVine(World worldIn, EaglercraftRandom rand, BlockPos pos, EnumFacing enumfacing) {
      IBlockState iblockstate = this.vineBlock.getDefaultState().withProperty(this.propNorth, Boolean.valueOf(enumfacing == EnumFacing.SOUTH)).withProperty(this.propEast, Boolean.valueOf(enumfacing == EnumFacing.WEST)).withProperty(this.propSouth, Boolean.valueOf(enumfacing == EnumFacing.NORTH)).withProperty(this.propWest, Boolean.valueOf(enumfacing == EnumFacing.EAST));
      this.setBlockAndNotifyAdequately(worldIn, pos, iblockstate);
      int i = rand.nextInt(4) + 1;

      for(BlockPos var7 = pos.down(); worldIn.isAirBlock(var7) && i > 0; --i) {
         this.setBlockAndNotifyAdequately(worldIn, var7, iblockstate);
         var7 = var7.down();
      }

   }

   public Block getVineBlock() {
      return this.vineBlock;
   }

   public WorldGenVinesRTG setVineBlock(Block vineBlock) {
      this.vineBlock = vineBlock;
      return this;
   }

   public int getMaxY() {
      return this.maxY;
   }

   public WorldGenVinesRTG setMaxY(int maxY) {
      this.maxY = maxY;
      return this;
   }

   public PropertyBool getPropNorth() {
      return this.propNorth;
   }

   public WorldGenVinesRTG setPropNorth(PropertyBool propNorth) {
      this.propNorth = propNorth;
      return this;
   }

   public PropertyBool getPropEast() {
      return this.propEast;
   }

   public WorldGenVinesRTG setPropEast(PropertyBool propEast) {
      this.propEast = propEast;
      return this;
   }

   public PropertyBool getPropSouth() {
      return this.propSouth;
   }

   public WorldGenVinesRTG setPropSouth(PropertyBool propSouth) {
      this.propSouth = propSouth;
      return this;
   }

   public PropertyBool getPropWest() {
      return this.propWest;
   }

   public WorldGenVinesRTG setPropWest(PropertyBool propWest) {
      this.propWest = propWest;
      return this;
   }
}
