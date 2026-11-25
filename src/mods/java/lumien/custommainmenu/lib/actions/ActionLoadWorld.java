package lumien.custommainmenu.lib.actions;

import java.io.File;
import java.io.FileInputStream;
import lumien.custommainmenu.gui.GuiCustom;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSettings;
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
      File dir = new File(FMLClientHandler.instance().getSavesDir(), this.dirName);

      NBTTagCompound leveldat;
      try {
         leveldat = CompressedStreamTools.readCompressed(new FileInputStream(new File(dir, "level.dat")));
      } catch (Exception var9) {
         try {
            leveldat = CompressedStreamTools.readCompressed(new FileInputStream(new File(dir, "level.dat_old")));
         } catch (Exception var8) {
            FMLLog.warning("There appears to be a problem loading the save %s, both level files are unreadable.", new Object[]{this.dirName});
            return;
         }
      }

      NBTTagCompound fmlData = leveldat.getCompoundTag("FML");
      if (fmlData.hasKey("ModItemData")) {
         FMLClientHandler.instance().showGuiScreen(new GuiOldSaveLoadConfirm(this.dirName, this.saveName, menu));
      } else {
         try {
            Minecraft.getMinecraft().launchIntegratedServer(this.dirName, this.saveName, (WorldSettings)null);
         } catch (AbortedException var7) {
            ;
         }
      }

   }
}
