package ichttt.mods.firstaid.common.network;

import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageSyncDamageModel implements IMessage {
   private NBTTagCompound playerDamageModel;
   private boolean scaleMaxHealth;

   public MessageSyncDamageModel() {
   }

   public MessageSyncDamageModel(AbstractPlayerDamageModel damageModel, boolean scaleMaxHealth) {
      this.playerDamageModel = (NBTTagCompound)damageModel.serializeNBT();
      this.scaleMaxHealth = scaleMaxHealth;
   }

   public void fromBytes(ByteBuf buf) {
      this.playerDamageModel = ByteBufUtils.readTag(buf);
      this.scaleMaxHealth = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeTag(buf, this.playerDamageModel);
      buf.writeBoolean(this.scaleMaxHealth);
   }

   public static final class Handler implements IMessageHandler<MessageSyncDamageModel, IMessage> {
      public Handler() {
      }

      @SideOnly(Side.CLIENT)
      public IMessage onMessage(MessageSyncDamageModel message, MessageContext ctx) {
         Minecraft mc = Minecraft.getMinecraft();
         mc.addScheduledTask(() -> {
            AbstractPlayerDamageModel damageModel = (AbstractPlayerDamageModel)Objects.requireNonNull(mc.player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null));
            if (message.scaleMaxHealth) {
               damageModel.runScaleLogic(mc.player);
            }

            damageModel.deserializeNBT(message.playerDamageModel);
         });
         return null;
      }
   }
}
