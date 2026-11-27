package mezz.jei.config;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.JustEnoughItems;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.network.packets.PacketRequestCheatPermission;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import com.arlenh7.eaglercraft.network.INetworkManager;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.GuiModList;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class JEIModConfigGui extends GuiConfig {
   public JEIModConfigGui(GuiScreen parent) {
      super(getParent(parent), getConfigElements(), "jei", false, false, getTitle(parent));
   }

   private static GuiScreen getParent(GuiScreen parent) {
      if (parent instanceof RecipesGui) {
         GuiScreen parentScreen = ((RecipesGui)parent).getParentScreen();
         if (parentScreen != null) {
            return parentScreen;
         }

         Minecraft minecraft = parent.mc;
         if (minecraft != null) {
            EntityPlayerSP player = minecraft.player;
            if (player != null) {
               return new GuiInventory(player);
            }
         }
      }

      return parent;
   }

   private static List<IConfigElement> getConfigElements() {
      List<IConfigElement> configElements = new ArrayList();
      if (Minecraft.getMinecraft().world != null) {
         Configuration worldConfig = Config.getWorldConfig();
         if (worldConfig != null) {
            INetworkManager networkManager = FMLClientHandler.instance().getClientToServerNetworkManager();
            ConfigCategory categoryWorldConfig = worldConfig.getCategory(ServerInfo.getWorldUid(networkManager));
            configElements.addAll((new ConfigElement(categoryWorldConfig)).getChildElements());
         }
      }

      LocalizedConfiguration config = Config.getConfig();
      if (config != null) {
         ConfigCategory categoryAdvanced = config.getCategory("advanced");
         configElements.addAll((new ConfigElement(categoryAdvanced)).getChildElements());
         ConfigCategory categorySearch = config.getCategory("search");
         configElements.add(new ConfigElement(categorySearch));
      }

      return configElements;
   }

   private static String getTitle(GuiScreen parent) {
      if (parent instanceof GuiModList) {
         LocalizedConfiguration config = Config.getConfig();
         if (config != null) {
            return GuiConfig.getAbridgedConfigPath(config.toString());
         }
      }

      return Translator.translateToLocal("config.jei.title").replace("%MODNAME", "Just Enough Items");
   }

   protected void actionPerformed(GuiButton button) {
      super.actionPerformed(button);
      if (Config.isCheatItemsEnabled() && ServerInfo.isJeiOnServer()) {
         JustEnoughItems.getProxy().sendPacketToServer(new PacketRequestCheatPermission());
      }

   }
}
