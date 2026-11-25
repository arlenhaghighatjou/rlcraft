package betterquesting.api2.client.gui.controls;

public interface IValueIO<T> {
   T readValue();

   void writeValue(T var1);

   T readValueRaw();

   void writeValueRaw(T var1);
}
