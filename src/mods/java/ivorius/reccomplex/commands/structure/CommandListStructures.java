package ivorius.reccomplex.commands.structure;

import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.NaP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex..shadow.mcopts.translation.ServerTranslations;
import ivorius.reccomplex.commands.RCCommands;
import ivorius.reccomplex.commands.RCTextStyle;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import java.util.ArrayList;
import java.util.List;
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

public class CommandListStructures extends CommandExpecting {
   public static final int RESULTS_PER_PAGE = 20;

   public CommandListStructures() {
   }

   public static void showList(ICommandSender commandSender, int page, List<String> structureNames) {
      int startIndex = page * 20;
      int endIndex = Math.min((page + 1) * 20, structureNames.size());
      if (endIndex - startIndex > 0) {
         List<ITextComponent> components = new ArrayList(endIndex - startIndex + 2);
         components.add(new TextComponentString("[<--]"));
         if (page > 0) {
            linkToPage((ITextComponent)components.get(0), page - 1, RecurrentComplex.translations.format("commands.rclist.previous"));
         }

         for(int i = 0; i < endIndex - startIndex; ++i) {
            components.add(RCTextStyle.structure((String)structureNames.get(startIndex + i)));
         }

         components.add(new TextComponentString("[-->]"));
         if (page < (structureNames.size() - 1) / 20) {
            linkToPage((ITextComponent)components.get(components.size() - 1), page + 1, RecurrentComplex.translations.format("commands.rclist.next"));
         }

         commandSender.sendMessage(ServerTranslations.join(components));
      } else {
         commandSender.sendMessage(RecurrentComplex.translations.get("commands.rclist.none"));
      }

   }

   public static void linkToPage(ITextComponent component, int page, ITextComponent hoverTitle) {
      component.getStyle().setClickEvent(new ClickEvent(Action.RUN_COMMAND, String.format("/%s", RCCommands.structures.list(page))));
      component.getStyle().setHoverEvent(new HoverEvent(net.minecraft.util.text.event.HoverEvent.Action.SHOW_TEXT, hoverTitle));
      component.getStyle().setColor(TextFormatting.AQUA);
   }

   public String getName() {
      return "list";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void expect(Expect expect) {
      expect.any(Integer.valueOf(0)).descriptionU("page");
   }

   public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      int page = ((Integer)parameters.get(0).to(NaP::asInt).optional().orElse(Integer.valueOf(0))).intValue();
      List<String> structureNames = new ArrayList();
      structureNames.addAll(StructureRegistry.INSTANCE.ids());
      structureNames.sort(String.CASE_INSENSITIVE_ORDER);
      showList(commandSender, page, structureNames);
   }
}
