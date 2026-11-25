package reborncore.api.praescriptum.recipes;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.StringUtils;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import reborncore.api.praescriptum.ingredients.input.FluidStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.InputIngredient;
import reborncore.api.praescriptum.ingredients.input.ItemStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.OreDictionaryInputIngredient;
import reborncore.api.praescriptum.ingredients.output.OutputIngredient;
import reborncore.common.util.FluidUtils;
import reborncore.common.util.ItemUtils;

public class RecipeHandler {
   public final String name;
   protected final Logger logger;
   protected final Queue<Recipe> recipes = new ArrayDeque();
   protected final LoadingCache<Collection<InputIngredient<?>>, Recipe> cachedRecipes = Caffeine.newBuilder().expireAfterAccess(10L, TimeUnit.MINUTES).maximumSize(100L).<Collection<InputIngredient<?>>, Recipe>build(this::getRecipe);

   public RecipeHandler(String name) {
      if (StringUtils.isBlank(name)) {
         throw new IllegalArgumentException("The recipe handler name cannot be blank");
      } else {
         this.name = name;
         this.logger = LogManager.getLogger("team_reborn|Praescriptum|" + name);
      }
   }

   public Recipe createRecipe() {
      return new Recipe(this);
   }

   public boolean addRecipe(Recipe recipe, boolean replace) {
      Objects.requireNonNull(recipe.getInputIngredients(), "The input is null");
      if (recipe.getInputIngredients().size() <= 0) {
         throw new IllegalArgumentException("No inputs");
      } else if (recipe.getItemOutputs().length <= 0 && recipe.getFluidOutputs().length <= 0) {
         throw new IllegalArgumentException("No outputs");
      } else {
         Queue<InputIngredient<?>> queueOfInputs = new ArrayDeque();

         for(InputIngredient<?> ingredient : recipe.getInputIngredients()) {
            if (ingredient.isEmpty()) {
               this.logger.warn(String.format("%s: The %s %s is invalid. Skipping...", this.name, ingredient.getClass().getSimpleName(), ingredient.toFormattedString()));
               return false;
            }

            queueOfInputs.add(ingredient);
         }

         for(ItemStack itemOutput : recipe.getItemOutputs()) {
            if (ItemUtils.isEmpty(itemOutput)) {
               this.logger.warn(String.format("%s: The ItemStack %s is invalid. Skipping...", this.name, ItemUtils.toFormattedString(itemOutput)));
               return false;
            }
         }

         for(FluidStack fluidOutput : recipe.getFluidOutputs()) {
            if (fluidOutput.getFluid() == null || fluidOutput.amount <= 0) {
               this.logger.warn(String.format("%s: The FluidStack %s is invalid. Skipping...", this.name, FluidUtils.toFormattedString(fluidOutput)));
               return false;
            }
         }

         for(InputIngredient<?> inputIngredient : queueOfInputs) {
            if (inputIngredient instanceof OreDictionaryInputIngredient && OreDictionary.getOres((String)((OreDictionaryInputIngredient)inputIngredient).ingredient).isEmpty()) {
               this.logger.warn(String.format("%s: Skipping a recipe with input %s due to the non existence of items that are registered to a provided ore type", this.name, inputIngredient.ingredient));
               return false;
            }
         }

         Recipe temp;
         if (recipe.shouldUseNBT()) {
            temp = this.getRecipeStrict(queueOfInputs);
         } else {
            temp = this.getRecipe(queueOfInputs);
         }

         if (temp != null) {
            if (!replace) {
               this.logger.error(String.format("%s: Skipping recipe with input %s due to the existence of an equal input", this.name, queueOfInputs));
               return false;
            }

            while(true) {
               if (!this.removeRecipe(queueOfInputs)) {
                  this.logger.error(String.format("%s: Something went wrong while removing the recipe with inputs %s", this.name, queueOfInputs));
               }

               if (this.getRecipe(queueOfInputs) == null) {
                  break;
               }
            }
         }

         Recipe newRecipe = this.createRecipe().withInput(queueOfInputs).withOutput(recipe.getItemOutputs(), recipe.getFluidOutputs()).withMetadata(recipe.getMetadata());
         this.recipes.add(newRecipe);
         return true;
      }
   }

   public Recipe getRecipe(Collection<InputIngredient<?>> ingredients) {
      Iterator var2 = this.recipes.iterator();

      Recipe recipe;
      while(true) {
         if (!var2.hasNext()) {
            return null;
         }

         recipe = (Recipe)var2.next();
         if (ingredients.size() == recipe.getInputIngredients().size()) {
            Queue<InputIngredient<?>> adjusted = new ArrayDeque(recipe.getInputIngredients());

            for(InputIngredient<?> entry : ingredients) {
               adjusted.removeIf((temp) -> {
                  return temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount();
               });
            }

            if (adjusted.isEmpty()) {
               break;
            }
         }
      }

      return recipe;
   }

   public Recipe getRecipeStrict(Collection<InputIngredient<?>> ingredients) {
      Iterator var2 = this.recipes.iterator();

      Recipe recipe;
      while(true) {
         if (!var2.hasNext()) {
            return null;
         }

         recipe = (Recipe)var2.next();
         if (ingredients.size() == recipe.getInputIngredients().size()) {
            Queue<InputIngredient<?>> adjusted = new ArrayDeque(recipe.getInputIngredients());

            for(InputIngredient<?> entry : ingredients) {
               adjusted.removeIf((temp) -> {
                  return temp.matchesStrict(entry.ingredient) && entry.getCount() >= temp.getCount();
               });
            }

            if (adjusted.isEmpty()) {
               break;
            }
         }
      }

      return recipe;
   }

   public Recipe getRecipeByOutput(Collection<OutputIngredient<?>> ingredients) {
      Iterator var2 = this.recipes.iterator();

      Recipe recipe;
      while(true) {
         if (!var2.hasNext()) {
            return null;
         }

         recipe = (Recipe)var2.next();
         if (ingredients.size() == recipe.getItemOutputs().length) {
            Queue<ItemStack> adjusted = new LinkedList(Arrays.asList(recipe.getItemOutputs()));

            for(OutputIngredient<?> entry : ingredients) {
               adjusted.removeIf((temp) -> {
                  return entry.matches(temp) && entry.getCount() >= temp.getCount();
               });
            }

            if (adjusted.isEmpty()) {
               break;
            }
         }
      }

      return recipe;
   }

   public Recipe findRecipe(ItemStack itemStack) {
      return ItemUtils.isEmpty(itemStack) ? null : (Recipe)this.cachedRecipes.get(Collections.singletonList(ItemStackInputIngredient.copyOf(itemStack)));
   }

   public Recipe findRecipe(Collection<ItemStack> itemStacks) {
      Queue<InputIngredient<?>> ingredients = new ArrayDeque();

      for(ItemStack stack : itemStacks) {
         if (!ItemUtils.isEmpty(stack)) {
            ingredients.add(ItemStackInputIngredient.copyOf(stack));
         }
      }

      if (ingredients.isEmpty()) {
         return null;
      } else {
         return this.cachedRecipes.get(ingredients);
      }
   }

   public Recipe findRecipe2(FluidStack fluidStack) {
      return fluidStack.amount <= 0 ? null : (Recipe)this.cachedRecipes.get(Collections.singletonList(FluidStackInputIngredient.copyOf(fluidStack)));
   }

   public Recipe findRecipe2(Collection<FluidStack> fluidStacks) {
      Queue<InputIngredient<?>> ingredients = new ArrayDeque();

      for(FluidStack stack : fluidStacks) {
         if (stack.amount <= 0) {
            ingredients.add(FluidStackInputIngredient.copyOf(stack));
         }
      }

      if (ingredients.isEmpty()) {
         return null;
      } else {
         return this.cachedRecipes.get(ingredients);
      }
   }

   public Recipe findRecipe3(Collection<ItemStack> itemStacks, Collection<FluidStack> fluidStacks) {
      Queue<InputIngredient<?>> ingredients = new ArrayDeque();

      for(ItemStack stack : itemStacks) {
         if (!ItemUtils.isEmpty(stack)) {
            ingredients.add(ItemStackInputIngredient.copyOf(stack));
         }
      }

      for(FluidStack stack : fluidStacks) {
         if (stack.amount <= 0) {
            ingredients.add(FluidStackInputIngredient.copyOf(stack));
         }
      }

      if (ingredients.isEmpty()) {
         return null;
      } else {
         return this.cachedRecipes.get(ingredients);
      }
   }

   public Recipe findAndApply(ItemStack itemStack, boolean simulate) {
      if (ItemUtils.isEmpty(itemStack)) {
         return null;
      } else {
         List<InputIngredient<?>> ingredients = Collections.singletonList(ItemStackInputIngredient.of(itemStack));
         Recipe recipe = this.cachedRecipes.get(ingredients);
         if (recipe == null) {
            return null;
         } else if (ingredients.size() != recipe.getInputIngredients().size()) {
            return null;
         } else {
            Queue<InputIngredient<?>> queueA = new ArrayDeque(recipe.getInputIngredients());

            for(InputIngredient<?> entry : ingredients) {
               queueA.removeIf((temp) -> {
                  return temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount();
               });
            }

            if (!queueA.isEmpty()) {
               return null;
            } else {
               if (!simulate) {
                  Queue<InputIngredient<?>> queueB = new ArrayDeque(recipe.getInputIngredients());

                  for(InputIngredient<?> entry : ingredients) {
                     queueB.removeIf((temp) -> {
                        if (temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount()) {
                           entry.shrink(temp.getCount());
                           return true;
                        } else {
                           return false;
                        }
                     });
                  }
               }

               return recipe;
            }
         }
      }
   }

   public Recipe findAndApply(Collection<ItemStack> itemStacks, boolean simulate) {
      Queue<InputIngredient<?>> ingredients = new ArrayDeque();

      for(ItemStack stack : itemStacks) {
         if (!ItemUtils.isEmpty(stack)) {
            ingredients.add(ItemStackInputIngredient.of(stack));
         }
      }

      if (ingredients.isEmpty()) {
         return null;
      } else {
         Recipe recipe = this.cachedRecipes.get(ingredients);
         if (recipe == null) {
            return null;
         } else if (ingredients.size() != recipe.getInputIngredients().size()) {
            return null;
         } else {
            Queue<InputIngredient<?>> queueA = new ArrayDeque(recipe.getInputIngredients());

            for(InputIngredient<?> entry : ingredients) {
               queueA.removeIf((temp) -> {
                  return temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount();
               });
            }

            if (!queueA.isEmpty()) {
               return null;
            } else {
               if (!simulate) {
                  Queue<InputIngredient<?>> queueB = new ArrayDeque(recipe.getInputIngredients());

                  for(InputIngredient<?> entry : ingredients) {
                     queueB.removeIf((temp) -> {
                        if (temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount()) {
                           entry.shrink(temp.getCount());
                           return true;
                        } else {
                           return false;
                        }
                     });
                  }
               }

               return recipe;
            }
         }
      }
   }

   public Recipe findAndApply2(FluidStack fluidStack, boolean simulate) {
      if (fluidStack.amount <= 0) {
         return null;
      } else {
         List<InputIngredient<?>> ingredients = Collections.singletonList(FluidStackInputIngredient.of(fluidStack));
         Recipe recipe = this.cachedRecipes.get(ingredients);
         if (recipe == null) {
            return null;
         } else if (ingredients.size() != recipe.getInputIngredients().size()) {
            return null;
         } else {
            Queue<InputIngredient<?>> queueA = new ArrayDeque(recipe.getInputIngredients());

            for(InputIngredient<?> entry : ingredients) {
               queueA.removeIf((temp) -> {
                  return temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount();
               });
            }

            if (!queueA.isEmpty()) {
               return null;
            } else {
               if (!simulate) {
                  Queue<InputIngredient<?>> queueB = new ArrayDeque(recipe.getInputIngredients());

                  for(InputIngredient<?> entry : ingredients) {
                     queueB.removeIf((temp) -> {
                        if (temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount()) {
                           entry.shrink(temp.getCount());
                           return true;
                        } else {
                           return false;
                        }
                     });
                  }
               }

               return recipe;
            }
         }
      }
   }

   public Recipe findAndApply2(Collection<FluidStack> fluidStacks, boolean simulate) {
      Queue<InputIngredient<?>> ingredients = new ArrayDeque();

      for(FluidStack stack : fluidStacks) {
         if (stack.amount <= 0) {
            ingredients.add(FluidStackInputIngredient.of(stack));
         }
      }

      if (ingredients.isEmpty()) {
         return null;
      } else {
         Recipe recipe = this.cachedRecipes.get(ingredients);
         if (recipe == null) {
            return null;
         } else if (ingredients.size() != recipe.getInputIngredients().size()) {
            return null;
         } else {
            Queue<InputIngredient<?>> queueA = new ArrayDeque(recipe.getInputIngredients());

            for(InputIngredient<?> entry : ingredients) {
               queueA.removeIf((temp) -> {
                  return temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount();
               });
            }

            if (!queueA.isEmpty()) {
               return null;
            } else {
               if (!simulate) {
                  Queue<InputIngredient<?>> queueB = new ArrayDeque(recipe.getInputIngredients());

                  for(InputIngredient<?> entry : ingredients) {
                     queueB.removeIf((temp) -> {
                        if (temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount()) {
                           entry.shrink(temp.getCount());
                           return true;
                        } else {
                           return false;
                        }
                     });
                  }
               }

               return recipe;
            }
         }
      }
   }

   public Recipe findAndApply3(Collection<ItemStack> itemStacks, Collection<FluidStack> fluidStacks, boolean simulate) {
      Queue<InputIngredient<?>> ingredients = new ArrayDeque();

      for(ItemStack stack : itemStacks) {
         if (!ItemUtils.isEmpty(stack)) {
            ingredients.add(ItemStackInputIngredient.of(stack));
         }
      }

      for(FluidStack stack : fluidStacks) {
         if (stack.amount <= 0) {
            ingredients.add(FluidStackInputIngredient.of(stack));
         }
      }

      if (ingredients.isEmpty()) {
         return null;
      } else {
         Recipe recipe = this.cachedRecipes.get(ingredients);
         if (recipe == null) {
            return null;
         } else if (ingredients.size() != recipe.getInputIngredients().size()) {
            return null;
         } else {
            Queue<InputIngredient<?>> queueA = new ArrayDeque(recipe.getInputIngredients());

            for(InputIngredient<?> entry : ingredients) {
               queueA.removeIf((temp) -> {
                  return temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount();
               });
            }

            if (!queueA.isEmpty()) {
               return null;
            } else {
               if (!simulate) {
                  Queue<InputIngredient<?>> queueB = new ArrayDeque(recipe.getInputIngredients());

                  for(InputIngredient<?> entry : ingredients) {
                     queueB.removeIf((temp) -> {
                        if (temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount()) {
                           entry.shrink(temp.getCount());
                           return true;
                        } else {
                           return false;
                        }
                     });
                  }
               }

               return recipe;
            }
         }
      }
   }

   public boolean apply(Recipe recipe, ItemStack itemStack, boolean simulate) {
      if (ItemUtils.isEmpty(itemStack)) {
         return false;
      } else {
         List<InputIngredient<?>> ingredients = Collections.singletonList(ItemStackInputIngredient.of(itemStack));
         if (recipe == null) {
            return false;
         } else if (ingredients.size() != recipe.getInputIngredients().size()) {
            return false;
         } else {
            Queue<InputIngredient<?>> queueA = new ArrayDeque(recipe.getInputIngredients());

            for(InputIngredient<?> entry : ingredients) {
               queueA.removeIf((temp) -> {
                  return temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount();
               });
            }

            if (!queueA.isEmpty()) {
               return false;
            } else {
               if (!simulate) {
                  Queue<InputIngredient<?>> queueB = new ArrayDeque(recipe.getInputIngredients());

                  for(InputIngredient<?> entry : ingredients) {
                     queueB.removeIf((temp) -> {
                        if (temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount()) {
                           entry.shrink(temp.getCount());
                           return true;
                        } else {
                           return false;
                        }
                     });
                  }
               }

               return true;
            }
         }
      }
   }

   public boolean apply(Recipe recipe, Collection<ItemStack> itemStacks, boolean simulate) {
      Queue<InputIngredient<?>> ingredients = new ArrayDeque();

      for(ItemStack stack : itemStacks) {
         if (!ItemUtils.isEmpty(stack)) {
            ingredients.add(ItemStackInputIngredient.of(stack));
         }
      }

      if (ingredients.isEmpty()) {
         return false;
      } else if (recipe == null) {
         return false;
      } else if (ingredients.size() != recipe.getInputIngredients().size()) {
         return false;
      } else {
         Queue<InputIngredient<?>> queueA = new ArrayDeque(recipe.getInputIngredients());

         for(InputIngredient<?> entry : ingredients) {
            queueA.removeIf((temp) -> {
               return temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount();
            });
         }

         if (!queueA.isEmpty()) {
            return false;
         } else {
            if (!simulate) {
               Queue<InputIngredient<?>> queueB = new ArrayDeque(recipe.getInputIngredients());

               for(InputIngredient<?> entry : ingredients) {
                  queueB.removeIf((temp) -> {
                     if (temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount()) {
                        entry.shrink(temp.getCount());
                        return true;
                     } else {
                        return false;
                     }
                  });
               }
            }

            return true;
         }
      }
   }

   public boolean apply2(Recipe recipe, FluidStack fluidStack, boolean simulate) {
      if (fluidStack.amount <= 0) {
         return false;
      } else {
         List<InputIngredient<?>> ingredients = Collections.singletonList(FluidStackInputIngredient.of(fluidStack));
         if (recipe == null) {
            return false;
         } else if (ingredients.size() != recipe.getInputIngredients().size()) {
            return false;
         } else {
            Queue<InputIngredient<?>> queueA = new ArrayDeque(recipe.getInputIngredients());

            for(InputIngredient<?> entry : ingredients) {
               queueA.removeIf((temp) -> {
                  return temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount();
               });
            }

            if (!queueA.isEmpty()) {
               return false;
            } else {
               if (!simulate) {
                  Queue<InputIngredient<?>> queueB = new ArrayDeque(recipe.getInputIngredients());

                  for(InputIngredient<?> entry : ingredients) {
                     queueB.removeIf((temp) -> {
                        if (temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount()) {
                           entry.shrink(temp.getCount());
                           return true;
                        } else {
                           return false;
                        }
                     });
                  }
               }

               return true;
            }
         }
      }
   }

   public boolean apply2(Recipe recipe, Collection<FluidStack> fluidStacks, boolean simulate) {
      Queue<InputIngredient<?>> ingredients = new ArrayDeque();

      for(FluidStack stack : fluidStacks) {
         if (stack.amount <= 0) {
            ingredients.add(FluidStackInputIngredient.of(stack));
         }
      }

      if (ingredients.isEmpty()) {
         return false;
      } else if (recipe == null) {
         return false;
      } else if (ingredients.size() != recipe.getInputIngredients().size()) {
         return false;
      } else {
         Queue<InputIngredient<?>> queueA = new ArrayDeque(recipe.getInputIngredients());

         for(InputIngredient<?> entry : ingredients) {
            queueA.removeIf((temp) -> {
               return temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount();
            });
         }

         if (!queueA.isEmpty()) {
            return false;
         } else {
            if (!simulate) {
               Queue<InputIngredient<?>> queueB = new ArrayDeque(recipe.getInputIngredients());

               for(InputIngredient<?> entry : ingredients) {
                  queueB.removeIf((temp) -> {
                     if (temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount()) {
                        entry.shrink(temp.getCount());
                        return true;
                     } else {
                        return false;
                     }
                  });
               }
            }

            return true;
         }
      }
   }

   public boolean apply3(Recipe recipe, Collection<ItemStack> itemStacks, Collection<FluidStack> fluidStacks, boolean simulate) {
      Queue<InputIngredient<?>> ingredients = new ArrayDeque();

      for(ItemStack stack : itemStacks) {
         if (!ItemUtils.isEmpty(stack)) {
            ingredients.add(ItemStackInputIngredient.of(stack));
         }
      }

      for(FluidStack stack : fluidStacks) {
         if (stack.amount <= 0) {
            ingredients.add(FluidStackInputIngredient.of(stack));
         }
      }

      if (ingredients.isEmpty()) {
         return false;
      } else if (recipe == null) {
         return false;
      } else if (ingredients.size() != recipe.getInputIngredients().size()) {
         return false;
      } else {
         Queue<InputIngredient<?>> queueA = new ArrayDeque(recipe.getInputIngredients());

         for(InputIngredient<?> entry : ingredients) {
            queueA.removeIf((temp) -> {
               return temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount();
            });
         }

         if (!queueA.isEmpty()) {
            return false;
         } else {
            if (!simulate) {
               Queue<InputIngredient<?>> queueB = new ArrayDeque(recipe.getInputIngredients());

               for(InputIngredient<?> entry : ingredients) {
                  queueB.removeIf((temp) -> {
                     if (temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount()) {
                        entry.shrink(temp.getCount());
                        return true;
                     } else {
                        return false;
                     }
                  });
               }
            }

            return true;
         }
      }
   }

   public boolean removeRecipe(Recipe recipe) {
      if (recipe == null) {
         return false;
      } else {
         this.cachedRecipes.invalidate(recipe);
         return this.recipes.remove(recipe);
      }
   }

   public boolean removeRecipe(Collection<InputIngredient<?>> ingredients) {
      Recipe recipe = this.getRecipe(ingredients);
      if (recipe == null) {
         return false;
      } else {
         this.cachedRecipes.invalidate(ingredients);
         return this.recipes.remove(recipe);
      }
   }

   public Collection<Recipe> getRecipes() {
      return this.recipes;
   }
}
