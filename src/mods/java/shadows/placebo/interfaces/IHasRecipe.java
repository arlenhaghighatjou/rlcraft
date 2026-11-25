package shadows.placebo.interfaces;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent.Register;

public interface IHasRecipe {
   void initRecipes(Register<IRecipe> var1);
}
