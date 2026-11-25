package betterquesting.api.api;

import betterquesting.api.storage.INameCache;
import java.util.HashMap;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.player.EntityPlayer;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class QuestingAPI {
   private static final HashMap<ApiKey<?>, Object> apis = new HashMap();
   private static Logger logger = null;

   public QuestingAPI() {
   }

   public static <T> void registerAPI(ApiKey<T> key, T api) {
      if (key == null) {
         throw new NullPointerException("API key can not be NULL");
      } else if (api == null) {
         throw new NullPointerException("Tried to registed NULL API");
      } else if (apis.containsKey(key)) {
         throw new IllegalArgumentException("Key cannot be registered twice");
      } else {
         apis.put(key, api);
      }
   }

   public static <T> T getAPI(ApiKey<T> key) {
      Object obj = apis.get(key);
      return (T)(obj == null ? null : obj);
   }

   public static EaglercraftUUID getQuestingUUID(EntityPlayer player) {
      if (player == null) {
         return null;
      } else {
         if (player.world.isRemote) {
            EaglercraftUUID uuid = ((INameCache)getAPI(ApiReference.NAME_CACHE)).getUUID(player.getGameProfile().getName());
            if (uuid != null) {
               return uuid;
            }
         }

         return player.getGameProfile().getId();
      }
   }

   public static Logger getLogger() {
      if (logger == null) {
         logger = LogManager.getLogger("betterquesting");
      }

      return logger;
   }
}
