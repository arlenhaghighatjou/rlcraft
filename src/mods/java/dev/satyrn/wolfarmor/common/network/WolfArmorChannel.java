package dev.satyrn.wolfarmor.common.network;

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
      this.registerMessage(WolfEatMessage.class);
      this.registerMessage(WolfHealMessage.class);
      this.registerMessage(WolfDropChestMessage.class);
      this.registerMessage(ConfigSyncMessage.class);
      this.registerMessage(UpdateFoodStatsMessage.class);
      this.registerMessage(UpdatePotionEffectMessage.class);
      this.registerMessage(RemovePotionEffectMessage.class);
   }

   private <T extends MessageBase<T> & IMessageHandler<T, IMessage>> void registerMessage(Class<T> clazz) {
      if (MessageBase.ClientMessageBase.class.isAssignableFrom(clazz)) {
         this.registerMessage(clazz, clazz, ID++, Side.CLIENT);
      } else if (MessageBase.ServerMessageBase.class.isAssignableFrom(clazz)) {
         this.registerMessage(clazz, clazz, ID++, Side.SERVER);
      } else {
         this.registerMessage(clazz, clazz, ID, Side.CLIENT);
         this.registerMessage(clazz, clazz, ID++, Side.SERVER);
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
