package com.charles445.rltweaker.reflect;

import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.registries.IForgeRegistry;

public class ReskillableReflect {
   public final Class c_TransmutationRegistry = Class.forName("codersafterdark.reskillable.api.transmutations.TransmutationRegistry");
   public final Method m_TransmutationRegistry_addEntryToReagent;
   public final Class c_ReskillableRegistries;
   public final Field f_ReskillableRegistries_SKILLS;
   public final Field f_ReskillableRegistries_UNLOCKABLES;
   public final Class c_Unlockable;
   public final Method m_Unlockable_getKey;
   public final Class c_PlayerData;
   public final Method m_PlayerData_getSkillInfo;
   public final Method m_PlayerData_saveAndSync;
   public final Class c_PlayerDataHandler;
   public final Method m_PlayerDataHandler_get;
   public final Class c_PlayerSkillInfo;
   public final Method m_PlayerSkillInfo_isUnlocked;
   public final Method m_PlayerSkillInfo_lock;
   public final Class c_LockUnlockableEvent;
   public final Method m_LockUnlockableEvent_getUnlockable;
   public final Class c_LockUnlockableEvent$Pre;
   public final Constructor con_LockUnlockableEvent$Pre;
   public final Class c_LockUnlockableEvent$Post;
   public final Constructor con_LockUnlockableEvent$Post;

   public ReskillableReflect() throws Exception {
      this.m_TransmutationRegistry_addEntryToReagent = ReflectUtil.findMethod(this.c_TransmutationRegistry, "addEntryToReagent");
      this.c_ReskillableRegistries = Class.forName("codersafterdark.reskillable.api.ReskillableRegistries");
      this.f_ReskillableRegistries_SKILLS = ReflectUtil.findField(this.c_ReskillableRegistries, "SKILLS");
      this.f_ReskillableRegistries_UNLOCKABLES = ReflectUtil.findField(this.c_ReskillableRegistries, "UNLOCKABLES");
      this.c_Unlockable = Class.forName("codersafterdark.reskillable.api.unlockable.Unlockable");
      this.m_Unlockable_getKey = ReflectUtil.findMethod(this.c_Unlockable, "getKey");
      this.c_PlayerData = Class.forName("codersafterdark.reskillable.api.data.PlayerData");
      this.m_PlayerData_getSkillInfo = ReflectUtil.findMethod(this.c_PlayerData, "getSkillInfo");
      this.m_PlayerData_saveAndSync = ReflectUtil.findMethod(this.c_PlayerData, "saveAndSync");
      this.c_PlayerDataHandler = Class.forName("codersafterdark.reskillable.api.data.PlayerDataHandler");
      this.m_PlayerDataHandler_get = ReflectUtil.findMethod(this.c_PlayerDataHandler, "get", EntityPlayer.class);
      this.c_PlayerSkillInfo = Class.forName("codersafterdark.reskillable.api.data.PlayerSkillInfo");
      this.m_PlayerSkillInfo_isUnlocked = ReflectUtil.findMethod(this.c_PlayerSkillInfo, "isUnlocked");
      this.m_PlayerSkillInfo_lock = ReflectUtil.findMethod(this.c_PlayerSkillInfo, "lock");
      this.c_LockUnlockableEvent = Class.forName("codersafterdark.reskillable.api.event.LockUnlockableEvent");
      this.m_LockUnlockableEvent_getUnlockable = ReflectUtil.findMethod(this.c_LockUnlockableEvent, "getUnlockable");
      this.c_LockUnlockableEvent$Pre = Class.forName("codersafterdark.reskillable.api.event.LockUnlockableEvent$Pre");
      this.con_LockUnlockableEvent$Pre = this.c_LockUnlockableEvent$Pre.getDeclaredConstructor(EntityPlayer.class, this.c_Unlockable);
      this.c_LockUnlockableEvent$Post = Class.forName("codersafterdark.reskillable.api.event.LockUnlockableEvent$Post");
      this.con_LockUnlockableEvent$Post = this.c_LockUnlockableEvent$Post.getDeclaredConstructor(EntityPlayer.class, this.c_Unlockable);
   }

   public void addEntryToReagent(Item item, IBlockState state1, IBlockState state2) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      this.m_TransmutationRegistry_addEntryToReagent.invoke((Object)null, item, state1, state2);
   }

   public IForgeRegistry<?> getSkillsRegistry() throws IllegalArgumentException, IllegalAccessException {
      return (IForgeRegistry)this.f_ReskillableRegistries_SKILLS.get((Object)null);
   }

   public IForgeRegistry<?> getUnlockablesRegistry() throws IllegalArgumentException, IllegalAccessException {
      return (IForgeRegistry)this.f_ReskillableRegistries_UNLOCKABLES.get((Object)null);
   }

   public Object getPlayerData(EntityPlayer player) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_PlayerDataHandler_get.invoke((Object)null, player);
   }

   public Object getSkillInfo(Object playerData, Object skill) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_PlayerData_getSkillInfo.invoke(playerData, skill);
   }

   public boolean isUnlocked(Object skillInfo, Object unlockable) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return ((Boolean)this.m_PlayerSkillInfo_isUnlocked.invoke(skillInfo, unlockable)).booleanValue();
   }

   public boolean postLockUnlockableEventPre(EntityPlayer player, Object unlockable) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return MinecraftForge.EVENT_BUS.post((Event)this.con_LockUnlockableEvent$Pre.newInstance(player, unlockable));
   }

   public boolean postLockUnlockableEventPost(EntityPlayer player, Object unlockable) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return MinecraftForge.EVENT_BUS.post((Event)this.con_LockUnlockableEvent$Post.newInstance(player, unlockable));
   }

   public void lockPlayerSkill(Object skillInfo, Object unlockable, EntityPlayer player) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      this.m_PlayerSkillInfo_lock.invoke(skillInfo, unlockable, player);
   }

   public void saveAndSyncPlayerData(Object playerData) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      this.m_PlayerData_saveAndSync.invoke(playerData);
   }

   public Object getUnlockableFromLockedEvent(Object lockedEvent) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_LockUnlockableEvent_getUnlockable.invoke(lockedEvent);
   }

   public String getUnlockableName(Object unlockable) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return (String)this.m_Unlockable_getKey.invoke(unlockable);
   }
}
