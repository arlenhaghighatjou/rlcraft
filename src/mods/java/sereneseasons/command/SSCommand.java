package sereneseasons.command;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import sereneseasons.api.season.Season;
import sereneseasons.handler.season.SeasonHandler;
import sereneseasons.season.SeasonSavedData;
import sereneseasons.season.SeasonTime;

public class SSCommand extends CommandBase {
   public SSCommand() {
   }

   public String getName() {
      return "sereneseasons";
   }

   public List getAliases() {
      return Lists.newArrayList(new String[]{"ss"});
   }

   public String getUsage(ICommandSender sender) {
      return "commands.sereneseasons.usage";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 1) {
         throw new WrongUsageException("commands.sereneseasons.usage", new Object[0]);
      } else {
         if ("setseason".equals(args[0])) {
            this.setSeason(sender, args);
         }

      }
   }

   private void setSeason(ICommandSender sender, String[] args) throws CommandException {
      EntityPlayerMP player = getCommandSenderAsPlayer(sender);
      Season.SubSeason newSeason = null;

      for(Season.SubSeason season : Season.SubSeason.VALUES) {
         if (season.toString().toLowerCase().equals(args[1].toLowerCase())) {
            newSeason = season;
            break;
         }
      }

      if (newSeason != null) {
         SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(player.world);
         seasonData.seasonCycleTicks = SeasonTime.ZERO.getSubSeasonDuration() * newSeason.ordinal();
         seasonData.markDirty();
         SeasonHandler.sendSeasonUpdate(player.world);
         sender.sendMessage(new TextComponentTranslation("commands.sereneseasons.setseason.success", new Object[]{args[1]}));
      } else {
         sender.sendMessage(new TextComponentTranslation("commands.sereneseasons.setseason.fail", new Object[]{args[1]}));
      }

   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[]{"setseason"}) : null;
   }
}
