package codersafterdark.compatskills.common.compats.astralsorcery;

import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.common.compats.astralsorcery.requirements.AstralTierRequirement;
import codersafterdark.compatskills.common.compats.astralsorcery.requirements.AttunedRequirement;
import codersafterdark.compatskills.common.compats.astralsorcery.requirements.ConstellationRequirement;
import codersafterdark.compatskills.utils.CompatModuleBase;
import codersafterdark.reskillable.api.ReskillableAPI;
import codersafterdark.reskillable.api.requirement.RequirementFunction;
import codersafterdark.reskillable.api.requirement.RequirementRegistry;

public class AstralCompatHandler extends CompatModuleBase {
   public static boolean ENABLED;

   public AstralCompatHandler() {
   }

   public void preInit() {
      ENABLED = true;
      RequirementRegistry registry = ReskillableAPI.getInstance().getRequirementRegistry();
      registry.addRequirementHandler("constellation", ConstellationRequirement::fromString);
      registry.addRequirementHandler("attuned", AttunedRequirement::fromString);
      registry.addRequirementHandler("astral_tier", AstralTierRequirement::fromString);
   }

   public void loadComplete() {
      if (CompatSkills.craftweakerLoaded) {
         CompatSkills.registerCommand(new AstralConstellationDump());
      }

   }
}
