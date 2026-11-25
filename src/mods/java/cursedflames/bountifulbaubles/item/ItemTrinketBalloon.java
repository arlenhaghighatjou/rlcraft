package cursedflames.bountifulbaubles.item;

import baubles.api.BaubleType;
import cursedflames.bountifulbaubles.BountifulBaubles;
import cursedflames.bountifulbaubles.baubleeffect.IJumpBoost;
import cursedflames.bountifulbaubles.item.base.AGenericItemBauble;
import net.minecraft.item.ItemStack;

public class ItemTrinketBalloon extends AGenericItemBauble implements IJumpBoost {
   public ItemTrinketBalloon() {
      super("trinketBalloon", BountifulBaubles.TAB);
      BountifulBaubles.registryHelper.addItemModel(this);
   }

   public BaubleType getBaubleType(ItemStack arg0) {
      return BaubleType.TRINKET;
   }

   public float getJumpBoost() {
      return 0.3F;
   }

   public float getFallResist() {
      return 5.0F;
   }
}
