package ivorius.reccomplex.commands.former;

import ivorius.ivtoolkit.blocks.BlockSurfaceArea;
import ivorius.ivtoolkit.blocks.BlockSurfacePos;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.MCP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.MCE;
import ivorius.reccomplex.commands.RCCommands;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class CommandSelectSetBiome extends CommandExpecting {
   public CommandSelectSetBiome() {
   }

   public static int biomeArrayIndex(BlockSurfacePos p) {
      int i = p.getX() & 15;
      int j = p.getZ() & 15;
      return j << 4 | i;
   }

   public String getName() {
      return RCConfig.commandPrefix + "setbiome";
   }

   public void expect(Expect expect) {
      expect.then(MCE::biome);
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      Biome biome = (Biome)parameters.get(0).to(MCP::biome).require();
      byte biomeID = (byte)(Biome.REGISTRY.getIDForObject(biome) & 255);
      World world = commandSender.getEntityWorld();
      BlockSurfaceArea.from(RCCommands.getSelectionOwner(commandSender, (EnumFacing)null, true).getSelection()).forEach((p) -> {
         Chunk chunk = world.getChunkFromChunkCoords(p.getX() >> 4, p.getZ() >> 4);
         chunk.getBiomeArray()[biomeArrayIndex(p)] = biomeID;
         chunk.setModified(true);
      });
   }
}
