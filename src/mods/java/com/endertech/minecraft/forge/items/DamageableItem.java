package com.endertech.minecraft.forge.items;

import com.endertech.minecraft.forge.ForgeBounds;
import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.ForgeConfig;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.forge.materials.RepairMatcher;
import com.endertech.minecraft.forge.math.Percentage;
import com.endertech.minecraft.forge.units.UnitId;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public abstract class DamageableItem extends ForgeItem {
   protected final List<UnitId> repairMaterials;
   private final boolean repairCostGrow;
   private final int durability;

   public DamageableItem(ForgeMod mod, UnitConfig config, String name, CreativeTabs tab, int durability, UnitId repair) {
      super(mod, config, name, tab);
      this.durability = ForgeConfig.getInt(this.getConfig(), this.getUnitCategory(), "durability", durability, ForgeBounds.DURABILITY.getIntBounds(), "Defines durability of the item.");
      this.repairMaterials = this.parseRepairMaterials(this.getConfig(), this.getUnitCategory(), repair.toString());
      this.repairCostGrow = UnitConfig.getBool(this.getConfig(), this.getUnitCategory(), "repairCostGrow", false, "If true, repair cost will grow for this item after each repair using an anvil.");
      this.setMaxDamage(this.durability);
      this.setMaxStackSize(1);
   }

   public int getMaxDamage(ItemStack stack) {
      String key = "maxDamage";
      return stack.hasTagCompound() && stack.getTagCompound().hasKey(key) ? stack.getTagCompound().getInteger(key) : this.durability;
   }

   public void setDamage(ItemStack stack, int damage) {
      super.setDamage(stack, damage);
   }

   public void updateDurability(ItemStack stack, EntityPlayerMP player) {
      ForgeItem.ensureHasNBT(stack);
      stack.getTagCompound().setInteger("maxDamage", this.durability);
   }

   public boolean isDamageable() {
      return true;
   }

   @SubscribeEvent
   public static void onAnvilUpdate(AnvilUpdateEvent event) {
      for(ItemStack stack : new ItemStack[]{event.getLeft(), event.getRight()}) {
         Item item = stack.getItem();
         if (item instanceof DamageableItem && !((DamageableItem)item).isRepairCostGrow()) {
            removeRepairPenaltyFor(stack);
         }
      }

   }

   public static void removeRepairPenaltyFor(ItemStack stack) {
      if (!stack.isEmpty() && stack.hasTagCompound()) {
         stack.getTagCompound().removeTag("RepairCost");
      }

   }

   public boolean getIsRepairable(ItemStack stack, ItemStack material) {
      RepairMatcher matcher = RepairMatcher.from(material);

      for(UnitId id : this.repairMaterials) {
         if (matcher.matches(id)) {
            return true;
         }
      }

      return false;
   }

   public boolean isRepairCostGrow() {
      return this.repairCostGrow;
   }

   public Percentage getWearoutPercentage(ItemStack stack) {
      return Percentage.from(stack.getItemDamage(), stack.getMaxDamage());
   }

   protected List<UnitId> parseRepairMaterials(ForgeConfig config, String category, String defaultValue) {
      String stringValue = UnitConfig.getStr(this.getConfig(), this.getUnitCategory(), "repair", defaultValue, "Defines anvil repair materials.\nYou can define several materials by separating them with commas (e.g.: minecraft:leather, #wool)");
      List<UnitId> list = new ArrayList();

      for(String str : stringValue.split(",")) {
         str = str.trim();
         if (!str.isEmpty()) {
            UnitId id = UnitId.from(str);
            if (id.notEmpty()) {
               list.add(id);
            }
         }
      }

      return list;
   }
}
