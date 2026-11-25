package de.mennomax.astikorcarts.packets;

import de.mennomax.astikorcarts.capabilities.IPull;
import de.mennomax.astikorcarts.capabilities.PullProvider;
import de.mennomax.astikorcarts.entity.AbstractDrawn;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CPacketToggleSlow implements IMessage {
   public CPacketToggleSlow() {
   }

   public void fromBytes(ByteBuf buf) {
   }

   public void toBytes(ByteBuf buf) {
   }

   public static class ToggleSlowHandler implements IMessageHandler<CPacketToggleSlow, IMessage> {
      public ToggleSlowHandler() {
      }

      public IMessage onMessage(CPacketToggleSlow message, MessageContext ctx) {
         EntityPlayerMP player = ctx.getServerHandler().player;
         if (player.isRiding()) {
            Entity ridden = player.getRidingEntity();
            if (ridden instanceof EntityLivingBase && ridden.hasCapability(PullProvider.PULL, (EnumFacing)null) && ((IPull)ridden.getCapability(PullProvider.PULL, (EnumFacing)null)).getDrawn() != null) {
               if (((EntityLivingBase)ridden).getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(AbstractDrawn.PULL_SLOWLY_MODIFIER)) {
                  ((EntityLivingBase)ridden).getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(AbstractDrawn.PULL_SLOWLY_MODIFIER);
               } else {
                  ((EntityLivingBase)ridden).getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(AbstractDrawn.PULL_SLOWLY_MODIFIER);
               }
            }
         }

         return null;
      }
   }
}
