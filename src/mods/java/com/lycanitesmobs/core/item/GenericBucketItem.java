package com.lycanitesmobs.core.item;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.info.ModInfo;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GenericBucketItem extends ItemBucket {
   public String itemName;
   public ModInfo group = LycanitesMobs.modInfo;

   public GenericBucketItem(Fluid fluid, Block fluidBlock, String fluidName) {
      super(fluidBlock);
      this.itemName = "bucket" + fluidName;
      this.setRegistryName(this.group.modid, this.itemName);
      this.setUnlocalizedName(this.itemName);
      this.setCreativeTab(LycanitesMobs.itemsTab);
      ObjectManager.addBucket(this, fluidBlock, fluid);
   }

   public String getItemStackDisplayName(ItemStack stack) {
      return LanguageManager.translate(this.getUnlocalizedNameInefficiently(stack) + ".name").trim();
   }

   public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      String description = this.getDescription(stack, worldIn, tooltip, flagIn);
      if (!"".equalsIgnoreCase(description) && !("item." + this.itemName + ".description").equals(description)) {
         FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

         for(Object formattedDescription : fontRenderer.listFormattedStringToWidth(description, ItemBase.DESCRIPTION_WIDTH)) {
            if (formattedDescription instanceof String) {
               tooltip.add("§a" + formattedDescription);
            }
         }
      }

      super.addInformation(stack, worldIn, tooltip, flagIn);
   }

   public String getDescription(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      return LanguageManager.translate("item." + this.itemName + ".description");
   }

   @SideOnly(Side.CLIENT)
   public boolean isFull3D() {
      return true;
   }
}
