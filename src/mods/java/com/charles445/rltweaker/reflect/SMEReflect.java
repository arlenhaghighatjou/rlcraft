package com.charles445.rltweaker.reflect;

import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Method;

public class SMEReflect {
   public final Class c_Enchantmentadvancedmending = Class.forName("com.Shultrea.Rin.Ench0_4_0.Enchantmentadvancedmending");
   public final Method m_Enchantmentadvancedmending_handler;
   public final Class c_EnchantmentSwiper;
   public final Method m_EnchantmentSwiper_handler;
   public final Class c_EnchantmentEmpoweredDefence;
   public final Method m_EnchantmentEmpoweredDefence_handler;
   public final Class c_EnchantmentCurseofPossession;
   public final Method m_EnchantmentCurseofPossession_onExist;
   public final Method m_EnchantmentCurseofPossession_toss;
   public final Class c_EnchantmentEvasion;
   public final Method m_EnchantmentEvasion_handler;
   public final Class c_EnchantmentFreezing;
   public final Method m_EnchantmentFreezing_handler;
   public final Class c_EnchantmentParry;
   public final Method m_EnchantmentParry_handler;
   public final Class c_EnchantmentFrenzy;
   public final Method m_EnchantmentFrenzy_handler;
   public final Class c_EnchantmentUpgradedPotentials;
   public final Method m_EnchantmentUpgradedPotentials_handler;

   public SMEReflect() throws Exception {
      this.m_Enchantmentadvancedmending_handler = ReflectUtil.findMethod(this.c_Enchantmentadvancedmending, "onXP");
      this.c_EnchantmentSwiper = Class.forName("com.Shultrea.Rin.Ench0_4_0.EnchantmentSwiper");
      this.m_EnchantmentSwiper_handler = ReflectUtil.findMethod(this.c_EnchantmentSwiper, "HandleEnchant");
      this.c_EnchantmentCurseofPossession = Class.forName("com.Shultrea.Rin.Ench0_4_0.EnchantmentCurseofPossession");
      this.m_EnchantmentCurseofPossession_onExist = ReflectUtil.findMethod(this.c_EnchantmentCurseofPossession, "onExist");
      this.m_EnchantmentCurseofPossession_toss = ReflectUtil.findMethod(this.c_EnchantmentCurseofPossession, "toss");
      this.c_EnchantmentEmpoweredDefence = Class.forName("com.Shultrea.Rin.Ench0_3_0.EnchantmentEmpoweredDefence");
      this.m_EnchantmentEmpoweredDefence_handler = ReflectUtil.findMethod(this.c_EnchantmentEmpoweredDefence, "EmpoweredDefenceEvent");
      this.c_EnchantmentEvasion = Class.forName("com.Shultrea.Rin.Ench0_4_5.EnchantmentEvasion");
      this.m_EnchantmentEvasion_handler = ReflectUtil.findMethod(this.c_EnchantmentEvasion, "HandleEnchant");
      this.c_EnchantmentFreezing = Class.forName("com.Shultrea.Rin.Ench0_4_0.EnchantmentFreezing");
      this.m_EnchantmentFreezing_handler = ReflectUtil.findMethod(this.c_EnchantmentFreezing, "onEntityDamaged");
      this.c_EnchantmentParry = Class.forName("com.Shultrea.Rin.Ench0_2_0.EnchantmentParry");
      this.m_EnchantmentParry_handler = ReflectUtil.findMethod(this.c_EnchantmentParry, "HandleEnchant");
      this.c_EnchantmentFrenzy = Class.forName("com.Shultrea.Rin.Ench0_4_5.EnchantmentFrenzy");
      this.m_EnchantmentFrenzy_handler = ReflectUtil.findMethod(this.c_EnchantmentFrenzy, "onAttack");
      this.c_EnchantmentUpgradedPotentials = Class.forName("com.Shultrea.Rin.Ench0_4_0.EnchantmentUpgradedPotentials");
      this.m_EnchantmentUpgradedPotentials_handler = ReflectUtil.findMethod(this.c_EnchantmentUpgradedPotentials, "onAnvilAttach");
   }
}
