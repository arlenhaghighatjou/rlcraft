package com.tmtravlr.qualitytools;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ClientEventHandler {
   public ClientEventHandler() {
   }

   public static void register(EventBus bus) {
      bus.register(ItemTooltipEvent.class, ClientEventHandler::onItemTooltip);
   }

   public static void onItemTooltip(ItemTooltipEvent event) {
      NBTTagCompound tag = QualityToolsHelper.getQualityTag(event.getItemStack());
      if (!tag.hasNoTags()) {
         event.getToolTip().add("");
         TextFormatting color = TextFormatting.getValueByName(tag.getString("Color"));
         event.getToolTip().add(TextFormatting.GRAY + I18n.format("info.quality.name", new Object[0]) + (color == null ? "" : color) + I18n.format(tag.getString("Name"), new Object[0]));
         NBTTagList slotList = tag.getTagList("Slots", 8);

         for(int i = 0; i < slotList.tagCount(); ++i) {
            String slot = slotList.getStringTagAt(i);
            event.getToolTip().add(TextFormatting.GRAY + I18n.format("item.modifiers." + slot.toLowerCase(), new Object[0]));
         }

         NBTTagList attributeList = tag.getTagList("AttributeModifiers", 10);

         for(int i = 0; i < attributeList.tagCount(); ++i) {
            AttributeModifier modifier = SharedMonsterAttributes.readAttributeModifierFromNBT(attributeList.getCompoundTagAt(i));
            String attributeName = attributeList.getCompoundTagAt(i).getString("AttributeName");
            double amount = modifier.getAmount();
            double displayAmount;
            if (modifier.getOperation() != 1 && modifier.getOperation() != 2) {
               displayAmount = amount;
            } else {
               displayAmount = amount * 100.0D;
            }

            if (amount > 0.0D) {
               event.getToolTip().add(TextFormatting.BLUE + " " + I18n.format("attribute.modifier.plus." + modifier.getOperation(), new Object[]{ItemStack.DECIMALFORMAT.format(displayAmount), I18n.format("attribute.name." + attributeName, new Object[0])}));
            } else if (amount < 0.0D) {
               displayAmount = displayAmount * -1.0D;
               event.getToolTip().add(TextFormatting.RED + " " + I18n.format("attribute.modifier.take." + modifier.getOperation(), new Object[]{ItemStack.DECIMALFORMAT.format(displayAmount), I18n.format("attribute.name." + attributeName, new Object[0])}));
            }
         }
      }

   }
}
