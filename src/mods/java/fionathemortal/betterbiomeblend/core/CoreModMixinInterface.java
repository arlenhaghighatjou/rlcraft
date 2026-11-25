package fionathemortal.betterbiomeblend.core;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLCallHook;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

public class CoreModMixinInterface implements IFMLCallHook {
   public CoreModMixinInterface() {
   }

   public void injectData(Map<String, Object> map) {
   }

   public Void call() throws Exception {
      MixinBootstrap.init();
      Mixins.addConfiguration("mixins.betterbiomeblend.json");
      MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
      return null;
   }
}
