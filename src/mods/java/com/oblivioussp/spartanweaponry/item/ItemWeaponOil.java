package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.util.StringHelper;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemWeaponOil extends ItemSW {
   public ItemWeaponOil(String unlocName) {
      super(unlocName);
      this.setMaxStackSize(1);
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (this.isInCreativeTab(tab)) {
         for(PotionType potiontype : PotionType.REGISTRY) {
            if (!potiontype.getEffects().isEmpty() && ((PotionEffect)potiontype.getEffects().get(0)).getPotion().isBadEffect()) {
               items.add(PotionUtils.addPotionToItemStack(new ItemStack(this), potiontype));
            }
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.add(TextFormatting.RED + StringHelper.translateString("wip", "dev"));
      tooltip.add("");
      PotionUtils.addPotionTooltip(stack, tooltip, 1.0F);
   }

   public String getItemStackDisplayName(ItemStack stack) {
      String name = I18n.translateToLocalFormatted(String.format("item.%s:proj_tipped.%s", "spartanweaponry", PotionUtils.getPotionFromItem(stack).getNamePrefixed("")), new Object[]{StringHelper.translateString("weapon_oil.name", "item")});
      return name;
   }
}
