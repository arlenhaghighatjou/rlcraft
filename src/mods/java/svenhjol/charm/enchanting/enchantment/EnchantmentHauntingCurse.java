package svenhjol.charm.enchanting.enchantment;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import svenhjol.charm.enchanting.feature.ExtraCurses;
import svenhjol.meson.MesonEnchantment;
import svenhjol.meson.helper.EntityHelper;
import svenhjol.meson.iface.IMesonEnchantment;

public class EnchantmentHauntingCurse extends MesonEnchantment implements IMesonEnchantment.ICurse {
   private double spawnChance = ExtraCurses.hauntingSpawnChance;
   private int spawnRange = ExtraCurses.hauntingSpawnRange;
   private String[] spawnableMobs = ExtraCurses.hauntingMobs;

   public EnchantmentHauntingCurse() {
      super("haunting_curse", Rarity.VERY_RARE, EnumEnchantmentType.WEAPON, EntityEquipmentSlot.MAINHAND);
   }

   public String getModId() {
      return "charm";
   }

   public void onBreak(EntityPlayer player, BreakEvent event) {
      this.trySpawn(player);
   }

   public void onInteract(EntityPlayer player, RightClickBlock event) {
      this.trySpawn(player);
   }

   public void onAttack(EntityPlayer player, AttackEntityEvent event) {
      this.trySpawn(player);
   }

   private void trySpawn(EntityPlayer player) {
      if (this.isHeldItemEnchanted(player, this) && (double)player.world.rand.nextFloat() < this.spawnChance) {
         String mob = this.spawnableMobs[player.world.rand.nextInt(this.spawnableMobs.length)];
         EntityHelper.spawnEntityNearPlayer(player, this.spawnRange, new ResourceLocation(mob));
      }

   }
}
