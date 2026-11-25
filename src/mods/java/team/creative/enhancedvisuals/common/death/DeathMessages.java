package team.creative.enhancedvisuals.common.death;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.api.ICreativeConfig;
import java.util.Arrays;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class DeathMessages implements ICreativeConfig {
   @CreativeConfig
   public List<String> deathMessages = Arrays.asList("Do you really want to respawn? think of it again.", "Life is hard. Deal with it!", "You are dead ... wait you already know that.", "Did I let the stove on...?", "Should have shot back first...", "Yep, that's messed up...", "Rage incomming!", "I think you dropped something.", "Time for a break?");
   public boolean enabled = true;
   private EaglercraftRandom rand = new EaglercraftRandom();

   public DeathMessages() {
   }

   public String pickRandomDeathMessage() {
      return this.deathMessages.size() == 0 ? null : (String)this.deathMessages.get(this.rand.nextInt(this.deathMessages.size()));
   }

   public void configured() {
   }
}
