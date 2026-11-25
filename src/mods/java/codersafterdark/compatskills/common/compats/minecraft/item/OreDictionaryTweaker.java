package codersafterdark.compatskills.common.compats.minecraft.item;

import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.common.compats.minecraft.MinecraftCompatHandler;
import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.data.RequirementHolder;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.oredict.IOreDictEntry;
import crafttweaker.mc1120.data.NBTConverter;
import net.minecraft.nbt.NBTTagCompound;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.compatskills.OreDictLock")
@ZenRegister
public class OreDictionaryTweaker {
   public OreDictionaryTweaker() {
   }

   @ZenMethod
   public static void addOreDictLock(IOreDictEntry entry, String... locked) {
      if (MinecraftCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new OreDictionaryTweaker.AddOreDict(entry, locked));
      }

   }

   @ZenMethod
   public static void addNBTOreDictLock(IOreDictEntry entry, IData tag, String... locked) {
      if (MinecraftCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new OreDictionaryTweaker.AddOreDictNBT(entry, tag, locked));
      }

   }

   private static class AddOreDict implements IAction {
      private final IOreDictEntry entry;
      private final String[] requirements;

      private AddOreDict(IOreDictEntry entry, String... requirements) {
         this.entry = entry;
         this.requirements = requirements;
      }

      public void apply() {
         if (CheckMethods.checkIOreDictEntry(this.entry) & CheckMethods.checkStringArray(this.requirements)) {
            MinecraftCompatHandler.addMCLock(new OreDictLock(this.entry.getName()), RequirementHolder.fromStringList(this.requirements));
         }

      }

      public String describe() {
         return "Setting the requirement of Ore Dictionary Entry: " + this.entry + " to: " + Utils.formatRequirements(this.requirements);
      }
   }

   private static class AddOreDictNBT implements IAction {
      private final IOreDictEntry entry;
      private final IData data;
      private final String[] requirements;

      private AddOreDictNBT(IOreDictEntry entry, IData tag, String... locked) {
         this.entry = entry;
         this.data = tag;
         this.requirements = locked;
      }

      public void apply() {
         if (CheckMethods.checkIOreDictEntry(this.entry) & CheckMethods.checkValidNBTTagCompound(this.data) & CheckMethods.checkStringArray(this.requirements)) {
            RequirementHolder holder = RequirementHolder.fromStringList(this.requirements);
            MinecraftCompatHandler.addMCLock(new OreDictLock(this.entry.getName(), (NBTTagCompound)NBTConverter.from(this.data)), holder);
         }

      }

      public String describe() {
         return "Adding NBT lock: " + this.data + " for Ore Dictionary Entry: " + this.entry + " with Requirements: " + Utils.formatRequirements(this.requirements);
      }
   }
}
