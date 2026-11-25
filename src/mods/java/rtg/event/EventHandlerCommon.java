package rtg.event;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import rtg.RTGConfig;
import rtg.api.RTGAPI;
import rtg.api.util.BlockUtil;
import rtg.api.util.Logger;
import rtg.api.world.biome.IRealisticBiome;
import rtg.api.world.deco.DecoBase;
import rtg.api.world.gen.feature.tree.rtg.TreeRTG;
import rtg.world.biome.BiomeProviderRTG;

public final class EventHandlerCommon {
   private EventHandlerCommon() {
   }

   public static void init() {
      MinecraftForge.TERRAIN_GEN_BUS.register(EventHandlerCommon.class);
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onDecorateBiome(Decorate event) {
      World world = event.getWorld();
      if (world.getBiomeProvider() instanceof BiomeProviderRTG) {
         EventType eventType = event.getType();
         if (eventType == EventType.LAKE_WATER || eventType == EventType.LAKE_LAVA) {
            event.setResult(Result.DENY);
            generateFalls(world, event.getRand(), event.getChunkPos(), eventType);
         }
      }

   }

   private static void generateFalls(World world, Random rand, ChunkPos chunkPos, EventType type) {
      BlockPos offsetpos = new BlockPos(chunkPos.x * 16 + 8, 0, chunkPos.z * 16 + 8);
      switch(type) {
      case LAKE_WATER:
         for(int i = 0; i < 20; ++i) {
            (new WorldGenLiquids(Blocks.FLOWING_WATER)).generate(world, rand, offsetpos.add(rand.nextInt(16), rand.nextInt(64) + 8, rand.nextInt(16)));
         }

         return;
      case LAKE_LAVA:
         for(int i = 0; i < 5; ++i) {
            (new WorldGenLiquids(Blocks.FLOWING_LAVA)).generate(world, rand, offsetpos.add(rand.nextInt(16), rand.nextInt(rand.nextInt(rand.nextInt(64) + 8) + 8), rand.nextInt(16)));
         }
      }

   }

   @SubscribeEvent
   public static void saplingGrowTreeRTG(SaplingGrowTreeEvent event) {
      World world = event.getWorld();
      if (RTGConfig.rtgTreesFromSaplings() && world.getBiomeProvider() instanceof BiomeProviderRTG) {
         BlockPos pos = event.getPos();
         IBlockState saplingBlock = world.getBlockState(pos);
         Logger.trace("Handling SaplingGrowTreeEvent in dim: {}, at: {}, for: {}", world.provider.getDimension(), pos, saplingBlock);
         if (!(saplingBlock.getBlock() instanceof BlockSapling)) {
            Logger.debug("[SaplingGrowTreeEvent] Aborting: Sapling is not a sapling block ({})", saplingBlock.getBlock().getClass().getName());
         } else {
            Random rand = event.getRand();
            int chance = RTGConfig.rtgTreeChance();
            if (rand.nextInt(chance < 1 ? 1 : chance) != 0) {
               Logger.debug("[SaplingGrowTreeEvent] Aborting RTG tree generation: random chance");
            } else {
               IRealisticBiome rtgBiome = RTGAPI.getRTGBiome(world.getBiome(pos));
               Collection<TreeRTG> biomeTrees = rtgBiome.getTrees();
               if (biomeTrees.isEmpty()) {
                  Logger.debug("[SaplingGrowTreeEvent] Aborting RTG tree generation: No RTG trees to generate in Biome: {}", rtgBiome.baseBiomeResLoc());
               } else {
                  List<TreeRTG> validTrees = (List)biomeTrees.stream().filter((treex) -> {
                     return saplingBlock.getBlock() == tree.getSaplingBlock().getBlock() && BlockUtil.getTypeFromSapling(saplingBlock) == BlockUtil.getTypeFromSapling(tree.getSaplingBlock());
                  }).collect(Collectors.toList());
                  if (validTrees.isEmpty()) {
                     Logger.debug("[SaplingGrowTreeEvent] No RTG trees found for sapling, so generating original tree instead");
                  } else {
                     TreeRTG tree = (TreeRTG)validTrees.get(rand.nextInt(validTrees.size()));
                     if (tree.getMinTrunkSize() > 0 && tree.getMaxTrunkSize() > tree.getMinTrunkSize()) {
                        tree.setTrunkSize(DecoBase.getRangedRandom(rand, tree.getMinTrunkSize(), tree.getMaxTrunkSize()));
                     }

                     if (tree.getMinCrownSize() > 0 && tree.getMaxCrownSize() > tree.getMinCrownSize()) {
                        tree.setCrownSize(DecoBase.getRangedRandom(rand, tree.getMinCrownSize(), tree.getMaxCrownSize()));
                     }

                     int treeHeight = tree.getTrunkSize() + tree.getCrownSize();
                     if (treeHeight < 1) {
                        Logger.debug("[SaplingGrowTreeEvent] Unable to grow RTG tree with no height: {}[logblock={}, leafblock={}, saplingblock={}]", tree.getClass().getSimpleName(), tree.getLogBlock(), tree.getLeavesBlock(), tree.getSaplingBlock());
                     } else if (!BlockUtil.checkVerticalMaterials(BlockUtil.MatchType.ALL_IGNORE_REPLACEABLE, world, pos.up(), treeHeight - 1)) {
                        Logger.debug("[SaplingGrowTreeEvent] Aborting RTG tree generation: not enough space above");
                     } else {
                        int oldFlag = tree.getGenerateFlag();
                        tree.setGenerateFlag(3);
                        boolean generated = tree.generate(world, rand, pos);
                        tree.setGenerateFlag(oldFlag);
                        if (generated) {
                           event.setResult(Result.DENY);
                           if (world.getBlockState(pos) == saplingBlock) {
                              world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
                           }
                        }

                        Logger.trace("Finished handling SaplingGrowTreeEvent in Biome: {}, with sapling: {}", rtgBiome.baseBiomeResLoc(), saplingBlock);
                     }
                  }
               }
            }
         }
      } else {
         Logger.debug("[SaplingGrowTreeEvent] Aborting: RTG trees are disabled, or not an RTG dimension");
      }
   }
}
