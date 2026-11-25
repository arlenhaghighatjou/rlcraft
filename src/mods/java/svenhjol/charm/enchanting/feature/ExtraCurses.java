package svenhjol.charm.enchanting.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Stop;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import svenhjol.charm.enchanting.enchantment.EnchantmentClumsinessCurse;
import svenhjol.charm.enchanting.enchantment.EnchantmentHarmingCurse;
import svenhjol.charm.enchanting.enchantment.EnchantmentHauntingCurse;
import svenhjol.charm.enchanting.enchantment.EnchantmentRustingCurse;
import svenhjol.meson.Feature;
import svenhjol.meson.MesonEnchantment;
import svenhjol.meson.helper.EnchantmentHelper;

public class ExtraCurses extends Feature {
   public static List<MesonEnchantment> activeCurses = new ArrayList();
   public static int rustingDamage;
   public static int hauntingSpawnRange;
   public static int harmingDamageAmount;
   public static double clumsinessMissChance;
   public static double hauntingSpawnChance;
   public static double harmingDamageChance;
   public static String[] hauntingMobs;

   public ExtraCurses() {
   }

   public String getDescription() {
      return "Additional curses to ruin your day.\nThese curses can be given to player items when touched by a Spectre or being affected by the Decay potion.";
   }

   public void configure() {
      super.configure();
      rustingDamage = this.propInt("Rusting damage", "Maximum amount of durability damage given to an item affected by Rusting when the item is used.", 12);
      clumsinessMissChance = this.propDouble("Clumsiness chance", "Chance (out of 1.0) that an item affected by Clumsiness fails to do its job.", 0.12D);
      harmingDamageChance = this.propDouble("Harming chance", "Chance (out of 1.0) that an item affected by Harming hurts the player.", 0.12D);
      harmingDamageAmount = this.propInt("Harming damage", "Amount of damage (in half-hearts) dealt to the player when using an item affected by Harming.", 1);
      hauntingSpawnChance = this.propDouble("Haunting spawn chance", "Chance (out of 1.0) that an item affected by Haunting causes a mob to spawn near the player.", 0.12D);
      hauntingSpawnRange = this.propInt("Haunting spawn range", "Distance (in blocks) where a mob can spawn when using an item affected by Haunting.", 8);
      hauntingMobs = this.propStringList("Haunting mobs", "List of mobs that can spawn when using an item affected by Haunting.", new String[]{"zombie", "skeleton", "witch", "enderman", "charm:spectre"});
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      activeCurses.add(new EnchantmentRustingCurse());
      activeCurses.add(new EnchantmentClumsinessCurse());
      activeCurses.add(new EnchantmentHauntingCurse());
      activeCurses.add(new EnchantmentHarmingCurse());
      activeCurses.forEach((curse) -> {
         EnchantmentHelper.addAvailableCurses(curse);
      });
   }

   @SubscribeEvent
   public void onBreak(BreakEvent event) {
      if (!event.isCanceled() && !event.getPlayer().world.isRemote) {
         for(MesonEnchantment curse : activeCurses) {
            curse.onBreak(event.getPlayer(), event);
         }
      }

   }

   @SubscribeEvent
   public void onAttack(AttackEntityEvent event) {
      if (!event.isCanceled() && !event.getEntityPlayer().world.isRemote) {
         for(MesonEnchantment curse : activeCurses) {
            curse.onAttack(event.getEntityPlayer(), event);
         }
      }

   }

   @SubscribeEvent
   public void onBreakDrop(HarvestDropsEvent event) {
      if (!event.isCanceled() && event.getHarvester() != null && !event.getHarvester().world.isRemote) {
         for(MesonEnchantment curse : activeCurses) {
            curse.onBreakDrop(event.getHarvester(), event);
         }
      }

   }

   @SubscribeEvent
   public void onDamage(LivingDamageEvent event) {
      if (!event.isCanceled() && event.getEntityLiving() instanceof EntityPlayer && !event.getEntityLiving().world.isRemote) {
         for(MesonEnchantment curse : activeCurses) {
            curse.onDamage((EntityPlayer)event.getEntityLiving(), event);
         }
      }

   }

   @SubscribeEvent
   public void onItemUseStop(Stop event) {
      if (!event.isCanceled() && event.getEntityLiving() instanceof EntityPlayer && !event.getEntityLiving().world.isRemote) {
         for(MesonEnchantment curse : activeCurses) {
            curse.onItemUseStop((EntityPlayer)event.getEntityLiving(), event);
         }
      }

   }

   @SubscribeEvent
   public void onInteract(RightClickBlock event) {
      if (!event.isCanceled() && !event.getEntityPlayer().world.isRemote) {
         for(MesonEnchantment curse : activeCurses) {
            curse.onInteract(event.getEntityPlayer(), event);
         }
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }
}
