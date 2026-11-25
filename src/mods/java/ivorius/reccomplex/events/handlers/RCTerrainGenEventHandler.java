package ivorius.reccomplex.events.handlers;

import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.world.gen.feature.decoration.RCBiomeDecorator;
import ivorius.reccomplex.world.gen.feature.sapling.RCSaplingGenerator;
import ivorius.reccomplex.world.gen.feature.structure.MapGenStructureHook;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenNetherBridge;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class RCTerrainGenEventHandler {
   public RCTerrainGenEventHandler() {
   }

   private static boolean hasAmountData(Decorate event) {
      try {
         event.getClass().getDeclaredMethod("getModifiedAmount");
         event.getClass().getDeclaredMethod("setModifiedAmount", Integer.TYPE);
         return ((Boolean)event.getClass().getDeclaredMethod("hasAmountData").invoke(event)).booleanValue();
      } catch (Exception var2) {
         return false;
      }
   }

   private static int getModifiedAmount(Decorate event) {
      try {
         return ((Integer)event.getClass().getDeclaredMethod("getModifiedAmount").invoke(event)).intValue();
      } catch (Exception var2) {
         return -1;
      }
   }

   private static void setModifiedAmount(Decorate event, int amount) {
      try {
         event.getClass().getDeclaredMethod("setModifiedAmount", Integer.TYPE).invoke(event, amount);
      } catch (Exception var3) {
         ;
      }

   }

   public void register() {
      MinecraftForge.TERRAIN_GEN_BUS.register(this);
   }

   @SubscribeEvent
   public void onSaplingGrow(SaplingGrowTreeEvent event) {
      if (event.getWorld() instanceof WorldServer && RCSaplingGenerator.maybeGrowSapling((WorldServer)event.getWorld(), event.getPos(), event.getRand())) {
         event.setResult(Result.DENY);
      }

   }

   @SubscribeEvent
   public void onDecoration(Decorate event) {
      if (event.getWorld() instanceof WorldServer) {
         RCBiomeDecorator.DecorationType type = RCBiomeDecorator.DecorationType.getDecorationType(event);
         if (type != null) {
            int amount;
            if (hasAmountData(event) && (amount = getModifiedAmount(event)) >= 0) {
               setModifiedAmount(event, RCBiomeDecorator.decorate((WorldServer)event.getWorld(), event.getRand(), event.getPos(), type, amount));
            } else {
               Result result = RCBiomeDecorator.decorate((WorldServer)event.getWorld(), event.getRand(), event.getPos(), type);
               if (result != null) {
                  event.setResult(result);
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onInitMapGen(InitMapGenEvent event) {
      if (RCConfig.decorationHacks) {
         EventType type = event.getType();
         switch(type) {
         case OCEAN_MONUMENT:
            final MapGenStructureHook hook = new MapGenStructureHook((MapGenStructure)event.getNewGen(), RCBiomeDecorator.DecorationType.OCEAN_MONUMENT);
            event.setNewGen(new StructureOceanMonument() {
               public String getStructureName() {
                  return hook.getStructureName();
               }

               public boolean generateStructure(World worldIn, EaglercraftRandom randomIn, ChunkPos chunkCoord) {
                  return hook.generateStructure(worldIn, randomIn, chunkCoord);
               }

               public boolean isInsideStructure(BlockPos pos) {
                  return hook.isInsideStructure(pos);
               }

               public boolean isPositionInStructure(World worldIn, BlockPos pos) {
                  return hook.isPositionInStructure(worldIn, pos);
               }

               @Nullable
               public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored) {
                  return hook.getNearestStructurePos(worldIn, pos, findUnexplored);
               }

               public void generate(World worldIn, int x, int z, ChunkPrimer primer) {
                  hook.generate(worldIn, x, z, primer);
               }
            });
            break;
         case SCATTERED_FEATURE:
            final MapGenStructureHook hook = new MapGenStructureHook((MapGenStructure)event.getNewGen(), RCBiomeDecorator.DecorationType.SCATTERED_FEATURE);
            event.setNewGen(new MapGenScatteredFeature() {
               public String getStructureName() {
                  return hook.getStructureName();
               }

               public boolean generateStructure(World worldIn, EaglercraftRandom randomIn, ChunkPos chunkCoord) {
                  return hook.generateStructure(worldIn, randomIn, chunkCoord);
               }

               public boolean isInsideStructure(BlockPos pos) {
                  return hook.isInsideStructure(pos);
               }

               public boolean isPositionInStructure(World worldIn, BlockPos pos) {
                  return hook.isPositionInStructure(worldIn, pos);
               }

               @Nullable
               public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored) {
                  return hook.getNearestStructurePos(worldIn, pos, findUnexplored);
               }

               public void generate(World worldIn, int x, int z, ChunkPrimer primer) {
                  hook.generate(worldIn, x, z, primer);
               }
            });
            break;
         case VILLAGE:
            final MapGenStructureHook hook = new MapGenStructureHook((MapGenStructure)event.getNewGen(), RCBiomeDecorator.DecorationType.VILLAGE);
            event.setNewGen(new MapGenVillage() {
               public String getStructureName() {
                  return hook.getStructureName();
               }

               public boolean generateStructure(World worldIn, EaglercraftRandom randomIn, ChunkPos chunkCoord) {
                  return hook.generateStructure(worldIn, randomIn, chunkCoord);
               }

               public boolean isInsideStructure(BlockPos pos) {
                  return hook.isInsideStructure(pos);
               }

               public boolean isPositionInStructure(World worldIn, BlockPos pos) {
                  return hook.isPositionInStructure(worldIn, pos);
               }

               @Nullable
               public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored) {
                  return hook.getNearestStructurePos(worldIn, pos, findUnexplored);
               }

               public void generate(World worldIn, int x, int z, ChunkPrimer primer) {
                  hook.generate(worldIn, x, z, primer);
               }
            });
            break;
         case NETHER_BRIDGE:
            final MapGenStructureHook hook = new MapGenStructureHook((MapGenStructure)event.getNewGen(), RCBiomeDecorator.DecorationType.NETHER_BRIDGE);
            event.setNewGen(new MapGenNetherBridge() {
               public String getStructureName() {
                  return hook.getStructureName();
               }

               public boolean generateStructure(World worldIn, EaglercraftRandom randomIn, ChunkPos chunkCoord) {
                  return hook.generateStructure(worldIn, randomIn, chunkCoord);
               }

               public boolean isInsideStructure(BlockPos pos) {
                  return hook.isInsideStructure(pos);
               }

               public boolean isPositionInStructure(World worldIn, BlockPos pos) {
                  return hook.isPositionInStructure(worldIn, pos);
               }

               @Nullable
               public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored) {
                  return hook.getNearestStructurePos(worldIn, pos, findUnexplored);
               }

               public void generate(World worldIn, int x, int z, ChunkPrimer primer) {
                  hook.generate(worldIn, x, z, primer);
               }
            });
            break;
         case STRONGHOLD:
            final MapGenStructureHook hook = new MapGenStructureHook((MapGenStructure)event.getNewGen(), RCBiomeDecorator.DecorationType.STRONGHOLD);
            event.setNewGen(new MapGenStronghold() {
               public String getStructureName() {
                  return hook.getStructureName();
               }

               public boolean generateStructure(World worldIn, EaglercraftRandom randomIn, ChunkPos chunkCoord) {
                  return hook.generateStructure(worldIn, randomIn, chunkCoord);
               }

               public boolean isInsideStructure(BlockPos pos) {
                  return hook.isInsideStructure(pos);
               }

               public boolean isPositionInStructure(World worldIn, BlockPos pos) {
                  return hook.isPositionInStructure(worldIn, pos);
               }

               @Nullable
               public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored) {
                  return hook.getNearestStructurePos(worldIn, pos, findUnexplored);
               }

               public void generate(World worldIn, int x, int z, ChunkPrimer primer) {
                  hook.generate(worldIn, x, z, primer);
               }
            });
            break;
         case MINESHAFT:
            final MapGenStructureHook hook = new MapGenStructureHook((MapGenStructure)event.getNewGen(), RCBiomeDecorator.DecorationType.MINESHAFT);
            event.setNewGen(new MapGenMineshaft() {
               public String getStructureName() {
                  return hook.getStructureName();
               }

               public boolean generateStructure(World worldIn, EaglercraftRandom randomIn, ChunkPos chunkCoord) {
                  return hook.generateStructure(worldIn, randomIn, chunkCoord);
               }

               public boolean isInsideStructure(BlockPos pos) {
                  return hook.isInsideStructure(pos);
               }

               public boolean isPositionInStructure(World worldIn, BlockPos pos) {
                  return hook.isPositionInStructure(worldIn, pos);
               }

               @Nullable
               public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored) {
                  return hook.getNearestStructurePos(worldIn, pos, findUnexplored);
               }

               public void generate(World worldIn, int x, int z, ChunkPrimer primer) {
                  hook.generate(worldIn, x, z, primer);
               }
            });
         }
      }

   }
}
