package vazkii.quark.world.world.underground;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStone.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import vazkii.quark.base.module.ConfigHelper;
import vazkii.quark.world.feature.RevampStoneGen;
import vazkii.quark.world.world.UndergroundBiomeGenerator;

public abstract class UndergroundBiome {
   public double dungeonChance;
   public static final Predicate<IBlockState> STONE_PREDICATE = (state) -> {
      if (state != null) {
         Block block = state.getBlock();
         if (block == Blocks.STONE) {
            EnumType type = (EnumType)state.getValue(BlockStone.VARIANT);
            return type.isNatural();
         } else {
            return block == RevampStoneGen.limestone || block == RevampStoneGen.marble;
         }
      } else {
         return false;
      }
   };

   public UndergroundBiome() {
   }

   public void fill(World world, BlockPos pos, UndergroundBiomeGenerator.UndergroundBiomeGenerationContext context) {
      IBlockState state = world.getBlockState(pos);
      if (state.getBlockHardness(world, pos) != -1.0F && !world.canBlockSeeSky(pos)) {
         if (this.isFloor(world, pos, state)) {
            context.floorList.add(pos);
            this.fillFloor(world, pos, state);
         } else if (this.isCeiling(world, pos, state)) {
            context.ceilingList.add(pos);
            this.fillCeiling(world, pos, state);
         } else if (this.isWall(world, pos, state)) {
            context.wallMap.put(pos, this.getBorderSide(world, pos));
            this.fillWall(world, pos, state);
         } else if (this.isInside(state)) {
            context.insideList.add(pos);
            this.fillInside(world, pos, state);
         }

      }
   }

   public abstract void fillFloor(World var1, BlockPos var2, IBlockState var3);

   public abstract void fillCeiling(World var1, BlockPos var2, IBlockState var3);

   public abstract void fillWall(World var1, BlockPos var2, IBlockState var3);

   public abstract void fillInside(World var1, BlockPos var2, IBlockState var3);

   public void finalFloorPass(World world, BlockPos pos) {
   }

   public void finalCeilingPass(World world, BlockPos pos) {
   }

   public void finalWallPass(World world, BlockPos pos) {
   }

   public void finalInsidePass(World world, BlockPos pos) {
   }

   public final void setupBaseConfig(String category) {
      if (this.hasDungeon()) {
         this.dungeonChance = ConfigHelper.loadLegacyPropChance("Dungeon Spawn Percentage Chance", category, "Dungeon Spawn Chance", "The chance that dungeons will spawn any given chunk of the biome. The lower the value, the fewer dungeons will spawn.", (double)this.getDungeonChance());
      }

      this.setupConfig(category);
   }

   public void setupConfig(String category) {
   }

   public boolean isValidBiome(Biome biome) {
      return true;
   }

   public boolean hasDungeon() {
      return false;
   }

   public float getDungeonChance() {
      return 0.05F;
   }

   public void spawnDungeon(WorldServer world, BlockPos pos, EnumFacing face) {
   }

   public boolean isFloor(World world, BlockPos pos, IBlockState state) {
      if (state.isFullBlock() && state.isOpaqueCube()) {
         BlockPos upPos = pos.up();
         return world.isAirBlock(upPos) || world.getBlockState(upPos).getBlock().isReplaceable(world, upPos);
      } else {
         return false;
      }
   }

   public boolean isCeiling(World world, BlockPos pos, IBlockState state) {
      if (state.isFullBlock() && state.isOpaqueCube()) {
         BlockPos downPos = pos.down();
         return world.isAirBlock(downPos) || world.getBlockState(downPos).getBlock().isReplaceable(world, downPos);
      } else {
         return false;
      }
   }

   public boolean isWall(World world, BlockPos pos, IBlockState state) {
      return state.isFullBlock() && state.isOpaqueCube() && STONE_PREDICATE.test(state) ? this.isBorder(world, pos) : false;
   }

   public EnumFacing getBorderSide(World world, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);

      for(EnumFacing facing : EnumFacing.HORIZONTALS) {
         BlockPos offsetPos = pos.offset(facing);
         IBlockState stateAt = world.getBlockState(offsetPos);
         if (state != stateAt && world.isAirBlock(offsetPos) || stateAt.getBlock().isReplaceable(world, offsetPos)) {
            return facing;
         }
      }

      return null;
   }

   public boolean isBorder(World world, BlockPos pos) {
      return this.getBorderSide(world, pos) != null;
   }

   public boolean isInside(IBlockState state) {
      return STONE_PREDICATE.test(state);
   }

   public static Rotation rotationFromFacing(EnumFacing facing) {
      switch(facing) {
      case SOUTH:
         return Rotation.CLOCKWISE_180;
      case WEST:
         return Rotation.COUNTERCLOCKWISE_90;
      case EAST:
         return Rotation.CLOCKWISE_90;
      default:
         return Rotation.NONE;
      }
   }
}
