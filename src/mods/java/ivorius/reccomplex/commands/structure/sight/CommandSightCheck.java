package ivorius.reccomplex.commands.structure.sight;

import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.MCP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.MCE;
import ivorius.reccomplex..shadow.mcopts.translation.ServerTranslations;
import ivorius.reccomplex.commands.CommandVanilla;
import ivorius.reccomplex.commands.RCTextStyle;
import ivorius.reccomplex.world.gen.feature.WorldStructureGenerationData;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class CommandSightCheck extends CommandExpecting {
   private String name;
   public boolean checkAll;

   public CommandSightCheck(String name, boolean checkAll) {
      this.name = name;
      this.checkAll = checkAll;
   }

   public static ITextComponent list(List<ITextComponent> names) {
      return names.size() == 0 ? RecurrentComplex.translations.format("commands.whatisthis.none") : RecurrentComplex.translations.format(names.size() > 1 ? "commands.whatisthis.many" : "commands.whatisthis.one", ServerTranslations.join(names));
   }

   public String getName() {
      return this.name;
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      World world = sender.getEntityWorld();
      BlockPos pos = (BlockPos)parameters.get(0).to(MCP.pos(sender.getPosition(), false)).require();
      List<ITextComponent> names = (List)WorldStructureGenerationData.get(world).entriesAt(pos).map(RCTextStyle::sight).collect(Collectors.toCollection(ArrayList::new));
      if (this.checkAll) {
         names.addAll(CommandVanilla.sightNames(world, pos));
      }

      sender.sendMessage(list(names));
   }

   public void expect(Expect expect) {
      expect.then(MCE::xyz);
   }
}
