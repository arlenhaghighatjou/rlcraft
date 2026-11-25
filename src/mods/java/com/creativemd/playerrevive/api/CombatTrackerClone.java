package com.creativemd.playerrevive.api;

import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import net.minecraft.util.CombatEntry;
import net.minecraft.util.CombatTracker;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class CombatTrackerClone {
   private static final Field combatEntriesField = ReflectionHelper.findField(CombatTracker.class, "combatEntries", "combatEntries");
   private static final Field lastDamageTimeField = ReflectionHelper.findField(CombatTracker.class, "lastDamageTime", "lastDamageTime");
   private static final Field combatStartTimeField = ReflectionHelper.findField(CombatTracker.class, "combatStartTime", "combatStartTime");
   private static final Field combatEndTimeField = ReflectionHelper.findField(CombatTracker.class, "combatEndTime", "combatEndTime");
   private static final Field inCombatField = ReflectionHelper.findField(CombatTracker.class, "inCombat", "inCombat");
   private static final Field takingDamageField = ReflectionHelper.findField(CombatTracker.class, "takingDamage", "takingDamage");
   private static final Field fallSuffixField = ReflectionHelper.findField(CombatTracker.class, "fallSuffix", "fallSuffix");
   private final List<CombatEntry> combatEntries = Lists.newArrayList();
   private int lastDamageTime;
   private int combatStartTime;
   private int combatEndTime;
   private boolean inCombat;
   private boolean takingDamage;
   private String fallSuffix;

   public CombatTrackerClone(CombatTracker tracker) {
      try {
         this.combatEntries.addAll((Collection)combatEntriesField.get(tracker));
         this.lastDamageTime = lastDamageTimeField.getInt(tracker);
         this.combatStartTime = combatStartTimeField.getInt(tracker);
         this.combatEndTime = combatEndTimeField.getInt(tracker);
         this.inCombat = inCombatField.getBoolean(tracker);
         this.takingDamage = takingDamageField.getBoolean(tracker);
         this.fallSuffix = (String)fallSuffixField.get(tracker);
      } catch (IllegalAccessException | IllegalArgumentException var3) {
         var3.printStackTrace();
      }

   }

   public void overwriteTracker(CombatTracker tracker) {
      try {
         List<CombatEntry> entries = (List)combatEntriesField.get(tracker);
         entries.clear();
         entries.addAll(this.combatEntries);
         lastDamageTimeField.setInt(tracker, this.lastDamageTime);
         combatStartTimeField.setInt(tracker, this.combatStartTime);
         combatEndTimeField.setInt(tracker, this.combatEndTime);
         inCombatField.setBoolean(tracker, this.inCombat);
         takingDamageField.setBoolean(tracker, this.takingDamage);
         fallSuffixField.set(tracker, this.fallSuffix);
      } catch (IllegalAccessException | IllegalArgumentException var3) {
         var3.printStackTrace();
      }

   }
}
