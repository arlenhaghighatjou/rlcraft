package ivorius.reccomplex.commands.preview;

import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameter;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex.capability.RCEntityInfo;
import ivorius.reccomplex.commands.RCCommands;
import ivorius.reccomplex.operation.Operation;
import java.util.function.Function;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;

public class CommandPreview extends CommandExpecting {
   public CommandPreview() {
   }

   public String getName() {
      return RCConfig.commandPrefix + "preview";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void expect(Expect expect) {
      expect.any((Object[])Operation.PreviewType.keys()).descriptionU("type").required();
   }

   public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      EntityPlayer player = getCommandSenderAsPlayer(commandSender);
      RCEntityInfo RCEntityInfo = RCCommands.getStructureEntityInfo(player, (EnumFacing)null);
      Operation.PreviewType previewType = (Operation.PreviewType)parameters.get(0).map(Operation.PreviewType::find, (s) -> {
         return RecurrentComplex.translations.commandException("commands.rcpreview.invalid");
      }).require();
      RCEntityInfo.setPreviewType(previewType);
      RCEntityInfo.sendPreviewTypeToClients(player);
      commandSender.sendMessage(RecurrentComplex.translations.format("commands.rcpreview.success", previewType.key));
   }
}
