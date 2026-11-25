package ljfa.tntutils.gui;

import java.util.ArrayList;
import java.util.List;
import ljfa.tntutils.Config;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class TntuConfigGui extends GuiConfig {
   public TntuConfigGui(GuiScreen parent) {
      super(parent, getConfigElements(), "tnt_utilities", false, false, "TNTUtils configuration");
   }

   private static List<IConfigElement> getConfigElements() {
      List<IConfigElement> list = new ArrayList();

      for(String name : Config.conf.getCategoryNames()) {
         list.add(new ConfigElement(Config.conf.getCategory(name)));
      }

      return list;
   }
}
