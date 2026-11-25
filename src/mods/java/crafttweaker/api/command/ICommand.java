package crafttweaker.api.command;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.server.IServer;
import crafttweaker.api.world.IBlockPos;
import java.util.List;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenOperator;

@ZenClass("crafttweaker.command.ICommand")
@ZenRegister
public interface ICommand {
   @ZenGetter("name")
   String getName();

   @ZenMethod
   String getUsage(ICommandSender var1);

   @ZenGetter("aliases")
   List<String> getAliases();

   @ZenMethod
   void execute(IServer var1, ICommandSender var2, String[] var3);

   @ZenMethod
   boolean checkPermission(IServer var1, ICommandSender var2);

   @ZenMethod
   List<String> getTabCompletions(IServer var1, ICommandSender var2, String[] var3, @Optional IBlockPos var4);

   @ZenMethod
   boolean isUsernameIndex(String[] var1, int var2);

   @ZenOperator(OperatorType.COMPARE)
   int compare(ICommand var1);

   Object getInternal();
}
