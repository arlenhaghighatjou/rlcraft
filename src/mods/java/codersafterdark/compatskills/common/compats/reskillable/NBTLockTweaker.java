package codersafterdark.compatskills.common.compats.reskillable;

import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.data.GenericNBTLockKey;
import codersafterdark.reskillable.api.data.ModLockKey;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.base.LevelLockHandler;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.mc1120.data.NBTConverter;
import net.minecraft.nbt.NBTTagCompound;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly("crafttweaker")
@ZenClass("mods.compatskills.NBTLock")
@ZenRegister
public class NBTLockTweaker {
   public NBTLockTweaker() {
   }

   @ZenMethod
   public static void addModNBTLock(String modId, IData tag, String... locked) {
      if (ReskillableCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new NBTLockTweaker.AddModNBTLock(modId, tag, locked));
      }

   }

   @ZenMethod
   public static void addGenericNBTLock(IData tag, String... locked) {
      if (ReskillableCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new NBTLockTweaker.AddGenericNBTLock(tag, locked));
      }

   }

   private static class AddGenericNBTLock implements IAction {
      private final IData data;
      private final String[] requirements;

      private AddGenericNBTLock(IData data, String... requirements) {
         this.data = data;
         this.requirements = requirements;
      }

      public void apply() {
         if (CheckMethods.checkValidNBTTagCompound(this.data) & CheckMethods.checkStringArray(this.requirements)) {
            RequirementHolder holder = RequirementHolder.fromStringList(this.requirements);
            LevelLockHandler.addLockByKey(new GenericNBTLockKey((NBTTagCompound)NBTConverter.from(this.data)), holder);
         }

      }

      public String describe() {
         return "Adding Generic NBT lock: " + this.data + " to Requirements: " + Utils.formatRequirements(this.requirements);
      }
   }

   private static class AddModNBTLock implements IAction {
      private final String modID;
      private final IData data;
      private final String[] requirements;

      private AddModNBTLock(String modId, IData tag, String... locked) {
         this.modID = modId;
         this.data = tag;
         this.requirements = locked;
      }

      public void apply() {
         if (CheckMethods.checkModLoaded(this.modID) & CheckMethods.checkValidNBTTagCompound(this.data) & CheckMethods.checkStringArray(this.requirements)) {
            RequirementHolder holder = RequirementHolder.fromStringList(this.requirements);
            LevelLockHandler.addLockByKey(new ModLockKey(this.modID, (NBTTagCompound)NBTConverter.from(this.data)), holder);
         }

      }

      public String describe() {
         return "Adding NBT lock: " + this.data + " for Mod: " + this.modID + " to Requirements: " + Utils.formatRequirements(this.requirements);
      }
   }
}
