package com.endertech.minecraft.forge.commands;

import com.endertech.common.Args;
import com.endertech.minecraft.forge.ForgeMod;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public abstract class ForgeCommand extends CommandBase {
   private final String name;
   private final ICommandTask[] tasks;

   public ForgeCommand(ForgeMod mod, ICommandTask... tasks) {
      this(mod.getId(), tasks);
   }

   public ForgeCommand(String name, ICommandTask... tasks) {
      this.name = name;
      this.tasks = tasks;
   }

   public String getName() {
      return this.name;
   }

   public String getUsage(ICommandSender sender) {
      List<String> names = new ArrayList();

      for(ICommandTask task : this.tasks) {
         names.add(task.getName());
      }

      String usage = "/" + this.getName() + " " + ICommandTask.enclosedArg("task") + ". Tasks are: " + Args.join(names.toArray());
      return usage;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (args.length == 0) {
         throw new WrongUsageException(this.getUsage(sender), new Object[0]);
      } else {
         this.executeTask(sender.getEntityWorld(), args[0], sender, (String[])Arrays.copyOfRange(args, 1, args.length));
      }
   }

   protected void sendChatMessageTo(ICommandSender sender, String string) {
      sender.sendMessage(new TextComponentString(TextFormatting.WHITE + string));
   }

   public abstract void executeTask(World var1, String var2, ICommandSender var3, String[] var4) throws CommandException;

   public int getRequiredPermissionLevel() {
      return 3;
   }
}
