package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.client.StatCollector;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSeaSerpentScales extends ItemGeneric {
   private TextFormatting color;
   private String colorName;

   public ItemSeaSerpentScales(String colorName, TextFormatting color) {
      super("sea_serpent_scales_" + colorName, "iceandfire.sea_serpent_scales_" + colorName);
      this.color = color;
      this.colorName = colorName;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
      tooltip.add(this.color + StatCollector.translateToLocal("sea_serpent." + this.colorName));
   }
}
