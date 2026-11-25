package invtweaks.integration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraftforge.fml.common.Loader;
import org.jetbrains.annotations.Nullable;

public class ItemListChecker {
   @Nullable
   private Method neiHidden = getNeiHidden();
   @Nullable
   private Method jeiShown = getJeiShown();
   private boolean visible = false;
   private boolean wasVisible = false;

   public ItemListChecker() {
   }

   private static Method getNeiHidden() {
      if (Loader.isModLoaded("nei")) {
         try {
            Class<?> clientConfig = Class.forName("codechicken.nei.NEIClientConfig");
            if (clientConfig != null) {
               return clientConfig.getMethod("isHidden");
            }
         } catch (NoSuchMethodException | SecurityException | ClassNotFoundException var1) {
            return null;
         }
      }

      return null;
   }

   private static Method getJeiShown() {
      if (Loader.isModLoaded("jei")) {
         try {
            Class<?> clientConfig = Class.forName("mezz.jei.config.Config");
            if (clientConfig != null) {
               return clientConfig.getMethod("isOverlayEnabled");
            }
         } catch (NoSuchMethodException | SecurityException | ClassNotFoundException var1) {
            return null;
         }
      }

      return null;
   }

   public boolean isVisible() {
      this.wasVisible = this.visible;
      this.visible = this.isNeiVisible() || this.isJeiVisible();
      return this.visible;
   }

   public boolean wasVisible() {
      return this.wasVisible;
   }

   private boolean isNeiVisible() {
      if (this.neiHidden != null) {
         try {
            return !((Boolean)this.neiHidden.invoke((Object)null)).booleanValue();
         } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var2) {
            this.neiHidden = null;
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean isJeiVisible() {
      if (this.jeiShown != null) {
         try {
            return ((Boolean)this.jeiShown.invoke((Object)null)).booleanValue();
         } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var2) {
            this.jeiShown = null;
            return false;
         }
      } else {
         return false;
      }
   }
}
