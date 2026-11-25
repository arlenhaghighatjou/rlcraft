package knightminer.inspirations.tools.enchantment;

import java.util.List;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.enchantment.EnchantmentProtection.Type;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentShieldProtection extends EnchantmentProtection {
   public EnchantmentShieldProtection(Rarity rarityIn, Type protectionTypeIn, EntityEquipmentSlot... slots) {
      super(rarityIn, protectionTypeIn, slots);
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack) {
      return stack.getItem().isShield(stack, (EntityLivingBase)null) || super.canApplyAtEnchantingTable(stack);
   }

   public List<ItemStack> getEntityEquipment(EntityLivingBase entity) {
      List<ItemStack> list = super.getEntityEquipment(entity);
      if (entity.isActiveItemStackBlocking()) {
         list.add(entity.getActiveItemStack());
      }

      return list;
   }
}
