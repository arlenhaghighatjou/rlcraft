package bq_standard.network.handlers;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.network.IPacketRegistry;
import betterquesting.api.network.IPacketSender;
import betterquesting.api.network.QuestingPacket;
import bq_standard.core.BQ_Standard;
import bq_standard.rewards.loot.LootRegistry;
import java.util.function.Consumer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class NetLootImport {
   private static final ResourceLocation ID_NAME = new ResourceLocation("bq_standard:loot_import");

   public NetLootImport() {
   }

   public static void registerHandler() {
      ((IPacketRegistry)QuestingAPI.getAPI(ApiReference.PACKET_REG)).registerServerHandler(ID_NAME, NetLootImport::onServer);
   }

   @SideOnly(Side.CLIENT)
   public static void importLoot(NBTTagCompound data) {
      NBTTagCompound payload = new NBTTagCompound();
      payload.setTag("data", data);
      ((IPacketSender)QuestingAPI.getAPI(ApiReference.PACKET_SENDER)).sendToServer(new QuestingPacket(ID_NAME, payload));
   }

   private static void onServer(Tuple<NBTTagCompound, EntityPlayerMP> message) {
      EntityPlayerMP sender = (EntityPlayerMP)message.getSecond();
      NBTTagCompound tag = (NBTTagCompound)message.getFirst();
      if (sender.getServer() != null) {
         if (!sender.getServer().getPlayerList().canSendCommands(sender.getGameProfile())) {
            BQ_Standard.logger.log(Level.WARN, "Player " + sender.getName() + " (UUID:" + QuestingAPI.getQuestingUUID(sender) + ") tried to import loot without OP permissions!");
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "You need to be OP to edit loot!"));
         } else {
            LootRegistry.INSTANCE.readFromNBT(tag.getCompoundTag("data"), false);
            NetLootSync.sendSync((EntityPlayerMP)null);
         }
      }
   }
}
