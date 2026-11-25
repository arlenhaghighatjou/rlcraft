package lumien.bloodmoon.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lumien.bloodmoon.Bloodmoon;
import lumien.bloodmoon.config.BloodmoonConfig;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandBloodmoon extends CommandBase {
   public CommandBloodmoon() {
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
      List<String> suggestions = new ArrayList();
      if (args.length == 1) {
         suggestions.add("force");
         suggestions.add("stop");
         suggestions.add("entitynames");
      }

      return getListOfStringsMatchingLastWord(args, suggestions);
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getName() {
      return "bloodmoon";
   }

   public String getUsage(ICommandSender sender) {
      return "/bloodmoon <force|stop|entitynames>";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (args.length == 0) {
         throw new WrongUsageException(this.getUsage(sender), new Object[0]);
      } else {
         String subCommand = args[0];
         if (BloodmoonHandler.INSTANCE == null) {
            throw new CommandException(I18n.format("text.bloodmoon.commandError", new Object[0]), new Object[0]);
         } else {
            if (subCommand.equals("force")) {
               BloodmoonHandler.INSTANCE.force();
               sender.sendMessage(new TextComponentTranslation("text.bloodmoon.force", new Object[0]));
            } else if (subCommand.equals("stop")) {
               BloodmoonHandler.INSTANCE.stop();
               sender.sendMessage(new TextComponentTranslation("text.bloodmoon.stop", new Object[0]));
            } else {
               if (!subCommand.equals("entitynames")) {
                  throw new WrongUsageException(this.getUsage(sender), new Object[0]);
               }

               Entity senderEntity = sender.getCommandSenderEntity();
               Set<String> names = new HashSet();

               for(Entity e : senderEntity.world.getEntitiesInAABBexcluding(senderEntity, senderEntity.getEntityBoundingBox().grow(10.0D, 10.0D, 10.0D), EntitySelectors.NOT_SPECTATING)) {
                  if (e instanceof IMob) {
                     BloodmoonConfig var10001 = Bloodmoon.config;
                     names.add(BloodmoonConfig.getEntityName(e.getClass()));
                  }
               }

               sender.sendMessage(new TextComponentTranslation("text.bloodmoon.entity", new Object[0]));

               for(String s : names) {
                  sender.sendMessage(new TextComponentString(" - " + s));
               }
            }

         }
      }
   }
}
