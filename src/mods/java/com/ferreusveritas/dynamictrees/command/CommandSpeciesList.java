package com.ferreusveritas.dynamictrees.command;

import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class CommandSpeciesList extends SubCommand {
   public static final String SPECIESLIST = "specieslist";

   public CommandSpeciesList() {
   }

   public String getName() {
      return "specieslist";
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
      return Collections.emptyList();
   }

   public void execute(World world, ICommandSender sender, String[] args) throws CommandException {
      TreeRegistry.getSpeciesDirectory().forEach((r) -> {
         sender.sendMessage(new TextComponentString(r.toString()));
      });
   }
}
