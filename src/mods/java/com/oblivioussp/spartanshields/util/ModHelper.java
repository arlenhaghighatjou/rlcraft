package com.oblivioussp.spartanshields.util;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;

public class ModHelper {
   public static ToolMaterial materialBronze = EnumHelper.addToolMaterial("ssBronze", 2, 320, 5.75F, 2.0F, 12);
   public static ToolMaterial materialSteel = EnumHelper.addToolMaterial("ssSteel", 2, 480, 6.5F, 2.5F, 14);
   public static ToolMaterial materialCopper = EnumHelper.addToolMaterial("ssCopper", 1, 200, 5.0F, 1.5F, 8);
   public static ToolMaterial materialTin = EnumHelper.addToolMaterial("ssTin", 1, 180, 5.25F, 1.75F, 6);
   public static ToolMaterial materialSilver = EnumHelper.addToolMaterial("ssSilver", 1, 48, 5.0F, 1.5F, 16);
   public static ToolMaterial materialObsidian = EnumHelper.addToolMaterial("ssObsidian", 3, 1024, 5.0F, 1.5F, 5);
   public static ToolMaterial materialEnderium = EnumHelper.addToolMaterial("ssEnderium", 4, 2048, 7.5F, 8.0F, 18);
   public static ToolMaterial materialInvar = EnumHelper.addToolMaterial("ssInvar", 2, 440, 6.0F, 2.2F, 12);
   public static ToolMaterial materialPlatinum = EnumHelper.addToolMaterial("ssPlatinum", 3, 1024, 4.0F, 4.0F, 18);
   public static ToolMaterial materialElectrum = EnumHelper.addToolMaterial("ssElectrum", 1, 180, 3.5F, 6.0F, 8);
   public static ToolMaterial materialNickel = EnumHelper.addToolMaterial("ssNickel", 1, 200, 4.5F, 2.0F, 6);
   public static ToolMaterial materialLead = EnumHelper.addToolMaterial("ssLead", 1, 240, 4.5F, 2.0F, 5);
   public static ToolMaterial materialSignalum = EnumHelper.addToolMaterial("ssSignalum", 3, 1536, 6.0F, 5.0F, 14);
   public static ToolMaterial materialLumium = EnumHelper.addToolMaterial("ssLumium", 1, 840, 5.0F, 4.0F, 16);
   public static ToolMaterial materialManasteel = EnumHelper.addToolMaterial("ssManasteel", 3, 300, 6.0F, 2.0F, 20);
   public static ToolMaterial materialTerrasteel = EnumHelper.addToolMaterial("ssTerrasteel", 3, 2300, 8.0F, 3.0F, 26);
   public static ToolMaterial materialElementium = EnumHelper.addToolMaterial("ssElementium", 3, 720, 6.0F, 2.0F, 20);
   public static ToolMaterial materialConstantan = EnumHelper.addToolMaterial("ssConstantan", 2, 640, 5.5F, 3.0F, 7);
   public static ToolMaterial materialDarkstone = EnumHelper.addToolMaterial("ssDarkstone", 1, 160, 4.0F, 1.0F, 5);
   public static ToolMaterial materialAbyssalnite = EnumHelper.addToolMaterial("ssAbyssalnite", 2, 384, 6.0F, 2.0F, 8);
   public static ToolMaterial materialCoralium = EnumHelper.addToolMaterial("ssCoralium", 3, 512, 7.0F, 2.5F, 10);
   public static ToolMaterial materialDreadium = EnumHelper.addToolMaterial("ssDreadium", 3, 1024, 8.0F, 3.0F, 14);
   public static ToolMaterial materialEthaxium = EnumHelper.addToolMaterial("ssEthaxium", 4, 2048, 12.0F, 4.0F, 22);
   public static ToolMaterial materialSoulforgedSteel = EnumHelper.addToolMaterial("ssSoulforgedSteel", 3, 1024, 6.5F, 3.0F, 16);
   public static ToolMaterial materialThaumium = EnumHelper.addToolMaterial("ssThaumium", 3, 500, 7.0F, 2.5F, 22);
   public static ToolMaterial materialVoid = EnumHelper.addToolMaterial("ssVoidmetal", 4, 150, 8.0F, 3.0F, 10);

   public ModHelper() {
   }

   public static void postInit() {
      ItemStack soulSteelStack = getMaterial("ingotSoulforgedSteel");
      if (!soulSteelStack.isEmpty()) {
         materialSoulforgedSteel.setRepairItem(soulSteelStack);
      }

      ItemStack thaumiumStack = getMaterial("ingotThaumium");
      if (!thaumiumStack.isEmpty()) {
         materialThaumium.setRepairItem(thaumiumStack);
      }

      ItemStack voidStack = getMaterial("ingotVoid");
      if (!voidStack.isEmpty()) {
         materialVoid.setRepairItem(voidStack);
      }

   }

   public static boolean isMaterialValid(String oreDict) {
      if (OreDictionary.doesOreNameExist(oreDict)) {
         List<ItemStack> stacks = OreDictionary.getOres(oreDict);
         if (!stacks.isEmpty() && !((ItemStack)stacks.get(0)).isEmpty()) {
            return true;
         }
      }

      return false;
   }

   public static ItemStack getMaterial(String oreDict) {
      if (OreDictionary.doesOreNameExist(oreDict)) {
         List<ItemStack> stacks = OreDictionary.getOres(oreDict);
         if (stacks.size() != 0) {
            return (ItemStack)stacks.get(0);
         }
      }

      return ItemStack.EMPTY;
   }
}
