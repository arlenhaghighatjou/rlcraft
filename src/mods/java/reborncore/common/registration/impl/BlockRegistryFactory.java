package reborncore.common.registration.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import reborncore.RebornRegistry;
import reborncore.common.registration.IRegistryFactory;
import reborncore.common.registration.RegistrationManager;
import reborncore.common.registration.RegistryTarget;

@IRegistryFactory.RegistryFactory
public class BlockRegistryFactory implements IRegistryFactory {
   public BlockRegistryFactory() {
   }

   public Class<? extends Annotation> getAnnotation() {
      return BlockRegistry.class;
   }

   public void handleField(Field field) {
      Class<?> clazz = field.getType();
      if (!Modifier.isStatic(field.getModifiers())) {
         throw new RuntimeException("Field must be static when used with RebornBlockRegistry");
      } else {
         try {
            Block block = null;
            BlockRegistry annotation = (BlockRegistry)RegistrationManager.getAnnoationFromArray(field.getAnnotations(), this);
            if (annotation != null && !annotation.param().isEmpty()) {
               String param = annotation.param();
               block = (Block)clazz.getDeclaredConstructor(String.class).newInstance(param);
            }

            if (block == null) {
               block = (Block)clazz.newInstance();
            }

            RebornRegistry.registerBlock(block);
            field.set((Object)null, block);
         } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException var6) {
            var6.printStackTrace();
         }

      }
   }

   public List<RegistryTarget> getTargets() {
      return Collections.singletonList(RegistryTarget.FIELD);
   }
}
