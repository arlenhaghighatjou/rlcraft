package ichttt.mods.firstaid.common.network;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.client.ClientProxy;
import ichttt.mods.firstaid.client.HUDHandler;
import ichttt.mods.firstaid.common.CapProvider;
import ichttt.mods.firstaid.common.config.ConfigEntry;
import ichttt.mods.firstaid.common.config.ExtraConfig;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageConfiguration implements IMessage {
   private NBTTagCompound playerDamageModel;
   private boolean syncConfig;

   public MessageConfiguration() {
   }

   public MessageConfiguration(AbstractPlayerDamageModel model, boolean syncConfig) {
      this.playerDamageModel = (NBTTagCompound)model.serializeNBT();
      this.syncConfig = syncConfig;
   }

   public void fromBytes(ByteBuf buf) {
      this.playerDamageModel = ByteBufUtils.readTag(buf);
      this.syncConfig = buf.readBoolean();
      if (this.syncConfig) {
         for(ConfigEntry<ExtraConfig.Sync> entry : FirstAid.syncedConfigOptions) {
            entry.readFromBuf(buf);
         }
      }

   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeTag(buf, this.playerDamageModel);
      buf.writeBoolean(this.syncConfig);
      if (this.syncConfig) {
         for(ConfigEntry<ExtraConfig.Sync> entry : FirstAid.syncedConfigOptions) {
            entry.writeToBuf(buf);
         }
      }

   }

   public static class Handler implements IMessageHandler<MessageConfiguration, IMessage> {
      public Handler() {
      }

      @SideOnly(Side.CLIENT)
      public IMessage onMessage(MessageConfiguration message, MessageContext ctx) {
         Minecraft mc = Minecraft.getMinecraft();
         FirstAid.LOGGER.info(message.syncConfig ? "Received remote damage model and config" : "Received remote damage model");
         mc.addScheduledTask(() -> {
            AbstractPlayerDamageModel damageModel = (AbstractPlayerDamageModel)Objects.requireNonNull(mc.player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null));
            damageModel.deserializeNBT(message.playerDamageModel);
            if (damageModel.hasTutorial) {
               CapProvider.tutorialDone.add(mc.player.getName());
            } else {
               mc.player.sendMessage(new TextComponentString("[First Aid] " + I18n.format("firstaid.tutorial.hint", new Object[]{ClientProxy.showWounds.getDisplayName()})));
            }

            HUDHandler.INSTANCE.ticker = 200;
            FirstAid.isSynced = true;
         });
         return null;
      }
   }
}
