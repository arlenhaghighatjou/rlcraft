package ivorius.reccomplex.commands.structure.sight;

import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex..shadow.mcopts.commands.CommandSplit;
import net.minecraft.command.ICommand;

public class CommandSight extends CommandSplit {
   public ICommand delete;
   public ICommand info;

   public CommandSight() {
      super(RCConfig.commandPrefix + "sight");
      this.add(this.delete = new CommandSightDelete());
      this.add(new CommandSightAdd());
      this.add(new CommandSightCheck("check", false));
      this.add(this.info = new CommandSightInfo());
   }
}
