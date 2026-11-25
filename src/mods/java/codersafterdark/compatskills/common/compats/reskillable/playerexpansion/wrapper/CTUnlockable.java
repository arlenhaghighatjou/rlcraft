package codersafterdark.compatskills.common.compats.reskillable.playerexpansion.wrapper;

import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.api.unlockable.Unlockable;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("mods.compatskills.Unlockable")
@ZenRegister
public class CTUnlockable {
   final Unlockable unlockable;

   public CTUnlockable(Unlockable unlockable) {
      this.unlockable = unlockable;
   }

   @ZenGetter("parent")
   @ZenMethod
   public CTSkill getParent() {
      return new CTSkill(this.unlockable.getParentSkill());
   }

   @ZenSetter("requirements")
   @ZenMethod
   public void setRequirements(String... requirements) {
      if (CheckMethods.checkOptionalRequirements(requirements)) {
         this.unlockable.getUnlockableConfig().setRequirementHolder(RequirementHolder.fromStringList(requirements));
      }

   }

   @ZenGetter("name")
   @ZenMethod
   public String getName() {
      return this.unlockable.getName();
   }

   @ZenGetter("getDescription")
   @ZenMethod
   public String getDescription() {
      return this.unlockable.getDescription();
   }

   @ZenGetter("spikes")
   @ZenMethod
   public boolean hasSpikes() {
      return this.unlockable.hasSpikes();
   }

   @ZenGetter("cost")
   @ZenMethod
   public int getCost() {
      return this.unlockable.getCost();
   }

   @ZenSetter("cost")
   @ZenMethod
   public void setCost(int cost) {
      this.unlockable.getUnlockableConfig().setCost(cost);
   }

   @ZenGetter("icon")
   @ZenMethod
   public String retrieveIcon() {
      return this.unlockable.getIcon().toString();
   }

   @ZenGetter("x")
   @ZenMethod
   public int getX() {
      return this.unlockable.getX();
   }

   @ZenSetter("x")
   @ZenMethod
   public void setX(int x) {
      this.unlockable.getUnlockableConfig().setX(x);
   }

   @ZenGetter("y")
   @ZenMethod
   public int getY() {
      return this.unlockable.getY();
   }

   @ZenSetter("y")
   @ZenMethod
   public void setY(int y) {
      this.unlockable.getUnlockableConfig().setY(y);
   }

   @ZenGetter("enabled")
   @ZenMethod
   public boolean getEnabled() {
      return this.unlockable.isEnabled();
   }

   @ZenSetter("enabled")
   @ZenMethod
   public void setEnabled(boolean enabled) {
      this.unlockable.getUnlockableConfig().setEnabled(enabled);
   }

   public Unlockable getUnlockable() {
      return this.unlockable;
   }
}
