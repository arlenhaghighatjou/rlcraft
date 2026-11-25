package betterquesting.network.handlers;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.enums.EnumPartyStatus;
import betterquesting.api.events.DatabaseEvent;
import betterquesting.api.network.QuestingPacket;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.party.IParty;
import betterquesting.api2.storage.DBEntry;
import betterquesting.core.BetterQuesting;
import betterquesting.network.PacketSender;
import betterquesting.network.PacketTypeRegistry;
import betterquesting.questing.party.PartyInvitations;
import betterquesting.questing.party.PartyManager;
import betterquesting.storage.NameCache;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

public class NetPartyAction {
   private static final ResourceLocation ID_NAME = new ResourceLocation("betterquesting:party_action");

   public NetPartyAction() {
   }

   public static void registerHandler() {
      PacketTypeRegistry.INSTANCE.registerServerHandler(ID_NAME, NetPartyAction::onServer);
      if (BetterQuesting.proxy.isClient()) {
         PacketTypeRegistry.INSTANCE.registerClientHandler(ID_NAME, NetPartyAction::onClient);
      }

   }

   @SideOnly(Side.CLIENT)
   public static void sendAction(NBTTagCompound payload) {
      PacketSender.INSTANCE.sendToServer(new QuestingPacket(ID_NAME, payload));
   }

   private static void onServer(Tuple<NBTTagCompound, EntityPlayerMP> message) {
      EntityPlayerMP sender = (EntityPlayerMP)message.getSecond();
      int action = !((NBTTagCompound)message.getFirst()).hasKey("action", 99) ? -1 : ((NBTTagCompound)message.getFirst()).getInteger("action");
      int partyID = !((NBTTagCompound)message.getFirst()).hasKey("partyID", 99) ? -1 : ((NBTTagCompound)message.getFirst()).getInteger("partyID");
      IParty party = (IParty)PartyManager.INSTANCE.getValue(partyID);
      int permission = party == null ? 0 : checkPermission(QuestingAPI.getQuestingUUID(sender), party);
      switch(action) {
      case 0:
         createParty(sender, ((NBTTagCompound)message.getFirst()).getString("name"));
         break;
      case 1:
         if (permission >= 3) {
            deleteParty(partyID);
         }
         break;
      case 2:
         if (permission >= 2) {
            editParty(partyID, party, ((NBTTagCompound)message.getFirst()).getCompoundTag("data"));
         }
         break;
      case 3:
         if (permission >= 2) {
            inviteUser(partyID, ((NBTTagCompound)message.getFirst()).getString("username"), ((NBTTagCompound)message.getFirst()).getLong("expiry"));
         }
         break;
      case 4:
         acceptInvite(partyID, sender);
         break;
      case 5:
         kickUser(partyID, sender, party, ((NBTTagCompound)message.getFirst()).getString("username"), permission);
         break;
      default:
         BetterQuesting.logger.log(Level.ERROR, "Invalid party action '" + action + "'. Full payload:\n" + ((NBTTagCompound)message.getFirst()).toString());
      }

   }

   private static void createParty(EntityPlayerMP sender, String name) {
      UUID playerID = QuestingAPI.getQuestingUUID(sender);
      if (PartyManager.INSTANCE.getParty(playerID) == null) {
         int partyID = PartyManager.INSTANCE.nextID();
         IParty party = PartyManager.INSTANCE.createNew(partyID);
         party.getProperties().setProperty(NativeProps.NAME, name);
         party.setStatus(playerID, EnumPartyStatus.OWNER);
         NetPartySync.sendSync(new EntityPlayerMP[]{sender}, new int[]{partyID});
      }
   }

   private static void deleteParty(int partyID) {
      PartyManager.INSTANCE.removeID(partyID);
      PartyInvitations.INSTANCE.purgeInvites(partyID);
      NBTTagCompound payload = new NBTTagCompound();
      payload.setInteger("action", 1);
      payload.setInteger("partyID", partyID);
      PacketSender.INSTANCE.sendToAll(new QuestingPacket(ID_NAME, payload));
   }

   private static void editParty(int partyID, IParty party, NBTTagCompound settings) {
      party.readProperties(settings);
      NetPartySync.quickSync(partyID);
   }

   private static void inviteUser(int partyID, String username, long expiry) {
      UUID uuid = null;
      MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
      EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(username);
      if (player != null) {
         uuid = QuestingAPI.getQuestingUUID(player);
      }

      if (uuid == null) {
         uuid = NameCache.INSTANCE.getUUID(username);
      }

      if (uuid != null) {
         PartyInvitations.INSTANCE.postInvite(uuid, partyID, expiry);
         if (player != null) {
            NetPartySync.sendSync(new EntityPlayerMP[]{player}, new int[]{partyID});
            NetInviteSync.sendSync(player);
         }
      } else {
         BetterQuesting.logger.error("Unable to identify " + username + " to invite to party " + partyID);
      }

   }

   private static void acceptInvite(int partyID, EntityPlayerMP sender) {
      UUID playerID = QuestingAPI.getQuestingUUID(sender);
      DBEntry<IParty> party = PartyManager.INSTANCE.getParty(playerID);
      if (party == null) {
         if (PartyInvitations.INSTANCE.acceptInvite(playerID, partyID)) {
            NetPartySync.quickSync(partyID);
            NetNameSync.quickSync(sender, partyID);
         } else {
            BetterQuesting.logger.error("Invalid invite for " + sender.getName() + " to party " + partyID);
         }

         NetInviteSync.sendSync(sender);
      }
   }

   private static void kickUser(int partyID, EntityPlayerMP sender, IParty party, String username, int permission) {
      if (party == null) {
         BetterQuesting.logger.error("Tried to kick a player from a non-existant party (" + partyID + ")");
      } else {
         UUID uuid = null;
         MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
         EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(username);
         if (player != null) {
            uuid = QuestingAPI.getQuestingUUID(player);
         }

         if (uuid == null) {
            uuid = NameCache.INSTANCE.getUUID(username);
         }

         if (uuid == null) {
            BetterQuesting.logger.error("Unable to identify " + username + " to remove them from party " + partyID);
         } else {
            if (!uuid.equals(QuestingAPI.getQuestingUUID(sender)) && checkPermission(uuid, party) >= permission) {
               BetterQuesting.logger.error("Insufficient permissions to kick " + username + " from party " + partyID);
            } else {
               if (party.getStatus(uuid) != null) {
                  party.kickUser(uuid);
               }

               if (party.getMembers().size() > 0) {
                  NetPartySync.quickSync(partyID);
                  if (player != null) {
                     NBTTagCompound payload = new NBTTagCompound();
                     payload.setInteger("action", 5);
                     payload.setInteger("partyID", partyID);
                     PacketSender.INSTANCE.sendToPlayers(new QuestingPacket(ID_NAME, payload), player);
                  }
               } else {
                  PartyManager.INSTANCE.removeID(partyID);
                  PartyInvitations.INSTANCE.purgeInvites(partyID);
                  NBTTagCompound payload = new NBTTagCompound();
                  payload.setInteger("action", 1);
                  payload.setInteger("partyID", partyID);
                  PacketSender.INSTANCE.sendToAll(new QuestingPacket(ID_NAME, payload));
               }
            }

         }
      }
   }

   private static int checkPermission(UUID playerID, IParty party) {
      MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
      EntityPlayerMP player = server == null ? null : server.getPlayerList().getPlayerByUUID(playerID);
      if (player != null && server.getPlayerList().canSendCommands(player.getGameProfile())) {
         return 4;
      } else {
         EnumPartyStatus status = party.getStatus(playerID);
         if (status == null) {
            return 0;
         } else {
            switch(status) {
            case MEMBER:
               return 1;
            case ADMIN:
               return 2;
            case OWNER:
               return 3;
            default:
               return 0;
            }
         }
      }
   }

   @SideOnly(Side.CLIENT)
   private static void onClient(NBTTagCompound message) {
      int action = !message.hasKey("action", 99) ? -1 : message.getInteger("action");
      int partyID = !message.hasKey("partyID", 99) ? -1 : message.getInteger("partyID");
      switch(action) {
      case 1:
         PartyManager.INSTANCE.removeID(partyID);
         PartyInvitations.INSTANCE.purgeInvites(partyID);
         MinecraftForge.EVENT_BUS.post(new DatabaseEvent.Update(DatabaseEvent.DBType.PARTY));
         break;
      case 5:
         IParty party = (IParty)PartyManager.INSTANCE.getValue(partyID);
         if (party != null) {
            party.kickUser(QuestingAPI.getQuestingUUID(Minecraft.getMinecraft().player));
            MinecraftForge.EVENT_BUS.post(new DatabaseEvent.Update(DatabaseEvent.DBType.PARTY));
         }
      }

   }
}
