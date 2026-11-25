package ivorius.reccomplex.commands;

import ivorius.reccomplex..shadow.mcopts.commands.CommandSplit;
import ivorius.reccomplex.commands.structure.CommandListStructures;
import ivorius.reccomplex.commands.structure.CommandLookupStructure;
import net.minecraft.command.ICommand;

public class CommandStructures extends CommandSplit {
   protected CommandLookupStructure lookup;
   protected CommandListStructures list;

   public CommandStructures(String name, CommandLookupStructure lookup, CommandListStructures list, ICommand... commands) {
      super(name, commands);
      this.add(this.lookup = lookup);
      this.add(this.list = list);
   }

   public String lookup(String id) {
      return String.format("%s %s", this.getName(), this.lookup.getName());
   }

   public String list() {
      return String.format("%s %s", this.getName(), this.list.getName());
   }

   public String list(int page) {
      return String.format("%s %s %d", this.getName(), this.list.getName(), page);
   }
}
