package ivorius.reccomplex.commands;

import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex.commands.structure.CommandSearchStructure;
import ivorius.reccomplex.files.RCFiles;
import ivorius.reccomplex.files.loading.ResourceDirectory;
import ivorius.reccomplex.world.gen.feature.selector.StructureSelector;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericStructure;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.GenerationType;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.ListGeneration;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.MazeGeneration;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.NaturalGeneration;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.SaplingGeneration;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.StaticGeneration;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.VanillaDecorationGeneration;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.VanillaGeneration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.PriorityQueue;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class CommandSanity extends CommandExpecting {
   public CommandSanity() {
   }

   public static <K, T> Stream<T> values(IRegistry<K, T> registry) {
      Stream var10000 = registry.getKeys().stream();
      registry.getClass();
      return var10000.map(registry::getObject);
   }

   public static Stream<World> dimensions(MinecraftServer server) {
      Stream var10000 = Stream.of(DimensionManager.getStaticDimensionIDs());
      server.getClass();
      return var10000.map(server::getWorld);
   }

   public String getName() {
      return RCConfig.commandPrefix + "sanity";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void expect(Expect expect) {
      expect.flag("silent").flag("short");
   }

   public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      boolean sane = true;
      if (RecurrentComplex.isLite()) {
         commandSender.sendMessage(new TextComponentString("Recurrent Complex is in lightweight mode!"));
      }

      if (StructureRegistry.INSTANCE.ids().isEmpty()) {
         commandSender.sendMessage(new TextComponentString("No registered structures!"));
         sane = false;
      }

      if (!Files.isReadable(ResourceDirectory.getCustomDirectory().toPath())) {
         commandSender.sendMessage(new TextComponentString("Can't read files from custom directory"));
         sane = false;
      }

      for(ModContainer mod : Loader.instance().getModList()) {
         String domain = mod.getModId();
         Path path = null;

         try {
            path = RCFiles.pathFromResourceLocation(new ResourceLocation(domain.toLowerCase(), ""));
            if (path != null && !Files.isReadable(path)) {
               commandSender.sendMessage(new TextComponentString("Can't read files from mod: " + mod.getModId()));
               sane = false;
            }
         } catch (RCFiles.ResourceLocationLoadException var14) {
            RecurrentComplex.logger.error(var14);
            commandSender.sendMessage(new TextComponentString("Error reading files from mod " + mod.getModId() + ": "));
            commandSender.sendMessage(new TextComponentString(RCCommands.reason(var14)));
            sane = false;
         } finally {
            if (path != null) {
               RCFiles.closeQuietly(path.getFileSystem());
            }

         }
      }

      if (!Files.isReadable(ResourceDirectory.getServerDirectory().toPath())) {
         commandSender.sendMessage(new TextComponentString("Can't read files from server directory"));
         sane = false;
      }

      if (!parameters.has("short")) {
         sane = sane & this.addStructureLog(commandSender, (s, structure) -> {
            return !structure.generationTypes(GenerationType.class).isEmpty();
         }, "Missing generation type");
         sane = sane & this.addGenericStructureLog(commandSender, (s, structure) -> {
            return !structure.metadata.authors.isEmpty();
         }, "No author");
         sane = sane & this.addGenericStructureLog(commandSender, (s, structure) -> {
            return structure.transformer.getTransformers().stream().allMatch((t) -> {
               return t.id().length() > 0;
            });
         }, "Transformer has empty ID");
         sane = sane & this.addGenerationLog(commandSender, GenerationType.class, (structure, gen) -> {
            return gen.id().length() > 0;
         }, "Generation type has empty ID");
         sane = sane & this.addGenerationLog(commandSender, NaturalGeneration.class, (structure, gen) -> {
            return values(Biome.REGISTRY).anyMatch((b) -> {
               return StructureSelector.generationWeightInBiome(gen.biomeWeights, b) > 0.0D;
            });
         }, "Natural generation type won't accept any known biomes");
         sane = sane & this.addGenerationLog(commandSender, NaturalGeneration.class, (structure, gen) -> {
            return dimensions(server).anyMatch((d) -> {
               return StructureSelector.generationWeightInDimension(gen.dimensionWeights, d.provider) > 0.0D;
            });
         }, "Natural generation type won't accept any known dimensions");
         sane = sane & this.addGenerationLog(commandSender, NaturalGeneration.class, (structure, gen) -> {
            return gen.getActiveGenerationWeight() > 0.0D;
         }, "Natural generation type has no weight");
         sane = sane & this.addGenerationLog(commandSender, VanillaGeneration.class, (structure, gen) -> {
            return values(Biome.REGISTRY).anyMatch((b) -> {
               return gen.biomeExpression.test(b);
            });
         }, "Vanilla structure generation type won't accept any known biomes");
         sane = sane & this.addGenerationLog(commandSender, VanillaGeneration.class, (structure, gen) -> {
            return gen.getActiveWeight() > 0.0D;
         }, "Vanilla structure generation type has no weight");
         sane = sane & this.addGenerationLog(commandSender, VanillaGeneration.class, (structure, gen) -> {
            return gen.minBaseLimit > 0.0D || gen.maxBaseLimit > 0.0D || gen.maxScaledLimit > 0.0D || gen.minScaledLimit > 0.0D;
         }, "Vanilla structure is always limited to zero instances");
         sane = sane & this.addGenerationLog(commandSender, VanillaDecorationGeneration.class, (structure, gen) -> {
            return values(Biome.REGISTRY).anyMatch((b) -> {
               return StructureSelector.generationWeightInBiome(gen.biomeWeights, b) > 0.0D;
            });
         }, "Vanilla structure generation type won't accept any known biomes");
         sane = sane & this.addGenerationLog(commandSender, VanillaDecorationGeneration.class, (structure, gen) -> {
            return dimensions(server).anyMatch((d) -> {
               return StructureSelector.generationWeightInDimension(gen.dimensionWeights, d.provider) > 0.0D;
            });
         }, "Natural generation type won't accept any dimensions");
         sane = sane & this.addGenerationLog(commandSender, MazeGeneration.class, (structure, gen) -> {
            return gen.getWeight() > 0.0D;
         }, "Maze generation type has no weight");
         sane = sane & this.addGenerationLog(commandSender, MazeGeneration.class, (structure, gen) -> {
            return !gen.getMazeID().trim().isEmpty();
         }, "Maze generation type has maze id");
         sane = sane & this.addGenerationLog(commandSender, MazeGeneration.class, (structure, gen) -> {
            return !gen.mazeComponent.rooms.isEmpty();
         }, "Maze generation type has no rooms");
         sane = sane & this.addGenerationLog(commandSender, MazeGeneration.class, (structure, gen) -> {
            return !gen.mazeComponent.exitPaths.isEmpty() || !gen.mazeComponent.defaultConnector.id.equals("Wall");
         }, "Maze generation type has no walkable exits");
         sane = sane & this.addGenerationLog(commandSender, ListGeneration.class, (structure, gen) -> {
            return !gen.listID.trim().isEmpty();
         }, "List generation has no list id");
         sane = sane & this.addGenerationLog(commandSender, ListGeneration.class, (structure, gen) -> {
            return gen.getWeight() > 0.0D;
         }, "List generation has no weight");
         sane = sane & this.addGenerationLog(commandSender, SaplingGeneration.class, (structure, gen) -> {
            return gen.getActiveWeight() > 0.0D;
         }, "Sapling generation has no weight");
         sane = sane & this.addGenerationLog(commandSender, StaticGeneration.class, (structure, gen) -> {
            return dimensions(server).anyMatch((d) -> {
               return gen.dimensionExpression.test(d.provider);
            });
         }, "Static generation won't accept any known dimensions");
      }

      if (sane && !parameters.has("silent")) {
         commandSender.sendMessage(new TextComponentString("No problems identified!"));
      }

   }

   protected <T extends GenerationType> boolean addGenerationLog(ICommandSender commandSender, Class<T> tClass, BiPredicate<Structure<?>, T> predicate, String msg) {
      return this.addStructureLog(commandSender, (s, structure) -> {
         return structure.generationTypes(tClass).stream().allMatch((gen) -> {
            return predicate.test(structure, gen);
         });
      }, msg);
   }

   protected boolean addGenericStructureLog(ICommandSender commandSender, BiPredicate<String, GenericStructure> predicate, String msg) {
      return this.addStructureLog(commandSender, (s, structure) -> {
         return !(structure instanceof GenericStructure) || predicate.test(s, (GenericStructure)structure);
      }, msg);
   }

   protected boolean addStructureLog(ICommandSender commandSender, BiPredicate<String, Structure<?>> predicate, String msg) {
      PriorityQueue<String> structures = CommandSearchStructure.<String>search(StructureRegistry.INSTANCE.activeIDs(), (name) -> {
         return (boolean)(predicate.test(name, StructureRegistry.INSTANCE.get(name)) ? 0.0D : 1.0D);
      });
      if (structures.size() > 0) {
         commandSender.sendMessage(new TextComponentString(msg + ":"));
         CommandSearchStructure.postResultMessage("", commandSender, RCTextStyle::structure, structures);
         return false;
      } else {
         return true;
      }
   }
}
