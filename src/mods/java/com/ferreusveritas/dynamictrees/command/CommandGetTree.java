package com.ferreusveritas.dynamictrees.command;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.worldgen.JoCode;
import java.util.List;
import java.util.function.Function;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class CommandGetTree extends SubCommand {
   public static final String GETTREE = "gettree";

   public CommandGetTree() {
   }

   public String getName() {
      return "gettree";
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
         throw new WrongUsageException("commands.dynamictrees.getree.usage", new Object[0]);
      } else {
         BlockPos pos = CommandBase.parseBlockPos(sender, args, 1, false);
         Species species = TreeHelper.getBestGuessSpecies(world, pos);
         String code = (String)TreeHelper.getJoCode(world, pos, species).map(JoCode::toString).orElse("?");
         sender.sendMessage(new TextComponentString(species.toString()));
         sender.sendMessage(new TextComponentString(code));
      }
   }
}
