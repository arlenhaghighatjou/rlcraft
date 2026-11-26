package net.silentchaos512.lib.util;

import java.util.ArrayDeque;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public final class EntityHelper {
   private static volatile Queue<Entity> entitiesToSpawn = new ArrayDeque<>();

   private EntityHelper() {
      throw new IllegalAccessError("Utility class");
   }

   /** @deprecated */
   @Deprecated
   public static void moveSelf(Entity entity, double x, double y, double z) {
      entity.move(MoverType.SELF, x, y, z);
   }

   /** @deprecated */
   @Deprecated
   public static List<String> getEntityNameList() {
      List<String> list = Lists.newArrayList();
      EntityList.getEntityNameList().forEach((res) -> {
         list.add(EntityList.getTranslationName(res));
      });
      return list;
   }

   public static void safeSpawn(Entity entity) {
      entitiesToSpawn.add(entity);
   }

   private static void handleSpawns() {
      Entity entity;
      while((entity = (Entity)entitiesToSpawn.poll()) != null) {
         entity.world.spawnEntity(entity);
      }

   }

   public static void heal(EntityLivingBase entityLiving, float healAmount, boolean cancelable) {
      if (cancelable) {
         entityLiving.heal(healAmount);
      } else {
         entityLiving.setHealth(entityLiving.getHealth() + healAmount);
      }

   }

   public static void register(EventBus bus) {
      bus.register(WorldTickEvent.class, EntityHelper::onWorldTick);
   }

   public static void onWorldTick(WorldTickEvent event) {
      if (event.phase == Phase.START) {
         handleSpawns();
      }

   }
}
