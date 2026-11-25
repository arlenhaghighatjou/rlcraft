package betterquesting.network.handlers;

import betterquesting.api.events.DatabaseEvent;
import betterquesting.api.network.QuestingPacket;
import betterquesting.api.questing.party.IParty;
import betterquesting.api2.storage.DBEntry;
import betterquesting.core.BetterQuesting;
import betterquesting.network.PacketSender;
import betterquesting.network.PacketTypeRegistry;
import betterquesting.questing.party.PartyManager;
import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NetPartySync {
   private static final ResourceLocation ID_NAME = new ResourceLocation("betterquesting:party_sync");

   public NetPartySync() {
   }

   public static void registerHandler() {
      PacketTypeRegistry.INSTANCE.registerServerHandler(ID_NAME, NetPartySync::onServer);
      if (BetterQuesting.proxy.isClient()) {
         PacketTypeRegistry.INSTANCE.registerClientHandler(ID_NAME, NetPartySync::onClient);
      }

   }

   public static void quickSync(int partyID) {
      MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
      IParty party = (IParty)PartyManager.INSTANCE.getValue(partyID);
      if (server != null && party != null) {
         List<EntityPlayerMP> players = new ArrayList();

         for(EaglercraftUUID uuid : party.getMembers()) {
            EntityPlayerMP p = server.getPlayerList().getPlayerByUUID(uuid);
            if (p != null) {
               players.add(p);
            }
         }

         sendSync((EntityPlayerMP[])players.toArray(new EntityPlayerMP[0]), new int[]{partyID});
      }
   }

   public static void sendSync(@Nullable EntityPlayerMP[] players, @Nullable int[] partyIDs) {
      if (partyIDs == null || partyIDs.length > 0) {
         if (players == null || players.length > 0) {
            NBTTagList dataList = new NBTTagList();

            for(DBEntry<IParty> party : partyIDs == null ? PartyManager.INSTANCE.getEntries() : PartyManager.INSTANCE.bulkLookup(partyIDs)) {
               NBTTagCompound entry = new NBTTagCompound();
               entry.setInteger("partyID", party.getID());
               entry.setTag("config", ((IParty)party.getValue()).writeToNBT(new NBTTagCompound()));
               dataList.appendTag(entry);
            }

            NBTTagCompound payload = new NBTTagCompound();
            payload.setTag("data", dataList);
            payload.setBoolean("merge", partyIDs != null);
            if (players == null) {
               PacketSender.INSTANCE.sendToAll(new QuestingPacket(ID_NAME, payload));
            } else {
               PacketSender.INSTANCE.sendToPlayers(new QuestingPacket(ID_NAME, payload), players);
            }

         }
      }
   }

   @SideOnly(Side.CLIENT)
   public static void requestSync(@Nullable int[] partyIDs) {
      NBTTagCompound payload = new NBTTagCompound();
      if (partyIDs != null) {
         payload.setIntArray("partyIDs", partyIDs);
      }

      PacketSender.INSTANCE.sendToServer(new QuestingPacket(ID_NAME, payload));
   }

   private static void onServer(Tuple<NBTTagCompound, EntityPlayerMP> message) {
      NBTTagCompound payload = (NBTTagCompound)message.getFirst();
      int[] reqIDs = !payload.hasKey("partyIDs") ? null : payload.getIntArray("partyIDs");
      sendSync(new EntityPlayerMP[]{(EntityPlayerMP)message.getSecond()}, reqIDs);
   }

   @SideOnly(Side.CLIENT)
   private static void onClient(NBTTagCompound message) {
      NBTTagList data = message.getTagList("data", 10);
      if (!message.getBoolean("merge")) {
         PartyManager.INSTANCE.reset();
      }

      for(int i = 0; i < data.tagCount(); ++i) {
         NBTTagCompound tag = data.getCompoundTagAt(i);
         if (tag.hasKey("partyID", 99)) {
            int partyID = tag.getInteger("partyID");
            IParty party = (IParty)PartyManager.INSTANCE.getValue(partyID);
            if (party == null) {
               party = PartyManager.INSTANCE.createNew(partyID);
            }

            party.readFromNBT(tag.getCompoundTag("config"));
         }
      }

      MinecraftForge.EVENT_BUS.post(new DatabaseEvent.Update(DatabaseEvent.DBType.PARTY));
   }
}
