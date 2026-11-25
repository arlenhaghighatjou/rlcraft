package crafttweaker;

public interface IAction {
   void apply();

   String describe();

   default boolean validate() {
      return true;
   }

   default String describeInvalid() {
      return "Valid by default!";
   }
}
