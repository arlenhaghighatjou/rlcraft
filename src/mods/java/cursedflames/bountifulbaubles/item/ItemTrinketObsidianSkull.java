package cursedflames.bountifulbaubles.item;

import baubles.api.BaubleType;
import cursedflames.bountifulbaubles.BountifulBaubles;
import cursedflames.bountifulbaubles.baubleeffect.IFireResistance;
import cursedflames.bountifulbaubles.item.base.AGenericItemBauble;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.item.ItemStack;

public class ItemTrinketObsidianSkull extends AGenericItemBauble implements IFireResistance {
   public static final EaglercraftUUID FIRE_RESIST_UUID = EaglercraftUUID.fromString("ecde20fe-73a4-466d-8796-9884d6297ba6");

   public ItemTrinketObsidianSkull() {
      super("trinketObsidianSkull", BountifulBaubles.TAB);
      BountifulBaubles.registryHelper.addItemModel(this);
   }

   public BaubleType getBaubleType(ItemStack arg0) {
      return BaubleType.TRINKET;
   }

   public float getResistance() {
      return 0.5F;
   }

   public float getMaxNegate() {
      return 1.1F;
   }

   public EaglercraftUUID getFireResistID() {
      return FIRE_RESIST_UUID;
   }
}
