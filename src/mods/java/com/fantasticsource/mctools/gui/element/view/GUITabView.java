package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.tools.datastructures.Color;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraftforge.common.MinecraftForge;

public class GUITabView extends GUIView {
   public final double scale;
   public ArrayList<GUIElement> tabs;
   public ArrayList<GUIView> tabViews;
   private GUIElement tabBackground;
   private int current;
   private boolean autocalcTabs;
   private boolean autocalcTabviews;

   public GUITabView(GUIScreen screen, double width, double height, String... tabNames) {
      this(screen, width, height, 1.0D, tabNames);
   }

   public GUITabView(GUIScreen screen, double width, double height, double scale, String... tabNames) {
      this(screen, width, height, scale, genTabs(screen, scale, tabNames));
      this.autocalcTabs = true;
      this.autocalcTabviews = true;
      GUITextButton tab = (GUITextButton)this.tabs.get(0);
      this.tabBackground = new GUIGradientBorder(screen, 0.0D, 0.0D, 1.0D, 1.0D, 0.1D, ((GUIGradientBorder)tab.background).border, ((GUIGradientBorder)tab.background).center);
      this.add(0, this.tabBackground);
      this.recalc(0);
   }

   public GUITabView(GUIScreen screen, double width, double height, String[] tabNames, GUIView... tabViews) {
      this(screen, width, height, 1.0D, tabNames, tabViews);
   }

   public GUITabView(GUIScreen screen, double width, double height, double scale, String[] tabNames, GUIView... tabViews) {
      this(screen, width, height, scale, genTabs(screen, scale, tabNames), tabViews);
      this.autocalcTabs = true;
      GUITextButton tab = (GUITextButton)this.tabs.get(0);
      this.tabBackground = new GUIGradientBorder(screen, 0.0D, 0.0D, 1.0D, 1.0D, 0.1D, ((GUIGradientBorder)tab.background).border, ((GUIGradientBorder)tab.background).center);
      this.add(0, this.tabBackground);
      this.recalc(0);
   }

   public GUITabView(GUIScreen screen, double width, double height, GUIElement[] tabs, GUIView... tabViews) {
      this(screen, width, height, 1.0D, tabs, tabViews);
   }

   public GUITabView(GUIScreen screen, double width, double height, double scale, GUIElement[] tabs, GUIView... tabViews) {
      super(screen, width, height);
      this.tabs = new ArrayList();
      this.tabViews = new ArrayList();
      this.tabBackground = null;
      this.current = 0;
      this.autocalcTabs = false;
      this.autocalcTabviews = false;
      this.scale = scale;
      if (tabs.length != tabViews.length) {
         if (tabViews.length != 0) {
            throw new IllegalStateException("There must be the same number of tab names and tab elements!");
         }

         this.tabViews = genTabViews(screen, tabs.length);
      } else {
         this.tabViews.addAll(Arrays.asList(tabViews));
      }

      this.tabs.addAll(Arrays.asList(tabs));

      for(GUIElement element : tabs) {
         this.children.add(element);
         element.parent = this;
         element.setExternalDeactivation(true, true);
      }

      for(GUIElement element : this.tabViews) {
         element.parent = this;
      }

      if (this.tabViews.size() > 0) {
         tabs[0].setActive(true);
         this.children.add(this.tabViews.get(0));
      }

      this.recalc(0);
   }

   public GUITabView(GUIScreen screen, double x, double y, double width, double height, String... tabNames) {
      this(screen, x, y, width, height, 1.0D, tabNames);
   }

   public GUITabView(GUIScreen screen, double x, double y, double width, double height, double scale, String... tabNames) {
      this(screen, x, y, width, height, genTabs(screen, scale, tabNames));
      this.autocalcTabs = true;
      this.autocalcTabviews = true;
      GUITextButton tab = (GUITextButton)this.tabs.get(0);
      this.tabBackground = new GUIGradientBorder(screen, 0.0D, 0.0D, 1.0D, 1.0D, 0.1D, ((GUIGradientBorder)tab.background).border, ((GUIGradientBorder)tab.background).center);
      this.add(0, this.tabBackground);
      this.recalc(0);
   }

   public GUITabView(GUIScreen screen, double x, double y, double width, double height, String[] tabNames, GUIView... tabViews) {
      this(screen, x, y, width, height, 1.0D, tabNames, tabViews);
   }

   public GUITabView(GUIScreen screen, double x, double y, double width, double height, double scale, String[] tabNames, GUIView... tabViews) {
      this(screen, x, y, width, height, genTabs(screen, scale, tabNames), tabViews);
      this.autocalcTabs = true;
      GUITextButton tab = (GUITextButton)this.tabs.get(0);
      this.tabBackground = new GUIGradientBorder(screen, 0.0D, 0.0D, 1.0D, 1.0D, 0.1D, ((GUIGradientBorder)tab.background).border, ((GUIGradientBorder)tab.background).center);
      this.add(0, this.tabBackground);
      this.recalc(0);
   }

   public GUITabView(GUIScreen screen, double x, double y, double width, double height, GUIElement[] tabs, GUIView... tabViews) {
      this(screen, x, y, width, height, 1.0D, tabs, tabViews);
   }

   public GUITabView(GUIScreen screen, double x, double y, double width, double height, double scale, GUIElement[] tabs, GUIView... tabViews) {
      super(screen, x, y, width, height);
      this.tabs = new ArrayList();
      this.tabViews = new ArrayList();
      this.tabBackground = null;
      this.current = 0;
      this.autocalcTabs = false;
      this.autocalcTabviews = false;
      this.scale = scale;
      if (tabs.length != tabViews.length) {
         if (tabViews.length != 0) {
            throw new IllegalStateException("There must be the same number of tab names and tab elements!");
         }

         this.tabViews = genTabViews(screen, tabs.length);
      } else {
         this.tabViews.addAll(Arrays.asList(tabViews));
      }

      this.tabs.addAll(Arrays.asList(tabs));

      for(GUIElement element : tabs) {
         this.children.add(element);
         element.parent = this;
         element.setExternalDeactivation(true, true);
      }

      for(GUIElement element : this.tabViews) {
         element.parent = this;
      }

      if (this.tabViews.size() > 0) {
         tabs[0].setActive(true);
         this.children.add(this.tabViews.get(0));
      }

      this.recalc(0);
   }

   private static GUIElement[] genTabs(GUIScreen screen, double scale, String[] tabNames) {
      GUIElement[] result = new GUIElement[tabNames.length];

      for(int i = 0; i < result.length; ++i) {
         result[i] = new GUITextButton(screen, 0.0D, 0.0D, tabNames[i], Color.WHITE, T_GRAY, scale);
      }

      return result;
   }

   private static ArrayList<GUIView> genTabViews(GUIScreen screen, int count) {
      ArrayList<GUIView> result = new ArrayList(count);

      for(int i = 0; i < count; ++i) {
         result.add(new GUIView(screen, 0.0D, 0.0D, 1.0D, 1.0D));
      }

      return result;
   }

   public int currentTab() {
      return this.current;
   }

   public int addTab(String name) {
      GUIView view = new GUIView(this.screen, 0.0D, 0.0D, 1.0D, 1.0D);
      this.tabViews.add(view);
      view.parent = this;
      GUITextButton tab = new GUITextButton(this.screen, 0.0D, 0.0D, name, Color.WHITE, T_GRAY, this.scale);
      this.tabs.add(tab);
      this.add(tab);
      this.recalc(0);
      return this.tabs.size() - 1;
   }

   public void removeTab(int index) {
      GUIElement tab = (GUIElement)this.tabs.get(index);
      if (this.current == index) {
         this.setActiveTab(0);
      }

      this.tabs.remove(index);
      this.remove(tab);
      this.tabViews.remove(index);
   }

   public GUITabView recalc(int subIndexChanged) {
      if (this.autocalcTabs) {
         double xx = 0.0D;
         double yy = 0.0D;

         for(GUIElement tab : this.tabs) {
            if (xx + tab.width > 1.0D) {
               yy += tab.height;
               xx = 0.0D;
            }

            tab.x = xx;
            tab.y = yy;
            xx += tab.width;
         }

         yy = yy + ((GUIElement)this.tabs.get(0)).height;
         if (this.autocalcTabviews) {
            for(GUIView view : this.tabViews) {
               view.y = yy;
               view.height = 1.0D - yy;
            }
         }
      }

      if (this.tabBackground != null) {
         GUIElement element = (GUIElement)this.tabs.get(this.tabs.size() - 1);
         this.tabBackground.height = element.y + element.height;
      }

      this.recalcAndRepositionSubElements(0);
      this.postRecalc();
      return this;
   }

   public void setActiveTab(int index) {
      GUIElement currentElement = (GUIElement)this.tabs.get(index);

      for(GUIElement element : this.tabs) {
         element.setActive(element == currentElement, true);
      }

      if (index != this.current) {
         int i = this.children.indexOf(this.tabViews.get(this.current));
         this.children.remove(i);
         this.current = index;
         this.children.add(i, ((GUIView)this.tabViews.get(this.current)).recalc(0));
      }
   }

   public boolean mouseReleased(int button) {
      boolean result = false;
      if (button == 0) {
         if (this.active && this.isMouseWithin()) {
            if (!MinecraftForge.EVENT_BUS.post(new GUILeftClickEvent(this.screen, this))) {
               this.click();
            }

            result = true;
         }

         this.setActive(false);
      }

      int index = -1;

      for(GUIElement child : (ArrayList)this.children.clone()) {
         if (child.mouseReleased(button)) {
            for(int i = 0; i < this.tabs.size(); ++i) {
               GUIElement tab = (GUIElement)this.tabs.get(i);
               if (child == tab) {
                  index = i;
                  break;
               }
            }
         }
      }

      if (index != -1 && index != this.current) {
         this.setActiveTab(index);
      } else {
         this.setActiveTab(this.current);
      }

      return result;
   }
}
