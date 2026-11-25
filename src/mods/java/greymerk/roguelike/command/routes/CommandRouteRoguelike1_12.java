package greymerk.roguelike.command.routes;

import greymerk.roguelike.command.CommandBase1_12;
import greymerk.roguelike.command.CommandContext1_12;
import greymerk.roguelike.command.CommandRouteBase;
import java.util.List;

public class CommandRouteRoguelike1_12 extends CommandRouteBase {
   public CommandRouteRoguelike1_12() {
      super(new CommandBase1_12());
      this.addRoute("dungeon", new CommandRouteDungeon(this.commandBase));
      this.addRoute("give", new CommandRouteGive1_12(this.commandBase));
      this.addRoute("config", new CommandRouteConfig(this.commandBase));
      this.addRoute("settings", new CommandRouteSettings(this.commandBase));
      this.addRoute("tower", new CommandRouteTower(this.commandBase));
      this.addRoute("biome", new CommandRouteBiome1_12(this.commandBase));
      this.addRoute("citadel", new CommandRouteCitadel(this.commandBase));
   }

   public void execute(CommandContext1_12 context, List<String> args) {
      if (args.size() == 0) {
         context.sendInfo("Usage: roguelike [dungeon | give | config | settings | tower]");
      }

      super.execute(context, args);
   }
}
