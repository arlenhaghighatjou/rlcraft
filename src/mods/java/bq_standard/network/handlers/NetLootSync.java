package bq_standard.network.handlers;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.network.IPacketRegistry;
import betterquesting.api.network.IPacketSender;
import betterquesting.api.network.QuestingPacket;
import bq_standard.core.BQ_Standard;
import bq_standard.rewards.loot.LootRegistry;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

public class NetLootSync {
   private static final ResourceLocation ID_NAME = new ResourceLocation("bq_standard:loot_database");

   public NetLootSync() {
   }

   public static void registerHandler() {
      ((IPacketRegistry)QuestingAPI.getAPI(ApiReference.PACKET_REG)).registerServerHandler(ID_NAME, NetLootSync::onServer);
      if (BQ_Standard.proxy.isClient()) {
         ((IPacketRegistry)QuestingAPI.getAPI(ApiReference.PACKET_REG)).registerClientHandler(ID_NAME, NetLootSync::onClient);
      }

   }

   @SideOnly(Side.CLIENT)
   public static void requestEdit(NBTTagCompound data) {
      NBTTagCompound payload = new NBTTagCompound();
      payload.setTag("data", data);
      ((IPacketSender)QuestingAPI.getAPI(ApiReference.PACKET_SENDER)).sendToServer(new QuestingPacket(ID_NAME, payload));
   }

   public static void sendSync(@Nullable EntityPlayerMP player) {
      NBTTagCompound payload = new NBTTagCompound();
      payload.setTag("data", LootRegistry.INSTANCE.writeToNBT(new NBTTagCompound(), (List)null));
      if (player == null) {
         ((IPacketSender)QuestingAPI.getAPI(ApiReference.PACKET_SENDER)).sendToAll(new QuestingPacket(ID_NAME, payload));
      } else {
         ((IPacketSender)QuestingAPI.getAPI(ApiReference.PACKET_SENDER)).sendToPlayers(new QuestingPacket(ID_NAME, payload), new EntityPlayerMP[]{player});
      }

   }

   private static void onServer(Tuple<NBTTagCompound, EntityPlayerMP> message) {
      EntityPlayerMP sender = (EntityPlayerMP)message.getSecond();
      NBTTagCompound data = (NBTTagCompound)message.getFirst();
      if (sender.getServer() != null) {
         if (!sender.getServer().getPlayerList().canSendCommands(sender.getGameProfile())) {
            BQ_Standard.logger.log(Level.WARN, "Player " + sender.getName() + " (UUID:" + QuestingAPI.getQuestingUUID(sender) + ") tried to edit loot chests without OP permissions!");
            sender.sendStatusMessage(new TextComponentString(TextFormatting.RED + "You need to be OP to edit loot!"), true);
         } else {
            BQ_Standard.logger.log(Level.INFO, "Player " + sender.getName() + " edited loot chests");
            LootRegistry.INSTANCE.readFromNBT(data.getCompoundTag("data"), false);
            sendSync((EntityPlayerMP)null);
         }
      }
   }

   @SideOnly(Side.CLIENT)
   private static void onClient(NBTTagCompound message) {
      LootRegistry.INSTANCE.readFromNBT(message.getCompoundTag("data"), false);
      LootRegistry.INSTANCE.updateUI = true;
   }
}
