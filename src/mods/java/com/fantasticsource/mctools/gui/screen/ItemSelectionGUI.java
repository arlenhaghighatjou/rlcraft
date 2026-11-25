package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIItemStackText;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class ItemSelectionGUI extends GUIScreen {
   public ItemStack selection;

   public ItemSelectionGUI(GUIItemStackText clickedElement) {
      this(clickedElement, 1.0D);
   }

   public ItemSelectionGUI(GUIItemStackText clickedElement, double textScale) {
      super(textScale);
      if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) {
         GUIScreen.showStacked(this);
      } else {
         Minecraft.getMinecraft().displayGuiScreen(this);
      }

      this.drawStack = false;
      this.selection = clickedElement.getStack();
      this.root.add(new GUIDarkenedBackground(this));
      GUITextSpacer spacer = new GUITextSpacer(this, true);
      GUIScrollView scrollView = new GUIScrollView(this, 0.98D - spacer.width * 2.0D, 1.0D, new GUIElement[0]);
      this.root.add(spacer.addRecalcActions(new Runnable[]{() -> {
         scrollView.width = 0.98D - spacer.width * 2.0D;
      }}));
      this.root.add(scrollView);
      this.root.add(new GUITextSpacer(this, true));
      this.root.add(new GUIVerticalScrollbar(this, 0.98D, 0.0D, 0.02D, 1.0D, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, scrollView));
      scrollView.add(new GUITextSpacer(this));
      GUIItemStackText stackElement = new GUIItemStackText(this, clickedElement.getStack().copy());
      stackElement.setText("" + TextFormatting.RESET + TextFormatting.DARK_PURPLE + "(currently selected)");
      scrollView.add(stackElement);
      scrollView.add(new GUITextSpacer(this));
      scrollView.add(new GUITextSpacer(this));
      stackElement = new GUIItemStackText(this, ItemStack.EMPTY);
      stackElement.setText(TextFormatting.DARK_PURPLE + "(Remove item)");
      scrollView.add(stackElement);
      scrollView.add(new GUITextSpacer(this));
      scrollView.add(new GUITextSpacer(this));
      scrollView.add(new GUITextSpacer(this));
      EntityPlayer player = Minecraft.getMinecraft().player;

      for(int i = 0; i < player.inventory.getSizeInventory(); ++i) {
         ItemStack stack = player.inventory.getStackInSlot(i);
         if (!stack.isEmpty()) {
            scrollView.add(new GUIItemStackText(this, stack.copy()));
            scrollView.add(new GUITextSpacer(this));
         }
      }

      for(int i = scrollView.size() - 1; i >= 0; --i) {
         GUIElement element = scrollView.get(i);
         if (element instanceof GUIItemStackText) {
            element.addClickActions(() -> {
               this.selection = ((GUIItemStackText)element).getStack();
               this.close();
            });
         }
      }

   }

   public String title() {
      return "Item";
   }
}
