package ivorius.reccomplex.commands.structure;

import ivorius.ivtoolkit.util.IvStreams;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.MCP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.MCE;
import ivorius.reccomplex.commands.parameters.RCP;
import ivorius.reccomplex.commands.parameters.expect.RCE;
import ivorius.reccomplex.files.RCFiles;
import ivorius.reccomplex.files.loading.FileSuffixFilter;
import ivorius.reccomplex.world.gen.feature.WorldGenStructures;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import java.io.File;
import java.util.Arrays;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.storage.RegionFile;
import net.minecraft.world.chunk.storage.RegionFileCache;
import org.apache.commons.lang3.tuple.Pair;

public class CommandRetrogen extends CommandExpecting {
   public CommandRetrogen() {
   }

   public static Stream<Pair<Integer, Integer>> existingRegions(File worldDir) {
      File regionsDirectory = RCFiles.getValidatedFolder(new File(worldDir, "region"), false);
      if (regionsDirectory == null) {
         return Stream.empty();
      } else {
         String[] mcas = regionsDirectory.list(new FileSuffixFilter(new String[]{"mca"}));
         if (mcas == null) {
            throw new IllegalStateException();
         } else {
            return Arrays.stream(mcas).map((s) -> {
               return s.split("\\.");
            }).filter((p) -> {
               return p.length == 4 && p[0].equals("r");
            }).map((p) -> {
               return Pair.of(Integer.valueOf(Integer.parseInt(p[1])), Integer.valueOf(Integer.parseInt(p[2])));
            }).filter((rfc) -> {
               return rfc.getLeft() != null && rfc.getRight() != null;
            });
         }
      }
   }

   public static Stream<ChunkPos> existingChunks(World world) {
      File worldDirectory = world.getSaveHandler().getWorldDirectory();
      return existingRegions(worldDirectory).map((rfc) -> {
         return new ChunkPos(((Integer)rfc.getLeft()).intValue() << 5, ((Integer)rfc.getRight()).intValue() << 5);
      }).map((rflc) -> {
         return Pair.of(RegionFileCache.createOrLoadRegionFile(worldDirectory, rflc.x, rflc.z), rflc);
      }).flatMap((r) -> {
         return IvStreams.flatMapToObj(IntStream.range(0, 32), (x) -> {
            return IntStream.range(0, 32).mapToObj((z) -> {
               return Pair.of(r.getLeft(), add((ChunkPos)r.getRight(), x, z));
            });
         });
      }).filter((p) -> {
         return ((RegionFile)p.getLeft()).chunkExists(((ChunkPos)p.getRight()).x & 31, ((ChunkPos)p.getRight()).z & 31);
      }).map(Pair::getRight);
   }

   @Nonnull
   protected static ChunkPos add(ChunkPos pos, int x, int z) {
      return new ChunkPos(x + pos.x, z + pos.z);
   }

   public static EaglercraftRandom getRandom(WorldServer world, ChunkPos pos) {
      return world.setRandomSeed(pos.x, pos.z, -559038737);
   }

   public static long retrogen(WorldServer world, Predicate<Structure> structurePredicate) {
      return existingChunks(world).filter((pos) -> {
         return (long)world.getChunkFromChunkCoords(pos.x, pos.z).isTerrainPopulated();
      }).filter((pos) -> {
         return (long)WorldGenStructures.decorate(world, getRandom(world, pos), pos, structurePredicate);
      }).count();
   }

   public String getName() {
      return RCConfig.commandPrefix + "retro";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void expect(Expect expect) {
      expect.named("exp").words(RCE::structurePredicate).descriptionU("resource expression: only generate these structures").named("dimension", "d").then(MCE::dimension);
   }

   public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      Predicate<Structure> structurePredicate = (Predicate)parameters.get("exp").to(RCP::structurePredicate).optional().orElse((Object)null);
      WorldServer world = (WorldServer)parameters.get("dimension").to(MCP.dimension(server, commandSender)).require();
      long count = retrogen(world, structurePredicate);
      commandSender.sendMessage(RecurrentComplex.translations.format("commands.rcretro.count", String.valueOf(count)));
   }
}
