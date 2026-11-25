package xzeroair.trinkets.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.DefaultGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import xzeroair.trinkets.Trinkets;

public class TrinketsGuiFactory extends DefaultGuiFactory {
   public TrinketsGuiFactory() {
      super("xat", GuiConfig.getAbridgedConfigPath(Trinkets.config.toString()));
   }

   public GuiScreen createConfigGui(GuiScreen parent) {
      return new GuiConfig(parent, getConfigElements(), this.modid, false, false, this.title);
   }

   private static List<IConfigElement> getConfigElements() {
      List<IConfigElement> list = new ArrayList();
      list.addAll((new ConfigElement(Trinkets.config.getCategory("general"))).getChildElements());
      list.addAll((new ConfigElement(Trinkets.config.getCategory("client"))).getChildElements());
      return list;
   }
}
