package slimeknights.mantle.configurate.objectmapping.serialize;

import com.google.common.reflect.TypeToken;
import slimeknights.mantle.configurate.ConfigurationNode;
import slimeknights.mantle.configurate.objectmapping.ObjectMappingException;

public interface TypeSerializer<T> {
   T deserialize(TypeToken<?> var1, ConfigurationNode var2) throws ObjectMappingException;

   void serialize(TypeToken<?> var1, T var2, ConfigurationNode var3) throws ObjectMappingException;
}
