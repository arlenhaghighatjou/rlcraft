package me.ichun.mods.ichunutil.client.gui.window;

import java.util.ArrayList;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementTitle;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementWindow;

public class WindowTabs extends Window {
   public ArrayList<ElementWindow> tabs = new ArrayList();
   public int selectedTab;

   public WindowTabs(IWorkspace parent, Window original) {
      super(parent, original.posX, original.posY, original.width, original.height, original.minWidth, original.minHeight, "", true);
      this.docked = original.docked;

      for(int i = 0; i < this.elements.size(); ++i) {
         if (this.elements.get(i) instanceof ElementTitle) {
            this.elements.remove(i);
         }
      }

      this.addWindow(original, false);
   }

   public int onClick(int mouseX, int mouseY, int id) {
      ((ElementWindow)this.tabs.get(this.selectedTab)).mountedWindow.onClick(mouseX, mouseY, id);
      return super.onClick(mouseX, mouseY, id);
   }

   public void addWindow(Window window, boolean select) {
      if (window instanceof WindowTabs) {
         WindowTabs windowTabs = (WindowTabs)window;

         for(int i = 0; i < windowTabs.tabs.size(); ++i) {
            this.addWindow(((ElementWindow)windowTabs.tabs.get(i)).mountedWindow, i == windowTabs.selectedTab && select);
         }
      } else {
         this.tabs.add(new ElementWindow(this, 0, 0, 10, 10, this.tabs.size(), window));
         this.elements.add(this.tabs.get(this.tabs.size() - 1));
         if (select) {
            this.selectedTab = this.tabs.size() - 1;
         }

         window.docked = this.docked;
         if (window.minWidth > this.minWidth) {
            this.minWidth = window.minWidth;
            if (this.width < this.minWidth) {
               this.oriWidth = this.width = this.minWidth;
            }
         }

         if (window.minHeight > this.minHeight) {
            this.minHeight = window.minHeight;
            if (this.height < this.minHeight) {
               this.oriHeight = this.height = this.minHeight;
            }
         }

         window.width = this.width;
         window.height = this.height;
      }

      this.resized();
   }

   public static Window detach(ElementWindow element) {
      WindowTabs tab = (WindowTabs)element.parent;
      tab.tabs.remove(element);
      tab.elements.remove(element);
      if (tab.tabs.size() <= 1) {
         if (tab.docked >= 0) {
            for(int i = ((ArrayList)element.parent.workspace.levels.get(tab.docked)).size() - 1; i >= 0; --i) {
               Window w = (Window)((ArrayList)element.parent.workspace.levels.get(tab.docked)).get(i);
               if (w == tab) {
                  ((ArrayList)element.parent.workspace.levels.get(tab.docked)).remove(i);
                  if (tab.tabs.size() == 1) {
                     ((ArrayList)element.parent.workspace.levels.get(tab.docked)).add(i, ((ElementWindow)tab.tabs.get(0)).mountedWindow);
                  }
                  break;
               }
            }
         } else {
            element.parent.workspace.removeWindow(tab);
            if (tab.tabs.size() == 1) {
               Window window = ((ElementWindow)tab.tabs.get(0)).mountedWindow;
               element.parent.workspace.addWindowOnTop(window);
               window.resized();
            }
         }
      } else {
         for(int i = 0; i < tab.tabs.size(); ((ElementWindow)tab.tabs.get(i)).id = i++) {
            ;
         }

         while(tab.selectedTab >= tab.tabs.size() && tab.selectedTab > 0) {
            --tab.selectedTab;
         }

         tab.resized();
      }

      element.parent.workspace.addWindowOnTop(element.mountedWindow);
      return element.mountedWindow;
   }

   public boolean allowMultipleInstances() {
      return true;
   }
}
