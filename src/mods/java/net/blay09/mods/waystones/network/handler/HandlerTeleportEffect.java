package net.blay09.mods.waystones.network.handler;

import javax.annotation.Nullable;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageTeleportEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerTeleportEffect implements IMessageHandler<MessageTeleportEffect, IMessage> {
   public HandlerTeleportEffect() {
   }

   @Nullable
   public IMessage onMessage(final MessageTeleportEffect message, MessageContext ctx) {
      NetworkHandler.getThreadListener(ctx).addScheduledTask(new Runnable() {
         public void run() {
            Minecraft mc = Minecraft.getMinecraft();
            mc.ingameGUI.getBossOverlay().clearBossInfos();
            Waystones.proxy.playSound(SoundEvents.BLOCK_PORTAL_TRAVEL, message.getPos(), 1.0F);

            for(int i = 0; i < 128; ++i) {
               mc.world.spawnParticle(EnumParticleTypes.PORTAL, (double)message.getPos().getX() + (mc.world.rand.nextDouble() - 0.5D) * 3.0D, (double)message.getPos().getY() + mc.world.rand.nextDouble() * 3.0D, (double)message.getPos().getZ() + (mc.world.rand.nextDouble() - 0.5D) * 3.0D, (mc.world.rand.nextDouble() - 0.5D) * 2.0D, -mc.world.rand.nextDouble(), (mc.world.rand.nextDouble() - 0.5D) * 2.0D, new int[0]);
            }

         }
      });
      return null;
   }
}
