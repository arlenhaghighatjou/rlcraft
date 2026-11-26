package levelup2.network;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.lax1dude.eaglercraft.v1_8.netty.Unpooled;
import java.util.Map;
import levelup2.LevelUp2;
import levelup2.api.IPlayerSkill;
import levelup2.config.LevelUpConfig;
import levelup2.player.IPlayerClass;
import levelup2.skills.SkillRegistry;
import levelup2.util.SkillProperties;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;

public class SkillPacketHandler {
   public static final String[] CHANNELS = new String[]{"levelupinit", "levelupclasses", "levelupskills", "levelupcfg", "levelupproperties", "leveluprefresh"};
   public static FMLEventChannel initChannel;
   public static FMLEventChannel classChannel;
   public static FMLEventChannel skillChannel;
   public static FMLEventChannel configChannel;
   public static FMLEventChannel propertyChannel;
   public static FMLEventChannel refreshChannel;

   public SkillPacketHandler() {
   }

   public static void init() {
      SkillPacketHandler handler = new SkillPacketHandler();
      initChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(CHANNELS[0]);
      handler.register(initChannel);
      classChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(CHANNELS[1]);
      handler.register(classChannel);
      skillChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(CHANNELS[2]);
      handler.register(skillChannel);
      configChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(CHANNELS[3]);
      handler.register(configChannel);
      propertyChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(CHANNELS[4]);
      handler.register(propertyChannel);
      refreshChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(CHANNELS[5]);
      handler.register(refreshChannel);
      handler.register(MinecraftForge.EVENT_BUS);
   }

   public void register(EventBus bus) {
      bus.register(ServerCustomPacketEvent.class, this::onServerPacket);
      bus.register(ClientCustomPacketEvent.class, this::onClientPacket);
   }

   public void register(FMLEventChannel channel) {
      channel.register(ServerCustomPacketEvent.class, this::onServerPacket);
      channel.register(ClientCustomPacketEvent.class, this::onClientPacket);
   }

   public void onServerPacket(ServerCustomPacketEvent evt) {
      ByteBuf in = evt.getPacket().payload();
      EntityPlayerMP player = ((NetHandlerPlayServer)evt.getHandler()).player;
      if (evt.getPacket().channel().equals(CHANNELS[1])) {
         this.addTask(evt.getHandler(), () -> {
            this.handleClassChange(in.readByte(), in.readBoolean(), player);
         });
      } else if (evt.getPacket().channel().equals(CHANNELS[2])) {
         this.addTask(evt.getHandler(), () -> {
            this.handlePacket(in, player);
         });
      }

   }

   public void onClientPacket(ClientCustomPacketEvent evt) {
      ByteBuf in = evt.getPacket().payload();
      if (evt.getPacket().channel().equals(CHANNELS[0])) {
         this.addTask(evt.getHandler(), () -> {
            this.handlePacket(in, LevelUp2.proxy.getPlayer());
         });
      } else if (evt.getPacket().channel().equals(CHANNELS[3])) {
         this.addTask(evt.getHandler(), () -> {
            this.handleConfig(in);
         });
      } else if (evt.getPacket().channel().equals(CHANNELS[4])) {
         this.addTask(evt.getHandler(), () -> {
            this.handleProperties(in);
         });
      } else if (evt.getPacket().channel().equals(CHANNELS[5])) {
         this.addTask(evt.getHandler(), () -> {
            this.refreshValues();
         });
      }

   }

   private void addTask(INetHandler netHandler, Runnable runnable) {
      FMLCommonHandler.instance().getWorldThread(netHandler).addScheduledTask(runnable);
   }

   private void handleClassChange(byte newClass, boolean reclass, EntityPlayerMP player) {
      if (newClass >= 0 && SkillRegistry.getPlayer(player).getSpecialization() != newClass) {
         if (LevelUpConfig.reclassCost > 0 && reclass) {
            player.addExperienceLevel(-LevelUpConfig.reclassCost);
         }

         SkillRegistry.getPlayer(player).setSpecialization(newClass);
         SkillRegistry.loadPlayer(player);
      }

   }

   private void handlePacket(ByteBuf buf, EntityPlayer player) {
      boolean isInit = player.world.isRemote;
      byte button = buf.readByte();
      int levelSpend = buf.readInt();
      String[] skills = null;
      int[] data = null;
      if (isInit || button == -1) {
         data = new int[SkillRegistry.getSkillRegistry().size()];
         skills = new String[SkillRegistry.getSkillRegistry().size()];

         for(int i = 0; i < data.length; ++i) {
            skills[i] = ByteBufUtils.readUTF8String(buf);
            data[i] = buf.readInt();
         }
      }

      IPlayerClass properties = SkillRegistry.getPlayer(player);
      if (!isInit) {
         if (data != null && button == -1) {
            for(int i = 0; i < data.length; ++i) {
               properties.setSkillLevel(skills[i], data[i]);
            }

            SkillRegistry.loadPlayer(player);
         }
      } else if (data != null) {
         properties.setSpecialization(button);
         properties.setPlayerData(skills, data);
      }

      if (levelSpend > 0) {
         player.addExperienceLevel(-levelSpend);
      }

   }

   public static FMLProxyPacket getPacket(Side side, int channel, byte ID, Object... data) {
      ByteBuf buf = Unpooled.buffer();
      buf.writeByte(ID);
      if ((ID < 0 || channel == 0) && data != null) {
         if (data.length != 1 || !(data[0] instanceof Map)) {
            if (data.length == 2 && data[0] instanceof Map && data[1] instanceof Integer) {
               buf.writeInt(((Integer)data[1]).intValue());
               Map<String, Integer> map = (Map)data[0];

               for(String str : map.keySet()) {
                  ByteBufUtils.writeUTF8String(buf, str);
                  buf.writeInt(((Integer)map.get(str)).intValue());
               }
            } else {
               for(Object dat : data) {
                  if (dat instanceof String) {
                     ByteBufUtils.writeUTF8String(buf, (String)dat);
                  } else if (dat instanceof Integer) {
                     buf.writeInt(((Integer)dat).intValue());
                  }
               }
            }
         } else {
            Map<String, Integer> map = (Map)data[0];
            buf.writeInt(0);

            for(String str : map.keySet()) {
               ByteBufUtils.writeUTF8String(buf, str);
               buf.writeInt(((Integer)map.get(str)).intValue());
            }
         }
      } else if (channel == 1) {
         if (data != null && data[0] != null && data[0] instanceof Boolean) {
            buf.writeBoolean(((Boolean)data[0]).booleanValue());
         } else {
            buf.writeBoolean(false);
         }
      }

      FMLProxyPacket pkt = new FMLProxyPacket(new PacketBuffer(buf), CHANNELS[channel]);
      pkt.setTarget(side);
      return pkt;
   }

   public static FMLProxyPacket getConfigPacket(Property... dat) {
      ByteBuf buf = Unpooled.buffer();

      for(int i = 0; i < dat.length; ++i) {
         buf.writeBoolean(dat[i].getBoolean());
      }

      FMLProxyPacket pkt = new FMLProxyPacket(new PacketBuffer(buf), CHANNELS[3]);
      pkt.setTarget(Side.CLIENT);
      return pkt;
   }

   private void handleConfig(ByteBuf buf) {
      Property[] properties = LevelUpConfig.getServerProperties();

      for(int i = 0; i < properties.length; ++i) {
         properties[i].set(buf.readBoolean());
      }

      LevelUpConfig.useServerProperties();
   }

   public static FMLProxyPacket getPropertyPackets(IPlayerSkill skill) {
      SkillProperties prop = SkillRegistry.getProperty(skill);
      ByteBuf buf = Unpooled.buffer();
      if (prop != null) {
         prop.writeToBytes(buf);
      }

      FMLProxyPacket pkt = new FMLProxyPacket(new PacketBuffer(buf), CHANNELS[4]);
      pkt.setTarget(Side.CLIENT);
      return pkt;
   }

   private void handleProperties(ByteBuf buf) {
      NBTTagCompound tag = ByteBufUtils.readTag(buf);
      SkillProperties.fromNBT(tag);
   }

   public static FMLProxyPacket getRefreshPacket() {
      ByteBuf buf = Unpooled.buffer();
      buf.writeBoolean(true);
      FMLProxyPacket pkt = new FMLProxyPacket(new PacketBuffer(buf), CHANNELS[5]);
      pkt.setTarget(Side.CLIENT);
      return pkt;
   }

   private void refreshValues() {
      SkillRegistry.calculateHighLow();
   }
}
