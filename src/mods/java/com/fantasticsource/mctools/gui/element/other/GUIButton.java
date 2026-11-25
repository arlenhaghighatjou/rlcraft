package com.fantasticsource.mctools.gui.element.other;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.textured.GUIImage;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.util.ResourceLocation;

public class GUIButton extends GUIAutocroppedView {
   protected static final Color LIGHT_BLUE = Color.BLUE.copy().setSF(0.2F);
   protected final boolean locks;
   protected GUIElement idleElement;
   protected GUIElement hoverElement;
   protected GUIElement activeElement;

   public GUIButton(GUIScreen screen, GUIElement idleElement, GUIElement hoverElement, GUIElement activeElement) {
      this(screen, idleElement, hoverElement, activeElement, false);
   }

   public GUIButton(GUIScreen screen, GUIElement idleElement, GUIElement hoverElement, GUIElement activeElement, boolean locks) {
      super(screen);
      this.idleElement = idleElement;
      this.hoverElement = hoverElement;
      this.activeElement = activeElement;
      this.locks = locks;
      this.add(idleElement);
      this.setExternalDeactivation(locks, true);
   }

   public GUIButton(GUIScreen screen, double x, double y, GUIElement idleElement, GUIElement hoverElement, GUIElement activeElement) {
      this(screen, x, y, idleElement, hoverElement, activeElement, false);
   }

   public GUIButton(GUIScreen screen, double x, double y, GUIElement idleElement, GUIElement hoverElement, GUIElement activeElement, boolean locks) {
      super(screen, x, y);
      this.idleElement = idleElement;
      this.hoverElement = hoverElement;
      this.activeElement = activeElement;
      this.locks = locks;
      this.add(idleElement);
      this.setExternalDeactivation(locks, true);
   }

   public GUIElement add(int index, GUIElement element) {
      GUIElement result = super.add(index, element);
      this.setExternalDeactivation(this.locks, true);
      return result;
   }

   public static GUIButton newAddButton(GUIScreen screen) {
      GUIImage idle = new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), 0.0D, 0.0D, 0.25D, 0.25D);
      idle.add(new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), GUIScreen.getIdleColor(Color.GREEN), 0.0D, 0.25D, 0.25D, 0.25D));
      GUIImage hover = new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), 0.0D, 0.0D, 0.25D, 0.25D);
      hover.add(new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), GUIScreen.getHoverColor(Color.GREEN), 0.0D, 0.25D, 0.25D, 0.25D));
      GUIImage active = new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), 0.25D, 0.0D, 0.25D, 0.25D);
      active.add(new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), Color.GREEN, 0.0D, 0.25D, 0.25D, 0.25D));
      return (GUIButton)(new GUIButton(screen, idle, hover, active)).setTooltip("Insert Entry");
   }

   public static GUIButton newRemoveButton(GUIScreen screen) {
      GUIImage idle = new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), 0.0D, 0.0D, 0.25D, 0.25D);
      idle.add(new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), GUIScreen.getIdleColor(Color.RED), 0.25D, 0.25D, 0.25D, 0.25D));
      GUIImage hover = new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), 0.0D, 0.0D, 0.25D, 0.25D);
      hover.add(new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), GUIScreen.getHoverColor(Color.RED), 0.25D, 0.25D, 0.25D, 0.25D));
      GUIImage active = new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), 0.25D, 0.0D, 0.25D, 0.25D);
      active.add(new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), Color.RED, 0.25D, 0.25D, 0.25D, 0.25D));
      return (GUIButton)(new GUIButton(screen, idle, hover, active)).setTooltip("Remove Entry");
   }

   public static GUIButton newEditButton(GUIScreen screen) {
      GUIImage idle = new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), 0.0D, 0.0D, 0.25D, 0.25D);
      idle.add(new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), GUIScreen.getIdleColor(Color.ORANGE), 0.5D, 0.25D, 0.25D, 0.25D));
      GUIImage hover = new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), 0.0D, 0.0D, 0.25D, 0.25D);
      hover.add(new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), GUIScreen.getHoverColor(Color.ORANGE), 0.5D, 0.25D, 0.25D, 0.25D));
      GUIImage active = new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), 0.25D, 0.0D, 0.25D, 0.25D);
      active.add(new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), Color.ORANGE, 0.5D, 0.25D, 0.25D, 0.25D));
      return (GUIButton)(new GUIButton(screen, idle, hover, active)).setTooltip("Edit Properties");
   }

   public static GUIButton newListButton(GUIScreen screen) {
      GUIImage idle = new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), 0.0D, 0.0D, 0.25D, 0.25D);
      idle.add(new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), GUIScreen.getIdleColor(Color.AQUA), 0.75D, 0.25D, 0.25D, 0.25D));
      GUIImage hover = new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), 0.0D, 0.0D, 0.25D, 0.25D);
      hover.add(new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), GUIScreen.getHoverColor(Color.AQUA), 0.75D, 0.25D, 0.25D, 0.25D));
      GUIImage active = new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), 0.25D, 0.0D, 0.25D, 0.25D);
      active.add(new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), Color.AQUA, 0.75D, 0.25D, 0.25D, 0.25D));
      return (GUIButton)(new GUIButton(screen, idle, hover, active)).setTooltip("Edit List");
   }

   public static GUIButton newDuplicateButton(GUIScreen screen) {
      GUIImage idle = new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), 0.0D, 0.0D, 0.25D, 0.25D);
      idle.add(new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), GUIScreen.getIdleColor(LIGHT_BLUE), 0.5D, 0.0D, 0.25D, 0.25D));
      GUIImage hover = new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), 0.0D, 0.0D, 0.25D, 0.25D);
      hover.add(new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), GUIScreen.getHoverColor(LIGHT_BLUE), 0.5D, 0.0D, 0.25D, 0.25D));
      GUIImage active = new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), 0.25D, 0.0D, 0.25D, 0.25D);
      active.add(new GUIImage(screen, 16.0D, 16.0D, new ResourceLocation("fantasticlib", "image/gui.png"), LIGHT_BLUE, 0.5D, 0.0D, 0.25D, 0.25D));
      return (GUIButton)(new GUIButton(screen, idle, hover, active)).setTooltip("Duplicate Entry");
   }

   public void draw() {
      if (this.locks && this.active) {
         this.set(this.activeElement);
      } else if (this.isMouseWithin()) {
         if (this.active) {
            this.set(this.activeElement);
         } else {
            this.set(this.hoverElement);
         }
      } else {
         this.set(this.idleElement);
      }

      super.draw();
   }

   protected void set(GUIElement element) {
      int index = this.indexOf(element);
      if (index == -1) {
         index = this.indexOf(this.idleElement);
         if (index != -1) {
            this.remove(index);
         }

         index = this.indexOf(this.hoverElement);
         if (index != -1) {
            this.remove(index);
         }

         index = this.indexOf(this.activeElement);
         if (index != -1) {
            this.remove(index);
         }

         this.add(element);
      }
   }
}
