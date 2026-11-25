package ivorius.reccomplex.commands;

import ivorius.ivtoolkit.blocks.BlockSurfacePos;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex..shadow.mcopts.commands.CommandSplit;
import ivorius.reccomplex..shadow.mcopts.commands.SimpleCommand;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.MCP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameter;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.MCE;
import ivorius.reccomplex.commands.parameters.IvP;
import ivorius.reccomplex.commands.parameters.expect.IvE;
import ivorius.reccomplex.commands.parameters.expect.RCE;
import ivorius.reccomplex.commands.structure.sight.CommandSightCheck;
import ivorius.reccomplex.random.Person;
import ivorius.reccomplex.utils.RCBlockAreas;
import ivorius.reccomplex.utils.RCStrings;
import ivorius.reccomplex.utils.RCStructureBoundingBoxes;
import ivorius.reccomplex.utils.accessor.RCAccessorMapGenStructure;
import ivorius.reccomplex.utils.accessor.SafeReflector;
import ivorius.reccomplex.world.gen.feature.structure.MapGenStructureHook;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenNetherBridge;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureOceanMonument;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class CommandVanilla extends CommandSplit {
   private static Method recursiveGenerate;

   public CommandVanilla() {
      super(RCConfig.commandPrefix + "vanilla");
      this.add(new SimpleCommand("gen", (expect) -> {
         expect.any((Object[])CommandVanilla.Type.values()).then(IvE.surfacePos("x", "z")).named("dimension", "d").then(MCE::dimension).named("seed").then(RCE::randomString).named("biome", "b").then(MCE::biome).flag("select", "s").flag("suggest", "t");
      }) {
         public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            Expect var10001 = this.expect();
            var10001.getClass();
            Parameters parameters = Parameters.of(args, var10001::declare);
            CommandVanilla.Type type = (CommandVanilla.Type)parameters.get(0).map(CommandVanilla.Type::fromName, (s) -> {
               return new CommandException("No such structure type", new Object[0]);
            }).require();
            WorldServer world = (WorldServer)parameters.get("dimension").to(MCP.dimension(server, sender)).require();
            BlockSurfacePos pos = (BlockSurfacePos)parameters.get(IvP.surfacePos("x", "z", sender.getPosition(), false)).require();
            String seed = (String)parameters.get("seed").optional().orElseGet(() -> {
               return Person.chaoticName(new EaglercraftRandom(), (new EaglercraftRandom()).nextBoolean());
            });
            boolean suggest = parameters.has("suggest");
            Biome biome = (Biome)parameters.get("biome").to(MCP::biome).optional().orElse((Object)null);
            BiomeProvider biomeProvider = null;
            if (biome != null) {
               biomeProvider = world.provider.getBiomeProvider();
               CommandVanilla.setBiomeProvider(world.provider, new BiomeProviderSingle(biome));
            }

            try {
               MapGenStructure gen = type.generator(suggest);
               ChunkPos chunkPos = pos.chunkCoord();
               EaglercraftRandom random = new EaglercraftRandom(RCStrings.seed(seed).longValue());
               ReflectionHelper.setPrivateValue(MapGenBase.class, gen, random, "rand", "rand");
               CommandVanilla.recursiveGenerate(gen, world, chunkPos);
               StructureStart structureStart = MapGenStructureHook.getStructureStart(gen, chunkPos);
               if (structureStart == null || !RCStructureBoundingBoxes.valid(structureStart.getBoundingBox())) {
                  throw new CommandException("Failed to place structure!", new Object[0]);
               }

               for(ChunkPos retroPos : RCStructureBoundingBoxes.rasterize(structureStart.getBoundingBox(), true)) {
                  gen.generateStructure(world, random, retroPos);
               }

               sender.sendMessage(new TextComponentTranslation("Structure generated at %s with seed %s", new Object[]{RCTextStyle.chunkPos(chunkPos), RCTextStyle.copy(seed)}));
               if (parameters.has("select")) {
                  RCCommands.select(sender, RCBlockAreas.from(structureStart.getBoundingBox()));
               }
            } finally {
               if (biomeProvider != null) {
                  CommandVanilla.setBiomeProvider(world.provider, biomeProvider);
               }

            }

         }
      });
      this.add(new SimpleCommand("check", (expect) -> {
         expect.then(MCE::xyz);
      }) {
         public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            Expect var10001 = this.expect();
            var10001.getClass();
            Parameters parameters = Parameters.of(args, var10001::declare);
            World world = sender.getEntityWorld();
            BlockPos pos = (BlockPos)parameters.get(MCP.pos("x", "y", "z", sender.getPosition(), false)).require();
            sender.sendMessage(CommandSightCheck.list(CommandVanilla.sightNames(world, pos)));
         }
      });
   }

   public static void setBiomeProvider(WorldProvider provider, BiomeProvider biomeProvider) {
      ReflectionHelper.setPrivateValue(WorldProvider.class, provider, biomeProvider, "biomeProvider", "biomeProvider");
   }

   public static List<ITextComponent> sightNames(World world, BlockPos pos) {
      List<ITextComponent> sights = (List)sights(world, pos).map(RCTextStyle::vanillaSight).collect(Collectors.toCollection(ArrayList::new));
      sights.addAll((Collection)sights(world, pos).map((s) -> {
         return RCAccessorMapGenStructure.getStructureAt(s, pos);
      }).flatMap((s) -> {
         return s.getComponents().stream().filter((c) -> {
            return c.getBoundingBox().isVecInside(pos);
         });
      }).map(RCTextStyle::vanillaComponentSight).collect(Collectors.toList()));
      return sights;
   }

   public static Stream<MapGenStructure> sights(World world, BlockPos pos) {
      return Arrays.stream(CommandVanilla.Type.values()).map((t) -> {
         return t.generator(false);
      }).peek((m) -> {
         ReflectionHelper.setPrivateValue(MapGenBase.class, m, world, "world", "world");
      }).filter((m) -> {
         return m.isInsideStructure(pos);
      });
   }

   public static Stream<StructureComponent> sights(StructureStart structure, BlockPos pos) {
      return structure.getComponents().stream().filter((s) -> {
         return s.getBoundingBox().isVecInside(pos);
      });
   }

   public static void recursiveGenerate(MapGenBase gen, World world, ChunkPos pos) {
      ChunkPrimer primer = new ChunkPrimer();
      if (recursiveGenerate == null) {
         recursiveGenerate = ReflectionHelper.findMethod(MapGenBase.class, "recursiveGenerate", "recursiveGenerate", new Class[]{World.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, ChunkPrimer.class});
      }

      ReflectionHelper.setPrivateValue(MapGenBase.class, gen, world, "world", "world");
      SafeReflector.invoke(gen, recursiveGenerate, (Object)null, world, pos.x, pos.z, Integer.valueOf(0), Integer.valueOf(0), primer);
   }

   public static enum Type {
      VILLAGE,
      MINESHAFT,
      STRONGHOLD,
      TEMPLE,
      OCEAN_MONUMENT,
      NETHER_STRONGHOLD;

      private Type() {
      }

      public static CommandVanilla.Type fromName(String name) {
         return (CommandVanilla.Type)Arrays.stream(values()).filter((t) -> {
            return t.structureName().equals(name);
         }).findFirst().orElse((Object)null);
      }

      public String structureName() {
         return this.generator(false).getStructureName();
      }

      public MapGenStructure generator(final boolean suggest) {
         switch(this) {
         case VILLAGE:
            return new MapGenVillage() {
               protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
                  return !suggest || super.canSpawnStructureAtCoords(chunkX, chunkZ);
               }
            };
         case MINESHAFT:
            return new MapGenMineshaft() {
               protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
                  return !suggest || super.canSpawnStructureAtCoords(chunkX, chunkZ);
               }
            };
         case STRONGHOLD:
            return new MapGenStronghold() {
               protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
                  return !suggest || super.canSpawnStructureAtCoords(chunkX, chunkZ);
               }
            };
         case TEMPLE:
            return new MapGenScatteredFeature() {
               protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
                  return !suggest || super.canSpawnStructureAtCoords(chunkX, chunkZ);
               }
            };
         case OCEAN_MONUMENT:
            return new StructureOceanMonument() {
               protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
                  return !suggest || super.canSpawnStructureAtCoords(chunkX, chunkZ);
               }
            };
         case NETHER_STRONGHOLD:
            return new MapGenNetherBridge() {
               protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
                  return !suggest || super.canSpawnStructureAtCoords(chunkX, chunkZ);
               }
            };
         default:
            throw new InternalError();
         }
      }

      public String toString() {
         return this.structureName();
      }
   }
}
