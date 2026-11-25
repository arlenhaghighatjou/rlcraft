package ivorius.reccomplex.commands.structure.sight;

import ivorius.ivtoolkit.blocks.BlockAreas;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.NaP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex.capability.SelectionOwner;
import ivorius.reccomplex.commands.RCCommands;
import ivorius.reccomplex.commands.parameters.expect.RCE;
import ivorius.reccomplex.world.gen.feature.WorldStructureGenerationData;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;

public class CommandSightAdd extends CommandExpecting {
   public CommandSightAdd() {
   }

   public String getName() {
      return "remember";
   }

   public void expect(Expect expect) {
      expect.then(RCE::randomString).descriptionU("name").required().repeat();
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      WorldStructureGenerationData generationData = WorldStructureGenerationData.get(commandSender.getEntityWorld());
      SelectionOwner owner = RCCommands.getSelectionOwner(commandSender, (EnumFacing)null, true);
      String name = (String)parameters.get(0).rest(NaP::join).require();
      generationData.addEntry(WorldStructureGenerationData.CustomEntry.from(name, BlockAreas.toBoundingBox(owner.getSelection())));
      commandSender.sendMessage(RecurrentComplex.translations.format("commands.rcremember.success", name));
   }
}
