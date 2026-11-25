package org.orecruncher.dsurround.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.orecruncher.dsurround.event.WeatherUpdateEvent;

public final class PacketWeatherUpdate implements IMessage {
   private int dimension;
   private float intensity;
   private float maxIntensity;
   private int nextRainChange;
   private float thunderStrength;
   private int thunderChange;
   private int thunderEvent;

   public PacketWeatherUpdate() {
   }

   public PacketWeatherUpdate(int dimension, float intensity, float maxIntensity, int nextRainChange, float thunderStrength, int thunderChange, int thunderEvent) {
      this.dimension = dimension;
      this.intensity = intensity;
      this.maxIntensity = maxIntensity;
      this.nextRainChange = nextRainChange;
      this.thunderStrength = thunderStrength;
      this.thunderChange = thunderChange;
      this.thunderEvent = thunderEvent;
   }

   public void fromBytes(@Nonnull ByteBuf buf) {
      this.dimension = buf.readInt();
      this.intensity = buf.readFloat();
      this.maxIntensity = buf.readFloat();
      this.nextRainChange = buf.readInt();
      this.thunderStrength = buf.readFloat();
      this.thunderChange = buf.readInt();
      this.thunderEvent = buf.readInt();
   }

   public void toBytes(@Nonnull ByteBuf buf) {
      buf.writeInt(this.dimension);
      buf.writeFloat(this.intensity);
      buf.writeFloat(this.maxIntensity);
      buf.writeInt(this.nextRainChange);
      buf.writeFloat(this.thunderStrength);
      buf.writeInt(this.thunderChange);
      buf.writeInt(this.thunderEvent);
   }

   public static class PacketHandler implements IMessageHandler<PacketWeatherUpdate, IMessage> {
      public PacketHandler() {
      }

      @Nullable
      public IMessage onMessage(@Nonnull PacketWeatherUpdate message, @Nullable MessageContext ctx) {
         if (ctx != null) {
            Network.postEvent(new WeatherUpdateEvent(message.dimension, message.intensity, message.maxIntensity, message.nextRainChange, message.thunderStrength, message.thunderChange, message.thunderEvent));
         }

         return null;
      }
   }
}
