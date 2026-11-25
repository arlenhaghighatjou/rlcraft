package vazkii.quark.world.world;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkGeneratorFlat;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import vazkii.quark.world.feature.MonsterBoxes;

public class MonsterBoxGenerator implements IWorldGenerator {
   public MonsterBoxGenerator() {
   }

   public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
      if (MonsterBoxes.dimensions.canSpawnHere(world) && !(chunkGenerator instanceof ChunkGeneratorFlat)) {
         for(float chance = MonsterBoxes.chunkChance; world.rand.nextFloat() <= chance; chance -= MonsterBoxes.chunkChance) {
            int x = chunkX * 16 + 8 + random.nextInt(16);
            int y = MonsterBoxes.minY + random.nextInt(MonsterBoxes.maxY - MonsterBoxes.minY + 1);
            int z = chunkZ * 16 + 8 + random.nextInt(16);
            BlockPos pos = new BlockPos(x, y, z);
            if (world.isAirBlock(pos)) {
               BlockPos testPos = pos;

               while(true) {
                  testPos = testPos.down();
                  IBlockState testState = world.getBlockState(testPos);
                  if (testState.getMaterial() == Material.ROCK || testPos.getY() < MonsterBoxes.minY) {
                     break;
                  }
               }

               BlockPos placePos = testPos.up();
               if (testPos.getY() >= MonsterBoxes.minY && world.isAirBlock(placePos)) {
                  world.setBlockState(placePos, MonsterBoxes.monster_box.getDefaultState());
               }
            }
         }

      }
   }
}
