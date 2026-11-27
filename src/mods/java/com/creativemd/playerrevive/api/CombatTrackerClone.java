package com.creativemd.playerrevive.api;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.util.CombatEntry;
import net.minecraft.util.CombatTracker;

public class CombatTrackerClone {
   private final List<CombatEntry> combatEntries = Lists.newArrayList();
   private int lastDamageTime;
   private int combatStartTime;
   private int combatEndTime;
   private boolean inCombat;
   private boolean takingDamage;
   private String fallSuffix;

   public CombatTrackerClone(CombatTracker tracker) {
      this.combatEntries.addAll(tracker.combatEntries);
      this.lastDamageTime = tracker.lastDamageTime;
      this.combatStartTime = tracker.combatStartTime;
      this.combatEndTime = tracker.combatEndTime;
      this.inCombat = tracker.inCombat;
      this.takingDamage = tracker.takingDamage;
      this.fallSuffix = tracker.fallSuffix;

   }

   public void overwriteTracker(CombatTracker tracker) {
      List<CombatEntry> entries = tracker.combatEntries;
      entries.clear();
      entries.addAll(this.combatEntries);
      tracker.lastDamageTime = this.lastDamageTime;
      tracker.combatStartTime = this.combatStartTime;
      tracker.combatEndTime = this.combatEndTime;
      tracker.inCombat = this.inCombat;
      tracker.takingDamage = this.takingDamage;
      tracker.fallSuffix = this.fallSuffix;

   }
}
