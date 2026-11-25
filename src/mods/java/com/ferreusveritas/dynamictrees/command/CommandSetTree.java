package com.ferreusveritas.dynamictrees.command;

import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommandSetTree extends SubCommand {
   public static final String SETTREE = "settree";
   public static final String DEFAULTJOCODE = "JP";

   public CommandSetTree() {
   }

   public String getName() {
      return "settree";
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
      switch(args.length) {
      case 2:
      case 3:
      case 4:
         return CommandBase.getTabCompletionCoordinate(args, 1, targetPos);
      case 5:
         return CommandBase.getListOfStringsMatchingLastWord(args, Species.REGISTRY.getKeys());
      case 6:
         return Lists.newArrayList(new String[]{"JP"});
      case 7:
         return Lists.newArrayList(new String[]{"0"});
      default:
         return super.getTabCompletions(server, sender, args, targetPos);
      }
   }

   public void execute(World world, ICommandSender sender, String[] args) throws CommandException {
      BlockPos pos = BlockPos.ORIGIN;
      Species species = null;
      String joCode = "";
      int turns = 0;
      if (args.length < 5) {
         throw new WrongUsageException("commands.dynamictrees.setree.usage", new Object[0]);
      } else {
         for(int arg = 0; arg < args.length; ++arg) {
            switch(arg) {
            case 3:
               pos = CommandBase.parseBlockPos(sender, args, 1, false);
               break;
            case 4:
               species = TreeRegistry.findSpeciesSloppy(args[4]);
               if (species == Species.NULLSPECIES) {
                  throw new WrongUsageException("commands.dynamictrees.setree.specieserror", new Object[]{args[4]});
               }
               break;
            case 5:
               joCode = args[5];
               break;
            case 6:
               try {
                  turns = Integer.parseInt(args[6]);
               } catch (NumberFormatException var10) {
                  throw new WrongUsageException("commands.dynamictrees.rotatejocode.turnserror", new Object[]{args[6]});
               }
            }
         }

         if (joCode.isEmpty()) {
            joCode = "JP";
         }

         turns = 3 - turns % 4 + 3;
         species.getJoCode(joCode).rotate(EnumFacing.getHorizontal(turns)).setCareful(true).generate(world, species, pos, world.getBiome(pos), EnumFacing.SOUTH, 8, SafeChunkBounds.ANY);
      }
   }
}
