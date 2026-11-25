package rlmixins.handlers.quark;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;
import rlmixins.RLMixins;
import vazkii.quark.base.module.ConfigHelper;

public class RightClickSignEditHandler {
   public static boolean isEnabled;

   public RightClickSignEditHandler() {
   }

   @SubscribeEvent
   public static void onPlayerLogin(PlayerLoggedInEvent event) {
      EntityPlayer player = event.player;
      World world = player.world;
      if (!world.isRemote && player instanceof EntityPlayerMP) {
         RLMixins.LOGGER.log(Level.INFO, "Syncing Quark configuration with player: " + player.getName());
         boolean prop = ConfigHelper.loadPropBool("Right click sign edit", "tweaks", "", true);
         isEnabled = prop;
         PacketHandler.instance.sendTo(new RightClickSignEditHandler.MessageSyncConfig(prop), (EntityPlayerMP)player);
      }
   }

   public static class MessageSyncConfig implements IMessage {
      private boolean rightClickSignEditEnabled;

      public MessageSyncConfig() {
      }

      public MessageSyncConfig(boolean rightClickSignEditEnabled) {
         this.rightClickSignEditEnabled = rightClickSignEditEnabled;
      }

      public void fromBytes(ByteBuf buf) {
         this.rightClickSignEditEnabled = buf.readBoolean();
      }

      public void toBytes(ByteBuf buf) {
         buf.writeBoolean(this.rightClickSignEditEnabled);
      }

      public static class Handler implements IMessageHandler<RightClickSignEditHandler.MessageSyncConfig, IMessage> {
         public Handler() {
         }

         public IMessage onMessage(RightClickSignEditHandler.MessageSyncConfig message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
               Minecraft.getMinecraft().addScheduledTask(() -> {
                  RightClickSignEditHandler.isEnabled = message.rightClickSignEditEnabled;
               });
            }

            return null;
         }
      }
   }
}
