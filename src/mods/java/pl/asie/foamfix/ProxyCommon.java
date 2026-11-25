package pl.asie.foamfix;

import com.google.common.cache.CacheBuilder;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.jar.Manifest;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import pl.asie.foamfix.common.TileEntityFasterHopper;
import pl.asie.foamfix.shared.FoamFixShared;

public class ProxyCommon {
   public ProxyCommon() {
   }

   public void preInit() {
      if (FoamFixShared.config.geFasterHopper) {
         TileEntity.register("hopper", TileEntityFasterHopper.class);
         FoamFix.TILE_OVERRIDES.put(TileEntityHopper.class, new ResourceLocation("hopper"));
      }

      if (this.getClass().getClassLoader() instanceof LaunchClassLoader && FoamFixShared.config.lwRemovePackageManifestMap) {
         FoamFix.getLogger().info("Removing LaunchWrapper package manifest map...");

         try {
            LaunchClassLoader loader = (LaunchClassLoader)this.getClass().getClassLoader();
            Field pmField = ReflectionHelper.findField(LaunchClassLoader.class, new String[]{"packageManifests"});
            pmField.set(loader, new Map<Package, Manifest>() {
               public int size() {
                  return 0;
               }

               public boolean isEmpty() {
                  return true;
               }

               public boolean containsKey(Object o) {
                  return false;
               }

               public boolean containsValue(Object o) {
                  return false;
               }

               public Manifest get(Object o) {
                  return null;
               }

               public Manifest put(Package o, Manifest o2) {
                  return o2;
               }

               public Manifest remove(Object o) {
                  return null;
               }

               public void putAll(Map map) {
               }

               public void clear() {
               }

               public Set keySet() {
                  return Collections.emptySet();
               }

               public Collection values() {
                  return Collections.emptySet();
               }

               public Set<Entry<Package, Manifest>> entrySet() {
                  return Collections.emptySet();
               }
            });
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      }

   }

   public void init() {
      if (this.getClass().getClassLoader() instanceof LaunchClassLoader && FoamFixShared.config.lwWeakenResourceCache) {
         FoamFix.getLogger().info("Weakening LaunchWrapper resource cache...");

         try {
            LaunchClassLoader loader = (LaunchClassLoader)this.getClass().getClassLoader();
            Field resourceCacheField = ReflectionHelper.findField(LaunchClassLoader.class, new String[]{"resourceCache"});
            Map oldResourceCache = (Map)resourceCacheField.get(loader);
            Map newResourceCache = CacheBuilder.newBuilder().weakValues().build().asMap();
            newResourceCache.putAll(oldResourceCache);
            resourceCacheField.set(loader, newResourceCache);
         } catch (Exception var5) {
            var5.printStackTrace();
         }
      }

   }

   public void postInit() {
   }

   public void refreshResources() {
   }

   public void updateFasterAnimationFlag() {
   }

   public void openUrlLinux(URI url) {
   }
}
