package slimeknights.mantle.configurate.objectmapping;

public interface ObjectMapperFactory {
   <T> ObjectMapper<T> getMapper(Class<T> var1) throws ObjectMappingException;
}
