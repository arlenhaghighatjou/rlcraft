package org.jwaresoftware.mcmods.lib;

import java.util.List;
import java.util.Vector;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

public final class NPE {
   private static final List<ResourceLocation> _BAD_GUYS = new Vector(40);

   public static final EntityLiving getOrNull(@Nullable Entity entity) {
      return entity instanceof EntityLiving ? (EntityLiving)entity : null;
   }

   public static final boolean isa(@Nullable Entity entity) {
      return getOrNull(entity) != null;
   }

   public static final boolean isaWitch(EnumCreatureAttribute creatureType, boolean modded) {
      return creatureType == EnumCreatureAttribute.UNDEFINED || modded && creatureType != null && creatureType.name().contains("WITCH");
   }

   public static final boolean isaWitch(Entity target, boolean modded) {
      if (target == null) {
         return false;
      } else if (modded) {
         if (target instanceof EntityWitch) {
            return true;
         } else {
            String eid = String.valueOf(EntityList.getKey(target));
            return eid.endsWith("witch");
         }
      } else {
         return !modded && target.getClass() == EntityWitch.class;
      }
   }

   public static final boolean isaFish(ItemStack stack, boolean modded) {
      if (ItemStacks.isEmpty(stack)) {
         return false;
      } else {
         Item item = stack.getItem();
         if (item instanceof ItemFishFood) {
            return true;
         } else {
            if (modded) {
               String cn = item.getClass().getName();
               if (cn.endsWith("ItemFish") && cn.contains(".aquaculture.")) {
                  return true;
               }

               if (cn.endsWith("Cod") && cn.contains(".combustfish.")) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public static final boolean isaSilverfish(Entity target, boolean modded) {
      return modded && target instanceof EntitySilverfish || !modded && target != null && target.getClass().equals(EntitySilverfish.class);
   }

   public static final boolean isaSlime(Entity target, boolean modded) {
      return modded && target instanceof EntitySlime || !modded && target != null && target.getClass().equals(EntitySlime.class);
   }

   public static final boolean isaSkeleton(Entity target, boolean modded, boolean wither_ok) {
      if (target == null) {
         return false;
      } else {
         boolean iz = target instanceof AbstractSkeleton;
         if (modded) {
            return wither_ok ? iz : !(target instanceof EntityWitherSkeleton);
         } else {
            if (iz) {
               Class<?> klass = target.getClass();
               iz = klass == EntitySkeleton.class || klass == EntityWitherSkeleton.class && wither_ok || klass == EntityStray.class;
            }

            return iz;
         }
      }
   }

   public static final boolean isaArthropod(Entity target, boolean modded) {
      if (target == null) {
         return false;
      } else {
         if (modded) {
            if (isaSpider(target, modded)) {
               return true;
            }

            if (isaSilverfish(target, modded)) {
               return true;
            }

            if (ModIntegration.THE_BETWEENLANDS.matchesAny(target, "sludge", "leech", "blood_snail", "mire_snail", "termite", "silt_crab")) {
               return true;
            }
         }

         boolean iz = target instanceof EntityLiving && ((EntityLiving)target).getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD;
         if (iz && !modded) {
            Class<?> klass = target.getClass();
            iz = klass == EntitySpider.class || klass == EntityCaveSpider.class || klass == EntitySilverfish.class || klass == EntityEndermite.class;
         }

         return iz;
      }
   }

   public static final boolean isaSkeleton(Entity target, boolean modded) {
      return isaSkeleton(target, modded, true);
   }

   public static final boolean isaTaintableSoul(Entity victim, boolean modded, boolean strict) {
      if (victim != null) {
         if (victim instanceof EntityZombie) {
            return !strict || !(victim instanceof EntityPigZombie);
         }

         if (victim instanceof AbstractIllager) {
            return true;
         }

         if (victim instanceof EntityVillager) {
            return !strict;
         }

         if (isaWitch(victim, modded)) {
            return true;
         }

         if (!strict && SharedGlue.isRealPlayer(victim)) {
            return true;
         }
      }

      return false;
   }

   public static final boolean isaTaintedSoul(Entity victim, boolean modded) {
      return isaTaintableSoul(victim, modded, true);
   }

   public static final boolean isZombieLike(Entity victim, boolean strict) {
      if (victim instanceof EntityLiving) {
         if (victim instanceof EntityZombie) {
            return true;
         }

         if (!strict && victim instanceof AbstractIllager) {
            return true;
         }

         if (!strict && isaWitch(victim, true)) {
            return true;
         }

         String cn = victim.getClass().getName();
         if (ModIntegration.ABYSSALCRAFT.belongsTo(victim)) {
            return cn.endsWith("Zombie");
         }

         if (ModIntegration.THE_BETWEENLANDS.belongsTo(victim)) {
            return cn.endsWith("Mummy");
         }
      }

      return false;
   }

   public static final boolean isVillagerish(Entity victim) {
      if (victim != null) {
         if (victim instanceof EntityVillager || victim instanceof EntityZombieVillager) {
            return true;
         }

         if (victim instanceof AbstractIllager) {
            return true;
         }

         if (isaWitch(victim, true)) {
            return true;
         }
      }

      return false;
   }

   public static final boolean isaSpider(Entity target, boolean modded) {
      if (target == null) {
         return false;
      } else {
         if (modded) {
            if (target instanceof EntitySpider) {
               return true;
            }

            String cn = target.getClass().getName();
            if (cn.contains(".primitivemobs.") && cn.endsWith("Spider")) {
               return true;
            }
         }

         return !modded && (target.getClass() == EntitySpider.class || target.getClass() == EntityCaveSpider.class);
      }
   }

   public static final boolean isaVenomousSpider(Entity target, boolean modded) {
      if (target == null) {
         return false;
      } else if (modded) {
         return target instanceof EntityCaveSpider;
      } else {
         return !modded && target.getClass() == EntityCaveSpider.class;
      }
   }

   public static final boolean isaCreeper(Entity target, boolean modded) {
      if (target == null) {
         return false;
      } else if (!modded) {
         return target.getClass() == EntityCreeper.class;
      } else {
         return target instanceof EntityCreeper ? true : ModIntegration.NETHER_EX.matches(target, "monster_spore_creeper");
      }
   }

   public static final boolean isaBlaze(Entity target, boolean modded) {
      if (target == null) {
         return false;
      } else if (!modded) {
         return target.getClass() == EntityBlaze.class;
      } else {
         return target instanceof EntityBlaze ? true : ModIntegration.PRIMITIVE_MOBS.matches(target, "blazing_juggernaut");
      }
   }

   public static final boolean isTooEasyTarget(Entity target, boolean modded, float threshold) {
      if (SharedGlue.isBoss(target)) {
         return false;
      } else if (target instanceof EntityLiving) {
         EntityLiving livingtarget = (EntityLiving)target;
         IAttributeInstance ia = livingtarget.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
         if (ia != null && ia.getAttributeValue() < (double)threshold) {
            return true;
         } else if (target instanceof EntitySlime) {
            return ((EntitySlime)target).getSlimeSize() < 3;
         } else {
            return false;
         }
      } else {
         return !SharedGlue.isRealPlayer(target);
      }
   }

   public static final boolean isaWither(Entity target, boolean modded) {
      if (target == null) {
         return false;
      } else {
         if (modded) {
            if (target instanceof EntityWither) {
               return true;
            }

            String cn = target.getClass().getName();
            if (cn.contains(".spectrite.") && cn.endsWith("Wither")) {
               return true;
            }
         }

         return !modded && target.getClass() == EntityWither.class;
      }
   }

   public static final boolean isaDragon(Entity target, boolean modded) {
      if (target != null) {
         if (modded && target instanceof EntityDragon || !modded && target.getClass().equals(EntityDragon.class)) {
            return true;
         }

         if (target instanceof MultiPartEntityPart) {
            MultiPartEntityPart mp = (MultiPartEntityPart)target;
            if (mp.parent instanceof Entity && mp.parent != mp) {
               return isaDragon((Entity)mp.parent, modded);
            }
         } else if (modded && target instanceof EntityLivingBase) {
            ResourceLocation eid = EntityList.getKey(target);
            if (eid != null && "dragonboss".equals(eid.getResourcePath())) {
               return true;
            }
         }
      }

      return false;
   }

   public static final boolean isaEnderman(Entity target, boolean modded) {
      if (target == null) {
         return false;
      } else {
         if (modded) {
            if (target instanceof EntityEnderman) {
               return true;
            }

            String cn = target.getClass().getName();
            if (cn.contains(".spectrite.") && cn.endsWith("Enderman")) {
               return true;
            }
         }

         return !modded && target.getClass() == EntityEnderman.class;
      }
   }

   static final boolean isaWitheringThingy(Entity thingy) {
      return thingy != null && (thingy.getClass() == EntityWitherSkeleton.class || thingy.getClass() == EntityWitherSkull.class);
   }

   public static final boolean isWardingProtectionFrom(Entity target, boolean modded) {
      return isaWitch(target, modded) || isaWither(target, modded) || isaWitheringThingy(target);
   }

   public static final boolean isMisusedMagic(@Nonnull DamageSource ds) {
      Entity e = ds.getTrueSource();
      return e != null && ds.isMagicDamage() ? e instanceof EntityGuardian : false;
   }

   public static final boolean isPrimedTnT(@Nullable Entity entity) {
      if (entity == null) {
         return false;
      } else {
         return entity instanceof EntityTNTPrimed ? true : ModIntegration.PRIMITIVE_MOBS.matches(entity, "primitive_tnt_primed");
      }
   }

   public static final String getTranslationName(@Nullable ResourceLocation subject) {
      if (ExtendedResourceLocation.isa(subject)) {
         subject = ExtendedResourceLocation.narrow(subject);
      }

      String translatedId = EntityList.getTranslationName(subject);
      translatedId = (String)StringUtils.defaultIfBlank(translatedId, "UNKNOWN");
      return "entity." + translatedId + ".name";
   }

   @Nonnull
   public static final String getTranslatedName(String translatedId) {
      translatedId = (String)StringUtils.defaultIfBlank(translatedId, "UNKNOWN");
      String lookup = "entity." + translatedId + ".name";
      return Strings.translate(lookup);
   }

   @Nonnull
   public static final String getTranslatedName(@Nullable ResourceLocation subject) {
      if (ExtendedResourceLocation.isa(subject)) {
         subject = ExtendedResourceLocation.narrow(subject);
      }

      String translatedId = EntityList.getTranslationName(subject);
      return getTranslatedName(translatedId);
   }

   @Nonnull
   public static final String getTranslatedName(Entity subject) {
      return getTranslatedName(EntityList.getKey(subject));
   }

   public static final void addModdedBadGuy(ResourceLocation rLoc) {
      if (rLoc != null) {
         synchronized(_BAD_GUYS) {
            if (!_BAD_GUYS.contains(rLoc)) {
               _BAD_GUYS.add(rLoc);
            }
         }
      }

   }

   static final boolean isKnownBadGuy(ResourceLocation rLoc) {
      return ModIntegration.MOWZIE_MOBS.belongsTo(rLoc) || ModIntegration.GHOSTLY.belongsTo(rLoc) || ModIntegration.DMONSTERS.belongsTo(rLoc);
   }

   static final boolean isBadGuy(ResourceLocation rLoc) {
      for(ResourceLocation deny : _BAD_GUYS) {
         if (deny.equals(rLoc)) {
            return true;
         }

         if ("*".equals(deny.getResourcePath()) && deny.getResourceDomain().equals(rLoc.getResourceDomain())) {
            return true;
         }
      }

      return isKnownBadGuy(rLoc);
   }

   public static final boolean isModdedBadGuy(@Nullable ResourceLocation rLoc) {
      return rLoc != null && isBadGuy(rLoc);
   }

   public static final void resetModdedBadGuys() {
      _BAD_GUYS.clear();
   }

   private NPE() {
   }

   public static final void init() {
   }
}
