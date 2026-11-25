package codersafterdark.reskillable.commands;

import javax.annotation.Nonnull;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;

public class ReskillableCmd extends CommandTreeBase {
   public ReskillableCmd() {
      this.addSubcommand(new CmdIncrementSkill());
      this.addSubcommand(new CmdResetAll());
      this.addSubcommand(new CmdResetSkill());
      this.addSubcommand(new CmdSetSkillLevel());
      this.addSubcommand(new CmdToggleTrait());
   }

   @Nonnull
   public String getName() {
      return "reskillable";
   }

   @Nonnull
   public String getUsage(@Nonnull ICommandSender sender) {
      return "reskillable.command.usage";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }
}
