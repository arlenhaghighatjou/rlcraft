package com.ferreusveritas.dynamictrees.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DTCommand extends CommandBase {
   public static final String COMMAND = "dt";
   private final Map<String, SubCommand> commands = new HashMap();

   private void addSubCommand(SubCommand command) {
      this.commands.put(command.getName(), command);
   }

   public DTCommand() {
      this.addSubCommand(new CommandRepop());
      this.addSubCommand(new CommandSetTree());
      this.addSubCommand(new CommandGetTree());
      this.addSubCommand(new CommandGrowPulse());
      this.addSubCommand(new CommandKillTree());
      this.addSubCommand(new CommandSoilLife());
      this.addSubCommand(new CommandSpeciesList());
      this.addSubCommand(new CommandCreateStaff());
      this.addSubCommand(new CommandRotateJoCode());
      this.addSubCommand(new CommandSetCoordXor());
      this.addSubCommand(new CommandClearOrphaned());
      this.addSubCommand(new CommandPurgeTrees());
      this.addSubCommand(new CommandTransform());
      this.addSubCommand(new CommandCreateTransformPotion());
   }

   public String getName() {
      return "dt";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getUsage(ICommandSender sender) {
      return "commands.dynamictrees.usage";
   }

   private String getSubCommands() {
      StringBuilder builder = new StringBuilder();
      boolean first = true;

      for(String name : this.commands.keySet()) {
         if (!first) {
            builder.append('|');
         }

         builder.append(name);
         first = false;
      }

      return builder.toString();
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (args.length == 0) {
         throw new WrongUsageException("commands.dynamictrees.usage", new Object[]{this.getSubCommands()});
      } else {
         String subCommand = args[0];
         World world = sender.getEntityWorld();
         if (args.length >= 1) {
            if (!this.commands.containsKey(subCommand)) {
               throw new WrongUsageException("commands.dynamictrees.usage", new Object[]{this.getSubCommands()});
            }

            ((SubCommand)this.commands.get(subCommand)).execute(world, sender, args);
         }

      }
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
      if (args.length == 1) {
         return getListOfStringsMatchingLastWord(args, this.commands.keySet());
      } else {
         String subCommand = args[0];
         return args.length >= 2 && this.commands.containsKey(subCommand) ? ((SubCommand)this.commands.get(subCommand)).getTabCompletions(server, sender, args, targetPos) : super.getTabCompletions(server, sender, args, targetPos);
      }
   }
}
