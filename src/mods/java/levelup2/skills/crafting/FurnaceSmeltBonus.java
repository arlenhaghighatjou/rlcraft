package levelup2.skills.crafting;

import levelup2.skills.BaseSkill;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class FurnaceSmeltBonus extends BaseSkill {
   public FurnaceSmeltBonus() {
   }

   public String getSkillName() {
      return "levelup:furnacebonus";
   }

   public byte getSkillType() {
      return 1;
   }

   public boolean hasSubscription() {
      return false;
   }

   public ItemStack getRepresentativeStack() {
      return new ItemStack(Items.COAL);
   }
}
