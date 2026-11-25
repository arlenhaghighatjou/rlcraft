package com.oblivioussp.spartanshields.item;

import com.oblivioussp.spartanshields.client.gui.CreativeTabsSS;
import com.oblivioussp.spartanshields.util.StringHelper;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ItemShieldBase extends ItemShield {
   public ItemShieldBase(String unlocName) {
      this.setCreativeTab(CreativeTabsSS.TAB_SS);
      this.setRegistryName(unlocName);
      this.setUnlocalizedName(unlocName);
   }

   public String getUnlocalizedName() {
      return StringHelper.getItemUnlocalizedName(super.getUnlocalizedName());
   }

   public String getUnlocalizedName(ItemStack itemStack) {
      return StringHelper.getItemUnlocalizedName(super.getUnlocalizedName());
   }

   public String getItemStackDisplayName(ItemStack stack) {
      return ("" + I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name")).trim();
   }

   @SideOnly(Side.CLIENT)
   public CreativeTabs getCreativeTab() {
      return CreativeTabsSS.TAB_SS;
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      return false;
   }

   public boolean isShield(ItemStack stack, @Nullable EntityLivingBase entity) {
      return true;
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return enchantment == Enchantments.KNOCKBACK || super.canApplyAtEnchantingTable(stack, enchantment);
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.BLOCK;
   }

   @SideOnly(Side.CLIENT)
   public void addEffectsTooltip(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
   }

   @SideOnly(Side.CLIENT)
   public void addShieldBashTooltip(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.add(TextFormatting.BLUE + StringHelper.translateFormattedString("shieldBash", "tooltip", "spartanshields", Minecraft.getMinecraft().gameSettings.keyBindAttack.getDisplayName()));
   }
}
