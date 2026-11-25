package codersafterdark.compatskills.common.compats.reskillable.playerexpansion.wrapper;

import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.reskillable.api.skill.Skill;
import crafttweaker.annotations.ZenRegister;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenOperator;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("mods.compatskills.Skill")
@ZenRegister
public class CTSkill {
   final Skill skill;

   public CTSkill(Skill skill) {
      this.skill = skill;
   }

   @ZenGetter("key")
   @ZenMethod
   public String getKey() {
      return this.skill.getKey();
   }

   @ZenGetter("name")
   @ZenMethod
   public String getName() {
      return this.skill.getName();
   }

   @ZenGetter("backgroundLocation")
   @ZenMethod
   public String getBackgroundLocation() {
      return this.skill.getBackground().toString();
   }

   @ZenSetter("backgroundLocation")
   @ZenMethod
   public void setBackgroundLocation(String resourceLocation) {
      if (CheckMethods.checkResourceLocation(resourceLocation)) {
         this.skill.setBackground(new ResourceLocation(resourceLocation));
      }

   }

   @ZenGetter("cap")
   @ZenMethod
   public int getCap() {
      return this.skill.getCap();
   }

   @ZenSetter("cap")
   @ZenMethod
   public void setCap(int cap) {
      this.skill.getSkillConfig().setLevelCap(cap);
   }

   @ZenGetter("enabled")
   @ZenMethod
   public boolean getEnabled() {
      return this.skill.isEnabled();
   }

   @ZenSetter("enabled")
   @ZenMethod
   public void setEnabled(boolean enabled) {
      this.skill.getSkillConfig().setEnabled(enabled);
   }

   @ZenGetter("hidden")
   @ZenMethod
   public boolean isHidden() {
      return this.skill.isHidden();
   }

   @ZenSetter("hidden")
   @ZenMethod
   public void setHidden(boolean hidden) {
      this.skill.setHidden(hidden);
   }

   @ZenGetter("levelButton")
   @ZenMethod
   public boolean hasLevelButton() {
      return this.skill.hasLevelButton();
   }

   @ZenSetter("levelButton")
   @ZenMethod
   public void setLevelButton(boolean button) {
      this.skill.getSkillConfig().setLevelButton(button);
   }

   @ZenOperator(OperatorType.COMPARE)
   public int compareTo(CTSkill other) {
      return this.skill.compareTo(other.skill);
   }

   @ZenGetter("skillPointInterval")
   @ZenMethod
   public int getSkillPointInterval() {
      return this.skill.getSkillPointInterval();
   }

   @ZenSetter("skillPointInterval")
   @ZenMethod
   public void setSkillPointInterval(int amount) {
      this.skill.getSkillConfig().setSkillPointInterval(amount);
   }

   @ZenMethod
   public int getLevelUpCost(int level) {
      return this.skill.getLevelUpCost(level);
   }

   public Skill getSkill() {
      return this.skill;
   }

   @ZenGetter("baseLevelCost")
   @ZenMethod
   public int getBaseLevelCost() {
      return this.skill.getSkillConfig().getBaseLevelCost();
   }

   @ZenSetter("baseLevelCost")
   @ZenMethod
   public void setBaseLevelCost(int cost) {
      this.skill.getSkillConfig().setBaseLevelCost(cost);
   }

   @ZenGetter("levelStaggering")
   @ZenMethod
   public String[] getLevelStaggering() {
      List<String> stagger = new ArrayList();
      this.skill.getSkillConfig().getLevelStaggering().forEach((key, value) -> {
         stagger.add(key + "|" + value);
      });
      return (String[])stagger.toArray(new String[0]);
   }

   @ZenSetter("levelStaggering")
   @ZenMethod
   public void setLevelStaggering(String[] stagger) {
      Map<Integer, Integer> configLevelStaggering = (Map)Arrays.stream(stagger).map((string) -> {
         return string.split("\\|");
      }).filter((array) -> {
         return array.length == 2;
      }).map((array) -> {
         return Pair.of(array[0], array[1]);
      }).map((pair) -> {
         return Pair.of(Integer.valueOf(Integer.parseInt((String)pair.getKey())), Integer.valueOf(Integer.parseInt((String)pair.getValue())));
      }).collect(Collectors.toMap(Pair::getKey, Pair::getValue));
      Map<Integer, Integer> levelStaggering = new HashMap();
      int lastLevel = this.skill.getSkillConfig().getBaseLevelCost();

      for(int i = 1; i < this.skill.getSkillConfig().getLevelCap(); ++i) {
         if (configLevelStaggering.containsKey(Integer.valueOf(i))) {
            lastLevel = ((Integer)configLevelStaggering.get(Integer.valueOf(i))).intValue();
         }

         levelStaggering.put(Integer.valueOf(i), Integer.valueOf(lastLevel));
      }

      this.skill.getSkillConfig().setLevelStaggering(levelStaggering);
   }
}
