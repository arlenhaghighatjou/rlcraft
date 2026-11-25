package mcjty.incontrol.rules.support;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nullable;
import mcjty.incontrol.InControl;
import mcjty.incontrol.compat.ModRuleCompatibilityLayer;
import mcjty.incontrol.rules.PotentialSpawnRule;
import mcjty.tools.rules.CommonRuleEvaluator;
import mcjty.tools.rules.IEventQuery;
import mcjty.tools.typed.AttributeMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

public class GenericRuleEvaluator extends CommonRuleEvaluator {
   public GenericRuleEvaluator(AttributeMap map) {
      super(map, InControl.setup.getLogger(), new ModRuleCompatibilityLayer());
   }

   protected void addChecks(AttributeMap map) {
      super.addChecks(map);
      if (map.has(RuleKeys.HOSTILE)) {
         this.addHostileCheck(map);
      }

      if (map.has(RuleKeys.PASSIVE)) {
         this.addPassiveCheck(map);
      }

      if (map.has(RuleKeys.CANSPAWNHERE)) {
         this.addCanSpawnHereCheck(map);
      }

      if (map.has(RuleKeys.NOTCOLLIDING)) {
         this.addNotCollidingCheck(map);
      }

      if (map.has(RuleKeys.SPAWNER)) {
         this.addSpawnerCheck(map);
      }

      if (map.has(RuleKeys.MOB)) {
         this.addMobsCheck(map);
      }

      if (map.has(RuleKeys.PLAYER)) {
         this.addPlayerCheck(map);
      }

      if (map.has(RuleKeys.REALPLAYER)) {
         this.addRealPlayerCheck(map);
      }

      if (map.has(RuleKeys.FAKEPLAYER)) {
         this.addFakePlayerCheck(map);
      }

      if (map.has(RuleKeys.EXPLOSION)) {
         this.addExplosionCheck(map);
      }

      if (map.has(RuleKeys.PROJECTILE)) {
         this.addProjectileCheck(map);
      }

      if (map.has(RuleKeys.FIRE)) {
         this.addFireCheck(map);
      }

      if (map.has(RuleKeys.MAGIC)) {
         this.addMagicCheck(map);
      }

      if (map.has(RuleKeys.SOURCE)) {
         this.addSourceCheck(map);
      }

      if (map.has(RuleKeys.MOD)) {
         this.addModsCheck(map);
      }

      if (map.has(RuleKeys.MINCOUNT)) {
         this.addMinCountCheck(map);
      }

      if (map.has(RuleKeys.MAXCOUNT)) {
         this.addMaxCountCheck(map);
      }

   }

   private void addCanSpawnHereCheck(AttributeMap map) {
      boolean c = ((Boolean)map.get(RuleKeys.CANSPAWNHERE)).booleanValue();
      if (c) {
         this.checks.add((event, query) -> {
            Entity entity = query.getEntity(event);
            return entity instanceof EntityLiving ? ((EntityLiving)entity).getCanSpawnHere() : false;
         });
      } else {
         this.checks.add((event, query) -> {
            Entity entity = query.getEntity(event);
            return entity instanceof EntityLiving ? !((EntityLiving)entity).getCanSpawnHere() : true;
         });
      }

   }

   private void addNotCollidingCheck(AttributeMap map) {
      boolean c = ((Boolean)map.get(RuleKeys.NOTCOLLIDING)).booleanValue();
      if (c) {
         this.checks.add((event, query) -> {
            Entity entity = query.getEntity(event);
            return entity instanceof EntityLiving ? ((EntityLiving)entity).isNotColliding() : false;
         });
      } else {
         this.checks.add((event, query) -> {
            Entity entity = query.getEntity(event);
            return entity instanceof EntityLiving ? !((EntityLiving)entity).isNotColliding() : true;
         });
      }

   }

   private void addSpawnerCheck(AttributeMap map) {
      boolean c = ((Boolean)map.get(RuleKeys.SPAWNER)).booleanValue();
      if (c) {
         this.checks.add((event, query) -> {
            if (event instanceof CheckSpawn) {
               CheckSpawn checkSpawn = (CheckSpawn)event;
               return checkSpawn.isSpawner();
            } else {
               return false;
            }
         });
      } else {
         this.checks.add((event, query) -> {
            if (event instanceof CheckSpawn) {
               CheckSpawn checkSpawn = (CheckSpawn)event;
               return !checkSpawn.isSpawner();
            } else {
               return false;
            }
         });
      }

   }

   private void addHostileCheck(AttributeMap map) {
      if (((Boolean)map.get(RuleKeys.HOSTILE)).booleanValue()) {
         this.checks.add((event, query) -> {
            return query.getEntity(event) instanceof IMob;
         });
      } else {
         this.checks.add((event, query) -> {
            return !(query.getEntity(event) instanceof IMob);
         });
      }

   }

   private void addPassiveCheck(AttributeMap map) {
      if (((Boolean)map.get(RuleKeys.PASSIVE)).booleanValue()) {
         this.checks.add((event, query) -> {
            return query.getEntity(event) instanceof IAnimals && !(query.getEntity(event) instanceof IMob);
         });
      } else {
         this.checks.add((event, query) -> {
            return !(query.getEntity(event) instanceof IAnimals) || query.getEntity(event) instanceof IMob;
         });
      }

   }

   private void addMobsCheck(AttributeMap map) {
      List<String> mobs = map.getList(RuleKeys.MOB);
      if (mobs.size() == 1) {
         String name = (String)mobs.get(0);
         String id = PotentialSpawnRule.fixEntityId(name);
         EntityEntry ee = (EntityEntry)ForgeRegistries.ENTITIES.getValue(new ResourceLocation(id));
         Class<? extends Entity> clazz = ee == null ? null : ee.getEntityClass();
         if (clazz != null) {
            this.checks.add((event, query) -> {
               return clazz.equals(query.getEntity(event).getClass());
            });
         } else {
            InControl.setup.getLogger().log(Level.ERROR, "Unknown mob '" + name + "'!");
         }
      } else {
         Set<Class> classes = new HashSet();

         for(String name : mobs) {
            String id = PotentialSpawnRule.fixEntityId(name);
            EntityEntry ee = (EntityEntry)ForgeRegistries.ENTITIES.getValue(new ResourceLocation(id));
            Class<? extends Entity> clazz = ee == null ? null : ee.getEntityClass();
            if (clazz != null) {
               classes.add(clazz);
            } else {
               InControl.setup.getLogger().log(Level.ERROR, "Unknown mob '" + name + "'!");
            }
         }

         if (!classes.isEmpty()) {
            this.checks.add((event, query) -> {
               return classes.contains(query.getEntity(event).getClass());
            });
         }
      }

   }

   private void addModsCheck(AttributeMap map) {
      List<String> mods = map.getList(RuleKeys.MOD);
      if (mods.size() == 1) {
         String modid = (String)mods.get(0);
         this.checks.add((event, query) -> {
            String mod = InControl.instance.modCache.getMod(query.getEntity(event));
            return modid.equals(mod);
         });
      } else {
         Set<String> modids = new HashSet();

         for(String modid : mods) {
            modids.add(modid);
         }

         this.checks.add((event, query) -> {
            String mod = InControl.instance.modCache.getMod(query.getEntity(event));
            return modids.contains(mod);
         });
      }

   }

   @Nullable
   private GenericRuleEvaluator.CountInfo parseCountInfo(String json) {
      JsonParser parser = new JsonParser();
      JsonElement element = parser.parse(json);
      if (element.isJsonPrimitive()) {
         if (element.getAsJsonPrimitive().isString()) {
            String[] splitted = StringUtils.split(element.getAsString(), ',');

            int amount;
            try {
               amount = Integer.parseInt(splitted[0]);
            } catch (NumberFormatException var12) {
               InControl.setup.getLogger().log(Level.ERROR, "Bad amount for mincount '" + splitted[0] + "'!");
               return null;
            }

            Class<? extends Entity> entityClass = null;
            if (splitted.length > 1) {
               entityClass = this.findEntity(splitted[1]);
               if (entityClass == null) {
                  InControl.setup.getLogger().log(Level.ERROR, "Cannot find mob '" + splitted[1] + "'!");
                  return null;
               }
            }

            return (new GenericRuleEvaluator.CountInfo()).setAmount(amount).addEntityClass(entityClass);
         } else {
            int amount = element.getAsInt();
            return (new GenericRuleEvaluator.CountInfo()).setAmount(amount);
         }
      } else if (!element.isJsonObject()) {
         InControl.setup.getLogger().log(Level.ERROR, "Count description '" + json + "' is not valid!");
         return null;
      } else {
         JsonObject obj = element.getAsJsonObject();
         int amount = obj.get("amount").getAsInt();
         GenericRuleEvaluator.CountInfo info = (new GenericRuleEvaluator.CountInfo()).setAmount(amount);
         if (obj.has("mob")) {
            if (obj.get("mob").isJsonPrimitive()) {
               String entity = obj.get("mob").getAsString();
               Class<? extends Entity> entityClass = this.findEntity(entity);
               if (entityClass == null) {
                  return null;
               }

               info.addEntityClass(entityClass);
            } else {
               if (!obj.get("mob").isJsonArray()) {
                  InControl.setup.getLogger().log(Level.ERROR, "Bad entity tag in count description!");
                  return null;
               }

               for(JsonElement el : obj.get("mob").getAsJsonArray()) {
                  String entity = el.getAsString();
                  Class<? extends Entity> entityClass = this.findEntity(entity);
                  if (entityClass == null) {
                     InControl.setup.getLogger().log(Level.ERROR, "Cannot find mob '" + entity + "'!");
                     return null;
                  }

                  info.addEntityClass(entityClass);
               }
            }
         }

         if (obj.has("mod")) {
            String mod = obj.get("mod").getAsString();
            info.setMod(mod);
         }

         if (obj.has("perplayer")) {
            info.setScaledPerPlayer(obj.get("perplayer").getAsBoolean());
         }

         if (obj.has("perchunk")) {
            info.setScaledPerChunk(obj.get("perchunk").getAsBoolean());
         }

         if (obj.has("passive")) {
            info.setPassive(obj.get("passive").getAsBoolean());
         }

         if (obj.has("hostile")) {
            info.setHostile(obj.get("hostile").getAsBoolean());
         }

         String error = info.validate();
         if (error != null) {
            InControl.setup.getLogger().log(Level.ERROR, error);
            return null;
         } else {
            return info;
         }
      }
   }

   private Class<? extends Entity> findEntity(String entity) {
      String id = PotentialSpawnRule.fixEntityId(entity);
      EntityEntry ee = (EntityEntry)ForgeRegistries.ENTITIES.getValue(new ResourceLocation(id));
      Class<? extends Entity> entityClass = ee == null ? null : ee.getEntityClass();
      if (entityClass == null) {
         InControl.setup.getLogger().log(Level.ERROR, "Unknown mob '" + entity + "'!");
         return null;
      } else {
         return entityClass;
      }
   }

   private void addMinCountCheck(AttributeMap map) {
      String json = (String)map.get(RuleKeys.MINCOUNT);
      GenericRuleEvaluator.CountInfo info = this.parseCountInfo(json);
      if (info != null) {
         BiFunction<World, Entity, Integer> counter = this.getCounter(info);
         Function<World, Integer> amountAdjuster = this.getAmountAdjuster(info, info.amount);
         this.checks.add((event, query) -> {
            World world = query.getWorld(event);
            Entity entity = query.getEntity(event);
            int count = ((Integer)counter.apply(world, entity)).intValue();
            int amount = ((Integer)amountAdjuster.apply(world)).intValue();
            return count >= amount;
         });
      }
   }

   private void addMaxCountCheck(AttributeMap map) {
      String json = (String)map.get(RuleKeys.MAXCOUNT);
      GenericRuleEvaluator.CountInfo info = this.parseCountInfo(json);
      BiFunction<World, Entity, Integer> counter = this.getCounter(info);
      Function<World, Integer> amountAdjuster = this.getAmountAdjuster(info, info.amount);
      this.checks.add((event, query) -> {
         World world = query.getWorld(event);
         Entity entity = query.getEntity(event);
         int count = ((Integer)counter.apply(world, entity)).intValue();
         int amount = ((Integer)amountAdjuster.apply(world)).intValue();
         return count < amount;
      });
   }

   private Function<World, Integer> getAmountAdjuster(GenericRuleEvaluator.CountInfo info, int infoAmount) {
      Function<World, Integer> amountAdjuster;
      if (info.scaledPerChunk) {
         amountAdjuster = (world) -> {
            return infoAmount * InControl.setup.cache.getValidSpawnChunks(world) / 289;
         };
      } else if (info.scaledPerPlayer) {
         amountAdjuster = (world) -> {
            return infoAmount * InControl.setup.cache.getValidPlayers(world);
         };
      } else {
         amountAdjuster = (world) -> {
            return infoAmount;
         };
      }

      return amountAdjuster;
   }

   private BiFunction<World, Entity, Integer> getCounter(GenericRuleEvaluator.CountInfo info) {
      BiFunction<World, Entity, Integer> counter;
      if (info.mod != null) {
         if (info.hostile) {
            counter = (world, entity) -> {
               return InControl.setup.cache.getCountPerModHostile(world, info.mod);
            };
         } else if (info.passive) {
            counter = (world, entity) -> {
               return InControl.setup.cache.getCountPerModPassive(world, info.mod);
            };
         } else {
            counter = (world, entity) -> {
               return InControl.setup.cache.getCountPerMod(world, info.mod);
            };
         }
      } else if (info.hostile) {
         counter = (world, entity) -> {
            return InControl.setup.cache.getCountHostile(world);
         };
      } else if (info.passive) {
         counter = (world, entity) -> {
            return InControl.setup.cache.getCountPassive(world);
         };
      } else {
         List<Class<? extends Entity>> infoEntityClass = info.entityClass;
         if (infoEntityClass.isEmpty()) {
            counter = (world, entity) -> {
               return InControl.setup.cache.getCount(world, entity.getClass());
            };
         } else if (infoEntityClass.size() == 1) {
            counter = (world, entity) -> {
               Class<? extends Entity> entityType = (Class)infoEntityClass.get(0);
               return InControl.setup.cache.getCount(world, entityType);
            };
         } else {
            counter = (world, entity) -> {
               int amount = 0;

               for(Class<? extends Entity> cls : infoEntityClass) {
                  amount += InControl.setup.cache.getCount(world, cls);
               }

               return amount;
            };
         }
      }

      return counter;
   }

   private void addPlayerCheck(AttributeMap map) {
      boolean asPlayer = ((Boolean)map.get(RuleKeys.PLAYER)).booleanValue();
      if (asPlayer) {
         this.checks.add((event, query) -> {
            return query.getAttacker(event) instanceof EntityPlayer;
         });
      } else {
         this.checks.add((event, query) -> {
            return query.getAttacker(event) instanceof EntityPlayer;
         });
      }

   }

   private boolean isFakePlayer(Entity entity) {
      if (!(entity instanceof EntityPlayer)) {
         return false;
      } else if (entity instanceof FakePlayer) {
         return true;
      } else {
         PlayerList playerList = DimensionManager.getWorld(0).getMinecraftServer().getPlayerList();
         EntityPlayerMP playerByUUID = playerList.getPlayerByUUID(((EntityPlayer)entity).getGameProfile().getId());
         if (playerByUUID == null) {
            return true;
         } else {
            return entity != playerByUUID;
         }
      }
   }

   private boolean isRealPlayer(Entity entity) {
      if (!(entity instanceof EntityPlayer)) {
         return false;
      } else {
         return !this.isFakePlayer(entity);
      }
   }

   private void addRealPlayerCheck(AttributeMap map) {
      boolean asPlayer = ((Boolean)map.get(RuleKeys.REALPLAYER)).booleanValue();
      if (asPlayer) {
         this.checks.add((event, query) -> {
            return query.getAttacker(event) == null ? false : this.isRealPlayer(query.getAttacker(event));
         });
      } else {
         this.checks.add((event, query) -> {
            return query.getAttacker(event) == null ? true : !this.isRealPlayer(query.getAttacker(event));
         });
      }

   }

   private void addFakePlayerCheck(AttributeMap map) {
      boolean asPlayer = ((Boolean)map.get(RuleKeys.FAKEPLAYER)).booleanValue();
      if (asPlayer) {
         this.checks.add((event, query) -> {
            return query.getAttacker(event) == null ? false : this.isFakePlayer(query.getAttacker(event));
         });
      } else {
         this.checks.add((event, query) -> {
            return query.getAttacker(event) == null ? true : !this.isFakePlayer(query.getAttacker(event));
         });
      }

   }

   private void addExplosionCheck(AttributeMap map) {
      boolean explosion = ((Boolean)map.get(RuleKeys.EXPLOSION)).booleanValue();
      if (explosion) {
         this.checks.add((event, query) -> {
            return query.getSource(event) == null ? false : query.getSource(event).isExplosion();
         });
      } else {
         this.checks.add((event, query) -> {
            return query.getSource(event) == null ? true : !query.getSource(event).isExplosion();
         });
      }

   }

   private void addProjectileCheck(AttributeMap map) {
      boolean projectile = ((Boolean)map.get(RuleKeys.PROJECTILE)).booleanValue();
      if (projectile) {
         this.checks.add((event, query) -> {
            return query.getSource(event) == null ? false : query.getSource(event).isProjectile();
         });
      } else {
         this.checks.add((event, query) -> {
            return query.getSource(event) == null ? true : !query.getSource(event).isProjectile();
         });
      }

   }

   private void addFireCheck(AttributeMap map) {
      boolean fire = ((Boolean)map.get(RuleKeys.FIRE)).booleanValue();
      if (fire) {
         this.checks.add((event, query) -> {
            return query.getSource(event) == null ? false : query.getSource(event).isFireDamage();
         });
      } else {
         this.checks.add((event, query) -> {
            return query.getSource(event) == null ? true : !query.getSource(event).isFireDamage();
         });
      }

   }

   private void addMagicCheck(AttributeMap map) {
      boolean magic = ((Boolean)map.get(RuleKeys.MAGIC)).booleanValue();
      if (magic) {
         this.checks.add((event, query) -> {
            return query.getSource(event) == null ? false : query.getSource(event).isMagicDamage();
         });
      } else {
         this.checks.add((event, query) -> {
            return query.getSource(event) == null ? true : !query.getSource(event).isMagicDamage();
         });
      }

   }

   private void addSourceCheck(AttributeMap map) {
      List<String> sources = map.getList(RuleKeys.SOURCE);
      Set<String> sourceSet = new HashSet(sources);
      this.checks.add((event, query) -> {
         return query.getSource(event) == null ? false : sourceSet.contains(query.getSource(event).getDamageType());
      });
   }

   public boolean match(Event event, IEventQuery query) {
      for(BiFunction<Event, IEventQuery, Boolean> rule : this.checks) {
         if (!((Boolean)rule.apply(event, query)).booleanValue()) {
            return false;
         }
      }

      return true;
   }

   private static class CountInfo {
      private int amount;
      private List<Class<? extends Entity>> entityClass = new ArrayList();
      private boolean scaledPerPlayer = false;
      private boolean scaledPerChunk = false;
      private boolean passive = false;
      private boolean hostile = false;
      private String mod = null;

      public CountInfo() {
      }

      public GenericRuleEvaluator.CountInfo setAmount(int amount) {
         this.amount = amount;
         return this;
      }

      public GenericRuleEvaluator.CountInfo addEntityClass(Class<? extends Entity> entityClass) {
         if (entityClass != null) {
            this.entityClass.add(entityClass);
         }

         return this;
      }

      public GenericRuleEvaluator.CountInfo setScaledPerPlayer(boolean scaledPerPlayer) {
         this.scaledPerPlayer = scaledPerPlayer;
         return this;
      }

      public GenericRuleEvaluator.CountInfo setScaledPerChunk(boolean scaledPerChunk) {
         this.scaledPerChunk = scaledPerChunk;
         return this;
      }

      public GenericRuleEvaluator.CountInfo setPassive(boolean passive) {
         this.passive = passive;
         return this;
      }

      public GenericRuleEvaluator.CountInfo setHostile(boolean hostile) {
         this.hostile = hostile;
         return this;
      }

      public GenericRuleEvaluator.CountInfo setMod(String mod) {
         this.mod = mod;
         return this;
      }

      public String validate() {
         if (this.scaledPerPlayer && this.scaledPerChunk) {
            return "You cannot combine 'perchunk' and 'perplayer'!";
         } else if (this.mod != null && !this.entityClass.isEmpty()) {
            return "You cannot combine 'mod' with 'mob'!";
         } else if (this.passive && this.hostile) {
            return "Don't use passive and hostile at the same time!";
         } else {
            return (this.passive || this.hostile) && !this.entityClass.isEmpty() ? "You cannot combine 'passive' or 'hostile' with 'mob'!" : null;
         }
      }
   }
}
