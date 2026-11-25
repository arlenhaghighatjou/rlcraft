package greymerk.roguelike.command;

import java.util.List;

public interface ICommandRouter {
   void execute(CommandContext1_12 var1, List<String> var2);

   List<String> getTabCompletion(List<String> var1);
}
