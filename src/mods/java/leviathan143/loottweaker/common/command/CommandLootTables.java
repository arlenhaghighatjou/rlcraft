package leviathan143.loottweaker.common.command;

import com.google.common.collect.ImmutableMap;
import crafttweaker.mc1120.commands.CraftTweakerCommand;
import java.util.Map;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandLootTables extends CraftTweakerCommand {
   private final Map<String, Subcommand> subcommands = ImmutableMap.builder().put("all", new SubcommandDumpAll()).put("byName", new SubcommandDumpNamed()).put("target", new SubcommandDumpTargetsLootTable()).put("list", new SubcommandListLootTables()).build();

   public CommandLootTables() {
      super("loottables");
   }

   protected void init() {
      this.setDescription(new ITextComponent[]{new TextComponentTranslation("loottweaker.commands.dump.desc", new Object[0])});
   }

   public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
      if (args.length < 1) {
         sender.sendMessage(new TextComponentTranslation("loottweaker.commands.dump.usage", new Object[0]));
      } else {
         Subcommand subcommand = (Subcommand)this.subcommands.get(args[0]);
         if (subcommand != null) {
            subcommand.execute(server, sender, args);
         } else {
            sender.sendMessage(new TextComponentTranslation("loottweaker.commands.dump.unknownSubcommand", new Object[]{args[0]}));
         }

      }
   }
}
