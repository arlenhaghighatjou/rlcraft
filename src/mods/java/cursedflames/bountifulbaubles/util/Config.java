package cursedflames.bountifulbaubles.util;

import cursedflames.bountifulbaubles.network.NBTPacket;
import cursedflames.bountifulbaubles.network.PacketHandler;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

public class Config {
   public static Config config;
   final String version;
   Configuration configuration;
   Logger logger;
   boolean configReset = false;
   boolean isDedicatedServer;
   Map<String, Property> clientProps = new HashMap();
   Map<String, Property> localProps = new HashMap();
   Map<String, Property> syncedProps = new HashMap();
   Map<String, Property> serverProps = new HashMap();

   public Map<String, Property> getLocalPropMap(Config.EnumPropSide side) {
      if (side == Config.EnumPropSide.CLIENT) {
         return this.clientProps;
      } else {
         return side == Config.EnumPropSide.SERVER ? this.serverProps : this.localProps;
      }
   }

   public Map<String, Property> getSyncedPropMap() {
      return this.isDedicatedServer ? this.localProps : this.syncedProps;
   }

   public Property getSyncedProperty(String key) {
      if (this.syncedProps.containsKey(key)) {
         return (Property)this.syncedProps.get(key);
      } else if (!this.localProps.containsKey(key)) {
         return null;
      } else {
         Property copy = this.copyProperty((Property)this.localProps.get(key));
         this.syncedProps.put(key, copy);
         return copy;
      }
   }

   public Config(String version, Logger logger) {
      this.version = version;
      this.logger = logger;
      config = this;
      this.isDedicatedServer = FMLCommonHandler.instance().getSide() == Side.SERVER;
   }

   public void preInit(FMLPreInitializationEvent e) {
      File directory = e.getModConfigurationDirectory();
      File configFile = new File(directory.getPath(), "bountifulbaubles.cfg");
      this.configuration = new Configuration(configFile, this.version);
      MinecraftForge.EVENT_BUS.register(this);
   }

   public void postInit(FMLPostInitializationEvent e) {
      if (this.configuration.hasChanged()) {
         this.configuration.save();
      }

   }

   private Property copyProperty(Property prop) {
      if (prop == null) {
         return null;
      } else {
         Type type = prop.getType();
         Property prop1 = new Property(prop.getName(), prop.getString(), type, prop.getValidValues(), prop.getLanguageKey());
         prop1.setComment(prop.getComment());
         return prop1;
      }
   }

   public void readConfig() {
      try {
         this.configuration.load();
      } catch (Exception var5) {
         this.logger.error("Failed to load config: ", var5);
      } finally {
         if (this.configuration.hasChanged() || this.configReset) {
            this.configuration.save();
         }

      }

   }

   public Property addPropInt(String name, String category, String desc, int defaultVal, Config.EnumPropSide side) {
      Property prop = this.configuration.get(category, name, defaultVal);
      prop.setComment(desc);
      this.getLocalPropMap(side).put(name, prop);
      return prop;
   }

   public Property addPropInt(String name, String category, String desc, int defaultVal, Config.EnumPropSide side, int min, int max) {
      Property prop = this.addPropInt(name, category, desc, defaultVal, side);
      prop.setMinValue(min);
      prop.setMaxValue(max);
      return prop;
   }

   public Property addPropDouble(String name, String category, String desc, double defaultVal, Config.EnumPropSide side) {
      Property prop = this.configuration.get(category, name, defaultVal);
      prop.setComment(desc);
      this.getLocalPropMap(side).put(name, prop);
      return prop;
   }

   public Property addPropDouble(String name, String category, String desc, double defaultVal, Config.EnumPropSide side, double min, double max) {
      Property prop = this.addPropDouble(name, category, desc, defaultVal, side);
      prop.setMinValue(min);
      prop.setMaxValue(max);
      return prop;
   }

   public Property addPropBoolean(String name, String category, String desc, boolean defaultVal, Config.EnumPropSide side) {
      Property prop = this.configuration.get(category, name, defaultVal);
      prop.setComment(desc);
      this.getLocalPropMap(side).put(name, prop);
      return prop;
   }

   public Property addPropString(String name, String category, String desc, String defaultVal, Config.EnumPropSide side) {
      Property prop = this.configuration.get(category, name, defaultVal);
      prop.setComment(desc);
      this.getLocalPropMap(side).put(name, prop);
      return prop;
   }

   public Property addPropStringList(String name, String category, String desc, String[] defaultVal, Config.EnumPropSide side) {
      Property prop = this.configuration.get(category, name, defaultVal);
      prop.setComment(desc);
      this.getLocalPropMap(side).put(name, prop);
      return prop;
   }

   @SubscribeEvent
   public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      if (!event.player.world.isRemote) {
         this.logger.info("Attempting to sync config to player " + event.player.getName());
         NBTTagCompound tag = this.getSyncTag();
         NBTTagCompound tag1 = new NBTTagCompound();
         tag1.setTag("values", tag);
         PacketHandler.INSTANCE.sendTo(new NBTPacket(tag1, PacketHandler.HandlerIds.SYNC_SERVER_DATA.id), (EntityPlayerMP)event.player);
      }

   }

   public NBTTagCompound getSyncTag() {
      NBTTagCompound tag = new NBTTagCompound();

      for(String key : this.localProps.keySet()) {
         Property prop = (Property)this.localProps.get(key);
         Type type = prop.getType();
         if (type == Type.INTEGER) {
            tag.setInteger(key, prop.getInt());
         } else if (type == Type.DOUBLE) {
            tag.setDouble(key, prop.getDouble());
         } else if (type == Type.BOOLEAN) {
            tag.setBoolean(key, prop.getBoolean());
         } else if (type == Type.STRING) {
            tag.setString(key, prop.getString());
         }
      }

      return tag;
   }

   public void loadSyncTag(NBTTagCompound tag) {
      for(String key : tag.getKeySet()) {
         Property prop = (Property)this.localProps.get(key);
         if (prop == null) {
            this.logger.warn("Attempted to load nonexistent config value \"" + key + "\"");
         } else {
            Type type = prop.getType();
            Property propSynced = this.getSyncedProperty(key);
            if (type == Type.INTEGER) {
               propSynced.setValue(tag.getInteger(key));
            } else if (type == Type.DOUBLE) {
               propSynced.setValue(tag.getDouble(key));
            } else if (type == Type.BOOLEAN) {
               propSynced.setValue(tag.getBoolean(key));
            } else if (type == Type.STRING) {
               propSynced.setValue(tag.getString(key));
            }
         }
      }

   }

   public static enum EnumPropSide {
      CLIENT,
      SERVER,
      SYNCED;

      private EnumPropSide() {
      }
   }
}
