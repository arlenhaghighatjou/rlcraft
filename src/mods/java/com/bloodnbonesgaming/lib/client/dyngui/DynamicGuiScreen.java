package com.bloodnbonesgaming.lib.client.dyngui;

import com.bloodnbonesgaming.lib.util.GuiHelper;
import com.google.common.collect.Lists;
import java.beans.ConstructorProperties;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class DynamicGuiScreen extends GuiScreen implements Cloneable {
   private final List<GuiButton> buttons;
   private final List<GuiTextField> textFields;
   private final List<GuiLabel> labels;

   public DynamicGuiScreen() {
      this(Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList());
   }

   public DynamicGuiScreen clone() {
      return new DynamicGuiScreen(GuiHelper.deepCloneButton(this.buttons), GuiHelper.deepCloneText(this.textFields), GuiHelper.deepCloneLabel(this.labels));
   }

   @ConstructorProperties({"buttons", "textFields", "labels"})
   public DynamicGuiScreen(List<GuiButton> buttons, List<GuiTextField> textFields, List<GuiLabel> labels) {
      this.buttons = buttons;
      this.textFields = textFields;
      this.labels = labels;
   }

   public List<GuiButton> getButtons() {
      return this.buttons;
   }

   public List<GuiTextField> getTextFields() {
      return this.textFields;
   }

   public List<GuiLabel> getLabels() {
      return this.labels;
   }
}
