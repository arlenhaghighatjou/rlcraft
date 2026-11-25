package yalter.mousetweaks.liteloader;

import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.RenderListener;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import java.io.File;
import yalter.mousetweaks.Constants;
import yalter.mousetweaks.Main;
import yalter.mousetweaks.OnTickMethod;

public class LiteModMouseTweaks implements RenderListener, Configurable {
   private static LiteModMouseTweaks instance;

   public static LiteModMouseTweaks getInstance() {
      if (instance == null) {
         instance = new LiteModMouseTweaks();
      }

      return instance;
   }

   public LiteModMouseTweaks() {
   }

   public void init(File file) {
      Main.initialize(Constants.EntryPoint.LITELOADER);
   }

   public void onRender() {
      if (Main.onTickMethod == OnTickMethod.LITELOADER) {
         Main.onUpdateInGame();
         Main.onMouseInput();
      }

   }

   public String getName() {
      return "Mouse Tweaks";
   }

   public String getVersion() {
      return "2.10.1";
   }

   public void onRenderGui(blk guiScreen) {
   }

   public void onSetupCameraTransform() {
   }

   public void upgradeSettings(String s, File file, File file2) {
   }

   public Class<? extends ConfigPanel> getConfigPanelClass() {
      return MouseTweaksConfigPanel.class;
   }
}
