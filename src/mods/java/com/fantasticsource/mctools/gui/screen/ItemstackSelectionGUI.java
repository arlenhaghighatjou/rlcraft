package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.textured.GUIItemStack;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.item.ItemStack;

public class ItemstackSelectionGUI extends GUIScreen {
   public static final int MODE_DROPDOWN = 0;
   public static final int MODE_FULLSCREEN = 1;
   public final String title;

   public ItemstackSelectionGUI(GUIItemStack clickedElement, String title, ItemStack... options) {
      this(clickedElement, title, 1.0D, options);
   }

   public ItemstackSelectionGUI(GUIItemStack clickedElement, int dropdownW, int dropdownH, String title, ItemStack... options) {
      this(clickedElement, dropdownW, dropdownH, title, 1.0D, options);
   }

   public ItemstackSelectionGUI(GUIItemStack clickedElement, String title, double textScale, ItemStack... options) {
      this(clickedElement, 9, 5, title, textScale, options);
   }

   public ItemstackSelectionGUI(GUIItemStack clickedElement, int dropdownW, int dropdownH, String title, double textScale, ItemStack... options) {
      super(textScale);
      this.title = title;
      this.show();
      this.root.add(new GUIDarkenedBackground(this));
      GUINavbar navbar = new GUINavbar(this);
      this.root.add(navbar);
      double x = 0.0D;
      double y = navbar.height;
      double w = 0.98D;
      double h = 1.0D - navbar.height;
      boolean scrollbar = true;
      if (dropdownW > 0 && dropdownH != 0) {
         w = clickedElement.absoluteWidth() * (double)dropdownW;
         h = clickedElement.absoluteHeight() * (double)dropdownH;
         x = clickedElement.absoluteX();
         double clickedBottom = clickedElement.absoluteY() + clickedElement.absoluteHeight();
         if (dropdownH > 0) {
            y = clickedBottom + h <= 1.0D ? clickedBottom : clickedElement.absoluteY() - h;
         } else {
            y = clickedElement.absoluteY() - h >= 0.0D ? clickedElement.absoluteY() - h : clickedBottom;
         }

         scrollbar = options.length > Math.abs(dropdownW * dropdownH) + 1 || options.length > Math.abs(dropdownW * dropdownH) && Tools.contains(options, clickedElement.getItemStack());
      }

      GUIScrollView view = new GUIScrollView(this, x, y, w, h, new GUIElement[0]);
      this.root.add(view);
      if (scrollbar) {
         this.root.add(new GUIVerticalScrollbar(this, view.x + view.width, view.y, clickedElement.absoluteWidth() / 4.0D, view.height, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, view));
      }

      if (Tools.contains(options, clickedElement.getItemStack())) {
         GUIItemStack guiItemStack = new GUIItemStack(this, clickedElement.absoluteX(), clickedElement.absoluteY(), 16.0D, 16.0D, clickedElement.getItemStack());
         guiItemStack.add(new GUIGradientBorder(this, 1.0D, 1.0D, 0.1D, getIdleColor(Color.PURPLE), Color.BLANK, getHoverColor(Color.PURPLE), Color.BLANK, Color.PURPLE, Color.BLANK));
         this.root.add(guiItemStack.addClickActions(new Runnable[]{this::close}));
      }

      for(ItemStack option : options) {
         if (!option.equals(clickedElement.getItemStack())) {
            GUIItemStack guiItemStack = new GUIItemStack(this, 16.0D, 16.0D, option);
            guiItemStack.add(new GUIGradientBorder(this, 1.0D, 1.0D, 0.1D, getIdleColor(Color.WHITE), Color.BLANK, getHoverColor(Color.WHITE), Color.BLANK, Color.WHITE, Color.BLANK));
            view.add(guiItemStack.addClickActions(new Runnable[]{() -> {
               clickedElement.setItemStack(option);
               this.close();
            }}));
         }
      }

   }

   public String title() {
      return this.title;
   }
}
