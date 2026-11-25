package com.oblivioussp.spartanweaponry.compat.rlcombat;

import bettercombat.mod.event.RLCombatSweepEvent;
import bettercombat.mod.event.RLCombatModifyDamageEvent.Post;
import com.oblivioussp.spartanweaponry.api.IWeaponPropertyContainer;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import com.oblivioussp.spartanweaponry.util.EntityDamageSourceArmorPiercing;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandlerRLCombat {
   public EventHandlerRLCombat() {
   }

   @SubscribeEvent
   public void onModifyDamage(Post ev) {
      ItemStack weaponStack = ev.getStack();
      if (weaponStack.getItem() instanceof IWeaponPropertyContainer) {
         IWeaponPropertyContainer weaponItem = (IWeaponPropertyContainer)weaponStack.getItem();
         WeaponProperty armourPiercingProp = weaponItem.getFirstWeaponPropertyWithType("armour_piercing");
         if (armourPiercingProp != null) {
            boolean isPlayer = ev.getEntityPlayer() != null;
            DamageSource source = new EntityDamageSourceArmorPiercing(isPlayer ? "player" : "mob", (Entity)(isPlayer ? ev.getEntityPlayer() : ev.getEntityLiving()), armourPiercingProp.getMagnitude() / 100.0F);
            ev.setDamageSource(source);
         }

         if (ConfigHandler.damageBonusRidingCheckSpeed && weaponItem.getFirstWeaponPropertyWithType("extra_damage_riding") != null) {
            Vec3d motionVec = new Vec3d(ev.getMotionX(), ev.getMotionY() + 0.0784000015258789D, ev.getMotionZ());
            ev.getEntityPlayer().getEntityData().setFloat("spartanweaponry_RidingSpeed", (float)motionVec.lengthVector());
         }
      }

   }

   @SubscribeEvent
   public void onSweepAttack(RLCombatSweepEvent ev) {
      ItemStack weaponStack = ev.getItemStack();
      if (weaponStack.getItem() instanceof IWeaponPropertyContainer) {
         IWeaponPropertyContainer weaponItem = (IWeaponPropertyContainer)weaponStack.getItem();
         WeaponProperty sweepProp = weaponItem.getFirstWeaponPropertyWithType("sweep_damage");
         if (sweepProp != null) {
            if (sweepProp.getLevel() > 1) {
               float modifier = sweepProp.getMagnitude() / 100.0F;
               float directDmg = ev.getBaseDamage() * modifier;
               modifier = modifier * ((directDmg - 1.0F) / directDmg);
               ev.setSweepModifier(modifier);
            }
         } else {
            ev.setDoSweep(false);
         }
      }

   }
}
