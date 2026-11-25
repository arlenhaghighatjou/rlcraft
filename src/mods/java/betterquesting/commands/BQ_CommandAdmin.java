package betterquesting.commands;

import betterquesting.commands.admin.QuestCommandComplete;
import betterquesting.commands.admin.QuestCommandDefaults;
import betterquesting.commands.admin.QuestCommandDelete;
import betterquesting.commands.admin.QuestCommandEdit;
import betterquesting.commands.admin.QuestCommandHardcore;
import betterquesting.commands.admin.QuestCommandLives;
import betterquesting.commands.admin.QuestCommandPurge;
import betterquesting.commands.admin.QuestCommandReset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

public class BQ_CommandAdmin extends CommandBase {
   private final List<QuestCommandBase> coms = new ArrayList();

   public BQ_CommandAdmin() {
      PermissionAPI.registerNode("betterquesting.command.admin", DefaultPermissionLevel.OP, "admin commmand permission");
      this.coms.add(new QuestCommandEdit());
      this.coms.add(new QuestCommandHardcore());
      this.coms.add(new QuestCommandReset());
      this.coms.add(new QuestCommandComplete());
      this.coms.add(new QuestCommandDelete());
      this.coms.add(new QuestCommandDefaults());
      this.coms.add(new QuestCommandLives());
      this.coms.add(new QuestCommandPurge());
   }

   public String getName() {
      return "bq_admin";
   }

   public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
      return !(sender instanceof EntityPlayer) || PermissionAPI.hasPermission((EntityPlayer)sender, "betterquesting.command.admin");
   }

   public String getUsage(ICommandSender sender) {
      StringBuilder txt = new StringBuilder();

      for(int i = 0; i < this.coms.size(); ++i) {
         QuestCommandBase c = (QuestCommandBase)this.coms.get(i);
         txt.append("/bq_admin ").append(c.getCommand());
         if (c.getUsageSuffix().length() > 0) {
            txt.append(" ").append(c.getUsageSuffix());
         }

         if (i < this.coms.size() - 1) {
            txt.append(", ");
         }
      }

      return txt.toString();
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] strings, BlockPos pos) {
      if (strings.length == 1) {
         List<String> base = new ArrayList();

         for(QuestCommandBase c : this.coms) {
            if (!(sender instanceof EntityPlayer) || PermissionAPI.hasPermission((EntityPlayer)sender, c.getPermissionNode())) {
               base.add(c.getCommand());
            }
         }

         return getListOfStringsMatchingLastWord(strings, (String[])base.toArray(new String[0]));
      } else {
         if (strings.length > 1) {
            for(QuestCommandBase c : this.coms) {
               if (c.getCommand().equalsIgnoreCase(strings[0]) && (!(sender instanceof EntityPlayer) || PermissionAPI.hasPermission((EntityPlayer)sender, c.getPermissionNode()))) {
                  return c.autoComplete(server, sender, strings);
               }
            }
         }

         return Collections.emptyList();
      }
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 1) {
         throw new WrongUsageException(this.getUsage(sender), new Object[0]);
      } else {
         for(QuestCommandBase c : this.coms) {
            if (c.getCommand().equalsIgnoreCase(args[0])) {
               if (sender instanceof EntityPlayer && !PermissionAPI.hasPermission((EntityPlayer)sender, c.getPermissionNode())) {
                  TextComponentTranslation cc = new TextComponentTranslation("commands.generic.permission", new Object[0]);
                  cc.getStyle().setColor(TextFormatting.RED);
                  sender.sendMessage(cc);
                  return;
               }

               if (c.validArgs(args)) {
                  c.runCommand(server, this, sender, args);
                  return;
               }

               throw c.getException(this);
            }
         }

         throw new WrongUsageException(this.getUsage(sender), new Object[0]);
      }
   }

   public boolean isUsernameIndex(String[] args, int index) {
      if (args.length < 1) {
         return false;
      } else {
         for(QuestCommandBase c : this.coms) {
            if (c.getCommand().equalsIgnoreCase(args[0])) {
               return c.isArgUsername(args, index);
            }
         }

         return false;
      }
   }
}
