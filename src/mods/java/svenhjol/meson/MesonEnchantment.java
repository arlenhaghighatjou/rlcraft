package svenhjol.meson;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Stop;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import svenhjol.meson.iface.IMesonEnchantment;

public abstract class MesonEnchantment extends Enchantment implements IMesonEnchantment {
   public MesonEnchantment(String name, Rarity rarity, EnumEnchantmentType type, EntityEquipmentSlot... slots) {
      super(rarity, type, slots);
      this.register(name);
   }

   public String getName() {
      return this.getModId() + "." + super.getName();
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return this instanceof IMesonEnchantment.ICurse ? 25 : super.getMinEnchantability(enchantmentLevel);
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return this instanceof IMesonEnchantment.ICurse ? 50 : super.getMaxEnchantability(enchantmentLevel);
   }

   public int getMaxLevel() {
      return this instanceof IMesonEnchantment.ICurse ? 1 : super.getMaxLevel();
   }

   public boolean isTreasureEnchantment() {
      return this instanceof IMesonEnchantment.ICurse ? true : super.isTreasureEnchantment();
   }

   public boolean isCurse() {
      return this instanceof IMesonEnchantment.ICurse ? true : super.isCurse();
   }

   public void onBreak(EntityPlayer player, BreakEvent event) {
   }

   public void onBreakDrop(EntityPlayer player, HarvestDropsEvent event) {
   }

   public void onAttack(EntityPlayer player, AttackEntityEvent event) {
   }

   public void onDamage(EntityPlayer player, LivingDamageEvent event) {
   }

   public void onInteract(EntityPlayer player, RightClickBlock event) {
   }

   public void onItemUseStop(EntityPlayer player, Stop event) {
   }
}
