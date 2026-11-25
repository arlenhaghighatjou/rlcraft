package codersafterdark.compatskills.common.compats.thaumcraft;

import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.common.compats.thaumcraft.keys.KnowledgeKey;
import codersafterdark.compatskills.common.compats.thaumcraft.keys.ResearchKey;
import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.data.RequirementHolder;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;

@ModOnly("thaumcraft")
@ZenClass("mods.compatskills.Thaumcraft")
@ZenRegister
public class ThaumcraftTweaker {
   public ThaumcraftTweaker() {
   }

   @ZenMethod
   public static void addResearchLock(String researchKey, String... requirements) {
      if (ThaumcraftCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new ThaumcraftTweaker.AddResearchLock(researchKey, requirements));
      }

   }

   @ZenMethod
   public static void addKnowledgeLock(String categoryName, String knowledgeType, String... requirements) {
      if (ThaumcraftCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new ThaumcraftTweaker.AddKnowledgeLock(categoryName, knowledgeType, requirements));
      }

   }

   private static class AddKnowledgeLock implements IAction {
      private final String categoryName;
      private final String knowledgeType;
      private final String[] requirements;

      private AddKnowledgeLock(String categoryName, String knowledgeType, String... requirements) {
         this.categoryName = categoryName;
         this.knowledgeType = knowledgeType;
         this.requirements = requirements;
      }

      public void apply() {
         if (CheckMethods.checkCategory(this.categoryName) & CheckMethods.checkKnowledgeType(this.knowledgeType) & CheckMethods.checkStringArray(this.requirements)) {
            ResearchCategory category = ResearchCategories.getResearchCategory(this.categoryName);
            if (category != null) {
               ThaumcraftCompatHandler.addThaumcraftLock(new KnowledgeKey(category, this.knowledgeType), RequirementHolder.fromStringList(this.requirements));
            }
         }

      }

      public String describe() {
         return "Added Knowledge Lock for category: + " + this.categoryName + " of knowledge type: " + this.knowledgeType + " With Requirements: " + Utils.formatRequirements(this.requirements);
      }
   }

   private static class AddResearchLock implements IAction {
      private final String researchKey;
      private final String[] requirements;

      private AddResearchLock(String researchKey, String... requirements) {
         this.researchKey = researchKey;
         this.requirements = requirements;
      }

      public void apply() {
         if (CheckMethods.checkString(this.researchKey) & CheckMethods.checkStringArray(this.requirements)) {
            ThaumcraftCompatHandler.addThaumcraftLock(new ResearchKey(this.researchKey), RequirementHolder.fromStringList(this.requirements));
         }

      }

      public String describe() {
         return "Added Research Lock for: " + this.researchKey + " With Requirements: " + Utils.formatRequirements(this.requirements);
      }
   }
}
