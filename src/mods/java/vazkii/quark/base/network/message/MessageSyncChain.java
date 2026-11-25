package vazkii.quark.base.network.message;

import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkMessage;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.automation.feature.ChainLinkage;
import vazkii.quark.world.base.ChainHandler;

public class MessageSyncChain extends NetworkMessage<MessageSyncChain> {
   public int vehicle;
   public EaglercraftUUID other;

   public MessageSyncChain() {
   }

   public MessageSyncChain(int vehicle, EaglercraftUUID other) {
      this.vehicle = vehicle;
      this.other = other;
   }

   @SideOnly(Side.CLIENT)
   public IMessage handleMessage(MessageContext context) {
      ClientTicker.addAction(() -> {
         World world = Minecraft.getMinecraft().world;
         if (world != null) {
            Entity boatEntity = world.getEntityByID(this.vehicle);
            if (boatEntity == null) {
               ChainLinkage.queueChainUpdate(this.vehicle, this.other);
            } else {
               ChainHandler.setLink(boatEntity, this.other, false);
            }
         }

      });
      return null;
   }
}
