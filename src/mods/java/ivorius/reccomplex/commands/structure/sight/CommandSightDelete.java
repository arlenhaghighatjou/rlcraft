package ivorius.reccomplex.commands.structure.sight;

import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.CommandSplit;
import ivorius.reccomplex..shadow.mcopts.commands.SimpleCommand;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.MCP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.MCE;
import ivorius.reccomplex.world.gen.feature.WorldStructureGenerationData;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandSightDelete extends CommandSplit {
   public CommandSightDelete() {
      super("forget");
      this.add(new SimpleCommand("id", (expect) -> {
         expect.skip().descriptionU("id").required();
      }) {
         public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            Expect var10001 = this.expect();
            var10001.getClass();
            Parameters parameters = Parameters.of(args, var10001::declare);
            WorldStructureGenerationData generationData = WorldStructureGenerationData.get(sender.getEntityWorld());
            WorldStructureGenerationData.Entry entry = generationData.removeEntry(UUID.fromString((String)parameters.get(0).require()));
            if (entry == null) {
               throw RecurrentComplex.translations.commandException("commands.rcsightinfo.unknown");
            } else {
               sender.sendMessage(RecurrentComplex.translations.format("commands.rcforget.success", entry.description()));
            }
         }
      });
      this.add(new SimpleCommand("all", (expect) -> {
         expect.then(MCE::xyz);
      }) {
         public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            Expect var10001 = this.expect();
            var10001.getClass();
            Parameters parameters = Parameters.of(args, var10001::declare);
            WorldStructureGenerationData generationData = WorldStructureGenerationData.get(sender.getEntityWorld());
            BlockPos pos = (BlockPos)parameters.get(0).to(MCP.pos(sender.getPosition(), false)).require();
            List<WorldStructureGenerationData.Entry> entries = (List)generationData.entriesAt(pos).collect(Collectors.toList());
            entries.forEach((e) -> {
               generationData.removeEntry(e.getUuid());
            });
            if (entries.size() == 1) {
               sender.sendMessage(RecurrentComplex.translations.format("commands.rcforget.success", ((WorldStructureGenerationData.Entry)entries.get(0)).description()));
            } else {
               sender.sendMessage(RecurrentComplex.translations.format("commands.rcforgetall.success", entries.size()));
            }

         }
      });
      this.permitFor(2);
   }
}
