package betterquesting.api.client.toolbox;

import betterquesting.api2.client.toolbox.IToolTab;
import java.util.Collection;
import net.minecraft.util.ResourceLocation;

public interface IToolRegistry {
   void registerToolTab(ResourceLocation var1, IToolTab var2);

   IToolTab getTabByID(ResourceLocation var1);

   Collection<IToolTab> getAllTabs();
}
