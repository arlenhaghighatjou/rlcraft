package codersafterdark.compatskills.common.compats.reskillable.customcontent;

import codersafterdark.compatskills.common.compats.reskillable.ReskillableCompatHandler;
import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.skill.Skill;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.formatting.IFormattedText;
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
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("mods.compatskills.SkillCreator")
@ZenRegister
public class CrTSkill extends Skill {
   @ZenProperty
   public IFormattedText name;

   public CrTSkill(ResourceLocation name, ResourceLocation background) {
      super(name, background);
      ReskillableRegistries.SKILLS.register(this);
   }

   private static CrTSkill createSkill(ResourceLocation name, ResourceLocation background) {
      CrTSkill customSkill = new CrTSkill(name, background);
      if (ReskillableRegistries.SKILLS.containsKey(name)) {
         Skill value = (Skill)ReskillableRegistries.SKILLS.getValue(name);
         if (value instanceof CrTSkill) {
            customSkill = (CrTSkill)value;
            if (!background.equals(customSkill.getBackground())) {
               customSkill.background = background;
               CraftTweakerAPI.logInfo("Loaded Skill: " + name + " - Updated Background: " + background);
            } else {
               CraftTweakerAPI.logInfo("Created or Loaded Skill: " + name + " - With Background: " + background);
            }
         }
      }

      return customSkill;
   }

   @ZenMethod
   public static CrTSkill createSkill(String name, String backGroundLocation) {
      return ReskillableCompatHandler.ENABLED & CheckMethods.checkString(name) & CheckMethods.checkString(backGroundLocation) ? createSkill(new ResourceLocation("compatskills", name), new ResourceLocation(backGroundLocation)) : null;
   }

   @ZenMethod
   public static CrTSkill createNewSkill(String nameLocation, String backGroundLocation) {
      return ReskillableCompatHandler.ENABLED & CheckMethods.checkString(nameLocation) & CheckMethods.checkString(backGroundLocation) ? createSkill(new ResourceLocation(nameLocation), new ResourceLocation(backGroundLocation)) : null;
   }

   @ZenGetter("levelCap")
   @ZenMethod
   public int getLevelCap() {
      return this.getCap();
   }

   @ZenSetter("levelCap")
   @ZenMethod
   public void setLevelCap(int cap) {
      this.skillConfig.setLevelCap(cap);
   }

   @ZenGetter("enabled")
   @ZenMethod
   public boolean getEnabled() {
      return this.isEnabled();
   }

   @ZenSetter("enabled")
   @ZenMethod
   public void setEnabled(boolean enabled) {
      this.skillConfig.setEnabled(enabled);
   }

   @ZenGetter("levelStaggering")
   @ZenMethod
   public String[] getLevelStaggering() {
      List<String> stagger = new ArrayList();
      this.skillConfig.getLevelStaggering().forEach((key, value) -> {
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
      int lastLevel = this.skillConfig.getBaseLevelCost();

      for(int i = 1; i < this.skillConfig.getLevelCap(); ++i) {
         if (configLevelStaggering.containsKey(Integer.valueOf(i))) {
            lastLevel = ((Integer)configLevelStaggering.get(Integer.valueOf(i))).intValue();
         }

         levelStaggering.put(Integer.valueOf(i), Integer.valueOf(lastLevel));
      }

      this.skillConfig.setLevelStaggering(levelStaggering);
   }

   @ZenSetter("skillPointInterval")
   @ZenMethod
   public void setSkillPointInterval(int amount) {
      this.skillConfig.setSkillPointInterval(amount);
   }

   @ZenGetter("baseLevelCost")
   @ZenMethod
   public int getBaseLevelCost() {
      return this.skillConfig.getBaseLevelCost();
   }

   @ZenSetter("baseLevelCost")
   @ZenMethod
   public void setBaseLevelCost(int cost) {
      this.skillConfig.setBaseLevelCost(cost);
   }

   @ZenGetter("hidden")
   @ZenMethod
   public boolean isHidden() {
      return super.isHidden();
   }

   @ZenSetter("hidden")
   @ZenMethod
   public void setHidden(boolean hidden) {
      super.setHidden(hidden);
   }

   @ZenGetter("levelButton")
   @ZenMethod
   public boolean hasLevelButton() {
      return super.hasLevelButton();
   }

   @ZenSetter("levelButton")
   @ZenMethod
   public void setLevelButton(boolean button) {
      this.skillConfig.setLevelButton(button);
   }

   public String getName() {
      return this.name == null ? super.getName() : this.name.getText();
   }

   @ZenMethod
   public void setRankIcon(int rank, String resourceLocation) {
      if (CheckMethods.checkResourceLocation(resourceLocation)) {
         this.setCustomSprite(rank, new ResourceLocation(resourceLocation));
      }

   }

   @ZenMethod
   public void removeRankIcon(int rank) {
      this.removeCustomSprite(rank);
   }
}
