package svenhjol.charm.enchanting.enchantment;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Stop;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import svenhjol.charm.enchanting.feature.ExtraCurses;
import svenhjol.meson.MesonEnchantment;
import svenhjol.meson.helper.EnchantmentHelper;
import svenhjol.meson.iface.IMesonEnchantment;

public class EnchantmentHarmingCurse extends MesonEnchantment implements IMesonEnchantment.ICurse {
   private int amount = ExtraCurses.harmingDamageAmount;
   private double chance = ExtraCurses.harmingDamageChance;

   public EnchantmentHarmingCurse() {
      super("harming_curse", Rarity.VERY_RARE, EnumEnchantmentType.ALL, EntityEquipmentSlot.MAINHAND);
   }

   public String getModId() {
      return "charm";
   }

   public void onBreak(EntityPlayer player, BreakEvent event) {
      if (this.isHeldItemEnchanted(player, this)) {
         this.tryDamage(player);
      }

   }

   public void onAttack(EntityPlayer player, AttackEntityEvent event) {
      if (this.isHeldItemEnchanted(player, this)) {
         this.tryDamage(player);
      }

   }

   public void onDamage(EntityPlayer player, LivingDamageEvent event) {
      for(ItemStack item : player.getEquipmentAndArmor()) {
         if (EnchantmentHelper.hasEnchantment(this, item)) {
            this.tryDamage(player);
         }
      }

   }

   public void onInteract(EntityPlayer player, RightClickBlock event) {
      if (this.isHeldItemEnchanted(player, this)) {
         this.tryDamage(player);
      }

   }

   public void onItemUseStop(EntityPlayer player, Stop event) {
      if (this.isHeldItemEnchanted(player, this)) {
         this.tryDamage(player);
      }

   }

   private void tryDamage(EntityPlayer player) {
      if ((double)player.world.rand.nextFloat() < this.chance) {
         player.attackEntityFrom(DamageSource.MAGIC, (float)this.amount);
      }

   }
}
