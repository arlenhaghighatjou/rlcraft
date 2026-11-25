package betterquesting.api2.client.gui.controls;

public interface IFieldFilter<T> {
   boolean isValid(String var1);

   String filterText(String var1);

   T parseValue(String var1);
}
