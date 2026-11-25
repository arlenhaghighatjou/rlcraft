package com.creativemd.itemphysic;

import com.creativemd.creativecore.transformer.TransformerNames;
import java.io.File;
import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class ItemPatchingLoader implements IFMLLoadingPlugin {
   public static File location;

   public ItemPatchingLoader() {
   }

   public String[] getASMTransformerClass() {
      return new String[]{ItemTransformer.class.getName()};
   }

   public String getModContainerClass() {
      return ItemDummyContainer.class.getName();
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
