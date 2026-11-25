package reborncore.common.registration;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;

public interface IRegistryFactory {
   Class<? extends Annotation> getAnnotation();

   default void handleField(Field field) {
   }

   default void handleMethod(Method method) {
   }

   default void handleClass(Class<?> clazz) {
   }

   default void factoryComplete() {
   }

   default List<RegistryTarget> getTargets() {
      return Arrays.asList(RegistryTarget.CLASS, RegistryTarget.MEHTOD, RegistryTarget.FIELD);
   }

   default Class<? extends FMLStateEvent> getProcessSate() {
      return FMLPreInitializationEvent.class;
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.TYPE})
   public @interface RegistryFactory {
      ExecutionSide side() default ExecutionSide.COMMON;

      String modID() default "reborncore";
   }
}
