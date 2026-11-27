package dev.satyrn.wolfarmor.common.network;

import java.util.function.Supplier;
import dev.satyrn.wolfarmor.common.network.packets.ConfigSyncMessage;
import dev.satyrn.wolfarmor.common.network.packets.RemovePotionEffectMessage;
import dev.satyrn.wolfarmor.common.network.packets.UpdateFoodStatsMessage;
import dev.satyrn.wolfarmor.common.network.packets.UpdatePotionEffectMessage;
import dev.satyrn.wolfarmor.common.network.packets.WolfDropChestMessage;
import dev.satyrn.wolfarmor.common.network.packets.WolfEatMessage;
import dev.satyrn.wolfarmor.common.network.packets.WolfHealMessage;
import java.util.function.Predicate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class WolfArmorChannel extends SimpleNetworkWrapper {
   private static byte ID = 0;

   public WolfArmorChannel() {
      super("wolfarmor");
      this.registerMessage(WolfEatMessage.class, WolfEatMessage::new);
      this.registerMessage(WolfHealMessage.class, WolfHealMessage::new);
      this.registerMessage(WolfDropChestMessage.class, WolfDropChestMessage::new);
      this.registerMessage(ConfigSyncMessage.class, ConfigSyncMessage::new);
      this.registerMessage(UpdateFoodStatsMessage.class, UpdateFoodStatsMessage::new);
      this.registerMessage(UpdatePotionEffectMessage.class, UpdatePotionEffectMessage::new);
      this.registerMessage(RemovePotionEffectMessage.class, RemovePotionEffectMessage::new);
   }

   private <T extends MessageBase<T> & IMessageHandler<T, IMessage>> void registerMessage(Class<T> clazz, Supplier<T> factory) {
      T handler = factory.get();
      if (handler instanceof MessageBase.ClientMessageBase) {
         this.registerMessage(handler, clazz, factory, ID++, Side.CLIENT);
      } else if (handler instanceof MessageBase.ServerMessageBase) {
         this.registerMessage(handler, clazz, factory, ID++, Side.SERVER);
      } else {
         this.registerMessage(handler, clazz, factory, ID, Side.CLIENT);
         this.registerMessage(factory.get(), clazz, factory, ID++, Side.SERVER);
      }
   }

   public void sendToAllWhere(IMessage message, Predicate<EntityPlayer> filter) {
      for(EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
         if (filter.test(player)) {
            this.sendTo(message, (EntityPlayerMP)player);
         }
      }

   }
}
