package dev.satyrn.wolfarmor.api.compatibility;

import dev.satyrn.wolfarmor.api.compatibility.client.LayerProvider;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class CompatibilityProvider extends Provider.ProviderType {
   protected static final Logger logger = LogManager.getLogger("wolfarmor");

   public CompatibilityProvider() {
   }

   @SideOnly(Side.CLIENT)
   public void loadComplete_Client(FMLLoadCompleteEvent event) {
   }

   public void loadComplete(FMLLoadCompleteEvent event) {
   }

   @SideOnly(Side.CLIENT)
   public void preInit_Client(FMLPreInitializationEvent event) {
   }

   public void preInit(FMLPreInitializationEvent event) {
   }

   @SideOnly(Side.CLIENT)
   public void init_Client(FMLInitializationEvent event) {
   }

   public void init(FMLInitializationEvent event) {
   }

   @SideOnly(Side.CLIENT)
   public void postInit_Client(FMLPostInitializationEvent event) {
   }

   public void postInit(FMLPostInitializationEvent event) {
   }

   @Nonnull
   @SideOnly(Side.CLIENT)
   public List<LayerProvider> getLayerProviders() {
      return new ArrayList();
   }
}
