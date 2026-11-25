package crafttweaker.mc1120.commands;

import crafttweaker.mc1120.commands.dumpzscommand.DumpZsTarget;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

public class DumpZsCommand extends CraftTweakerCommand {
   private final Map<String, DumpZsTarget> targets;

   public DumpZsCommand(DumpZsTarget... targets) {
      super("dumpzs");
      this.targets = (Map)Arrays.stream(targets).collect(Collectors.toMap((t) -> {
         return t.argumentName;
      }, Function.identity()));
      ITextComponent[] components = new ITextComponent[this.targets.size() + 2];
      components[0] = SpecialMessagesChat.getClickableCommandText("\u00a72/ct dumpzs " + (String)this.targets.keySet().stream().collect(Collectors.joining(" | ", "[", "]")), "/ct dumpzs", true);
      components[1] = SpecialMessagesChat.getNormalMessage(" \u00a73Dumps the whole ZenScript Registry");
      int i = 2;

      for(DumpZsTarget value : this.targets.values()) {
         components[i++] = SpecialMessagesChat.getNormalMessage(String.format(" \u00a73[%s]: %s", value.argumentName, value.getDescription()));
      }

      this.setDescription(components);
   }

   protected void init() {
   }

   public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
      if (args.length == 0) {
         sender.sendMessage(SpecialMessagesChat.getNormalMessage("Please provide a target"));
      } else {
         for(String arg : args) {
            if (!this.targets.containsKey(arg)) {
               sender.sendMessage(SpecialMessagesChat.getNormalMessage("No target found for argument " + arg));
            } else {
               ((DumpZsTarget)this.targets.get(arg)).execute(sender, server);
            }
         }

      }
   }

   public List<String> getSubSubCommand(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
      return new ArrayList(this.targets.keySet());
   }
}
