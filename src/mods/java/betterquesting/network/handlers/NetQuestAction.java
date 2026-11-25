package betterquesting.network.handlers;

import betterquesting.api.network.QuestingPacket;
import betterquesting.api.questing.IQuest;
import betterquesting.api2.storage.DBEntry;
import betterquesting.core.BetterQuesting;
import betterquesting.network.PacketSender;
import betterquesting.network.PacketTypeRegistry;
import betterquesting.questing.QuestDatabase;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

public class NetQuestAction {
   private static final ResourceLocation ID_NAME = new ResourceLocation("betterquesting:quest_action");

   public NetQuestAction() {
   }

   public static void registerHandler() {
      PacketTypeRegistry.INSTANCE.registerServerHandler(ID_NAME, NetQuestAction::onServer);
   }

   @SideOnly(Side.CLIENT)
   public static void requestClaim(@Nonnull int[] questIDs) {
      if (questIDs.length > 0) {
         NBTTagCompound payload = new NBTTagCompound();
         payload.setInteger("action", 0);
         payload.setIntArray("questIDs", questIDs);
         PacketSender.INSTANCE.sendToServer(new QuestingPacket(ID_NAME, payload));
      }
   }

   @SideOnly(Side.CLIENT)
   public static void requestDetect(@Nonnull int[] questIDs) {
      if (questIDs.length > 0) {
         NBTTagCompound payload = new NBTTagCompound();
         payload.setInteger("action", 1);
         payload.setIntArray("questIDs", questIDs);
         PacketSender.INSTANCE.sendToServer(new QuestingPacket(ID_NAME, payload));
      }
   }

   private static void onServer(Tuple<NBTTagCompound, EntityPlayerMP> message) {
      int action = !((NBTTagCompound)message.getFirst()).hasKey("action", 99) ? -1 : ((NBTTagCompound)message.getFirst()).getInteger("action");
      switch(action) {
      case 0:
         claimQuest(((NBTTagCompound)message.getFirst()).getIntArray("questIDs"), (EntityPlayerMP)message.getSecond());
         break;
      case 1:
         detectQuest(((NBTTagCompound)message.getFirst()).getIntArray("questIDs"), (EntityPlayerMP)message.getSecond());
         break;
      default:
         BetterQuesting.logger.log(Level.ERROR, "Invalid quest user action '" + action + "'. Full payload:\n" + ((NBTTagCompound)message.getFirst()).toString());
      }

   }

   public static void claimQuest(int[] questIDs, EntityPlayerMP player) {
      for(DBEntry<IQuest> entry : QuestDatabase.INSTANCE.bulkLookup(questIDs)) {
         if (((IQuest)entry.getValue()).canClaim(player)) {
            ((IQuest)entry.getValue()).claimReward(player);
         }
      }

   }

   public static void detectQuest(int[] questIDs, EntityPlayerMP player) {
      for(DBEntry<IQuest> entry : QuestDatabase.INSTANCE.bulkLookup(questIDs)) {
         ((IQuest)entry.getValue()).detect(player);
      }

   }
}
