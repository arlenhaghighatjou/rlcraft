package net.silentchaos512.scalinghealth.lib.module;

import java.util.Calendar;
import net.minecraftforge.common.config.Configuration;

public class ModuleAprilTricks {
   public static ModuleAprilTricks instance = new ModuleAprilTricks();
   public Calendar today;
   private boolean rightDay = this.checkDate();
   boolean moduleEnabled = true;
   boolean forcedOn = false;

   public ModuleAprilTricks() {
   }

   private boolean checkDate() {
      this.today = Calendar.getInstance();
      int year = this.today.get(1);
      int month = this.today.get(2);
      int date = this.today.get(5);
      this.rightDay = month == 3 && date >= 1 && date <= 2;
      return this.rightDay;
   }

   public boolean isEnabled() {
      return this.moduleEnabled || this.forcedOn;
   }

   public boolean isRightDay() {
      return this.rightDay || this.forcedOn;
   }

   public boolean isForcedOn() {
      return this.forcedOn;
   }

   public void loadConfig(Configuration c) {
      String cat = "main.holidays" + "." + "april_trickery";
      c.setCategoryComment(cat, "April Fools event options.");
      this.moduleEnabled = c.getBoolean("Enabled", cat, true, "May cause silly things to happen on certain day(s) in April.");
      this.forcedOn = c.getBoolean("Forced On", cat, false, "I need this in my life 24/7!");
   }
}
