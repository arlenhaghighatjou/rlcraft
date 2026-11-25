package org.orecruncher.dsurround.capabilities.entitydata;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIAttackRangedBow;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOcelotAttack;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIRunAroundLikeCrazy;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAIZombieAttack;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntitySpellcasterIllager.AICastingApell;
import net.minecraft.entity.monster.EntitySpellcasterIllager.AIUseSpell;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntityWolf;
import org.orecruncher.lib.ReflectedField.ObjectField;

public final class EntityDataTables {
   private static final ObjectField<EntityAITasks, Set<EntityAITaskEntry>> executingTasks = new ObjectField(EntityAITasks.class, "executingTaskEntries", "executingTaskEntries");
   private static final Map<Class<?>, EntityDataTables.TaskType> AI_TASKS = new Reference2ObjectOpenHashMap(128);
   private static final Map<String, EntityDataTables.TaskType> MAPPINGS = new Object2ObjectOpenHashMap();

   public EntityDataTables() {
   }

   private static void add(@Nonnull Class<?> clazz, @Nonnull EntityDataTables.TaskType ct) {
      AI_TASKS.put(clazz, ct);
   }

   private static void add(@Nonnull Class<? extends EntityLiving> clazz, @Nonnull EntityDataTables.TaskType ct, @Nonnull String... className) {
      for(String cn : className) {
         String name = resolveName(clazz) + "$" + cn;
         MAPPINGS.put(name, ct);
      }

   }

   private static String resolveName(@Nonnull Class<?> clazz) {
      String n = clazz.getName();
      int i = n.lastIndexOf(46);
      return i > 0 ? n.substring(i + 1) : n;
   }

   @Nonnull
   private static EntityDataTables.TaskType find(@Nonnull EntityAIBase aiTask) {
      Class<?> clazz = aiTask.getClass();
      EntityDataTables.TaskType ct = (EntityDataTables.TaskType)AI_TASKS.get(clazz);
      if (ct == null) {
         for(Entry<Class<?>, EntityDataTables.TaskType> kvp : AI_TASKS.entrySet()) {
            if (((Class)kvp.getKey()).isInstance(aiTask)) {
               ct = (EntityDataTables.TaskType)kvp.getValue();
               break;
            }
         }

         if (ct == null) {
            String name = resolveName(clazz);
            ct = (EntityDataTables.TaskType)MAPPINGS.get(name);
            if (ct == null) {
               ct = EntityDataTables.TaskType.None;
            }
         }

         AI_TASKS.put(clazz, ct);
      }

      return ct;
   }

   private static boolean eval(@Nonnull EntityLiving entity, @Nonnull EntityDataTables.TaskType desiredType) {
      for(EntityAITaskEntry task : (Set)executingTasks.get(entity.tasks)) {
         if (find(task.action) == desiredType) {
            return true;
         }
      }

      for(EntityAITaskEntry task : (Set)executingTasks.get(entity.targetTasks)) {
         if (find(task.action) == desiredType) {
            return true;
         }
      }

      return false;
   }

   public static void assess(@Nonnull IEntityDataSettable data) {
      EntityLiving entity = data.getEntity();
      boolean isAttacking = eval(entity, EntityDataTables.TaskType.Attack);
      boolean isFleeing = eval(entity, EntityDataTables.TaskType.Flee);
      data.setAttacking(isAttacking);
      data.setFleeing(isFleeing);
   }

   static {
      add(EntityAIAttackMelee.class, EntityDataTables.TaskType.Attack);
      add(EntityAIAttackRanged.class, EntityDataTables.TaskType.Attack);
      add(EntityAIAttackRangedBow.class, EntityDataTables.TaskType.Attack);
      add(EntityAICreeperSwell.class, EntityDataTables.TaskType.Attack);
      add(EntityAILeapAtTarget.class, EntityDataTables.TaskType.Attack);
      add(EntityAIOcelotAttack.class, EntityDataTables.TaskType.Attack);
      add(EntityAIOwnerHurtByTarget.class, EntityDataTables.TaskType.Attack);
      add(EntityAIZombieAttack.class, EntityDataTables.TaskType.Attack);
      add(EntityAINearestAttackableTarget.class, EntityDataTables.TaskType.Attack);
      add(AICastingApell.class, EntityDataTables.TaskType.Attack);
      add(AIUseSpell.class, EntityDataTables.TaskType.Attack);
      add(EntityAIAvoidEntity.class, EntityDataTables.TaskType.Flee);
      add(EntityAIFleeSun.class, EntityDataTables.TaskType.Flee);
      add(EntityAIHurtByTarget.class, EntityDataTables.TaskType.Flee);
      add(EntityAIPanic.class, EntityDataTables.TaskType.Flee);
      add(EntityAIRunAroundLikeCrazy.class, EntityDataTables.TaskType.Flee);
      add(EntityRabbit.class, EntityDataTables.TaskType.Attack, "AIEvilAttack", "a");
      add(EntityRabbit.class, EntityDataTables.TaskType.Flee, "AIAvoidEntity", "b");
      add(EntityRabbit.class, EntityDataTables.TaskType.Flee, "AIPanic", "f");
      add(EntityPolarBear.class, EntityDataTables.TaskType.Attack, "AIMeleeAttack", "d");
      add(EntityPolarBear.class, EntityDataTables.TaskType.Attack, "AIAttackPlayer", "a");
      add(EntityPolarBear.class, EntityDataTables.TaskType.Flee, "AIPanic", "e");
      add(EntityShulker.class, EntityDataTables.TaskType.Attack, "AIAttack", "a");
      add(EntityShulker.class, EntityDataTables.TaskType.Attack, "AIDefenseAttack", "c");
      add(EntityShulker.class, EntityDataTables.TaskType.Attack, "AIAttackNearest", "d");
      add(EntityEvoker.class, EntityDataTables.TaskType.Attack, "AICastingSpell", "b");
      add(EntityEvoker.class, EntityDataTables.TaskType.Attack, "AISummonSpell", "c");
      add(EntityEvoker.class, EntityDataTables.TaskType.Attack, "AIAttackSpell", "a");
      add(EntityEvoker.class, EntityDataTables.TaskType.Attack, "AIWololoSpell", "d");
      add(EntityEnderman.class, EntityDataTables.TaskType.Attack, "AIFindPlayer", "b");
      add(EntityGhast.class, EntityDataTables.TaskType.Attack, "AIFireballAttack", "c");
      add(EntityGuardian.class, EntityDataTables.TaskType.Attack, "AIGuardianAttack", "a");
      add(EntitySlime.class, EntityDataTables.TaskType.Attack, "AISlimeAttack", "a");
      add(EntitySpider.class, EntityDataTables.TaskType.Attack, "AISpiderAttack", "a");
      add(EntityBlaze.class, EntityDataTables.TaskType.Attack, "AIFireballAttack", "a");
      add(EntityVex.class, EntityDataTables.TaskType.Attack, "AIChargeAttack", "a");
      add(EntityVindicator.class, EntityDataTables.TaskType.Attack, "AIJohnnyAttack", "a");
      add(EntityWolf.class, EntityDataTables.TaskType.Flee, "AIAvoidEntity", "a");
   }

   private static enum TaskType {
      None,
      Attack,
      Flee;

      private TaskType() {
      }
   }
}
