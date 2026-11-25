package svenhjol.charm.world.feature;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import svenhjol.charm.base.CharmLootTables;
import svenhjol.charm.world.item.ItemTotemOfShielding;
import svenhjol.meson.Feature;
import svenhjol.meson.helper.ItemHelper;
import svenhjol.meson.helper.LootHelper;

public class TotemOfShielding extends Feature {
   public static Item totem;
   public static double damageMultiplier;
   public static int maxHealth;
   public static boolean addToLoot;
   public static boolean addToIllusioner;

   public TotemOfShielding() {
   }

   public String getDescription() {
      return "With the Totem of Shielding in your offhand (shield) slot, any damage you take is absorbed by the totem instead.";
   }

   public void configure() {
      super.configure();
      damageMultiplier = this.propDouble("Damage multiplier", "The incoming player damage is multiplied by this amount before being transferred to the totem.", 0.75D);
      maxHealth = this.propInt("Maximum Health", "Maximum durability of the totem.", 100);
      addToLoot = this.propBoolean("Add to loot", "Add the totem to mansions, pyramids and jungle temple loot.", true);
      addToIllusioner = this.propBoolean("Add as Illusioner drop", "If Illusioners are enabled, add the totem as a possible drop.", true);
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      totem = new ItemTotemOfShielding();
      ItemHelper.availableTotems.add(new ItemStack(totem));
   }

   @SubscribeEvent
   public void onDamage(LivingDamageEvent event) {
      if (!event.isCanceled() && event.getEntityLiving() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getEntityLiving();
         ItemStack stack = player.getHeldItemOffhand();
         if (stack.getItem() instanceof ItemTotemOfShielding) {
            double totemDamage = (double)event.getAmount() * damageMultiplier;
            boolean totemDead = stack.attemptDamageItem((int)Math.ceil(totemDamage), player.world.rand, (EntityPlayerMP)player);
            if (totemDead) {
               stack.shrink(1);
               player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ITEM_TOTEM_USE, SoundCategory.BLOCKS, 0.8F, 1.0F);
            } else {
               player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, 1.25F);
            }

            event.setCanceled(true);
         }
      }

   }

   @SubscribeEvent
   public void onLootTableLoad(LootTableLoadEvent event) {
      if (addToLoot) {
         int weight = 0;
         int quality = 0;
         LootFunction[] functions = new LootFunction[0];
         LootCondition[] conditions = new LootCondition[0];
         if (event.getName().equals(LootTableList.CHESTS_WOODLAND_MANSION)) {
            weight = 12;
         }

         if (event.getName().equals(LootTableList.CHESTS_DESERT_PYRAMID)) {
            weight = 10;
         }

         if (event.getName().equals(LootTableList.CHESTS_JUNGLE_TEMPLE)) {
            weight = 10;
         }

         if (event.getName().equals(CharmLootTables.VILLAGE_PRIEST)) {
            weight = 2;
         }

         if (event.getName().equals(CharmLootTables.TREASURE_VALUABLE)) {
            weight = 12;
         }

         if (event.getName().equals(CharmLootTables.TREASURE_RARE)) {
            weight = 16;
         }

         if (weight > 0) {
            LootHelper.addToLootTable(event.getTable(), totem, weight, quality, functions, conditions);
         }

      }
   }

   public boolean hasSubscriptions() {
      return true;
   }
}
