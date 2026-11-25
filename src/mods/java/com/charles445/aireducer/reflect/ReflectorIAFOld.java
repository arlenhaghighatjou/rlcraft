package com.charles445.aireducer.reflect;

import com.charles445.aireducer.util.ErrorUtil;
import com.charles445.aireducer.util.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;

public class ReflectorIAFOld {
   @Nullable
   public static ReflectorIAFOld reflector;
   public final Class c_EntityMyrmexBase = Class.forName("com.github.alexthe666.iceandfire.entity.EntityMyrmexBase");
   public final Method m_EntityMyrmexBase_canMove;
   public final Method m_EntityMyrmexBase_shouldLeaveHive;
   public final Method m_EntityMyrmexBase_shouldEnterHive;
   public final Class c_EntityMyrmexQueen;
   public final Class c_EntityMyrmexSentinel;
   public final Class c_MyrmexAILeaveHive;
   public final Field f_MyrmexAILeaveHive_nextEntrance;
   public final Field f_MyrmexAILeaveHive_path;
   public final Class c_iceandfire_VillagerAIFearUntamed;
   public final Class c_iceandfire_IAnimalFear;
   public final Method m_iceandfire_shouldAnimalsFear;
   public final Class c_iceandfire_IVillagerFear;
   public final Class c_iceandfire_DragonUtils;
   public final Method m_iceandfire_isLivestock;
   public final Method m_iceandfire_isVillager;
   public final Class c_iceandfire_MyrmexAIForage;
   public final Class c_iceandfire_MyrmexAIFindHidingSpot;
   public final Class c_iceandfire_MyrmexAILeaveHive;
   public final Class c_iceandfire_MyrmexAIMoveThroughHive;
   public final Class c_iceandfire_MyrmexAIReEnterHive;
   public final Class c_iceandfire_MyrmexAIEscortEntity;

   public ReflectorIAFOld() throws Exception {
      this.m_EntityMyrmexBase_canMove = ReflectUtil.findMethod(this.c_EntityMyrmexBase, "canMove");
      this.m_EntityMyrmexBase_shouldLeaveHive = ReflectUtil.findMethod(this.c_EntityMyrmexBase, "shouldLeaveHive");
      this.m_EntityMyrmexBase_shouldEnterHive = ReflectUtil.findMethod(this.c_EntityMyrmexBase, "shouldEnterHive");
      this.c_EntityMyrmexQueen = Class.forName("com.github.alexthe666.iceandfire.entity.EntityMyrmexQueen");
      this.c_EntityMyrmexSentinel = Class.forName("com.github.alexthe666.iceandfire.entity.EntityMyrmexSentinel");
      this.c_MyrmexAILeaveHive = Class.forName("com.github.alexthe666.iceandfire.entity.ai.MyrmexAILeaveHive");
      this.f_MyrmexAILeaveHive_nextEntrance = ReflectUtil.findField(this.c_MyrmexAILeaveHive, "nextEntrance");
      this.f_MyrmexAILeaveHive_path = ReflectUtil.findField(this.c_MyrmexAILeaveHive, "path");
      this.c_iceandfire_VillagerAIFearUntamed = Class.forName("com.github.alexthe666.iceandfire.entity.ai.VillagerAIFearUntamed");
      this.c_iceandfire_IAnimalFear = Class.forName("com.github.alexthe666.iceandfire.entity.IAnimalFear");
      this.m_iceandfire_shouldAnimalsFear = this.c_iceandfire_IAnimalFear.getDeclaredMethod("shouldAnimalsFear", Entity.class);
      this.c_iceandfire_IVillagerFear = Class.forName("com.github.alexthe666.iceandfire.entity.IVillagerFear");
      this.c_iceandfire_DragonUtils = Class.forName("com.github.alexthe666.iceandfire.entity.DragonUtils");
      this.m_iceandfire_isLivestock = this.c_iceandfire_DragonUtils.getDeclaredMethod("isLivestock", Entity.class);
      this.m_iceandfire_isVillager = this.c_iceandfire_DragonUtils.getDeclaredMethod("isVillager", Entity.class);
      this.c_iceandfire_MyrmexAIForage = Class.forName("com.github.alexthe666.iceandfire.entity.ai.MyrmexAIForage");
      this.c_iceandfire_MyrmexAIFindHidingSpot = Class.forName("com.github.alexthe666.iceandfire.entity.ai.MyrmexAIFindHidingSpot");
      this.c_iceandfire_MyrmexAILeaveHive = Class.forName("com.github.alexthe666.iceandfire.entity.ai.MyrmexAILeaveHive");
      this.c_iceandfire_MyrmexAIMoveThroughHive = Class.forName("com.github.alexthe666.iceandfire.entity.ai.MyrmexAIMoveThroughHive");
      this.c_iceandfire_MyrmexAIReEnterHive = Class.forName("com.github.alexthe666.iceandfire.entity.ai.MyrmexAIReEnterHive");
      this.c_iceandfire_MyrmexAIEscortEntity = Class.forName("com.github.alexthe666.iceandfire.entity.ai.MyrmexAIEscortEntity");
      reflector = this;
   }

   public Path getMyrmexAILeaveHive_path(Object taskMyrmexAILeaveHive) {
      try {
         return (Path)this.f_MyrmexAILeaveHive_path.get(taskMyrmexAILeaveHive);
      } catch (IllegalAccessException | IllegalArgumentException var3) {
         ErrorUtil.debugError("Invocation error in getMyrmexAILeaveHive_path", var3);
         return null;
      }
   }

   public BlockPos getMyrmexAILeaveHive_nextEntrance(Object taskMyrmexAILeaveHive) {
      try {
         return (BlockPos)this.f_MyrmexAILeaveHive_nextEntrance.get(taskMyrmexAILeaveHive);
      } catch (IllegalAccessException | IllegalArgumentException var3) {
         ErrorUtil.debugError("Invocation error in getMyrmexAILeaveHive_nextEntrance");
         return BlockPos.ORIGIN;
      }
   }

   public boolean canMove(EntityLivingBase entityMyrmexBase) {
      try {
         return ((Boolean)this.m_EntityMyrmexBase_canMove.invoke(entityMyrmexBase)).booleanValue();
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var3) {
         ErrorUtil.debugError("Invocation error in canMove");
         return false;
      }
   }

   public boolean shouldLeaveHive(EntityLivingBase entityMyrmexBase) {
      try {
         return ((Boolean)this.m_EntityMyrmexBase_shouldLeaveHive.invoke(entityMyrmexBase)).booleanValue();
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var3) {
         ErrorUtil.debugError("Invocation error in shouldLeaveHive");
         return false;
      }
   }

   public boolean shouldEnterHive(EntityLivingBase entityMyrmexBase) {
      try {
         return ((Boolean)this.m_EntityMyrmexBase_shouldEnterHive.invoke(entityMyrmexBase)).booleanValue();
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var3) {
         ErrorUtil.debugError("Invocation error in shouldEnterHive");
         return false;
      }
   }
}
