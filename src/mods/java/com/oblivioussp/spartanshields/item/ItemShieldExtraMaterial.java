package com.oblivioussp.spartanshields.item;

import com.oblivioussp.spartanshields.util.ConfigHandler;
import com.oblivioussp.spartanshields.util.StringHelper;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ItemShieldExtraMaterial extends ItemShieldBasic {
   protected String matOreDict;
   protected boolean doCraftCheck = true;
   protected boolean canBeCrafted = true;

   public ItemShieldExtraMaterial(String unlocName, int maxDamage, ToolMaterial toolMaterial, String materialOreDictName) {
      super(unlocName, maxDamage, toolMaterial);
      this.matOreDict = materialOreDictName;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      if (this.doCraftCheck && worldIn != null) {
         List<ItemStack> ores = OreDictionary.getOres(this.matOreDict, false);
         if (ores == null || ores.isEmpty()) {
            this.canBeCrafted = false;
         }
      }

      if (!ConfigHandler.forceDisableUncraftableTooltips && !this.canBeCrafted) {
         tooltip.add(TextFormatting.RED + StringHelper.translateFormattedString("cantCraftMissingMaterial", "tooltip", "spartanshields", StringHelper.translateString(this.matOreDict, "item")));
      }

      super.addInformation(stack, worldIn, tooltip, flagIn);
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      return OreDictionary.containsMatch(false, OreDictionary.getOres(this.matOreDict), new ItemStack[]{repair});
   }
}
