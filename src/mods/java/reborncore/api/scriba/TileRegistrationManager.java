package reborncore.api.scriba;

import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.commons.lang3.StringUtils;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class TileRegistrationManager {
   private final String domain;
   private final Logger logger;
   private final ConcurrentHashMap<ResourceLocation, Class<? extends TileEntity>> registeredTiles = new ConcurrentHashMap();

   public TileRegistrationManager(String domain) {
      if (StringUtils.isBlank(domain)) {
         throw new IllegalArgumentException("The domain cannot be blank");
      } else {
         this.domain = domain;
         this.logger = LogManager.getLogger(domain + "|Scriba");
      }
   }

   public void registerTiles() {
      ScanResult result = (new ClassGraph()).enableClassInfo().enableAnnotationInfo().scan();
      Throwable var2 = null;

      try {
         ClassInfoList classInfoList = result.getClassesWithAnnotation(RegisterTile.class.getName());
         classInfoList.filter((reference) -> {
            return reference.extendsSuperclass(TileEntity.class.getName());
         }).forEach((tileReference) -> {
            AnnotationParameterValueList annotationParameters = tileReference.getAnnotationInfo(RegisterTile.class.getName()).getParameterValues();
            String name = (String)annotationParameters.getValue("name");
            ResourceLocation resourceLocation = new ResourceLocation(this.domain, name);
            GameRegistry.registerTileEntity(tileReference.loadClass().asSubclass(TileEntity.class), resourceLocation);
            if (this.registeredTiles.put(resourceLocation, tileReference.loadClass().asSubclass(TileEntity.class)) != null) {
               this.logger.error(String.format("The tile with ResourceLocation %s was already registered before.", resourceLocation.toString()));
            }

         });
         classInfoList.filter((reference) -> {
            return !reference.extendsSuperclass(TileEntity.class.getName());
         }).forEach((badReference) -> {
            this.logger.error(String.format("The class %s annotated with %s was ignored because it is not a subclass of %s. ", badReference.getName(), RegisterTile.class.getName(), TileEntity.class.getName()));
         });
      } catch (Throwable var11) {
         var2 = var11;
         throw var11;
      } finally {
         if (result != null) {
            if (var2 != null) {
               try {
                  result.close();
               } catch (Throwable var10) {
                  var2.addSuppressed(var10);
               }
            } else {
               result.close();
            }
         }

      }

   }
}
