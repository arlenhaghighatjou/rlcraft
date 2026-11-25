package codersafterdark.compatskills.common.compats.minecraft.item.harvestlevel;

import codersafterdark.reskillable.api.requirement.Requirement;
import codersafterdark.reskillable.api.requirement.RequirementComparision;
import codersafterdark.reskillable.api.requirement.RequirementException;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class HarvestLevelRequirement extends Requirement {
   private final int harvestLevel;

   public HarvestLevelRequirement(int level) {
      this.harvestLevel = level;
      this.tooltip = TextFormatting.GRAY + " - " + (new TextComponentTranslation("compatskills.requirements.format.harvest_level", new Object[]{"%s", this.harvestLevel})).getUnformattedComponentText();
   }

   public static HarvestLevelRequirement fromString(String input) throws RequirementException {
      if (input.isEmpty()) {
         throw new RequirementException("No harvest level given.");
      } else {
         try {
            return new HarvestLevelRequirement(Integer.parseInt(input));
         } catch (NumberFormatException var2) {
            throw new RequirementException("Invalid harvest level '" + input + "'.");
         }
      }
   }

   public boolean achievedByPlayer(EntityPlayer player) {
      return this.hasHarvestLevel(player.getHeldItemMainhand()) || this.hasHarvestLevel(player.getHeldItemOffhand());
   }

   public RequirementComparision matches(Requirement o) {
      if (o instanceof HarvestLevelRequirement) {
         HarvestLevelRequirement other = (HarvestLevelRequirement)o;
         if (this.harvestLevel == other.harvestLevel) {
            return RequirementComparision.EQUAL_TO;
         } else {
            return this.harvestLevel > other.harvestLevel ? RequirementComparision.GREATER_THAN : RequirementComparision.LESS_THAN;
         }
      } else {
         return RequirementComparision.NOT_EQUAL;
      }
   }

   private boolean hasHarvestLevel(ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      } else {
         Item item = stack.getItem();
         return item.getToolClasses(stack).stream().anyMatch((toolClass) -> {
            return item.getHarvestLevel(stack, toolClass, (EntityPlayer)null, (IBlockState)null) >= this.harvestLevel;
         });
      }
   }

   public boolean equals(Object o) {
      return o == this || o instanceof HarvestLevelRequirement && this.harvestLevel == ((HarvestLevelRequirement)o).harvestLevel;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.harvestLevel});
   }
}
