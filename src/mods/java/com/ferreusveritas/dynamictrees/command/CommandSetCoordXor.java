package com.ferreusveritas.dynamictrees.command;

import com.ferreusveritas.dynamictrees.util.CoordUtils;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommandSetCoordXor extends SubCommand {
   public static final String SETCOORDXOR = "setcoordxor";

   public CommandSetCoordXor() {
   }

   public String getName() {
      return "setcoordxor";
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
      switch(args.length) {
      case 2:
         return Lists.newArrayList(new String[]{"0"});
      default:
         return super.getTabCompletions(server, sender, args, targetPos);
      }
   }

   public void execute(World world, ICommandSender sender, String[] args) throws CommandException {
      int xor = 0;
      int arg = 0;

      while(arg < args.length) {
         switch(arg) {
         case 1:
            String work = args[1];

            try {
               xor = Integer.parseInt(work);
            } catch (NumberFormatException var8) {
               xor = work.hashCode();
            }
         default:
            ++arg;
         }
      }

      CoordUtils.coordXor = xor;
   }
}
