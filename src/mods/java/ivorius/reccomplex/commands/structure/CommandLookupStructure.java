package ivorius.reccomplex.commands.structure;

import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex.commands.RCTextStyle;
import ivorius.reccomplex.commands.parameters.RCP;
import ivorius.reccomplex.commands.parameters.expect.RCE;
import ivorius.reccomplex.utils.RCStrings;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericStructure;
import ivorius.reccomplex.world.gen.feature.structure.generic.Metadata;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.ClickEvent.Action;

public class CommandLookupStructure extends CommandExpecting {
   public CommandLookupStructure() {
   }

   public String getName() {
      return "lookup";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void expect(Expect expect) {
      expect.then(RCE::structure);
   }

   public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      String id = (String)parameters.get(0).require();
      GenericStructure structure = (GenericStructure)parameters.get(0).to((p) -> {
         return RCP.genericStructure(p, false);
      }).require();
      Metadata metadata = structure.metadata;
      boolean hasWeblink = !metadata.weblink.trim().isEmpty();
      ITextComponent weblink = (ITextComponent)(hasWeblink ? new TextComponentString(RCStrings.abbreviateFormatted(metadata.weblink, 30)) : RecurrentComplex.translations.format("commands.rclookup.reply.nolink"));
      if (hasWeblink) {
         weblink.getStyle().setClickEvent(new ClickEvent(Action.OPEN_URL, metadata.weblink));
         weblink.getStyle().setHoverEvent(new HoverEvent(net.minecraft.util.text.event.HoverEvent.Action.SHOW_TEXT, new TextComponentString(metadata.weblink)));
      }

      ITextComponent level = new TextComponentString(StructureRegistry.INSTANCE.status(id).getLevel().toString());
      level.getStyle().setColor(TextFormatting.YELLOW);
      commandSender.sendMessage(RecurrentComplex.translations.format(StructureRegistry.INSTANCE.hasActive(id) ? "commands.rclookup.reply.generates" : "commands.rclookup.reply.silent", id, RCTextStyle.users(metadata.authors), level, weblink));
      if (!metadata.comment.trim().isEmpty()) {
         commandSender.sendMessage(RecurrentComplex.translations.format("commands.rclookup.reply.comment", metadata.comment));
      }

   }
}
