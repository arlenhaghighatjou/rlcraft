package codersafterdark.compatskills.common.compats.dynamicswordskills;

import codersafterdark.reskillable.api.requirement.Requirement;
import codersafterdark.reskillable.api.requirement.RequirementComparision;
import codersafterdark.reskillable.api.requirement.RequirementException;
import dynamicswordskills.entity.DSSPlayerInfo;
import dynamicswordskills.skills.SkillBase;
import java.util.Objects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class DSSkillRequirement extends Requirement {
   private final SkillBase skill;
   private final int level;

   public DSSkillRequirement(SkillBase skill, int level) {
      this.skill = skill;
      this.level = level;
      this.tooltip = TextFormatting.GRAY + " - " + TextFormatting.GOLD + (new TextComponentTranslation("compatskills.requirements.format.dss", new Object[]{"%s", skill.getDisplayName(), level})).getUnformattedComponentText();
   }

   public static DSSkillRequirement fromString(String input) throws RequirementException {
      if (input.isEmpty()) {
         throw new RequirementException("No Skill given.");
      } else {
         String[] parts = input.split("\\|");
         SkillBase skill = SkillBase.getSkillByName(parts[0]);
         if (skill == null) {
            throw new RequirementException("Invalid skill '" + parts[0] + "'.");
         } else if (parts.length == 1) {
            throw new RequirementException("No level given for skill '" + skill.getDisplayName() + "'.");
         } else {
            try {
               return new DSSkillRequirement(skill, Integer.parseInt(parts[1]));
            } catch (NumberFormatException var4) {
               throw new RequirementException("Invalid level '" + parts[1] + "' for skill: '" + skill.getDisplayName() + "'.");
            }
         }
      }
   }

   public boolean achievedByPlayer(EntityPlayer player) {
      DSSPlayerInfo info = DSSPlayerInfo.get(player);
      return info != null && info.getSkillLevel(this.skill) >= this.level;
   }

   public RequirementComparision matches(Requirement o) {
      if (o instanceof DSSkillRequirement) {
         DSSkillRequirement other = (DSSkillRequirement)o;
         if (this.skill.equals(other.skill)) {
            return this.level == other.level ? RequirementComparision.EQUAL_TO : (this.level > other.level ? RequirementComparision.GREATER_THAN : RequirementComparision.LESS_THAN);
         }
      }

      return RequirementComparision.NOT_EQUAL;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else {
         if (o instanceof DSSkillRequirement) {
            DSSkillRequirement other = (DSSkillRequirement)o;
            if (this.skill.equals(other.skill)) {
               return this.level == other.level;
            }
         }

         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.skill, this.level});
   }
}
