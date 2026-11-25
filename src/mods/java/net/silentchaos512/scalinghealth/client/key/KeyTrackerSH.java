package net.silentchaos512.scalinghealth.client.key;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.silentchaos512.lib.client.key.KeyTrackerSL;
import net.silentchaos512.scalinghealth.client.DifficultyDisplayHandler;
import net.silentchaos512.scalinghealth.config.Config;

public class KeyTrackerSH extends KeyTrackerSL {
   public static final KeyTrackerSH INSTANCE = new KeyTrackerSH();
   private KeyBinding keyShowDifficultyBar = this.createBinding("Difficulty Meter - Show", KeyConflictContext.IN_GAME, KeyModifier.NONE, 49);
   private KeyBinding keyShowDifficultyBarAlways = this.createBinding("Difficulty Meter - Show Always", KeyConflictContext.IN_GAME, KeyModifier.SHIFT, 49);

   private KeyTrackerSH() {
      super("scalinghealth");
   }

   public void onKeyInput(KeyInputEvent event) {
      if (this.keyShowDifficultyBarAlways.isPressed()) {
         Config.Client.Difficulty.renderMeterAlways = !Config.Client.Difficulty.renderMeterAlways;
         Config.INSTANCE.save();
      } else if (this.keyShowDifficultyBar.isPressed()) {
         DifficultyDisplayHandler.INSTANCE.showBar();
      }

   }
}
