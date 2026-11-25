package crafttweaker.mc1120.command;

import crafttweaker.api.command.ICommand;
import crafttweaker.api.command.ICommandManager;
import crafttweaker.api.command.ICommandSender;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MCCommandManager implements ICommandManager {
   private final net.minecraft.command.ICommandManager commandManager;

   public MCCommandManager(net.minecraft.command.ICommandManager commandManager) {
      this.commandManager = commandManager;
   }

   public int executeCommand(ICommandSender sender, String rawCommand) {
      return this.commandManager.executeCommand(CraftTweakerMC.getICommandSender(sender), rawCommand);
   }

   public List<String> getTabCompletions(ICommandSender sender, String input, IBlockPos pos) {
      return this.commandManager.getTabCompletions(CraftTweakerMC.getICommandSender(sender), input, CraftTweakerMC.getBlockPos(pos));
   }

   public List<ICommand> getPossibleCommands(ICommandSender sender) {
      return (List)this.commandManager.getPossibleCommands(CraftTweakerMC.getICommandSender(sender)).stream().map(CraftTweakerMC::getICommand).collect(Collectors.toList());
   }

   public Map<String, ICommand> getCommands() {
      Map<String, ICommand> out = new HashMap();
      this.commandManager.getCommands().forEach((key, command) -> {
         ICommand var10000 = (ICommand)out.put(key, CraftTweakerMC.getICommand(command));
      });
      return out;
   }
}
