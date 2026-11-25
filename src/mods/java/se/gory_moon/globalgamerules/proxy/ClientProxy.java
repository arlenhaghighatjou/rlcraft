package se.gory_moon.globalgamerules.proxy;

import se.gory_moon.globalgamerules.GlobalGR;
import se.gory_moon.globalgamerules.config.GGRConfig;
import se.gory_moon.globalgamerules.config.GGRConfigGUI;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void setConfigEntryClasses() {
      GlobalGR.getConfig().get("misc", "worldDifficulty", ((GGRConfig.Value)GlobalGR.getConfig().defaults.get("worldDifficulty")).getIntegerValue(), (String)GlobalGR.getConfig().comments.get("worldDifficulty")).setConfigEntryClass(GGRConfigGUI.DifficultyEntry.class);
   }

   public void registerEvents() {
      super.registerEvents();
   }
}
