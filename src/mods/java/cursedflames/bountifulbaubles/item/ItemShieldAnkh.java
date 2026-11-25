package cursedflames.bountifulbaubles.item;

import cursedflames.bountifulbaubles.baubleeffect.PotionNegation;
import cursedflames.bountifulbaubles.event.IItemHeldListener;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class ItemShieldAnkh extends ItemShieldObsidian implements IItemHeldListener, PotionNegation.IPotionNegateItem {
   public static final List<String> potions = Arrays.asList("minecraft:blindness", "minecraft:nausea", "minecraft:hunger", "minecraft:mining_fatigue", "minecraft:weakness", "minecraft:slowness", "minecraft:levitation", "minecraft:poison", "minecraft:wither");

   public ItemShieldAnkh(String name) {
      super(name);
      this.setMaxDamage(1680);
   }

   public void onWornTick(ItemStack stack, EntityLivingBase player) {
      PotionNegation.negatePotion(player, potions);
   }

   public void onHeldTick(ItemStack stack, EntityPlayer player, EnumHand hand) {
      this.onWornTick(stack, player);
   }

   public List<String> getCureEffects() {
      return potions;
   }
}
