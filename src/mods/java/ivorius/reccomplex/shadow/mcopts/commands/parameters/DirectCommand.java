package ivorius.reccomplex..shadow.mcopts.commands.parameters;

import ivorius.reccomplex..shadow.mcopts.commands.SimpleCommand;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import java.util.function.Consumer;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class DirectCommand extends SimpleCommand {
   public Consumer<ICommandSender> consumer;

   public DirectCommand(String name, Consumer<ICommandSender> consumer) {
      super(name);
      this.consumer = consumer;
   }

   public DirectCommand(String name, String usage, Consumer<Expect> expector, Consumer<ICommandSender> consumer) {
      super(name, usage, expector);
      this.consumer = consumer;
   }

   public DirectCommand(String name, Consumer<Expect> expector, Consumer<ICommandSender> consumer) {
      super(name, expector);
      this.consumer = consumer;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      this.consumer.accept(sender);
   }
}
