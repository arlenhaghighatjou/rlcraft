package reborncore.common.logic;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import reborncore.common.registration.IRegistryFactory;
import reborncore.common.registration.RegistrationManager;
import reborncore.common.registration.RegistryTarget;

@IRegistryFactory.RegistryFactory
public class LogicControllerRegistryFactory implements IRegistryFactory {
   public LogicControllerRegistryFactory() {
   }

   public Class<? extends Annotation> getAnnotation() {
      return LogicControllerRegistry.class;
   }

   public void handleField(Field field) {
      Class<?> clazz = field.getType();
      if (!Modifier.isStatic(field.getModifiers())) {
         throw new RuntimeException("Field must be static when used with RebornBlockRegistry");
      } else {
         try {
            LogicControllerRegistry annotation = (LogicControllerRegistry)RegistrationManager.getAnnoationFromArray(field.getAnnotations(), this);
            if (annotation == null || annotation.name().isEmpty()) {
               throw new RuntimeException("No name provided");
            }

            LogicController tile = (LogicController)clazz.newInstance();
            tile.setName(annotation.name());
            LogicBlock block = new LogicBlock(tile);
            LogicUtils.registerLogicController(block, tile);
            field.set((Object)null, tile);
         } catch (IllegalAccessException | InstantiationException var6) {
            var6.printStackTrace();
         }

      }
   }

   public List<RegistryTarget> getTargets() {
      return Collections.singletonList(RegistryTarget.FIELD);
   }
}
