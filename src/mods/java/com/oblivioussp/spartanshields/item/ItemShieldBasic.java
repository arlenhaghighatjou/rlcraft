package com.oblivioussp.spartanshields.item;

import com.oblivioussp.spartanshields.util.StringHelper;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ItemShieldBasic extends ItemShieldBase {
   protected ToolMaterial material;

   public ItemShieldBasic(String unlocName, int maxDamage, ToolMaterial toolMaterial) {
      super(unlocName);
      this.material = toolMaterial;
      this.setMaxDamage(maxDamage);
   }

   public ItemShieldBasic setToolMaterial(ToolMaterial toolMaterial) {
      this.material = toolMaterial;
      return this;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.add(StringHelper.translateFormattedString("protection", "tooltip", this.getMaxDamage()));
      if (!stack.isEmpty() && stack.hasTagCompound() && stack.getTagCompound().hasKey("BlockEntityTag")) {
         EnumDyeColor dyeColor = TileEntityBanner.getColor(stack);
         tooltip.add("");
         tooltip.add(StringHelper.translateString("hasPatterns", "tooltip"));
         tooltip.add(I18n.translateToLocal("item.banner." + dyeColor.getUnlocalizedName() + ".name"));
         ItemBanner.appendHoverTextFromTileEntityTag(stack, tooltip);
      }

      this.addEffectsTooltip(stack, worldIn, tooltip, flagIn);
      tooltip.add("");
      this.addShieldBashTooltip(stack, worldIn, tooltip, flagIn);
   }

   public int getItemEnchantability() {
      return this.material.getEnchantability();
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      ItemStack repairStack = this.material.getRepairItemStack();
      if (repairStack == null) {
         return false;
      } else {
         return OreDictionary.itemMatches(repairStack, repair, false);
      }
   }
}
