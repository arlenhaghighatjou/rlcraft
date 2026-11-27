package greymerk.roguelike;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.github.fnar.minecraft.EffectType;
import com.github.fnar.util.Pair;
import com.github.fnar.util.ReportThisIssueException;
import greymerk.roguelike.config.RogueConfig;
import greymerk.roguelike.monster.EntityProfiler1_12;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class EntityJoinWorld1_12 {
   public EntityJoinWorld1_12() {
   }

   public void register(EventBus bus) {
      bus.register(EntityJoinWorldEvent.class, this::OnEntityJoinWorld);
   }

   public void OnEntityJoinWorld(EntityJoinWorldEvent event) {
      try {
         equipMobIfSpawnedByRoguelikeSpawner(event);
      } catch (Exception var3) {
         (new ReportThisIssueException(var3)).printStackTrace();
      }

   }

   private static void equipMobIfSpawnedByRoguelikeSpawner(EntityJoinWorldEvent event) {
      World world = event.getWorld();
      if (!world.isRemote) {
         Entity entity = event.getEntity();
         if (entity instanceof EntityMob || entity instanceof EntitySlime) {
            if (entity.isDead) {
               event.setCanceled(true);
            } else {
               EntityLiving entityLiving = (EntityLiving)entity;
               if (isMobFromRoguelikeSpawner(entityLiving)) {
                  handleDespawning(entityLiving);
                  if (RogueConfig.MOBS_PROFILES_ENABLED.getBoolean()) {
                     applyMobProfile(world, entityLiving);
                  }

               }
            }
         }
      }
   }

   private static boolean isMobFromRoguelikeSpawner(EntityLiving entity) {
      return entity.getActivePotionEffects().stream().anyMatch(EntityJoinWorld1_12::isRoguelikeTag);
   }

   private static void applyMobProfile(World world, EntityLiving entity) {
      int level = MathHelper.clamp(getRoguelikeLevel(entity), 0, 4);
      int difficulty = world.getDifficulty().ordinal();
      EntityLiving newEntity = EntityProfiler1_12.applyProfile(entity, level, world.rand, difficulty);
      if (newEntity != null) {
         newEntity.copyLocationAndAnglesFrom(entity);
         copyTags(entity, newEntity);
         Arrays.stream(EntityEquipmentSlot.values()).forEach((value) -> {
            newEntity.setDropChance(value, (float)RogueConfig.MOBS_ITEMS_DROP_CHANCE.getDouble());
         });
         entity.world.removeEntity(entity);
         newEntity.world.spawnEntity(newEntity);
      }
   }

   private static int getRoguelikeLevel(EntityLiving entity) {
      return ((Integer)entity.getActivePotionEffects().stream().filter(EntityJoinWorld1_12::isRoguelikeTag).findFirst().map(PotionEffect::getAmplifier).orElse(Integer.valueOf(0))).intValue();
   }

   private static boolean isRoguelikeTag(PotionEffect buff) {
      return Potion.getIdFromPotion(buff.getPotion()) == EffectType.FATIGUE.getEffectID();
   }

   private static void copyTags(EntityLiving from, EntityLiving to) {
      handleDespawning(to);
      from.getTags().forEach(to::addTag);
      copyEntityData(from, to);
   }

   private static void handleDespawning(EntityLiving entityLiving) {
      if (!RogueConfig.MOBS_DESPAWN_ENABLED.getBoolean()) {
         entityLiving.enablePersistence();
      }

   }

   private static void copyEntityData(EntityLiving from, EntityLiving to) {
      from.getEntityData().getKeySet().stream().map((tag) -> {
         return new Pair(tag, from.getEntityData().getTag(tag));
      }).forEach((tagPair) -> {
         to.getEntityData().setTag((String)tagPair.getKey(), (NBTBase)tagPair.getValue());
      });
   }
}
