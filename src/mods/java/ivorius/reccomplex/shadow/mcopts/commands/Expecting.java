package ivorius.reccomplex..shadow.mcopts.commands;

import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public interface Expecting extends ICommand {
   default Expect expect() {
      return Parameters.expect().then(this::expect);
   }

   void expect(Expect var1);

   @Nonnull
   default String getUsage(ICommandSender var1) {
      return String.format("%s %s", this.getName(), this.expect().usage());
   }

   @Nonnull
   default List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
      return this.expect().get(server, sender, args, pos);
   }
}
