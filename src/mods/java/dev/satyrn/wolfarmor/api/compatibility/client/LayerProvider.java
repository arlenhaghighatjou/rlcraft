package dev.satyrn.wolfarmor.api.compatibility.client;

import dev.satyrn.wolfarmor.api.compatibility.Provider;
import java.lang.reflect.InvocationTargetException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;

@SideOnly(Side.CLIENT)
public class LayerProvider extends Provider.ProviderType {
   public LayerProvider() {
   }

   public boolean getProvidesArmorLayer() {
      return true;
   }

   public boolean getProvidesBackpackLayer() {
      return true;
   }

   @Nullable
   public LayerRenderer<?> getArmorLayer(@Nonnull RenderLiving<?> entityRenderer) {
      try {
         Class<?> layerClass = this.getClass().getClassLoader().loadClass("dev.satyrn.wolfarmor.client.renderer.entity.layer.LayerWolfArmor");
         return (LayerRenderer)layerClass.getConstructor(RenderLiving.class).newInstance(entityRenderer);
      } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | ExceptionInInitializerError | NoSuchMethodException | SecurityException | ClassCastException | ClassNotFoundException | InstantiationException var3) {
         LogManager.getLogger("wolfarmor").error(var3);
         return null;
      }
   }

   @Nullable
   public LayerRenderer<?> getBackpackLayer(@Nonnull RenderLiving<?> entityRenderer) {
      try {
         Class<?> layerClass = LayerProvider.class.getClassLoader().loadClass("dev.satyrn.wolfarmor.client.renderer.entity.layer.LayerWolfBackpack");
         return (LayerRenderer)layerClass.getConstructor(RenderLiving.class).newInstance(entityRenderer);
      } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | ExceptionInInitializerError | NoSuchMethodException | SecurityException | ClassCastException | ClassNotFoundException | InstantiationException var3) {
         LogManager.getLogger("wolfarmor").error(var3);
         return null;
      }
   }
}
