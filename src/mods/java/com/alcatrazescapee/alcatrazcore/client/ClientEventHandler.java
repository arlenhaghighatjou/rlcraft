package com.alcatrazescapee.alcatrazcore.client;

import com.alcatrazescapee.alcatrazcore.CoreConfig;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

@EventBusSubscriber(
   modid = "alcatrazcore",
   value = {Side.CLIENT}
)
public class ClientEventHandler {
   public ClientEventHandler() {
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public static void onItemTooltip(ItemTooltipEvent event) {
      ItemStack stack = event.getItemStack();
      List<String> tt = event.getToolTip();
      if (event.getFlags().isAdvanced() && CoreConfig.showDebugTooltips) {
         tt.add(TextFormatting.AQUA + "Translation Key: " + TextFormatting.GRAY + stack.getUnlocalizedName());
         if (stack.hasTagCompound()) {
            tt.add(TextFormatting.AQUA + "NBT: " + TextFormatting.GRAY + stack.getTagCompound().toString());
         }

         int[] ids = OreDictionary.getOreIDs(stack);
         if (ids.length != 0) {
            tt.add(TextFormatting.AQUA + "Ore Dictionary Names:");
            Arrays.stream(ids).mapToObj(OreDictionary::getOreName).sorted().map((x) -> {
               return TextFormatting.GRAY + x;
            }).forEachOrdered(tt::add);
         }
      }

   }
}
