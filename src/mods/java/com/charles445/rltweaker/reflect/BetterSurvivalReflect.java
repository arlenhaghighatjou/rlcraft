package com.charles445.rltweaker.reflect;

import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class BetterSurvivalReflect {
   public final Class c_NunchakuComboProvider = Class.forName("com.mujmajnkraft.bettersurvival.capabilities.nunchakucombo.NunchakuComboProwider");
   public final Field f_NunchakuComboProvider_NUNCHAKUCOMBO_CAP;
   public final Capability o_NUNCHAKUCOMBO_CAP;
   public final Class c_INunchakuCombo;
   public final Method m_INunchakuCombo_getComboPower;
   public final Method m_INunchakuCombo_countDown;
   public final Method m_INunchakuCombo_setComboTime;
   public final Class c_ItemNunchaku;
   public final Class c_ModEnchantments;
   public final Field f_ModEnchantments_agility;
   public final Field f_ModEnchantments_vitality;
   public final Enchantment o_ModEnchantments_agility;
   public final Enchantment o_ModEnchantments_vitality;
   public final Class c_ConfigHandler;
   public final Field f_ConfigHandler_agilitylevel;
   public final Field f_ConfigHandler_vitalitylevel;
   public final int o_ConfigHandler_agilitylevel;
   public final int o_ConfigHandler_vitalitylevel;

   public BetterSurvivalReflect() throws Exception {
      this.f_NunchakuComboProvider_NUNCHAKUCOMBO_CAP = ReflectUtil.findField(this.c_NunchakuComboProvider, "NUNCHAKUCOMBO_CAP");
      this.o_NUNCHAKUCOMBO_CAP = (Capability)this.f_NunchakuComboProvider_NUNCHAKUCOMBO_CAP.get((Object)null);
      this.c_INunchakuCombo = Class.forName("com.mujmajnkraft.bettersurvival.capabilities.nunchakucombo.INunchakuCombo");
      this.m_INunchakuCombo_getComboPower = ReflectUtil.findMethod(this.c_INunchakuCombo, "getComboPower");
      this.m_INunchakuCombo_countDown = ReflectUtil.findMethod(this.c_INunchakuCombo, "countDown");
      this.m_INunchakuCombo_setComboTime = ReflectUtil.findMethod(this.c_INunchakuCombo, "setComboTime");
      this.c_ItemNunchaku = Class.forName("com.mujmajnkraft.bettersurvival.items.ItemNunchaku");
      this.c_ModEnchantments = Class.forName("com.mujmajnkraft.bettersurvival.init.ModEnchantments");
      this.f_ModEnchantments_agility = ReflectUtil.findField(this.c_ModEnchantments, "agility");
      this.f_ModEnchantments_vitality = ReflectUtil.findField(this.c_ModEnchantments, "vitality");
      this.o_ModEnchantments_agility = (Enchantment)this.f_ModEnchantments_agility.get((Object)null);
      this.o_ModEnchantments_vitality = (Enchantment)this.f_ModEnchantments_vitality.get((Object)null);
      this.c_ConfigHandler = Class.forName("com.mujmajnkraft.bettersurvival.config.ConfigHandler");
      this.f_ConfigHandler_agilitylevel = ReflectUtil.findField(this.c_ConfigHandler, "agilitylevel");
      this.f_ConfigHandler_vitalitylevel = ReflectUtil.findField(this.c_ConfigHandler, "vitalitylevel");
      this.o_ConfigHandler_agilitylevel = this.f_ConfigHandler_agilitylevel.getInt((Object)null);
      this.o_ConfigHandler_vitalitylevel = this.f_ConfigHandler_vitalitylevel.getInt((Object)null);
   }

   public boolean hasINunchakuCombo(EntityPlayer player) {
      return player.hasCapability(this.o_NUNCHAKUCOMBO_CAP, (EnumFacing)null);
   }

   public Object getINunchakuCombo(EntityPlayer player) {
      return player.getCapability(this.o_NUNCHAKUCOMBO_CAP, (EnumFacing)null);
   }

   public float getComboPower(Object inunchakucombo) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return ((Float)this.m_INunchakuCombo_getComboPower.invoke(inunchakucombo)).floatValue();
   }

   public void countDown(Object inunchakucombo) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      this.m_INunchakuCombo_countDown.invoke(inunchakucombo);
   }

   public void setComboTime(Object inunchakucombo, int time) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      this.m_INunchakuCombo_setComboTime.invoke(inunchakucombo, time);
   }
}
