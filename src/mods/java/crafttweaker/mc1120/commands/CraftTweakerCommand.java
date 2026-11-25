package crafttweaker.mc1120.commands;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

public abstract class CraftTweakerCommand {
   private static final ITextComponent[] NO_DESCRIPTION = new ITextComponent[]{SpecialMessagesChat.getNormalMessage("No Description provided")};
   protected final String subCommandName;
   private ITextComponent[] description;

   public CraftTweakerCommand(String subCommandName) {
      this.subCommandName = subCommandName;
      this.init();
   }

   protected abstract void init();

   public ITextComponent[] getDescription() {
      return this.description == null ? NO_DESCRIPTION : this.description;
   }

   public void setDescription(ITextComponent... descriptionIn) {
      this.description = descriptionIn;
   }

   public List<String> getSubSubCommand(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
      return Collections.emptyList();
   }

   public String getSubCommandName() {
      return this.subCommandName;
   }

   public abstract void executeCommand(MinecraftServer var1, ICommandSender var2, String[] var3);
}
