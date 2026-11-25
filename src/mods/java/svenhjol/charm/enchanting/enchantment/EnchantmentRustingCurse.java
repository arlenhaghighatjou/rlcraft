package svenhjol.charm.enchanting.enchantment;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Stop;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import svenhjol.charm.enchanting.feature.ExtraCurses;
import svenhjol.meson.MesonEnchantment;
import svenhjol.meson.helper.EnchantmentHelper;
import svenhjol.meson.iface.IMesonEnchantment;

public class EnchantmentRustingCurse extends MesonEnchantment implements IMesonEnchantment.ICurse {
   private int rustDamage = ExtraCurses.rustingDamage;

   public EnchantmentRustingCurse() {
      super("rusting_curse", Rarity.VERY_RARE, EnumEnchantmentType.ALL, EntityEquipmentSlot.MAINHAND);
   }

   public String getModId() {
      return "charm";
   }

   public void onBreak(EntityPlayer player, BreakEvent event) {
      if (this.isHeldItemEnchanted(player, this, EnumHand.MAIN_HAND)) {
         this.damageItem(player, player.getHeldItemMainhand());
      }

      if (this.isHeldItemEnchanted(player, this, EnumHand.OFF_HAND)) {
         this.damageItem(player, player.getHeldItemOffhand());
      }

   }

   public void onInteract(EntityPlayer player, RightClickBlock event) {
      if (this.isHeldItemEnchanted(player, this, EnumHand.MAIN_HAND)) {
         this.damageItem(player, player.getHeldItemMainhand());
      }

      if (this.isHeldItemEnchanted(player, this, EnumHand.OFF_HAND)) {
         this.damageItem(player, player.getHeldItemOffhand());
      }

   }

   public void onAttack(EntityPlayer player, AttackEntityEvent event) {
      if (this.isHeldItemEnchanted(player, this, EnumHand.MAIN_HAND)) {
         this.damageItem(player, player.getHeldItemMainhand());
      }

      if (this.isHeldItemEnchanted(player, this, EnumHand.OFF_HAND)) {
         this.damageItem(player, player.getHeldItemOffhand());
      }

   }

   public void onItemUseStop(EntityPlayer player, Stop event) {
      if (this.isHeldItemEnchanted(player, this, EnumHand.MAIN_HAND)) {
         this.damageItem(player, player.getHeldItemMainhand());
      }

   }

   public void onDamage(EntityPlayer player, LivingDamageEvent event) {
      for(ItemStack item : player.getEquipmentAndArmor()) {
         if (EnchantmentHelper.hasEnchantment(this, item)) {
            this.damageItem(player, item);
         }
      }

   }

   private void damageItem(EntityPlayer player, ItemStack item) {
      item.attemptDamageItem((new EaglercraftRandom()).nextInt(this.rustDamage), player.world.rand, (EntityPlayerMP)player);
   }
}
