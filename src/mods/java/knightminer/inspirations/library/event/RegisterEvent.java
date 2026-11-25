package knightminer.inspirations.library.event;

import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public abstract class RegisterEvent<T> extends Event {
   private final T recipe;

   public RegisterEvent(T recipe) {
      this.recipe = recipe;
   }

   public T getRecipe() {
      return this.recipe;
   }

   public boolean fire() {
      return !MinecraftForge.EVENT_BUS.post(this);
   }

   public static class RegisterCauldronRecipe extends RegisterEvent<ICauldronRecipe> {
      public RegisterCauldronRecipe(ICauldronRecipe recipe) {
         super(recipe);
      }
   }
}
