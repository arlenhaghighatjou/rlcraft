package svenhjol.charm.enchanting.enchantment;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Stop;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import svenhjol.charm.enchanting.feature.ExtraCurses;
import svenhjol.meson.MesonEnchantment;
import svenhjol.meson.helper.SoundHelper;
import svenhjol.meson.iface.IMesonEnchantment;

public class EnchantmentClumsinessCurse extends MesonEnchantment implements IMesonEnchantment.ICurse {
   private double clumsiness = ExtraCurses.clumsinessMissChance;

   public EnchantmentClumsinessCurse() {
      super("clumsiness_curse", Rarity.VERY_RARE, EnumEnchantmentType.WEAPON, EntityEquipmentSlot.MAINHAND);
   }

   public String getModId() {
      return "charm";
   }

   public void onBreakDrop(EntityPlayer player, HarvestDropsEvent event) {
      if (this.isHeldItemEnchanted(player, this) && (double)player.world.rand.nextFloat() < this.clumsiness) {
         event.setDropChance(0.0F);
      }

   }

   public void onAttack(EntityPlayer player, AttackEntityEvent event) {
      this.tryMiss(player, event);
   }

   public void onInteract(EntityPlayer player, RightClickBlock event) {
      this.tryMiss(player, event);
   }

   public void onItemUseStop(EntityPlayer player, Stop event) {
      this.tryMiss(player, event);
   }

   private void tryMiss(EntityPlayer player, Event event) {
      if (this.isHeldItemEnchanted(player, this) && (double)player.world.rand.nextFloat() < this.clumsiness) {
         event.setCanceled(true);
         this.playSound(player);
      }

   }

   private void playSound(EntityPlayer player) {
      SoundHelper.playerSound(player, SoundEvents.ENTITY_ITEM_BREAK, 0.5F, 1.5F, 0.15F, (SoundCategory)null);
   }
}
