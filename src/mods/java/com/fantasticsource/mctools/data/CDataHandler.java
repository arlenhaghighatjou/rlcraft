package com.fantasticsource.mctools.data;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.Network;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.function.Function;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public abstract class CDataHandler extends Component {
   private static final LinkedHashMap<Class<? extends CDataHandler>, LinkedHashMap<String, CDataHandler>> SERVER_DATA_HANDLERS = new LinkedHashMap();
   private static final LinkedHashMap<Class<? extends CDataHandler>, LinkedHashMap<String, CDataHandler>> SYNCED_DATA_HANDLERS = new LinkedHashMap();
   String key;
   final boolean sync;

   public CDataHandler() {
      this.sync = true;
   }

   public CDataHandler(String key, boolean sync) {
      this.key = key;
      this.sync = sync;
      if (sync) {
         ((LinkedHashMap)SYNCED_DATA_HANDLERS.computeIfAbsent(this.getClass(), (o) -> {
            return new LinkedHashMap();
         })).put(key, this);
      } else {
         ((LinkedHashMap)SERVER_DATA_HANDLERS.computeIfAbsent(this.getClass(), (o) -> {
            return new LinkedHashMap();
         })).put(key, this);
      }

      if (sync && MCTools.hosting()) {
         for(EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
            Network.WRAPPER.sendTo(new Network.GenericComponentPacket(this), player);
         }
      }

   }

   public CDataHandler write(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.key);
      return this;
   }

   public CDataHandler read(ByteBuf buf) {
      this.key = ByteBufUtils.readUTF8String(buf);
      return this;
   }

   public CDataHandler save(OutputStream stream) {
      (new CStringUTF8()).set(this.key).save(stream);
      return this;
   }

   public CDataHandler load(InputStream stream) {
      this.key = (new CStringUTF8()).load(stream).value;
      return this;
   }

   public CDataHandler writeText(BufferedWriter writer) {
      try {
         writer.write(this.key + "\r\n");
      } catch (IOException var3) {
         var3.printStackTrace();
      }

      return this;
   }

   public CDataHandler readText(BufferedReader reader) {
      try {
         this.key = reader.readLine();
      } catch (IOException var3) {
         var3.printStackTrace();
      }

      return this;
   }

   public abstract void load();

   public abstract void save();

   public abstract void clear();

   public final void reload() {
      this.clear();
      this.load();
   }

   public final void destroy() {
      LinkedHashMap<String, CDataHandler> dataHandlers = this.sync ? (LinkedHashMap)SYNCED_DATA_HANDLERS.get(this.getClass()) : (LinkedHashMap)SERVER_DATA_HANDLERS.get(this.getClass());
      if (dataHandlers != null) {
         dataHandlers.remove(this.key);
         if (dataHandlers.size() == 0) {
            if (this.sync) {
               SYNCED_DATA_HANDLERS.remove(this.getClass());
            } else {
               SERVER_DATA_HANDLERS.remove(this.getClass());
            }
         }
      }

      for(EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
         Network.WRAPPER.sendTo(new Network.GenericComponentPacket(new CDataHandler.Removal(this.key)), player);
      }

   }

   public final void syncToClient(EntityPlayerMP player) {
      Network.WRAPPER.sendTo(new Network.GenericComponentPacket(this), player);
   }

   public void onClientSync() {
      ((LinkedHashMap)SYNCED_DATA_HANDLERS.computeIfAbsent(this.getClass(), (o) -> {
         return new LinkedHashMap();
      })).put(this.key, this);
   }

   @SubscribeEvent
   public static void syncOnLogin(PlayerLoggedInEvent event) {
      for(LinkedHashMap<String, CDataHandler> handlers : SYNCED_DATA_HANDLERS.values()) {
         for(CDataHandler handler : handlers.values()) {
            handler.syncToClient((EntityPlayerMP)event.player);
         }
      }

   }

   protected static void load(Class<? extends CDataHandler> cls) {
      for(LinkedHashMap<String, CDataHandler> dataHandlers : new LinkedHashMap[]{(LinkedHashMap)SYNCED_DATA_HANDLERS.get(cls), (LinkedHashMap)SERVER_DATA_HANDLERS.get(cls)}) {
         if (dataHandlers != null) {
            for(CDataHandler handler : dataHandlers.values()) {
               handler.load();
            }
         }
      }

   }

   protected static void clear(Class<? extends CDataHandler> cls) {
      LinkedHashMap<String, CDataHandler> dataHandlers = (LinkedHashMap)SYNCED_DATA_HANDLERS.get(cls);
      if (dataHandlers != null) {
         for(CDataHandler handler : dataHandlers.values()) {
            handler.load();
         }
      }

   }

   public static CDataHandler get(Class<? extends CDataHandler> cls, String key, boolean sync) {
      LinkedHashMap<String, CDataHandler> dataHandlers = sync ? (LinkedHashMap)SYNCED_DATA_HANDLERS.get(cls) : (LinkedHashMap)SERVER_DATA_HANDLERS.get(cls);
      return dataHandlers == null ? null : (CDataHandler)dataHandlers.get(key);
   }

   private static class Removal extends CDataHandler {
      public Removal(String key) {
         this.key = key;
      }

      public void load() {
      }

      public void save() {
      }

      public void clear() {
      }

      public void onClientSync() {
         LinkedHashMap<String, CDataHandler> dataHandlers = (LinkedHashMap)CDataHandler.SYNCED_DATA_HANDLERS.get(this.getClass());
         if (dataHandlers != null) {
            dataHandlers.remove(this.key);
            if (dataHandlers.size() == 0) {
               CDataHandler.SYNCED_DATA_HANDLERS.remove(this.getClass());
            }
         }

      }
   }
}
