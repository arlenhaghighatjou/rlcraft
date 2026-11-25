package com.charles445.rltweaker.entity.ai;

import com.charles445.rltweaker.handler.InvestigateAIHandler;
import java.util.List;
import java.util.Optional;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import meldexun.reflectionutil.ReflectionField;
import meldexun.reflectionutil.ReflectionMethod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class InvestigateAI extends EntityAIBase {
   private static final Class<?> c_EntityDragonBase;
   private static final ReflectionMethod<Boolean> IS_FLYING;
   private static final ReflectionField<BlockPos> AIR_TARGET;
   private static final ReflectionMethod<Boolean> CAN_NAVIGATE;
   private static final ReflectionField<Path> CURRENT_PATH;
   private final EntityLiving entity;
   private final InvestigateAIConfig config;
   private BlockPos target;
   private InvestigateAIConfig.Entry configEntry;
   private int ticksAtLastPos;
   private Vec3d lastPosCheck;
   private int lastTimeWithPath;

   public InvestigateAI(EntityLiving entity, InvestigateAIConfig config) {
      this.entity = entity;
      this.config = config;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      if (this.target == null) {
         return false;
      } else {
         Path path = this.getPathToTarget();
         if (path == null) {
            return false;
         } else {
            this.entity.getNavigator().setPath(path, (double)this.configEntry.movementSpeed);
            return true;
         }
      }
   }

   public boolean shouldContinueExecuting() {
      if (this.entity.getAttackTarget() != null) {
         return false;
      } else {
         if (this.entity.ticksExisted - this.ticksAtLastPos >= 60) {
            if (this.entity.getDistanceSq(this.lastPosCheck.x, this.lastPosCheck.y, this.lastPosCheck.z) < 1.0D) {
               return false;
            }

            this.ticksAtLastPos = this.entity.ticksExisted;
            this.lastPosCheck = this.entity.getPositionVector();
         }

         if (((Boolean)CAN_NAVIGATE.invoke(this.entity.getNavigator())).booleanValue() && (this.entity.getNavigator().noPath() || this.entity.getRNG().nextInt(10) == 0)) {
            Path path = this.getPathToTarget();
            if (path == null) {
               return false;
            }

            this.entity.getNavigator().setPath(path, (double)this.configEntry.movementSpeed);
         }

         if (!this.entity.getNavigator().noPath()) {
            this.lastTimeWithPath = this.entity.ticksExisted;
         }

         return this.entity.ticksExisted - this.lastTimeWithPath < 20;
      }
   }

   public void startExecuting() {
      this.ticksAtLastPos = this.entity.ticksExisted;
      this.lastPosCheck = this.entity.getPositionVector();
      this.lastTimeWithPath = this.entity.ticksExisted;
   }

   public void resetTask() {
      this.target = null;
      this.entity.getNavigator().clearPath();
   }

   public void setTarget(Entity targetEntity) {
      this.setTarget(targetEntity.getPositionVector());
   }

   public void setTarget(Vec3d target) {
      this.setTarget(target, (Entity)null, (Set)Stream.of(this.entity).collect(Collectors.toSet()));
   }

   private void setTarget(Vec3d target, @Nullable Entity parent, Set<Entity> checked) {
      this.config.entries.stream().filter((entry) -> {
         if (parent != null) {
            if (!entry.executeOnCalled) {
               return (void)false;
            }
         } else if (!entry.executeOnAttacked) {
            return (void)false;
         }

         return (void)(this.entity.getHealth() / this.entity.getMaxHealth() <= entry.healthThreshold);
      }).findFirst().ifPresent((entry) -> {
         boolean inCombat = this.entity.getAttackTarget() != null;
         boolean chanceTestFailed = this.entity.getRNG().nextFloat() >= entry.executionChance;
         if (!inCombat && !chanceTestFailed) {
            this.setTarget(this.calculateTarget(target, entry), entry);
         }

         this.callForHelp(target, parent, inCombat, chanceTestFailed, checked, entry);
      });
   }

   private void callForHelp(Vec3d target, @Nullable Entity parent, boolean parentInCombat, boolean parentChanceTestFailed, Set<Entity> checked, InvestigateAIConfig.Entry entry) {
      entry.callForHelpEntries.stream().filter((callForHelpEntry) -> {
         if (!callForHelpEntry.ignoreParentInCombat && parentInCombat) {
            return (void)false;
         } else {
            return (void)(callForHelpEntry.ignoreParentChanceTestFailed || !parentChanceTestFailed);
         }
      }).forEach((callForHelpEntry) -> {
         AxisAlignedBB aabb = new AxisAlignedBB(this.entity.posX - (double)callForHelpEntry.horizontalRange, this.entity.posY - (double)callForHelpEntry.verticalRange, this.entity.posZ - (double)callForHelpEntry.horizontalRange, this.entity.posX + (double)callForHelpEntry.horizontalRange, this.entity.posY + (double)callForHelpEntry.verticalRange, this.entity.posZ + (double)callForHelpEntry.horizontalRange);
         List<EntityLiving> entities = this.entity.world.getEntitiesWithinAABB(EntityLiving.class, aabb, (entity1) -> {
            if (!callForHelpEntry.entityNames.contains(EntityList.getKey(entity1))) {
               return (void)false;
            } else {
               return (void)(callForHelpEntry.requiresVision && !this.entity.canEntityBeSeen(entity1) ? false : checked.add(entity1));
            }
         });
         entities.stream().map(InvestigateAIHandler::getInvestigateAI).filter(Optional::isPresent).map(Optional::get).forEach((investigateAI) -> {
            investigateAI.setTarget(target, this.entity, checked);
         });
      });
   }

   private BlockPos calculateTarget(Vec3d target, InvestigateAIConfig.Entry entry) {
      EaglercraftRandom rand = this.entity.getRNG();
      double dist = this.entity.getDistance(target.x, target.y, target.z);
      double horizontalOffset = Math.min((double)entry.horizontalOffsetBase + dist * (double)entry.horizontalOffsetScale, (double)entry.horizontalOffsetMax);
      double verticalOffset = Math.min((double)entry.verticalOffsetBase + dist * (double)entry.verticalOffsetScale, (double)entry.verticalOffsetMax);
      double x = target.x + (rand.nextDouble() - 0.5D) * 2.0D * horizontalOffset;
      double y = target.y + (rand.nextDouble() - 0.5D) * 2.0D * verticalOffset;
      double z = target.z + (rand.nextDouble() - 0.5D) * 2.0D * horizontalOffset;
      return new BlockPos(x, y, z);
   }

   private void setTarget(BlockPos pos, InvestigateAIConfig.Entry entry) {
      if (c_EntityDragonBase != null && c_EntityDragonBase.isInstance(this.entity) && IS_FLYING.isPresent() && ((Boolean)IS_FLYING.invoke(this.entity)).booleanValue() && AIR_TARGET.isPresent()) {
         AIR_TARGET.set(this.entity, pos);
      } else {
         this.target = pos;
      }

      this.configEntry = entry;
   }

   private Path getPathToTarget() {
      IAttributeInstance followRangeAttribute = this.entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
      double amount = (Math.sqrt(this.entity.getDistanceSqToCenter(this.target)) + 8.0D) / followRangeAttribute.getAttributeValue();
      AttributeModifier mod = (new AttributeModifier("RLTweaker Investigate AI Bonus", amount, 2)).setSaved(false);
      Path currentPath = CURRENT_PATH.get(this.entity.getNavigator());
      this.entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(mod);
      CURRENT_PATH.set(this.entity.getNavigator(), (Object)null);
      Path path = this.entity.getNavigator().getPathToPos(this.target);
      CURRENT_PATH.set(this.entity.getNavigator(), currentPath);
      this.entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).removeModifier(mod);
      return path;
   }

   static {
      Class<?> c;
      try {
         c = Class.forName("com.github.alexthe666.iceandfire.entity.EntityDragonBase");
      } catch (ClassNotFoundException var2) {
         c = null;
      }

      c_EntityDragonBase = c;
      IS_FLYING = new ReflectionMethod<Boolean>("com.github.alexthe666.iceandfire.entity.EntityDragonBase", "isFlying", "isFlying", new Class[0]);
      AIR_TARGET = new ReflectionField<BlockPos>("com.github.alexthe666.iceandfire.entity.EntityDragonBase", "airTarget", "airTarget");
      CAN_NAVIGATE = new ReflectionMethod<Boolean>(PathNavigate.class, "canNavigate", "canNavigate", new Class[0]);
      CURRENT_PATH = new ReflectionField<Path>(PathNavigate.class, "currentPath", "currentPath");
   }
}
