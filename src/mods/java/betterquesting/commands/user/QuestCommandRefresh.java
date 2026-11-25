package betterquesting.commands.user;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.questing.party.IParty;
import betterquesting.api2.storage.DBEntry;
import betterquesting.commands.QuestCommandBase;
import betterquesting.network.handlers.NetBulkSync;
import betterquesting.network.handlers.NetNameSync;
import betterquesting.questing.party.PartyManager;
import betterquesting.storage.NameCache;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class QuestCommandRefresh extends QuestCommandBase {
   public QuestCommandRefresh() {
   }

   public String getCommand() {
      return "refresh";
   }

   public void runCommand(MinecraftServer server, CommandBase command, ICommandSender sender, String[] args) {
      if (sender instanceof EntityPlayerMP) {
         EntityPlayerMP player = (EntityPlayerMP)sender;
         if (!server.isDedicatedServer() && server.getServerOwner().equals(player.getGameProfile().getName())) {
            boolean nameChanged = NameCache.INSTANCE.updateName(player);
            DBEntry<IParty> party = PartyManager.INSTANCE.getParty(QuestingAPI.getQuestingUUID(player));
            if (nameChanged && party != null) {
               NetNameSync.quickSync((EntityPlayerMP)null, party.getID());
            }
         } else {
            NetBulkSync.sendReset(player, true, true);
            sender.sendMessage(new TextComponentTranslation("betterquesting.cmd.refresh", new Object[0]));
         }

      }
   }

   public String getPermissionNode() {
      return "betterquesting.command.user.refresh";
   }

   public DefaultPermissionLevel getPermissionLevel() {
      return DefaultPermissionLevel.ALL;
   }

   public String getPermissionDescription() {
      return "Permission to manually resync the local questing database with the server in case of potential desync issues";
   }
}
