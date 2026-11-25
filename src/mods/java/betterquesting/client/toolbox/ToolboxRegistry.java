package betterquesting.client.toolbox;

import betterquesting.api.client.toolbox.IToolRegistry;
import betterquesting.api2.client.toolbox.IToolTab;
import java.util.Collection;
import java.util.HashMap;
import net.minecraft.util.ResourceLocation;

public class ToolboxRegistry implements IToolRegistry {
   public static final ToolboxRegistry INSTANCE = new ToolboxRegistry();
   private final HashMap<ResourceLocation, IToolTab> toolTabs = new HashMap();

   public ToolboxRegistry() {
   }

   public void registerToolTab(ResourceLocation tabID, IToolTab tab) {
      if (tabID != null && tab != null) {
         if (this.toolTabs.containsKey(tabID)) {
            throw new IllegalArgumentException("Cannot register duplicate tab ID: " + tabID);
         } else {
            this.toolTabs.put(tabID, tab);
         }
      } else {
         throw new NullPointerException("Tried to register null tab or null ID");
      }
   }

   public IToolTab getTabByID(ResourceLocation tabID) {
      return (IToolTab)this.toolTabs.get(tabID);
   }

   public Collection<IToolTab> getAllTabs() {
      return this.toolTabs.values();
   }
}
