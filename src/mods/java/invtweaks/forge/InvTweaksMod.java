package invtweaks.forge;

import invtweaks.api.IItemTreeListener;
import invtweaks.api.InvTweaksAPI;
import invtweaks.api.SortingMethod;
import invtweaks.api.container.ContainerSection;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import org.jetbrains.annotations.NotNull;

@Mod(
   modid = "inventorytweaks",
   dependencies = "required-after:forge@[14.21.0,)",
   acceptableRemoteVersions = "*",
   acceptedMinecraftVersions = "",
   guiFactory = "invtweaks.forge.ModGuiFactory",
   certificateFingerprint = "55d2cd4f5f0961410bf7b91ef6c6bf00a766dcbe"
)
public class InvTweaksMod implements InvTweaksAPI {
   @Instance
   public static InvTweaksMod instance;
   @SidedProxy(
      clientSide = "invtweaks.forge.ClientProxy",
      serverSide = "invtweaks.forge.CommonProxy"
   )
   public static CommonProxy proxy;

   public InvTweaksMod() {
   }

   public static void setTextboxModeStatic(boolean enabled) {
      instance.setTextboxMode(enabled);
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent e) {
      proxy.preInit(e);
   }

   @EventHandler
   public void init(FMLInitializationEvent e) {
      proxy.init(e);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent e) {
      proxy.postInit(e);
   }

   @EventHandler
   public void serverAboutToStart(@NotNull FMLServerAboutToStartEvent e) {
      proxy.serverAboutToStart(e);
   }

   @EventHandler
   public void serverStopped(FMLServerStoppedEvent e) {
      proxy.serverStopped(e);
   }

   public void addOnLoadListener(IItemTreeListener listener) {
      proxy.addOnLoadListener(listener);
   }

   public boolean removeOnLoadListener(IItemTreeListener listener) {
      return proxy.removeOnLoadListener(listener);
   }

   public void setSortKeyEnabled(boolean enabled) {
      proxy.setSortKeyEnabled(enabled);
   }

   public void setTextboxMode(boolean enabled) {
      proxy.setTextboxMode(enabled);
   }

   public int compareItems(@NotNull ItemStack i, @NotNull ItemStack j) {
      return proxy.compareItems(i, j);
   }

   public int compareItems(@NotNull ItemStack i, @NotNull ItemStack j, boolean onlyTreeSort) {
      return proxy.compareItems(i, j, onlyTreeSort);
   }

   public void sort(ContainerSection section, SortingMethod method) {
      proxy.sort(section, method);
   }
}
