package com.fantasticsource.mctools;

import com.fantasticsource.mctools.animation.CBipedAnimation;
import com.fantasticsource.mctools.betterattributes.BetterAttribute;
import com.fantasticsource.mctools.component.CResourceLocation;
import com.fantasticsource.mctools.controlintercept.ControlEvent;
import com.fantasticsource.mctools.sound.SimpleSound;
import com.fantasticsource.tools.component.CUUID;
import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.component.path.CPath;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Network {
   public static final SimpleNetworkWrapper WRAPPER = new SimpleNetworkWrapper("fantasticlib");
   private static int discriminator = 0;

   public Network() {
   }

   public static void init() {
      WRAPPER.registerMessage(Network.PlaySimpleSoundPacketHandler.class, Network.PlaySimpleSoundPacket.class, discriminator++, Side.CLIENT);
      WRAPPER.registerMessage(Network.ControlEventPacketHandler.class, Network.ControlEventPacket.class, discriminator++, Side.SERVER);
      WRAPPER.registerMessage(Network.GenericComponentPacketHandler.class, Network.GenericComponentPacket.class, discriminator++, Side.CLIENT);
      WRAPPER.registerMessage(Network.RemoveEntityImmediatePacketHandler.class, Network.RemoveEntityImmediatePacket.class, discriminator++, Side.CLIENT);
      WRAPPER.registerMessage(Network.BetterAttributePacketHandler.class, Network.BetterAttributePacket.class, discriminator++, Side.CLIENT);
      WRAPPER.registerMessage(Network.AddBipedAnimationsPacketHandler.class, Network.AddBipedAnimationsPacket.class, discriminator++, Side.CLIENT);
      WRAPPER.registerMessage(Network.RemoveBipedAnimationPacketHandler.class, Network.RemoveBipedAnimationPacket.class, discriminator++, Side.CLIENT);
      WRAPPER.registerMessage(Network.UpdateBipedAnimationsPacketHandler.class, Network.UpdateBipedAnimationsPacket.class, discriminator++, Side.CLIENT);
   }

   public static class AddBipedAnimationsPacket implements IMessage {
      int entityID;
      CBipedAnimation[] animations;
      long serverSystemMillis;

      public AddBipedAnimationsPacket() {
      }

      public AddBipedAnimationsPacket(Entity entity, CBipedAnimation... animations) {
         this.entityID = entity.getEntityId();
         this.animations = animations;
      }

      public void toBytes(ByteBuf buf) {
         buf.writeInt(this.entityID);
         buf.writeInt(this.animations.length);

         for(CBipedAnimation animation : this.animations) {
            animation.write(buf);
         }

         buf.writeLong(System.currentTimeMillis());
      }

      public void fromBytes(ByteBuf buf) {
         this.entityID = buf.readInt();
         this.animations = new CBipedAnimation[buf.readInt()];

         for(int i = 0; i < this.animations.length; ++i) {
            this.animations[i] = (new CBipedAnimation()).read(buf);
         }

         this.serverSystemMillis = buf.readLong();
      }
   }

   public static class AddBipedAnimationsPacketHandler implements IMessageHandler<Network.AddBipedAnimationsPacket, IMessage> {
      public AddBipedAnimationsPacketHandler() {
      }

      @SideOnly(Side.CLIENT)
      public IMessage onMessage(Network.AddBipedAnimationsPacket packet, MessageContext ctx) {
         Minecraft mc = Minecraft.getMinecraft();
         mc.addScheduledTask(() -> {
            if (mc.world != null) {
               Entity entity = mc.world.getEntityByID(packet.entityID);
               if (entity != null) {
                  long offsetMillis = System.currentTimeMillis() - packet.serverSystemMillis;

                  for(CBipedAnimation animation : packet.animations) {
                     animation.startTime += offsetMillis;
                     animation.setAllStartTimes(animation.startTime);
                     if (animation.pauseTime > -1L) {
                        animation.pauseTime += offsetMillis;
                     }

                     for(CPath.CPathData data : animation.getAllData()) {
                        if (data.pauseTime > -1L) {
                           data.pauseTime += offsetMillis;
                        }
                     }

                     CBipedAnimation.addAnimation(entity, animation);
                  }

               }
            }
         });
         return null;
      }
   }

   public static class BetterAttributePacket implements IMessage {
      int entityID;
      String attributeName;
      double base;
      double total;
      double current;

      public BetterAttributePacket() {
      }

      public BetterAttributePacket(Entity entity, BetterAttribute attribute) {
         this.entityID = entity.getEntityId();
         this.attributeName = attribute.name;
         this.base = attribute.getBaseAmount(entity);
         this.total = attribute.getTotalAmount(entity);
         this.current = attribute.getCurrentAmount(entity);
      }

      public void toBytes(ByteBuf buf) {
         buf.writeInt(this.entityID);
         ByteBufUtils.writeUTF8String(buf, this.attributeName);
         buf.writeDouble(this.base);
         buf.writeDouble(this.total);
         buf.writeDouble(this.current);
      }

      public void fromBytes(ByteBuf buf) {
         this.entityID = buf.readInt();
         this.attributeName = ByteBufUtils.readUTF8String(buf);
         this.base = buf.readDouble();
         this.total = buf.readDouble();
         this.current = buf.readDouble();
      }
   }

   public static class BetterAttributePacketHandler implements IMessageHandler<Network.BetterAttributePacket, IMessage> {
      public BetterAttributePacketHandler() {
      }

      @SideOnly(Side.CLIENT)
      public IMessage onMessage(Network.BetterAttributePacket packet, MessageContext ctx) {
         Minecraft mc = Minecraft.getMinecraft();
         mc.addScheduledTask(() -> {
            if (mc.world != null) {
               Entity entity = mc.world.getEntityByID(packet.entityID);
               if (entity != null) {
                  BetterAttribute attribute = (BetterAttribute)BetterAttribute.BETTER_ATTRIBUTES.get(packet.attributeName);
                  if (attribute != null) {
                     NBTTagCompound compound = MCTools.getOrGenerateSubCompound(entity.getEntityData(), "fantasticlib");
                     attribute.setBaseAmount(entity, packet.base);
                     MCTools.getOrGenerateSubCompound(compound, "attributes").setDouble(attribute.name, packet.total);
                     attribute.setCurrentAmount(entity, packet.current);
                  }
               }
            }
         });
         return null;
      }
   }

   public static class ControlEventPacket implements IMessage {
      public ControlEvent event;

      public ControlEventPacket() {
      }

      public ControlEventPacket(ControlEvent event) {
         this.event = event;
      }

      public void toBytes(ByteBuf buf) {
         ByteBufUtils.writeUTF8String(buf, this.event.name);
         buf.writeBoolean(this.event.state);
         buf.writeBoolean(this.event.lastState != null);
         if (this.event.lastState != null) {
            buf.writeBoolean(this.event.lastState.booleanValue());
         }

         ByteBufUtils.writeUTF8String(buf, this.event.identifier);
      }

      public void fromBytes(ByteBuf buf) {
         this.event = new ControlEvent(ByteBufUtils.readUTF8String(buf), buf.readBoolean(), buf.readBoolean() ? buf.readBoolean() : null, ByteBufUtils.readUTF8String(buf));
      }
   }

   public static class ControlEventPacketHandler implements IMessageHandler<Network.ControlEventPacket, IMessage> {
      public ControlEventPacketHandler() {
      }

      public IMessage onMessage(Network.ControlEventPacket packet, MessageContext ctx) {
         MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
         server.addScheduledTask(() -> {
            MinecraftForge.EVENT_BUS.post(packet.event.setPlayer(ctx.getServerHandler().player));
         });
         return null;
      }
   }

   public static class GenericComponentPacket implements IMessage {
      public Component component;

      public GenericComponentPacket() {
      }

      public GenericComponentPacket(Component component) {
         this.component = component;
      }

      public void toBytes(ByteBuf buf) {
         Component.writeMarked(buf, this.component);
      }

      public void fromBytes(ByteBuf buf) {
         this.component = Component.readMarked(buf);
      }
   }

   public static class GenericComponentPacketHandler implements IMessageHandler<Network.GenericComponentPacket, IMessage> {
      public GenericComponentPacketHandler() {
      }

      @SideOnly(Side.CLIENT)
      public IMessage onMessage(Network.GenericComponentPacket packet, MessageContext ctx) {
         Minecraft mc = Minecraft.getMinecraft();
         mc.addScheduledTask(() -> {
            packet.component.onClientSync();
         });
         return null;
      }
   }

   public static class PlaySimpleSoundPacket implements IMessage {
      public Integer followingID;
      public Float x;
      public Float y;
      public Float z;
      public CResourceLocation rl;
      public int attenuationType;
      public float volume;
      public float pitch;
      public SoundCategory soundCategory;

      public PlaySimpleSoundPacket() {
         this.rl = new CResourceLocation();
      }

      public PlaySimpleSoundPacket(ResourceLocation rl) {
         this(rl, (Entity)null);
      }

      public PlaySimpleSoundPacket(ResourceLocation rl, Entity following) {
         this(rl, following, 2, 1.0F, 1.0F);
      }

      public PlaySimpleSoundPacket(ResourceLocation rl, Entity following, int attenuationType, float volume, float pitch) {
         this(rl, following, attenuationType, volume, pitch, SoundCategory.MASTER);
      }

      public PlaySimpleSoundPacket(ResourceLocation rl, Entity following, int attenuationType, float volume, float pitch, SoundCategory soundCategory) {
         this.rl = new CResourceLocation();
         this.x = null;
         this.y = null;
         this.z = null;
         this.rl.set(rl);
         this.followingID = following == null ? null : following.getEntityId();
         this.attenuationType = attenuationType;
         this.volume = volume;
         this.pitch = pitch;
         this.soundCategory = soundCategory;
      }

      public PlaySimpleSoundPacket(ResourceLocation rl, float x, float y, float z) {
         this(rl, x, y, z, 2, 1.0F, 1.0F);
      }

      public PlaySimpleSoundPacket(ResourceLocation rl, float x, float y, float z, int attenuationType, float volume, float pitch) {
         this(rl, x, y, z, attenuationType, volume, pitch, SoundCategory.MASTER);
      }

      public PlaySimpleSoundPacket(ResourceLocation rl, float x, float y, float z, int attenuationType, float volume, float pitch, SoundCategory soundCategory) {
         this.rl = new CResourceLocation();
         this.followingID = null;
         this.rl.set(rl);
         this.x = x;
         this.y = y;
         this.z = z;
         this.attenuationType = attenuationType;
         this.volume = volume;
         this.pitch = pitch;
         this.soundCategory = soundCategory;
      }

      public void toBytes(ByteBuf buf) {
         this.rl.write(buf);
         buf.writeBoolean(this.followingID != null);
         if (this.followingID != null) {
            buf.writeInt(this.followingID.intValue());
         }

         buf.writeBoolean(this.x != null);
         if (this.x != null) {
            buf.writeFloat(this.x.floatValue());
            buf.writeFloat(this.y.floatValue());
            buf.writeFloat(this.z.floatValue());
         }

         buf.writeInt(this.attenuationType);
         buf.writeFloat(this.volume);
         buf.writeFloat(this.pitch);
         ByteBufUtils.writeUTF8String(buf, this.soundCategory.getName());
      }

      public void fromBytes(ByteBuf buf) {
         this.rl.read(buf);
         if (buf.readBoolean()) {
            this.followingID = buf.readInt();
         }

         if (buf.readBoolean()) {
            this.x = buf.readFloat();
            this.y = buf.readFloat();
            this.z = buf.readFloat();
         }

         this.attenuationType = buf.readInt();
         this.volume = buf.readFloat();
         this.pitch = buf.readFloat();
         this.soundCategory = SoundCategory.getByName(ByteBufUtils.readUTF8String(buf));
      }
   }

   public static class PlaySimpleSoundPacketHandler implements IMessageHandler<Network.PlaySimpleSoundPacket, IMessage> {
      public PlaySimpleSoundPacketHandler() {
      }

      @SideOnly(Side.CLIENT)
      public IMessage onMessage(Network.PlaySimpleSoundPacket packet, MessageContext ctx) {
         Minecraft mc = Minecraft.getMinecraft();
         mc.addScheduledTask(() -> {
            SimpleSound simpleSound = null;
            if (packet.followingID != null) {
               Entity following = Minecraft.getMinecraft().world.getEntityByID(packet.followingID.intValue());
               if (following != null) {
                  simpleSound = new SimpleSound(packet.rl.value, packet.soundCategory, following);
               }
            } else if (packet.x != null) {
               simpleSound = new SimpleSound(packet.rl.value, packet.soundCategory, packet.x.floatValue(), packet.y.floatValue(), packet.z.floatValue());
            } else {
               simpleSound = new SimpleSound(packet.rl.value, packet.soundCategory);
            }

            if (simpleSound != null) {
               simpleSound.attenuationType = packet.attenuationType == 0 ? AttenuationType.NONE : AttenuationType.LINEAR;
               simpleSound.volume = packet.volume;
               simpleSound.pitch = packet.pitch;
               mc.getSoundHandler().playSound(simpleSound);
            }

         });
         return null;
      }
   }

   public static class RemoveBipedAnimationPacket implements IMessage {
      int entityID;
      UUID animationID;

      public RemoveBipedAnimationPacket() {
      }

      public RemoveBipedAnimationPacket(Entity entity, CBipedAnimation animation) {
         this.entityID = entity.getEntityId();
         this.animationID = animation.id;
      }

      public void toBytes(ByteBuf buf) {
         buf.writeInt(this.entityID);
         (new CUUID()).set(this.animationID).write(buf);
      }

      public void fromBytes(ByteBuf buf) {
         this.entityID = buf.readInt();
         this.animationID = (new CUUID()).read(buf).value;
      }
   }

   public static class RemoveBipedAnimationPacketHandler implements IMessageHandler<Network.RemoveBipedAnimationPacket, IMessage> {
      public RemoveBipedAnimationPacketHandler() {
      }

      @SideOnly(Side.CLIENT)
      public IMessage onMessage(Network.RemoveBipedAnimationPacket packet, MessageContext ctx) {
         Minecraft mc = Minecraft.getMinecraft();
         mc.addScheduledTask(() -> {
            if (mc.world != null) {
               Entity entity = mc.world.getEntityByID(packet.entityID);
               if (entity != null) {
                  CBipedAnimation.removeAnimations(entity, packet.animationID);
               }
            }
         });
         return null;
      }
   }

   public static class RemoveEntityImmediatePacket implements IMessage {
      public int id;

      public RemoveEntityImmediatePacket() {
      }

      public RemoveEntityImmediatePacket(Entity entity) {
         this.id = entity.getEntityId();
      }

      public void toBytes(ByteBuf buf) {
         buf.writeInt(this.id);
      }

      public void fromBytes(ByteBuf buf) {
         this.id = buf.readInt();
      }
   }

   public static class RemoveEntityImmediatePacketHandler implements IMessageHandler<Network.RemoveEntityImmediatePacket, IMessage> {
      public RemoveEntityImmediatePacketHandler() {
      }

      @SideOnly(Side.CLIENT)
      public IMessage onMessage(Network.RemoveEntityImmediatePacket packet, MessageContext ctx) {
         Minecraft mc = Minecraft.getMinecraft();
         mc.addScheduledTask(() -> {
            if (mc.world != null) {
               Entity entity = mc.world.getEntityByID(packet.id);
               if (entity != null) {
                  MCTools.removeEntityImmediate(entity);
                  mc.world.removeAllEntities();
               }
            }
         });
         return null;
      }
   }

   public static class UpdateBipedAnimationsPacket implements IMessage {
      int entityID;
      CBipedAnimation[] animations;
      long serverSystemMillis;

      public UpdateBipedAnimationsPacket() {
      }

      public UpdateBipedAnimationsPacket(Entity entity, CBipedAnimation... animations) {
         this.entityID = entity.getEntityId();
         this.animations = animations;
      }

      public void toBytes(ByteBuf buf) {
         buf.writeInt(this.entityID);
         buf.writeInt(this.animations.length);

         for(CBipedAnimation animation : this.animations) {
            animation.write(buf);
         }

         buf.writeLong(System.currentTimeMillis());
      }

      public void fromBytes(ByteBuf buf) {
         this.entityID = buf.readInt();
         this.animations = new CBipedAnimation[buf.readInt()];

         for(int i = 0; i < this.animations.length; ++i) {
            this.animations[i] = (new CBipedAnimation()).read(buf);
         }

         this.serverSystemMillis = buf.readLong();
      }
   }

   public static class UpdateBipedAnimationsPacketHandler implements IMessageHandler<Network.UpdateBipedAnimationsPacket, IMessage> {
      public UpdateBipedAnimationsPacketHandler() {
      }

      @SideOnly(Side.CLIENT)
      public IMessage onMessage(Network.UpdateBipedAnimationsPacket packet, MessageContext ctx) {
         Minecraft mc = Minecraft.getMinecraft();
         mc.addScheduledTask(() -> {
            if (mc.world != null) {
               Entity entity = mc.world.getEntityByID(packet.entityID);
               if (entity != null) {
                  long offsetMillis = System.currentTimeMillis() - packet.serverSystemMillis;

                  for(CBipedAnimation animation : packet.animations) {
                     animation.startTime += offsetMillis;
                     animation.setAllStartTimes(animation.startTime);
                     if (animation.pauseTime > -1L) {
                        animation.pauseTime += offsetMillis;
                     }

                     for(CPath.CPathData data : animation.getAllData()) {
                        if (data.pauseTime > -1L) {
                           data.pauseTime += offsetMillis;
                        }
                     }

                     if (CBipedAnimation.removeAnimations(entity, animation.id)) {
                        CBipedAnimation.addAnimation(entity, animation);
                     }
                  }

               }
            }
         });
         return null;
      }
   }
}
