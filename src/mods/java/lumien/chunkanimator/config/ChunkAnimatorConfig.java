package lumien.chunkanimator.config;

import java.util.List;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ChunkAnimatorConfig {
   Property propertyMode;
   int mode;
   Property propertyEasingFunction;
   int easingFunction;
   Property propertyAnimationDuration;
   int animationDuration;
   Property propertyDisableAroundPlayer;
   boolean disableAroundPlayer;
   Configuration config;

   public ChunkAnimatorConfig() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      this.config = new Configuration(event.getSuggestedConfigurationFile());
      this.config.load();
      this.propertyMode = this.config.get("settings", "Mode", 0, "How should the chunks be animated?\n 0: Chunks always appear from below\n 1: Chunks always appear from above\n 2: Chunks appear from below if they are lower than the Horizon and from above if they are higher than the Horizon\n 3: Chunks \"slide in\" from their respective cardinal direction (Relative to the Player)\n 4: Same as 3 but the cardinal direction of a chunk is determined slightly different (Just try both :D)");
      this.propertyEasingFunction = this.config.get("settings", "EasingFunction", 6, "The function that should be used to control the movement of chunks in ALL animation modes\nIf you want a visual comparison there is a link on the curseforge page\n0: Linear, 1: Quadratic, 2: Cubic, 3: Quartic, 4: Quintic, 5: Expo, 6: Sin, 7: Circle, 8: Back, 9: Bounce, 10: Elastic");
      this.propertyAnimationDuration = this.config.get("settings", "AnimationDuration", 1000, "How long should the animation last? (In milliseconds)");
      this.propertyDisableAroundPlayer = this.config.get("settings", "DisableAroundPlayer", false, "If enabled chunks that are next to the player will not animate");
      this.syncConfig();
   }

   public void syncConfig() {
      this.mode = this.propertyMode.getInt();
      this.easingFunction = this.propertyEasingFunction.getInt();
      this.animationDuration = this.propertyAnimationDuration.getInt();
      this.disableAroundPlayer = this.propertyDisableAroundPlayer.getBoolean();
      if (this.config.hasChanged()) {
         this.config.save();
      }

   }

   public int getMode() {
      return this.mode;
   }

   public boolean disableAroundPlayer() {
      return this.disableAroundPlayer;
   }

   public int getAnimationDuration() {
      return this.animationDuration;
   }

   public String getString() {
      return this.config.toString();
   }

   public List<IConfigElement> getConfigElements() {
      return (new ConfigElement(this.config.getCategory("settings"))).getChildElements();
   }

   public int getEasingFunction() {
      return this.easingFunction;
   }
}
