package crafttweaker.mc1120.commands;

import crafttweaker.mc1120.CraftTweaker;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.commons.lang3.ArrayUtils;

public class CTChatCommand extends CommandBase {
   public static final String CRAFTTWEAKER_LOG_PATH = (new File("crafttweaker.log")).getAbsolutePath();
   private static final List<String> aliases = new ArrayList();
   private static final Map<String, CraftTweakerCommand> craftTweakerCommands = new TreeMap();

   public CTChatCommand() {
   }

   public static void register(FMLServerStartingEvent ev) {
      Commands.registerCommands();
      ev.registerServerCommand(new CTChatCommand());
   }

   public static void registerCommand(CraftTweakerCommand command) {
      craftTweakerCommands.put(command.getSubCommandName(), command);
   }

   public String getName() {
      return "crafttweaker";
   }

   public String getUsage(ICommandSender sender) {
      StringBuilder sb = new StringBuilder();
      sb.append("/crafttweaker ");
      String[] commands = new String[craftTweakerCommands.keySet().size()];
      craftTweakerCommands.keySet().toArray(commands);

      for(int i = 0; i < commands.length; ++i) {
         sb.append(commands[i]);
         if (i != commands.length - 1) {
            sb.append(" | ");
         }
      }

      return sb.toString();
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
      if (args.length <= 0) {
         sender.sendMessage(new TextComponentString(this.getUsage(sender)));
      } else {
         if (craftTweakerCommands.containsKey(args[0])) {
            if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
               ((CraftTweakerCommand)craftTweakerCommands.get(args[0])).executeCommand(server, sender, (String[])ArrayUtils.subarray(args, 1, args.length));
            } else {
               ((CraftTweakerCommand)craftTweakerCommands.get(args[0])).executeCommand(server, sender, (String[])ArrayUtils.subarray(args, 1, args.length));
            }
         } else {
            sender.sendMessage(SpecialMessagesChat.getClickableCommandText("\u00a7cNo such command! \u00a76[Click to show help]", "/ct help", true));
         }

      }
   }

   public List<String> getAliases() {
      return aliases;
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
      Set<String> keys = craftTweakerCommands.keySet();
      List<String> currentPossibleCommands = new ArrayList();
      if (args.length <= 0) {
         return new ArrayList(keys);
      } else if (args.length == 1) {
         for(String cmd : keys) {
            if (cmd.startsWith(args[0])) {
               currentPossibleCommands.add(cmd);
            }
         }

         return currentPossibleCommands;
      } else {
         CraftTweakerCommand subCommand = (CraftTweakerCommand)craftTweakerCommands.get(args[0]);
         if (subCommand != null) {
            CraftTweaker.LOG.error(Arrays.toString(ArrayUtils.subarray(args, 1, args.length)));
            return subCommand.getSubSubCommand(server, sender, (String[])ArrayUtils.subarray(args, 1, args.length), targetPos);
         } else {
            return currentPossibleCommands;
         }
      }
   }

   public int getRequiredPermissionLevel() {
      return 4;
   }

   public static Map<String, CraftTweakerCommand> getCraftTweakerCommands() {
      return craftTweakerCommands;
   }

   static {
      aliases.add("ct");
      aliases.add("mt");
      aliases.add("minetweaker");
   }
}
