package ivorius.reccomplex.commands;

import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.NaP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex.capability.RCEntityInfo;
import java.util.function.Function;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;

public class CommandVisual extends CommandExpecting {
   public CommandVisual() {
   }

   public String getName() {
      return RCConfig.commandPrefix + "visual";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void expect(Expect expect) {
      expect.any("rulers").descriptionU("type").required().any("true", "false").descriptionU("true|false").required();
   }

   public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      boolean enabled = ((Boolean)parameters.get(1).to(NaP::asBoolean).require()).booleanValue();
      EntityPlayer player = getCommandSenderAsPlayer(commandSender);
      RCEntityInfo RCEntityInfo = RCCommands.getStructureEntityInfo(player, (EnumFacing)null);
      String type = (String)parameters.get(0).require();
      byte var10 = -1;
      switch(type.hashCode()) {
      case -919873987:
         if (type.equals("rulers")) {
            var10 = 0;
         }
      default:
         switch(var10) {
         case 0:
            RCEntityInfo.showGrid = enabled;
            RCEntityInfo.sendOptionsToClients(player);
            if (enabled) {
               commandSender.sendMessage(RecurrentComplex.translations.format("commands.rcvisual.enabled", type));
            } else {
               commandSender.sendMessage(RecurrentComplex.translations.format("commands.rcvisual.disabled", type));
            }

            return;
         default:
            throw RecurrentComplex.translations.wrongUsageException("commands.rcvisual.usage");
         }
      }
   }
}
