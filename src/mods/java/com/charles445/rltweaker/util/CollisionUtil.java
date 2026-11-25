package com.charles445.rltweaker.util;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class CollisionUtil {
   public static CollisionUtil instance = new CollisionUtil();
   private final Map<String, Double> stringReference = new ConcurrentHashMap();
   private final Map<Class, Double> collisionMapServer = new ConcurrentHashMap();
   private final Map<Class, Double> collisionMapClient = new ConcurrentHashMap();

   public CollisionUtil() {
   }

   public void addToStringReference(Map<String, Double> sent) {
      this.stringReference.putAll(sent);
   }

   public void refreshCollisionMaps() {
      this.collisionMapServer.clear();
      this.collisionMapClient.clear();
   }

   public double getRadiusForEntity(@Nonnull Entity entity) {
      if (entity.world.isRemote) {
         Double dub = (Double)this.collisionMapClient.get(entity.getClass());
         if (dub != null) {
            return dub.doubleValue();
         } else {
            dub = (Double)this.stringReference.get(entity.getClass().getName());
            if (dub == null) {
               dub = new Double(World.MAX_ENTITY_RADIUS);
            }

            if (ModConfig.server.minecraft.debug) {
               RLTweaker.logger.debug("Adding " + entity.getClass().getName() + " with radius " + dub + " to client");
            }

            this.collisionMapClient.put(entity.getClass(), Double.valueOf(Math.min(World.MAX_ENTITY_RADIUS, dub.doubleValue())));
            return dub.doubleValue();
         }
      } else {
         Double dub = (Double)this.collisionMapServer.get(entity.getClass());
         if (dub != null) {
            return dub.doubleValue();
         } else {
            dub = (Double)this.stringReference.get(entity.getClass().getName());
            if (dub == null) {
               dub = new Double(World.MAX_ENTITY_RADIUS);
            }

            if (ModConfig.server.minecraft.debug) {
               RLTweaker.logger.debug("Adding " + entity.getClass().getName() + " with radius " + dub + " to server");
            }

            this.collisionMapServer.put(entity.getClass(), Double.valueOf(Math.min(World.MAX_ENTITY_RADIUS, dub.doubleValue())));
            return dub.doubleValue();
         }
      }
   }
}
