package com.oblivioussp.spartanweaponry.api;

import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponPropertyDamageAbsorb;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponPropertyExtraDamage;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponPropertyKnockback;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponPropertyNausea;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponPropertySweepDamage;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponPropertyTwoHanded;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponPropertyVersatile;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponPropertyWithMagnitude;

public class WeaponProperties {
   public static final String PROPERTY_TYPE_THROWABLE = "throwable";
   public static final String PROPERTY_TYPE_BLOCK_MELEE = "block_melee";
   public static final String PROPERTY_TYPE_TWO_HANDED = "two_handed";
   public static final String PROPERTY_TYPE_EXTRA_DAMAGE = "extra_damage";
   public static final String PROPERTY_TYPE_EXTRA_DAMAGE_CHEST = "extra_damage_chest";
   public static final String PROPERTY_TYPE_EXTRA_DAMAGE_RIDING = "extra_damage_riding";
   public static final String PROPERTY_TYPE_EXTRA_DAMAGE_THROWN = "extra_damage_thrown";
   public static final String PROPERTY_TYPE_EXTRA_DAMAGE_UNARMOURED = "extra_damage_unarmoured";
   public static final String PROPERTY_TYPE_EXTRA_DAMAGE_UNDEAD = "extra_damage_undead";
   public static final String PROPERTY_TYPE_EXTRA_DAMAGE_BACKSTAB = "extra_damage_backstab";
   public static final String PROPERTY_TYPE_DAMAGE_ABSORB = "damage_absorb";
   public static final String PROPERTY_TYPE_REACH = "reach";
   public static final String PROPERTY_TYPE_SWEEP_DAMAGE = "sweep_damage";
   public static final String PROPERTY_TYPE_KNOCKBACK = "knockback";
   public static final String PROPERTY_TYPE_NAUSEA = "nausea";
   public static final String PROPERTY_TYPE_ARMOUR_PIERCING = "armour_piercing";
   public static final String PROPERTY_TYPE_SHIELD_BREACH = "shield_breach";
   public static final String PROPERTY_TYPE_VERSATILE = "versatile";
   public static final String PROPERTY_TYPE_QUICK_STRIKE = "quick_strike";
   public static final WeaponProperty THROWABLE = new WeaponProperty("throwable", "spartanweaponry");
   public static final WeaponProperty BLOCK_MELEE = new WeaponProperty("block_melee", "spartanweaponry");
   public static final WeaponProperty TWO_HANDED_1 = new WeaponPropertyTwoHanded("two_handed", "spartanweaponry", 1, 0.5F);
   public static final WeaponProperty TWO_HANDED_2 = new WeaponPropertyTwoHanded("two_handed", "spartanweaponry", 2, 0.75F);
   public static final WeaponProperty EXTRA_DAMAGE_2_CHEST = new WeaponPropertyExtraDamage("extra_damage_chest", "spartanweaponry", 2.0F);
   public static final WeaponProperty EXTRA_DAMAGE_2_RIDING = new WeaponPropertyExtraDamage("extra_damage_riding", "spartanweaponry", 2.0F);
   public static final WeaponProperty EXTRA_DAMAGE_2_THROWN = new WeaponPropertyExtraDamage("extra_damage_thrown", "spartanweaponry", 2.0F);
   public static final WeaponProperty EXTRA_DAMAGE_3_THROWN = new WeaponPropertyExtraDamage("extra_damage_thrown", "spartanweaponry", 3.0F);
   public static final WeaponProperty EXTRA_DAMAGE_3_NO_ARMOUR = new WeaponPropertyExtraDamage("extra_damage_unarmoured", "spartanweaponry", 3.0F);
   public static final WeaponProperty EXTRA_DAMAGE_50P_UNDEAD = new WeaponPropertyExtraDamage("extra_damage_undead", "spartanweaponry", 1.5F);
   public static final WeaponProperty EXTRA_DAMAGE_BACKSTAB = new WeaponPropertyExtraDamage("extra_damage_backstab", "spartanweaponry", 3.0F);
   public static final WeaponProperty DAMAGE_ABSORB_25 = new WeaponPropertyDamageAbsorb("damage_absorb", "spartanweaponry", 0.25F);
   public static final WeaponProperty REACH_1 = new WeaponProperty("reach", "spartanweaponry", 1, 6.0F);
   public static final WeaponProperty REACH_2 = new WeaponProperty("reach", "spartanweaponry", 2, 7.0F);
   public static final WeaponProperty SWEEP_DAMAGE_NORMAL = new WeaponPropertySweepDamage("sweep_damage", "spartanweaponry", 1, 1.0F);
   public static final WeaponProperty SWEEP_DAMAGE_HALF = new WeaponPropertySweepDamage("sweep_damage", "spartanweaponry", 2, 50.0F);
   public static final WeaponProperty SWEEP_DAMAGE_FULL = new WeaponPropertySweepDamage("sweep_damage", "spartanweaponry", 3, 100.0F);
   public static final WeaponProperty KNOCKBACK = new WeaponPropertyKnockback("knockback", "spartanweaponry");
   public static final WeaponProperty NAUSEA = new WeaponPropertyNausea("nausea", "spartanweaponry", 5.0F);
   public static final WeaponProperty ARMOUR_PIERCING_50 = new WeaponPropertyWithMagnitude("armour_piercing", "spartanweaponry", 50.0F);
   public static final WeaponProperty SHIELD_BREACH = new WeaponProperty("shield_breach", "spartanweaponry");
   public static final WeaponProperty VERSATILE = new WeaponPropertyVersatile("versatile", "spartanweaponry");
   public static final WeaponProperty QUICK_STRIKE = new WeaponProperty("quick_strike", "spartanweaponry");

   public WeaponProperties() {
   }
}
