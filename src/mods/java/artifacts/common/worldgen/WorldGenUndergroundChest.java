package artifacts.common.worldgen;

import artifacts.common.ModConfig;
import artifacts.common.entity.EntityMimic;
import artifacts.common.init.ModLootTables;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

@ParametersAreNonnullByDefault
public class WorldGenUndergroundChest implements IWorldGenerator {
   private final List<Integer> whitelistedDimensions;

   public WorldGenUndergroundChest(List<Integer> whitelistedDimensions) {
      this.whitelistedDimensions = whitelistedDimensions;
   }

   public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
      if (world.getWorldType() != WorldType.FLAT && this.whitelistedDimensions.contains(Integer.valueOf(world.provider.getDimension()))) {
         for(double chestChance = ModConfig.general.undergroundChestChance; chestChance > 0.0D; --chestChance) {
            if (random.nextDouble() <= chestChance) {
               this.generateChest(world, random, chunkX, chunkZ);
            }
         }

      }
   }

   private void generateChest(World world, Random random, int chunkX, int chunkZ) {
      BlockPos pos = this.getFirstTopSolidBlock(world, new BlockPos(chunkX * 16 + 8 + random.nextInt(16), ModConfig.general.undergroundChestBottomUp ? 1 : ModConfig.general.undergroundChestMaxY, chunkZ * 16 + 8 + random.nextInt(16)), ModConfig.general.undergroundChestBottomUp);
      if (pos != null) {
         if (random.nextDouble() < ModConfig.general.undergroundChestMimicRatio) {
            EntityMimic mimic = new EntityMimic(world);
            mimic.setPositionAndRotation((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, (float)(random.nextInt(4) * 90), 0.0F);
            mimic.setDormant(true);
            mimic.enablePersistence();
            world.spawnEntity(mimic);
         } else {
            world.setBlockState(pos, Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.HORIZONTALS[random.nextInt(4)]), 2);
            TileEntity tileentity = world.getTileEntity(pos);
            if (tileentity instanceof TileEntityChest) {
               ((TileEntityChest)tileentity).setLootTable(ModLootTables.CHEST_UNDERGROUND, random.nextLong());
            }
         }
      }

   }

   @Nullable
   private BlockPos getFirstTopSolidBlock(World world, BlockPos pos, boolean upwards) {
      if (upwards) {
         while(pos.getY() <= ModConfig.general.undergroundChestMaxY) {
            IBlockState downState = world.getBlockState(pos.down());
            if (downState.isSideSolid(world, pos.down(), EnumFacing.UP) && world.isAirBlock(pos)) {
               return pos;
            }

            pos = pos.up();
         }
      } else {
         while(pos.getY() > 1) {
            IBlockState downState = world.getBlockState(pos.down());
            if (downState.isSideSolid(world, pos.down(), EnumFacing.UP) && world.isAirBlock(pos)) {
               return pos;
            }

            pos = pos.down();
         }
      }

      return null;
   }
}
