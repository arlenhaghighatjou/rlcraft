package ivorius.reccomplex..shadow.mcopts.commands;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public abstract class CommandExpecting extends CommandBase implements Expecting {
   public CommandExpecting() {
   }

   @Nonnull
   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
      return super.getTabCompletions(server, sender, args, targetPos);
   }
}
