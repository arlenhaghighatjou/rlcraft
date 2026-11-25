package codersafterdark.reskillable.api;

import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.requirement.AdvancementRequirement;
import codersafterdark.reskillable.api.requirement.NoneRequirement;
import codersafterdark.reskillable.api.requirement.RequirementException;
import codersafterdark.reskillable.api.requirement.RequirementFunction;
import codersafterdark.reskillable.api.requirement.RequirementRegistry;
import codersafterdark.reskillable.api.requirement.TraitRequirement;
import codersafterdark.reskillable.api.requirement.UnobtainableRequirement;
import codersafterdark.reskillable.api.requirement.logic.LogicParser;
import codersafterdark.reskillable.api.skill.SkillConfig;
import codersafterdark.reskillable.api.unlockable.Unlockable;
import codersafterdark.reskillable.api.unlockable.UnlockableConfig;
import java.util.Objects;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class ReskillableAPI {
   private static ReskillableAPI instance;
   private IModAccess modAccess;
   private RequirementRegistry requirementRegistry;

   public ReskillableAPI(IModAccess modAccess) {
      this.modAccess = modAccess;
      this.requirementRegistry = new RequirementRegistry();
      this.requirementRegistry.addRequirementHandler("adv", (input) -> {
         return new AdvancementRequirement(new ResourceLocation(input));
      });
      this.requirementRegistry.addRequirementHandler("trait", (input) -> {
         Unlockable unlockable = (Unlockable)ReskillableRegistries.UNLOCKABLES.getValue(new ResourceLocation(input));
         if (unlockable == null) {
            throw new RequirementException("Unlockable '" + input + "' not found.");
         } else {
            return new TraitRequirement(unlockable);
         }
      });
      this.requirementRegistry.addRequirementHandler("unobtainable", (input) -> {
         return new UnobtainableRequirement();
      });
      this.requirementRegistry.addRequirementHandler("none", (input) -> {
         return new NoneRequirement();
      });
      this.requirementRegistry.addRequirementHandler("not", LogicParser::parseNOT);
      this.requirementRegistry.addRequirementHandler("and", LogicParser::parseAND);
      this.requirementRegistry.addRequirementHandler("nand", LogicParser::parseNAND);
      this.requirementRegistry.addRequirementHandler("or", LogicParser::parseOR);
      this.requirementRegistry.addRequirementHandler("nor", LogicParser::parseNOR);
      this.requirementRegistry.addRequirementHandler("xor", LogicParser::parseXOR);
      this.requirementRegistry.addRequirementHandler("xnor", LogicParser::parseXNOR);
   }

   public static ReskillableAPI getInstance() {
      return (ReskillableAPI)Objects.requireNonNull(instance, "Calling Reskillable API Instance before it's creation");
   }

   public static void setInstance(ReskillableAPI reskillableAPI) {
      instance = reskillableAPI;
   }

   public SkillConfig getSkillConfig(ResourceLocation name) {
      return this.modAccess.getSkillConfig(name);
   }

   public UnlockableConfig getTraitConfig(ResourceLocation name, int x, int y, int cost, String[] defaultRequirements) {
      return this.modAccess.getUnlockableConfig(name, x, y, cost, defaultRequirements);
   }

   public void syncPlayerData(EntityPlayer entityPlayer, PlayerData playerData) {
      this.modAccess.syncPlayerData(entityPlayer, playerData);
   }

   public AdvancementProgress getAdvancementProgress(EntityPlayer entityPlayer, Advancement advancement) {
      return this.modAccess.getAdvancementProgress(entityPlayer, advancement);
   }

   public RequirementRegistry getRequirementRegistry() {
      return this.requirementRegistry;
   }

   public void log(Level warn, String s) {
      this.modAccess.log(warn, s);
   }
}
