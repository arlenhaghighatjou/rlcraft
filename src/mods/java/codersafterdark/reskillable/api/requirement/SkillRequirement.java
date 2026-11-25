package codersafterdark.reskillable.api.requirement;

import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.skill.Skill;
import java.util.Objects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class SkillRequirement extends Requirement {
   private final Skill skill;
   private final int level;

   public SkillRequirement(Skill skill, int level) {
      this.skill = skill;
      this.level = level;
      this.tooltip = TextFormatting.GRAY + " - " + (new TextComponentTranslation("reskillable.requirements.format.skill", new Object[]{TextFormatting.DARK_AQUA, skill.getName(), "%s", level})).getUnformattedComponentText();
   }

   public boolean achievedByPlayer(EntityPlayer entityPlayer) {
      return PlayerDataHandler.get(entityPlayer).getSkillInfo(this.skill).getLevel() >= this.level;
   }

   public Skill getSkill() {
      return this.skill;
   }

   public int getLevel() {
      return this.level;
   }

   public RequirementComparision matches(Requirement other) {
      if (other instanceof SkillRequirement) {
         SkillRequirement skillRequirement = (SkillRequirement)other;
         if (this.skill == null || skillRequirement.skill == null) {
            return RequirementComparision.NOT_EQUAL;
         }

         if (this.skill.getKey().equals(skillRequirement.skill.getKey())) {
            if (this.level == skillRequirement.level) {
               return RequirementComparision.EQUAL_TO;
            }

            return this.level > skillRequirement.level ? RequirementComparision.GREATER_THAN : RequirementComparision.LESS_THAN;
         }
      }

      return RequirementComparision.NOT_EQUAL;
   }

   public boolean isEnabled() {
      return this.skill != null && this.skill.isEnabled();
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SkillRequirement)) {
         return false;
      } else {
         SkillRequirement sReq = (SkillRequirement)o;
         return this.skill.equals(sReq.skill) && this.level == sReq.level;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.skill, this.level});
   }
}
