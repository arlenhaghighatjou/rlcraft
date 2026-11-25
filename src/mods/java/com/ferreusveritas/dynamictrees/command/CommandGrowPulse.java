package com.ferreusveritas.dynamictrees.command;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.treedata.ITreePart;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommandGrowPulse extends SubCommand {
   public static final String GROWPULSE = "growpulse";

   public CommandGrowPulse() {
   }

   public String getName() {
      return "growpulse";
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
      switch(args.length) {
      case 2:
      case 3:
      case 4:
         return CommandBase.getTabCompletionCoordinate(args, 1, targetPos);
      default:
         return super.getTabCompletions(server, sender, args, targetPos);
      }
   }

   public void execute(World world, ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 4) {
         throw new WrongUsageException("commands.dynamictrees.growpulse.usage", new Object[0]);
      } else {
         BlockPos pos = CommandBase.parseBlockPos(sender, args, 1, false);
         ITreePart part = TreeHelper.getTreePart(world.getBlockState(pos));
         if (part.isRootNode()) {
            TreeHelper.growPulse(world, pos);
         } else {
            throw new CommandException("commands.dynamictrees.growpulse.norootyerror", new Object[]{pos.getX() + " " + pos.getY() + " " + pos.getZ()});
         }
      }
   }
}
