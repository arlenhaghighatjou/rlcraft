package com.ferreusveritas.dynamictrees.command;

import com.ferreusveritas.dynamictrees.api.WorldGenRegistry;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class CommandRepop extends SubCommand {
   public static final String REPOP = "repop";

   public CommandRepop() {
   }

   public String getName() {
      return "repop";
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
      return super.getTabCompletions(server, sender, args, targetPos);
   }

   public void execute(World world, ICommandSender sender, String[] args) throws CommandException {
      sender.sendMessage(new TextComponentTranslation("commands.dynamictrees.repop.run", new Object[0]));
      WorldGenRegistry.populateDataBase();
   }
}
