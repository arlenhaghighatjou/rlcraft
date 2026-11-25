package lumien.custommainmenu.lib.actions;

import lumien.custommainmenu.gui.GuiCustom;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.sp.SingleplayerServerController;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenIntegratedServerBusy;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenIntegratedServerStartup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.GuiOldSaveLoadConfirm;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.StartupQuery.AbortedException;

public class ActionLoadWorld implements IAction {
   String dirName;
   String saveName;

   public ActionLoadWorld(String dirName, String saveName) {
      this.dirName = dirName;
      this.saveName = saveName;
   }

   public void perform(Object source, GuiCustom menu) {
      Minecraft mc = Minecraft.getMinecraft();
      mc.displayGuiScreen(new GuiScreenIntegratedServerStartup(menu, () -> requestWorldList(mc, menu)));
   }

   private GuiScreen requestWorldList(Minecraft mc, GuiCustom menu) {
      mc.getSaveLoader().flushCache();
      return new GuiScreenIntegratedServerBusy(menu, "singleplayer.busy.listingworlds",
          "singleplayer.failed.listingworlds", SingleplayerServerController::isReady) {
         @Override
         public void updateScreen() {
            GuiScreen busy = mc.currentScreen;
            super.updateScreen();
            if (busy == this && mc.currentScreen == menu) {
               loadExistingWorld(mc, menu);
            }
         }
      };
   }

   private void loadExistingWorld(Minecraft mc, GuiScreen menu) {
      List<WorldSummary> worlds;
      try {
         worlds = mc.getSaveLoader().getSaveList();
      } catch (AnvilConverterException ex) {
         FMLLog.log.error("Could not read the world list", ex);
         return;
      }
      for (WorldSummary world : worlds) {
         if (this.dirName.equals(world.getFileName())) {
            NBTTagCompound levelDat = world.levelDat;
            if (levelDat != null && levelDat.getCompoundTag("FML").hasKey("ModItemData")) {
               FMLClientHandler.instance().showGuiScreen(new GuiOldSaveLoadConfirm(this.dirName, this.saveName, menu));
            } else {
               try {
                  mc.launchIntegratedServer(this.dirName, this.saveName, null);
               } catch (AbortedException ignored) {
               }
            }
            return;
         }
      }
      FMLLog.warning("Could not find the save named %s.", this.dirName);
   }
}
