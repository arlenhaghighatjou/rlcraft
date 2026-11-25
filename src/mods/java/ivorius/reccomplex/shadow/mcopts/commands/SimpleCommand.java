package ivorius.reccomplex..shadow.mcopts.commands;

import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public abstract class SimpleCommand extends CommandExpecting {
   public String name;
   public String usage;
   public Consumer<Expect> expector;
   public int permissionLevel = 4;

   public SimpleCommand(String name) {
      this.name = name;
   }

   public SimpleCommand(String name, Consumer<Expect> expector) {
      this.name = name;
      this.usage = Parameters.expect().then(expector).usage();
      this.expector = expector;
   }

   public SimpleCommand(String name, String usage, Consumer<Expect> expector) {
      this.name = name;
      this.usage = usage;
      this.expector = expector;
   }

   public int getRequiredPermissionLevel() {
      return this.permissionLevel;
   }

   public SimpleCommand permitFor(int level) {
      this.permissionLevel = level;
      return this;
   }

   public String getName() {
      return this.name;
   }

   public void expect(Expect expect) {
      if (this.expector != null) {
         this.expector.accept(expect);
      }

   }

   public String getUsage(ICommandSender sender) {
      return String.format("%s %s", this.name, this.usage != null ? this.usage : this.expect().usage());
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
      return this.expect().get(server, sender, args, targetPos);
   }
}
