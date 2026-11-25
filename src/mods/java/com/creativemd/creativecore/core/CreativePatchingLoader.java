package com.creativemd.creativecore.core;

import com.creativemd.creativecore.transformer.TransformerNames;
import java.io.File;
import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class CreativePatchingLoader implements IFMLLoadingPlugin {
   public static File location;

   public CreativePatchingLoader() {
   }

   public String[] getASMTransformerClass() {
      return null;
   }

   public String getModContainerClass() {
      return CreativeCoreDummy.class.getName();
   }

   public String getSetupClass() {
      return null;
   }

   public void injectData(Map<String, Object> data) {
      location = (File)data.get("coremodLocation");
      TransformerNames.obfuscated = ((Boolean)data.get("runtimeDeobfuscationEnabled")).booleanValue();
   }

   public String getAccessTransformerClass() {
      return null;
   }
}
