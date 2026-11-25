package net.darkhax.enchdesc.client;

import net.minecraft.util.text.TextFormatting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

@SideOnly(Side.CLIENT)
public class TooltipHandler {
   private static final KeyBinding keyBindSneak;

   public TooltipHandler() {
   }

   @SubscribeEvent
   public void onTooltipDisplayed(ItemTooltipEvent event) {
      if (event.getEntityPlayer() != null) {
         if (!event.getItemStack().isEmpty() && event.getItemStack().getItem() instanceof ItemEnchantedBook) {
            List<String> tooltip = event.getToolTip();
            if (GameSettings.isKeyDown(keyBindSneak)) {
               for(Enchantment enchant : this.getEnchantments(event.getItemStack())) {
                  tooltip.add(I18n.format("tooltip.enchdesc.name", new Object[0]) + ": " + I18n.format(enchant.getName(), new Object[0]));
                  tooltip.add(this.getDescription(enchant));
                  tooltip.add(I18n.format("tooltip.enchdesc.addedby", new Object[0]) + ": " + TextFormatting.BLUE + getModName(enchant));
               }
            } else {
               tooltip.add(I18n.format("tooltip.enchdesc.activate", new Object[]{TextFormatting.LIGHT_PURPLE, keyBindSneak.getDisplayName(), TextFormatting.GRAY}));
            }
         }

      }
   }

   private String getDescription(Enchantment enchantment) {
      String key = getTranslationKey(enchantment);
      String description = I18n.format(key, new Object[0]);
      if (description.startsWith("enchantment.")) {
         description = I18n.format("tooltip.enchdesc.missing", new Object[]{getModName(enchantment), key});
      }

      return description;
   }

   private List<Enchantment> getEnchantments(ItemStack stack) {
      NBTTagList enchTags = ItemEnchantedBook.getEnchantments(stack);
      List<Enchantment> enchantments = new ArrayList();
      if (enchTags != null) {
         for(int index = 0; index < enchTags.tagCount(); ++index) {
            int id = enchTags.getCompoundTagAt(index).getShort("id");
            Enchantment enchant = Enchantment.getEnchantmentByID(id);
            if (enchant != null) {
               enchantments.add(enchant);
            }
         }
      }

      return enchantments;
   }

   public static String getModName(Impl<?> registerable) {
      if (registerable != null && registerable.getRegistryName() != null) {
         String modID = registerable.getRegistryName().getResourceDomain();
         ModContainer mod = (ModContainer)Loader.instance().getIndexedModList().get(modID);
         return mod != null ? mod.getName() : modID;
      } else {
         return "NULL";
      }
   }

   public static String getTranslationKey(Enchantment enchant) {
      return enchant != null && enchant.getRegistryName() != null ? String.format("enchantment.%s.%s.desc", enchant.getRegistryName().getResourceDomain(), enchant.getRegistryName().getResourcePath()) : "NULL";
   }

   static {
      keyBindSneak = Minecraft.getMinecraft().gameSettings.keyBindSneak;
   }
}
