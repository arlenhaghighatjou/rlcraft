package svenhjol.meson.iface;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import svenhjol.meson.helper.EnchantmentHelper;
import svenhjol.meson.registry.ProxyRegistry;

public interface IMesonEnchantment {
   default void register(String name) {
      Enchantment self = (Enchantment)this;
      self.setName(name);
      self.setRegistryName(new ResourceLocation(this.getModId(), name));
      ProxyRegistry.register(self);
   }

   default boolean isHeldItemEnchanted(EntityPlayer player, Enchantment enchantment, EnumHand hand) {
      return !player.getHeldItem(hand).isEmpty() && EnchantmentHelper.hasEnchantment(enchantment, player.getHeldItem(hand));
   }

   default boolean isHeldItemEnchanted(EntityPlayer player, Enchantment enchantment) {
      return this.isHeldItemEnchanted(player, enchantment, EnumHand.MAIN_HAND) || this.isHeldItemEnchanted(player, enchantment, EnumHand.OFF_HAND);
   }

   String getModId();

   public interface ICurse {
   }
}
