package cursedflames.bountifulbaubles.item;

import baubles.api.BaubleType;
import cursedflames.bountifulbaubles.BountifulBaubles;
import cursedflames.bountifulbaubles.baubleeffect.PotionNegation;
import cursedflames.bountifulbaubles.item.base.AGenericItemBauble;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class ItemTrinketPotionCharm extends AGenericItemBauble implements PotionNegation.IPotionNegateItem {
   public List<String> potions;

   public ItemTrinketPotionCharm(String id, List<String> potionsIn) {
      super(id, BountifulBaubles.TAB);
      BountifulBaubles.registryHelper.addItemModel(this);
      this.potions = potionsIn;
   }

   public BaubleType getBaubleType(ItemStack arg0) {
      return BaubleType.TRINKET;
   }

   public void onWornTick(ItemStack stack, EntityLivingBase player) {
      PotionNegation.negatePotion(player, this.potions);
   }

   public List<String> getCureEffects() {
      return this.potions;
   }
}
