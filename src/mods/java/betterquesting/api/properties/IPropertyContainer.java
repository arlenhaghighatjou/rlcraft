package betterquesting.api.properties;

public interface IPropertyContainer {
   <T> T getProperty(IPropertyType<T> var1);

   <T> T getProperty(IPropertyType<T> var1, T var2);

   boolean hasProperty(IPropertyType<?> var1);

   void removeProperty(IPropertyType<?> var1);

   <T> void setProperty(IPropertyType<T> var1, T var2);

   void removeAllProps();
}
