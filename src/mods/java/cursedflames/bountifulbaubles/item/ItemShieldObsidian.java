package cursedflames.bountifulbaubles.item;

import cursedflames.bountifulbaubles.baubleeffect.IFireResistance;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemShieldObsidian extends ItemShieldCobalt implements IFireResistance {
   public ItemShieldObsidian(String name) {
      super(name);
      this.setMaxDamage(1344);
   }

   public float getResistance() {
      return 0.5F;
   }

   public float getMaxNegate() {
      return 1.1F;
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      return repair.getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN);
   }

   public EaglercraftUUID getFireResistID() {
      return ItemTrinketObsidianSkull.FIRE_RESIST_UUID;
   }
}
