package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.StatCollector;
import com.github.alexthe666.iceandfire.core.ModItems;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class ItemModArmor extends ItemArmor {
   public ItemModArmor(ArmorMaterial material, int renderIndex, EntityEquipmentSlot slot, String gameName, String name) {
      super(material, renderIndex, slot);
      this.setCreativeTab(IceAndFire.TAB);
      this.setUnlocalizedName(name);
      this.setRegistryName("iceandfire", gameName);
   }

   public String getUnlocalizedName(ItemStack stack) {
      if (this == ModItems.earplugs) {
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(new Date());
         if (calendar.get(2) + 1 == 4 && calendar.get(5) == 1) {
            return "item.iceandfire.air_pods";
         }
      }

      return super.getUnlocalizedName(stack);
   }

   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      if (this == ModItems.earplugs) {
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(new Date());
         if (calendar.get(2) + 1 == 4 && calendar.get(5) == 1) {
            tooltip.add(TextFormatting.GREEN + StatCollector.translateToLocal("item.iceandfire.air_pods.desc"));
         }
      }

      super.addInformation(stack, worldIn, tooltip, flagIn);
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      ItemStack mat = this.getArmorMaterial().getRepairItemStack();
      if (this.getArmorMaterial() == ModItems.silverMetal) {
         for(ItemStack ingot : OreDictionary.getOres("ingotSilver")) {
            if (OreDictionary.itemMatches(repair, ingot, false)) {
               return true;
            }
         }
      }

      return !mat.isEmpty() && OreDictionary.itemMatches(mat, repair, false) ? true : super.getIsRepairable(toRepair, repair);
   }
}
