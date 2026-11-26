package net.theawesomegem.fishingmadebetter.common.item.attachment.hook;

import net.lax1dude.eaglercraft.v1_8.HString;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.theawesomegem.fishingmadebetter.common.registry.FMBCreativeTab;

public abstract class ItemHook extends Item {
   protected final int tuggingReduction;
   protected final int treasureModifier;
   protected final int biteRateModifier;
   protected final int weightModifier;

   public ItemHook(String name, int maxDamage, int tuggingReduction, int treasureModifier, int biteRateModifier, int weightModifier) {
      this.tuggingReduction = tuggingReduction;
      this.treasureModifier = treasureModifier;
      this.biteRateModifier = biteRateModifier;
      this.weightModifier = weightModifier;
      this.setCreativeTab(FMBCreativeTab.instance);
      this.setNoRepair();
      this.setMaxStackSize(1);
      this.setMaxDamage(maxDamage);
      this.setRegistryName(name);
      this.setUnlocalizedName("fishingmadebetter." + name);
   }

   public int getTuggingReduction() {
      return this.tuggingReduction;
   }

   public int getTreasureModifier() {
      return this.treasureModifier;
   }

   public int getBiteRateModifier() {
      return this.biteRateModifier;
   }

   public int getWeightModifier() {
      return this.weightModifier;
   }

   public boolean isEnchantable(ItemStack stack) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      if (this.tuggingReduction != 0) {
         tooltip.add(I18n.format("item.fishingmadebetter.hook_barbed.tooltip", new Object[0]) + ": " + this.tuggingReduction);
      }

      if (this.treasureModifier != 0) {
         tooltip.add(I18n.format("item.fishingmadebetter.hook_magnetic.tooltip", new Object[0]) + ": +" + HString.format("%s%%", this.treasureModifier));
      }

      if (this.biteRateModifier != 0) {
         tooltip.add(I18n.format("item.fishingmadebetter.hook_shiny.tooltip", new Object[0]) + ": +" + HString.format("%s%%", this.biteRateModifier));
      }

      if (this.weightModifier != 0) {
         tooltip.add(I18n.format("item.fishingmadebetter.hook_fatty.tooltip", new Object[0]) + ": +" + HString.format("%s%%", this.weightModifier));
      }

   }
}
