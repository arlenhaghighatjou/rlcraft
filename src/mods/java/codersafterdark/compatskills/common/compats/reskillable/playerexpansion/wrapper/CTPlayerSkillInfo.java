package codersafterdark.compatskills.common.compats.reskillable.playerexpansion.wrapper;

import codersafterdark.reskillable.api.data.PlayerSkillInfo;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenRegister
@ZenClass("mods.compatskills.PlayerSkillInfo")
public class CTPlayerSkillInfo {
   private final PlayerSkillInfo playerSkillInfo;

   public CTPlayerSkillInfo(PlayerSkillInfo playerSkillInfo) {
      this.playerSkillInfo = playerSkillInfo;
   }

   @ZenGetter("level")
   @ZenMethod
   public int getLevel() {
      return this.playerSkillInfo.getLevel();
   }

   @ZenSetter("level")
   @ZenMethod
   public void setLevel(int level) {
      this.playerSkillInfo.setLevel(level);
   }

   @ZenGetter("skillPoints")
   @ZenMethod
   public int getSkillPoints() {
      return this.playerSkillInfo.getSkillPoints();
   }

   @ZenGetter("levelUpCost")
   @ZenMethod
   public int getLevelUpCost() {
      return this.playerSkillInfo.getLevelUpCost();
   }

   @ZenGetter("rank")
   @ZenMethod
   public int getRank() {
      return this.playerSkillInfo.getRank();
   }

   @ZenGetter("capped")
   @ZenMethod
   public boolean isCapped() {
      return this.playerSkillInfo.isCapped();
   }

   @ZenGetter("skill")
   @ZenMethod
   public CTSkill getSkill() {
      return new CTSkill(this.playerSkillInfo.skill);
   }

   @ZenMethod
   public void levelUp() {
      this.playerSkillInfo.levelUp();
   }

   @ZenMethod
   public void respec() {
      this.playerSkillInfo.respec();
   }

   @ZenMethod
   public void unlock(CTUnlockable ctUnlockable, IPlayer player) {
      this.playerSkillInfo.unlock(ctUnlockable.unlockable, CraftTweakerMC.getPlayer(player));
   }

   @ZenMethod
   public void lock(CTUnlockable ctUnlockable, IPlayer player) {
      this.playerSkillInfo.lock(ctUnlockable.unlockable, CraftTweakerMC.getPlayer(player));
   }

   @ZenMethod
   public boolean isUnlocked(CTUnlockable ctUnlockable) {
      return this.playerSkillInfo.isUnlocked(ctUnlockable.unlockable);
   }
}
