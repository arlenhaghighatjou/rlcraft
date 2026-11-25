package invtweaks.forge.asm;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@TransformerExclusions({"invtweaks.forge.asm", "invtweaks.forge.asm.compatibility"})
@SortingIndex(1001)
@Name("Inventory Tweaks Coremod")
@MCVersion("")
public class FMLPlugin implements IFMLLoadingPlugin {
   public static boolean runtimeDeobfEnabled = false;

   public FMLPlugin() {
   }

   @NotNull
   public String[] getASMTransformerClass() {
      return new String[]{"invtweaks.forge.asm.ContainerTransformer"};
   }

   @NotNull
   public String getAccessTransformerClass() {
      return "invtweaks.forge.asm.ITAccessTransformer";
   }

   @Nullable
   public String getModContainerClass() {
      return null;
   }

   @Nullable
   public String getSetupClass() {
      return null;
   }

   public void injectData(@NotNull Map<String, Object> data) {
      runtimeDeobfEnabled = ((Boolean)data.get("runtimeDeobfuscationEnabled")).booleanValue();
   }
}
