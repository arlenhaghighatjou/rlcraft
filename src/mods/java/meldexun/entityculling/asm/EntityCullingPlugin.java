package meldexun.entityculling.asm;

import java.util.Map;
import java.util.function.Predicate;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;

@MCVersion("1.12.2")
@TransformerExclusions({"meldexun.entityculling.asm"})
public class EntityCullingPlugin implements IFMLLoadingPlugin {
   public EntityCullingPlugin() {
   }

   public String[] getASMTransformerClass() {
      return new String[]{"meldexun.entityculling.asm.EntityCullingClassTransformer"};
   }

   public String getModContainerClass() {
      return null;
   }

   public String getSetupClass() {
      return null;
   }

   public void injectData(Map<String, Object> data) {
      if (Boolean.FALSE.equals(data.get("runtimeDeobfuscationEnabled"))) {
         MixinBootstrap.init();
         MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
         CoreModManager.getReparseableCoremods().removeIf((s) -> {
            return (void)StringUtils.containsIgnoreCase(s, "renderlib");
         });
      }

   }

   public String getAccessTransformerClass() {
      return null;
   }
}
