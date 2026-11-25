package codersafterdark.reskillable.api.requirement;

import codersafterdark.reskillable.api.ReskillableAPI;
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.RequirementHolder;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class AdvancementRequirement extends Requirement {
   private ResourceLocation advancementName;

   public AdvancementRequirement(ResourceLocation advancementName) {
      this.advancementName = advancementName;
   }

   public boolean achievedByPlayer(EntityPlayer entityPlayer) {
      return ((Boolean)Optional.ofNullable(this.getAdvancement()).map((advancement) -> {
         return ReskillableAPI.getInstance().getAdvancementProgress(entityPlayer, advancement);
      }).map(AdvancementProgress::isDone).orElse(Boolean.valueOf(false))).booleanValue();
   }

   public String getToolTip(PlayerData data) {
      if (this.tooltip.isEmpty()) {
         Advancement adv = this.getAdvancement();
         this.tooltip = TextFormatting.GRAY + " - " + TextFormatting.GOLD + (new TextComponentTranslation("reskillable.requirements.format.advancement", new Object[]{"%S", adv == null ? "" : adv.getDisplayText().getUnformattedText().replaceAll("[\\[\\]]", "")})).getUnformattedComponentText();
      }

      return super.getToolTip(data);
   }

   public Advancement getAdvancement() {
      return RequirementHolder.getAdvancementList().getAdvancement(this.advancementName);
   }

   public RequirementComparision matches(Requirement other) {
      return other instanceof AdvancementRequirement && this.advancementName.equals(((AdvancementRequirement)other).advancementName) ? RequirementComparision.EQUAL_TO : RequirementComparision.NOT_EQUAL;
   }

   public boolean equals(Object o) {
      return o == this || o instanceof AdvancementRequirement && this.advancementName.equals(((AdvancementRequirement)o).advancementName);
   }

   public int hashCode() {
      return this.advancementName.hashCode();
   }
}
