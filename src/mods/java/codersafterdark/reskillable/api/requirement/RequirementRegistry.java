package codersafterdark.reskillable.api.requirement;

import codersafterdark.reskillable.Reskillable;
import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.skill.Skill;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.ResourceLocation;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class RequirementRegistry {
   private Map<String, RequirementFunction<String, Requirement>> requirementHandlers = Maps.newHashMap();

   public RequirementRegistry() {
   }

   public Requirement getRequirement(String requirementString) {
      String[] requirements = requirementString.split("\\|");
      Requirement requirement = null;

      try {
         if (requirements.length == 2) {
            String requirementType = requirements[0];
            String requirementInputs = requirements[1];
            if (this.requirementHandlers.containsKey(requirementType)) {
               requirement = (Requirement)((RequirementFunction)this.requirementHandlers.get(requirementType)).apply(requirementInputs);
            } else {
               Skill skill = (Skill)ReskillableRegistries.SKILLS.getValue(new ResourceLocation(requirementType));
               if (skill == null) {
                  throw new RequirementException("Skill '" + requirementType + "' not found.");
               }

               try {
                  int level = Integer.parseInt(requirementInputs);
                  if (level <= 1) {
                     throw new RequirementException("Level must be greater than 1. Found: '" + level + "'.");
                  }

                  requirement = new SkillRequirement(skill, level);
               } catch (NumberFormatException var8) {
                  throw new RequirementException("Invalid level '" + requirementInputs + "' for skill '" + skill.getName() + "'.");
               }
            }
         } else if (requirements.length > 0) {
            String requirementType = requirements[0];
            if (this.requirementHandlers.containsKey(requirementType)) {
               int pos = requirementType.length() + 1;
               String input = pos > requirementString.length() ? "" : requirementString.substring(pos);
               requirement = (Requirement)((RequirementFunction)this.requirementHandlers.get(requirementType)).apply(input);
            }
         }
      } catch (RequirementException var9) {
         Reskillable.logger.log(Level.ERROR, "Requirement Format Exception (" + requirementString + "): " + var9.getMessage());
         return null;
      }

      if (requirement == null) {
         Reskillable.logger.log(Level.ERROR, "No Requirement found for Input: " + requirementString);
      } else if (!requirement.isEnabled()) {
         Reskillable.logger.log(Level.ERROR, "Disabled Requirement for Input: " + requirementString);
         return null;
      }

      return requirement;
   }

   public void addRequirementHandler(String identity, RequirementFunction<String, Requirement> creator) {
      this.requirementHandlers.put(identity, creator);
   }
}
