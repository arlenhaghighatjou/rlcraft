package com.bloodnbonesgaming.lib.util;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiTextField;

public class GuiHelper {
   public GuiHelper() {
   }

   public static GuiButton clone(GuiButton button) {
      GuiButton button2 = new GuiButton(button.id, button.x, button.y, button.width, button.height, button.displayString);
      button2.enabled = button.enabled;
      button2.visible = button.visible;
      button2.packedFGColour = button.packedFGColour;
      return button2;
   }

   public static GuiTextField clone(GuiTextField textField) {
      GuiTextField textField2 = new GuiTextField(textField.getId(), textField.fontRenderer, textField.x, textField.y, textField.width, textField.height);
      textField2.setText(textField.getText());
      textField2.setMaxStringLength(textField.getMaxStringLength());
      textField2.setEnableBackgroundDrawing(textField.getEnableBackgroundDrawing());
      textField2.setCanLoseFocus(textField.canLoseFocus);
      textField2.setFocused(textField.isFocused());
      textField2.setEnabled(textField.isEnabled);
      textField2.setTextColor(textField.enabledColor);
      textField2.setDisabledTextColour(textField.disabledColor);
      textField2.setVisible(textField.getVisible());
      return textField2;
   }

   public static GuiLabel clone(GuiLabel label) {
      GuiLabel label2 = new GuiLabel(label.fontRenderer, label.id, label.x, label.y, label.width, label.height, label.textColor);
      label2.centered = label.centered;
      label2.visible = label.visible;
      label2.labelBgEnabled = label.labelBgEnabled;
      return label2;
   }

   public static List<GuiButton> deepCloneButton(List<GuiButton> list) {
      List<GuiButton> list2 = Lists.newArrayList();

      for(GuiButton button : list) {
         list2.add(clone(button));
      }

      return list2;
   }

   public static List<GuiTextField> deepCloneText(List<GuiTextField> list) {
      List<GuiTextField> list2 = Lists.newArrayList();

      for(GuiTextField textField : list) {
         list2.add(clone(textField));
      }

      return list2;
   }

   public static List<GuiLabel> deepCloneLabel(List<GuiLabel> list) {
      List<GuiLabel> list2 = Lists.newArrayList();

      for(GuiLabel label : list) {
         list2.add(clone(label));
      }

      return list2;
   }
}
