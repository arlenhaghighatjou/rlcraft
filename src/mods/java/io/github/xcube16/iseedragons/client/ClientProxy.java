package io.github.xcube16.iseedragons.client;

import io.github.xcube16.iseedragons.DefaultProxy;
import net.minecraft.client.settings.GameSettings.Options;

public class ClientProxy extends DefaultProxy {
   public ClientProxy() {
   }

   public void setBrightness(float min, float max) {
      super.setBrightness(min, max);
      Options.GAMMA.valueMin = min;
      Options.GAMMA.valueMax = max;
   }
}
