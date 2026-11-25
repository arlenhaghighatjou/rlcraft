package crafttweaker.api.command;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.world.IBlockPos;
import java.util.List;
import java.util.Map;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.command.ICommandManager")
@ZenRegister
public interface ICommandManager {
   @ZenMethod
   int executeCommand(ICommandSender var1, String var2);

   @ZenMethod
   List<String> getTabCompletions(ICommandSender var1, String var2, @Optional IBlockPos var3);

   @ZenMethod
   List<ICommand> getPossibleCommands(ICommandSender var1);

   @ZenGetter("commands")
   Map<String, ICommand> getCommands();
}
