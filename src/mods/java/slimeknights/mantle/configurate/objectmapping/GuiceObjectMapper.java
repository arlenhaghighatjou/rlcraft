package slimeknights.mantle.configurate.objectmapping;

import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;

class GuiceObjectMapper<T> extends ObjectMapper<T> {
   private final Injector injector;
   private final Key<T> typeKey;

   protected GuiceObjectMapper(Injector injector, Class<T> clazz) throws ObjectMappingException {
      super(clazz);
      this.injector = injector;
      this.typeKey = Key.get(clazz);
   }

   public boolean canCreateInstances() {
      try {
         this.injector.getProvider(this.typeKey);
         return true;
      } catch (ConfigurationException var2) {
         return false;
      }
   }

   protected T constructObject() throws ObjectMappingException {
      return (T)this.injector.getInstance(this.typeKey);
   }
}
