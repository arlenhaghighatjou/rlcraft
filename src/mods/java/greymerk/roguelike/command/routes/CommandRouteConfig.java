package greymerk.roguelike.command.routes;

import greymerk.roguelike.command.CommandBase;
import greymerk.roguelike.command.CommandContext1_12;
import greymerk.roguelike.command.CommandRouteBase;
import greymerk.roguelike.config.RogueConfig;
import greymerk.roguelike.util.ArgumentParser;
import java.util.List;

public class CommandRouteConfig extends CommandRouteBase {
   public CommandRouteConfig(CommandBase commandBase) {
      super(commandBase);
   }

   public void execute(CommandContext1_12 context, List<String> args) {
      ArgumentParser ap = new ArgumentParser(args);
      if (!ap.hasEntry(0)) {
         context.sendInfo("Usage: roguelike config reload");
      } else if (ap.match(0, "reload")) {
         RogueConfig.reload(true);
         context.sendSuccess("Configurations Reloaded");
      }

   }
}
