package ichttt.mods.firstaid.common.network;

import ichttt.mods.firstaid.client.DebuffTimedSound;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessagePlayHurtSound implements IMessage {
   private SoundEvent sound;
   private int duration;

   public MessagePlayHurtSound() {
   }

   public MessagePlayHurtSound(SoundEvent sound, int duration) {
      this.sound = sound;
      this.duration = duration;
   }

   public void fromBytes(ByteBuf buf) {
      this.sound = (SoundEvent)ByteBufUtils.readRegistryEntry(buf, ForgeRegistries.SOUND_EVENTS);
      this.duration = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeRegistryEntry(buf, this.sound);
      buf.writeInt(this.duration);
   }

   public static class Handler implements IMessageHandler<MessagePlayHurtSound, IMessage> {
      public Handler() {
      }

      @SideOnly(Side.CLIENT)
      public IMessage onMessage(MessagePlayHurtSound message, MessageContext ctx) {
         Minecraft.getMinecraft().addScheduledTask(() -> {
            DebuffTimedSound.playHurtSound(message.sound, message.duration);
         });
         return null;
      }
   }
}
