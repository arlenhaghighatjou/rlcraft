package mezz.jei.api.ingredients;

public interface IIngredientBlacklist {
   <V> void addIngredientToBlacklist(V var1);

   <V> void removeIngredientFromBlacklist(V var1);

   <V> boolean isIngredientBlacklisted(V var1);
}
