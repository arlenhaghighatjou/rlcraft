package crafttweaker.mc1120.commands.dumpzscommand;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public abstract class DumpZsTarget {
   public final String argumentName;

   protected DumpZsTarget(String argumentName) {
      this.argumentName = argumentName;
   }

   public abstract String getDescription();

   public abstract void execute(ICommandSender var1, MinecraftServer var2);
}
