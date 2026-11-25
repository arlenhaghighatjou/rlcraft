package reborncore.common.registration.impl;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import net.minecraftforge.fml.common.registry.GameRegistry;
import reborncore.common.registration.IRegistryFactory;
import reborncore.common.registration.RegistrationManager;
import reborncore.common.registration.RegistryTarget;

@IRegistryFactory.RegistryFactory
public class TileRegistryFactory implements IRegistryFactory {
   public TileRegistryFactory() {
   }

   public Class<? extends Annotation> getAnnotation() {
      return TileRegistry.class;
   }

   public void handleClass(Class<?> clazz) {
      TileRegistry tileRegistry = (TileRegistry)RegistrationManager.getAnnoationFromArray(clazz.getAnnotations(), this);
      GameRegistry.registerTileEntity(clazz, tileRegistry.name());
   }

   public List<RegistryTarget> getTargets() {
      return Collections.singletonList(RegistryTarget.CLASS);
   }
}
