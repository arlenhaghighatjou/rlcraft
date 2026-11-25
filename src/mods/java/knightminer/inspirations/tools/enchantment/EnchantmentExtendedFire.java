package knightminer.inspirations.tools.enchantment;

import java.util.List;
import java.util.function.Predicate;
import knightminer.inspirations.common.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentFireAspect;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;

public class EnchantmentExtendedFire extends EnchantmentFireAspect {
   public EnchantmentExtendedFire(Rarity rarityIn, EntityEquipmentSlot... slots) {
      super(rarityIn, slots);
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack) {
      Item item = stack.getItem();
      return Config.moreShieldEnchantments && item.isShield(stack, (EntityLivingBase)null) || Config.axeEnchantmentTable && Config.axeWeaponEnchants && item instanceof ItemAxe || super.canApplyAtEnchantingTable(stack);
   }

   public boolean canApply(ItemStack stack) {
      return Config.axeWeaponEnchants && stack.getItem() instanceof ItemAxe || super.canApply(stack);
   }

   public List<ItemStack> getEntityEquipment(EntityLivingBase entity) {
      List<ItemStack> items = super.getEntityEquipment(entity);
      items.removeIf((stack) -> {
         return stack.getItem().isShield(stack, entity);
      });
      return items;
   }

   public boolean canApplyTogether(Enchantment ench) {
      return super.canApplyTogether(ench) && ench != Enchantments.THORNS && ench != Enchantments.EFFICIENCY;
   }
}
