package codersafterdark.reskillable.api.requirement;

import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.skill.Skill;
import codersafterdark.reskillable.api.unlockable.Unlockable;
import java.util.Objects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class TraitRequirement extends Requirement {
   private Unlockable unlockable;

   public TraitRequirement(Unlockable unlockable) {
      this.unlockable = unlockable;
      this.tooltip = TextFormatting.GRAY + " - " + TextFormatting.LIGHT_PURPLE + (new TextComponentTranslation("reskillable.requirements.format.trait", new Object[]{"%s", this.unlockable.getName()})).getUnformattedComponentText();
   }

   public boolean achievedByPlayer(EntityPlayer entityPlayer) {
      return PlayerDataHandler.get(entityPlayer).getSkillInfo(this.unlockable.getParentSkill()).isUnlocked(this.unlockable);
   }

   public Skill getSkill() {
      return this.unlockable.getParentSkill();
   }

   public Unlockable getUnlockable() {
      return this.unlockable;
   }

   public RequirementComparision matches(Requirement other) {
      return other instanceof TraitRequirement ? (this.unlockable.getKey().equals(((TraitRequirement)other).unlockable.getKey()) ? RequirementComparision.EQUAL_TO : RequirementComparision.NOT_EQUAL) : RequirementComparision.NOT_EQUAL;
   }

   public boolean isEnabled() {
      return this.unlockable != null && this.unlockable.isEnabled();
   }

   public boolean equals(Object o) {
      return o == this || o instanceof TraitRequirement && this.unlockable.equals(((TraitRequirement)o).unlockable);
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.unlockable});
   }
}
