package com.charles445.rltweaker.reflect;

import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;

public class LevelUpTwoReflect {
   public final Class c_IPlayerSkill = Class.forName("levelup2.api.IPlayerSkill");
   public final Method m_IPlayerSkill_isActive;
   public final Class c_SkillRegistry;
   public final Method m_SkillRegistry_getSkillLevel;
   private boolean isLycanitesAvailable = false;
   @Nullable
   private Class c_LM_BaseCreatureEntity;
   @Nullable
   private Class c_LM_IGroupBoss;

   public LevelUpTwoReflect() throws Exception {
      this.m_IPlayerSkill_isActive = ReflectUtil.findMethod(this.c_IPlayerSkill, "isActive");
      this.c_SkillRegistry = Class.forName("levelup2.skills.SkillRegistry");
      this.m_SkillRegistry_getSkillLevel = ReflectUtil.findMethod(this.c_SkillRegistry, "getSkillLevel");
      if (Loader.isModLoaded("lycanitesmobs")) {
         try {
            this.c_LM_BaseCreatureEntity = Class.forName("com.lycanitesmobs.core.entity.BaseCreatureEntity");
            this.c_LM_IGroupBoss = Class.forName("com.lycanitesmobs.api.IGroupBoss");
            this.isLycanitesAvailable = true;
         } catch (Exception var2) {
            this.isLycanitesAvailable = false;
         }
      }

   }

   public boolean skillIsActive(Object skill) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return ((Boolean)this.m_IPlayerSkill_isActive.invoke(skill)).booleanValue();
   }

   public int getSkillLevel(EntityPlayer player, String skillName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return ((Integer)this.m_SkillRegistry_getSkillLevel.invoke((Object)null, player, skillName)).intValue();
   }

   public boolean isLycanitesAvailable() {
      return this.isLycanitesAvailable;
   }

   public boolean canLycanitesStealth(EntityLiving living) {
      if (!this.isLycanitesAvailable()) {
         return false;
      } else if (this.c_LM_BaseCreatureEntity != null && this.c_LM_IGroupBoss != null) {
         return this.c_LM_BaseCreatureEntity.isInstance(living) && !this.c_LM_IGroupBoss.isInstance(living);
      } else {
         return false;
      }
   }
}
