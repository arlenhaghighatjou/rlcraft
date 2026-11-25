package reborncore.api.praescriptum.fuels;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reborncore.api.praescriptum.ingredients.input.FluidStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.InputIngredient;
import reborncore.api.praescriptum.ingredients.input.ItemStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.OreDictionaryInputIngredient;
import reborncore.common.util.ItemUtils;

public class FuelHandler {
   public final String name;
   protected final Logger logger;
   protected final Queue<Fuel> fuels = new ArrayDeque();
   protected final LoadingCache<Collection<InputIngredient<?>>, Fuel> cachedFuels = Caffeine.newBuilder().expireAfterAccess(10L, TimeUnit.MINUTES).maximumSize(100L).<Collection<InputIngredient<?>>, Fuel>build(this::getFuel);

   public FuelHandler(String name) {
      if (StringUtils.isBlank(name)) {
         throw new IllegalArgumentException("The fuel handler name cannot be blank");
      } else {
         this.name = name;
         this.logger = LogManager.getLogger("team_reborn|Praescriptum|" + name);
      }
   }

   public Fuel addFuel() {
      return new Fuel(this);
   }

   public boolean addFuel(Fuel fuel, boolean replace) {
      Objects.requireNonNull(fuel.getInputIngredients(), "The source is null");
      if (fuel.getInputIngredients().size() <= 0) {
         throw new IllegalArgumentException("No sources");
      } else if (fuel.getEnergyOutput() <= 0.0D) {
         throw new IllegalArgumentException("The output is 0");
      } else {
         Queue<InputIngredient<?>> queueOfSources = new ArrayDeque();

         for(InputIngredient<?> ingredient : fuel.getInputIngredients()) {
            if (ingredient.isEmpty()) {
               this.logger.warn(String.format("The %s %s is invalid. Skipping...", ingredient.getClass().getSimpleName(), ingredient.toFormattedString()));
            } else {
               queueOfSources.add(ingredient);
            }
         }

         for(InputIngredient<?> ingredient : queueOfSources) {
            if (ingredient instanceof OreDictionaryInputIngredient && OreDictionary.getOres((String)((OreDictionaryInputIngredient)ingredient).ingredient).isEmpty()) {
               this.logger.warn(String.format("Skipping %s => %s due to the non existence of items that are registered to a provided ore type", queueOfSources, fuel.getEnergyOutput()));
               return false;
            }
         }

         Fuel temp = this.getFuel(queueOfSources);
         if (temp != null) {
            if (!replace) {
               this.logger.error(String.format("Skipping %s => %s due to duplicate source for %s (%s => %s)", queueOfSources, fuel.getEnergyOutput(), queueOfSources, queueOfSources, fuel.getEnergyOutput()));
               return false;
            }

            while(true) {
               if (!this.removeFuel(queueOfSources)) {
                  this.logger.error(String.format("Something went wrong while removing the fuel with sources %s", queueOfSources));
               }

               if (this.getFuel(queueOfSources) == null) {
                  break;
               }
            }
         }

         Fuel newFuel = this.addFuel().addSources(queueOfSources).withEnergyOutput(fuel.getEnergyOutput()).withMetadata(fuel.getMetadata());
         this.fuels.add(newFuel);
         return true;
      }
   }

   protected Fuel getFuel(Collection<InputIngredient<?>> ingredients) {
      Iterator var2 = this.fuels.iterator();

      Fuel fuel;
      while(true) {
         if (!var2.hasNext()) {
            return null;
         }

         fuel = (Fuel)var2.next();
         if (ingredients.size() == fuel.getInputIngredients().size()) {
            Queue<InputIngredient<?>> adjusted = new ArrayDeque(fuel.getInputIngredients());

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

      return fuel;
   }

   public Fuel findFuel(ItemStack itemStack) {
      return ItemUtils.isEmpty(itemStack) ? null : (Fuel)this.cachedFuels.get(Collections.singletonList(ItemStackInputIngredient.copyOf(itemStack)));
   }

   public Fuel findFuel(Collection<ItemStack> itemStacks) {
      Queue<InputIngredient<?>> ingredients = new ArrayDeque();

      for(ItemStack stack : itemStacks) {
         if (!ItemUtils.isEmpty(stack)) {
            ingredients.add(ItemStackInputIngredient.copyOf(stack));
         }
      }

      if (ingredients.isEmpty()) {
         return null;
      } else {
         return this.cachedFuels.get(ingredients);
      }
   }

   public Fuel findFuel2(FluidStack fluidStack) {
      return fluidStack.amount <= 0 ? null : (Fuel)this.cachedFuels.get(Collections.singletonList(FluidStackInputIngredient.copyOf(fluidStack)));
   }

   public Fuel findFuel2(Collection<FluidStack> fluidStacks) {
      Queue<InputIngredient<?>> ingredients = new ArrayDeque();

      for(FluidStack stack : fluidStacks) {
         if (stack.amount <= 0) {
            ingredients.add(FluidStackInputIngredient.copyOf(stack));
         }
      }

      if (ingredients.isEmpty()) {
         return null;
      } else {
         return this.cachedFuels.get(ingredients);
      }
   }

   public Fuel findFuel3(Collection<ItemStack> itemStacks, Collection<FluidStack> fluidStacks) {
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
         return this.cachedFuels.get(ingredients);
      }
   }

   public Fuel findAndApply(ItemStack itemStack, boolean simulate) {
      if (ItemUtils.isEmpty(itemStack)) {
         return null;
      } else {
         List<InputIngredient<?>> ingredients = Collections.singletonList(ItemStackInputIngredient.of(itemStack));
         Fuel fuel = this.cachedFuels.get(ingredients);
         if (fuel == null) {
            return null;
         } else if (ingredients.size() != fuel.getInputIngredients().size()) {
            return null;
         } else {
            Queue<InputIngredient<?>> queueA = new ArrayDeque(fuel.getInputIngredients());

            for(InputIngredient<?> entry : ingredients) {
               queueA.removeIf((temp) -> {
                  return temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount();
               });
            }

            if (!queueA.isEmpty()) {
               return null;
            } else {
               if (!simulate) {
                  Queue<InputIngredient<?>> queueB = new ArrayDeque(fuel.getInputIngredients());

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

               return fuel;
            }
         }
      }
   }

   public Fuel findAndApply(Collection<ItemStack> itemStacks, boolean simulate) {
      Queue<InputIngredient<?>> ingredients = new ArrayDeque();

      for(ItemStack stack : itemStacks) {
         if (!ItemUtils.isEmpty(stack)) {
            ingredients.add(ItemStackInputIngredient.of(stack));
         }
      }

      if (ingredients.isEmpty()) {
         return null;
      } else {
         Fuel fuel = this.cachedFuels.get(ingredients);
         if (fuel == null) {
            return null;
         } else if (ingredients.size() != fuel.getInputIngredients().size()) {
            return null;
         } else {
            Queue<InputIngredient<?>> queueA = new ArrayDeque(fuel.getInputIngredients());

            for(InputIngredient<?> entry : ingredients) {
               queueA.removeIf((temp) -> {
                  return temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount();
               });
            }

            if (!queueA.isEmpty()) {
               return null;
            } else {
               if (!simulate) {
                  Queue<InputIngredient<?>> queueB = new ArrayDeque(fuel.getInputIngredients());

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

               return fuel;
            }
         }
      }
   }

   public Fuel findAndApply2(FluidStack fluidStack, boolean simulate) {
      if (fluidStack.amount <= 0) {
         return null;
      } else {
         List<InputIngredient<?>> ingredients = Collections.singletonList(FluidStackInputIngredient.of(fluidStack));
         Fuel fuel = this.cachedFuels.get(ingredients);
         if (fuel == null) {
            return null;
         } else if (ingredients.size() != fuel.getInputIngredients().size()) {
            return null;
         } else {
            Queue<InputIngredient<?>> queueA = new ArrayDeque(fuel.getInputIngredients());

            for(InputIngredient<?> entry : ingredients) {
               queueA.removeIf((temp) -> {
                  return temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount();
               });
            }

            if (!queueA.isEmpty()) {
               return null;
            } else {
               if (!simulate) {
                  Queue<InputIngredient<?>> queueB = new ArrayDeque(fuel.getInputIngredients());

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

               return fuel;
            }
         }
      }
   }

   public Fuel findAndApply2(Collection<FluidStack> fluidStacks, boolean simulate) {
      Queue<InputIngredient<?>> ingredients = new ArrayDeque();

      for(FluidStack stack : fluidStacks) {
         if (stack.amount <= 0) {
            ingredients.add(FluidStackInputIngredient.of(stack));
         }
      }

      if (ingredients.isEmpty()) {
         return null;
      } else {
         Fuel fuel = this.cachedFuels.get(ingredients);
         if (fuel == null) {
            return null;
         } else if (ingredients.size() != fuel.getInputIngredients().size()) {
            return null;
         } else {
            Queue<InputIngredient<?>> queueA = new ArrayDeque(fuel.getInputIngredients());

            for(InputIngredient<?> entry : ingredients) {
               queueA.removeIf((temp) -> {
                  return temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount();
               });
            }

            if (!queueA.isEmpty()) {
               return null;
            } else {
               if (!simulate) {
                  Queue<InputIngredient<?>> queueB = new ArrayDeque(fuel.getInputIngredients());

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

               return fuel;
            }
         }
      }
   }

   public Fuel findAndApply3(Collection<ItemStack> itemStacks, Collection<FluidStack> fluidStacks, boolean simulate) {
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
         Fuel fuel = this.cachedFuels.get(ingredients);
         if (fuel == null) {
            return null;
         } else if (ingredients.size() != fuel.getInputIngredients().size()) {
            return null;
         } else {
            Queue<InputIngredient<?>> queueA = new ArrayDeque(fuel.getInputIngredients());

            for(InputIngredient<?> entry : ingredients) {
               queueA.removeIf((temp) -> {
                  return temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount();
               });
            }

            if (!queueA.isEmpty()) {
               return null;
            } else {
               if (!simulate) {
                  Queue<InputIngredient<?>> queueB = new ArrayDeque(fuel.getInputIngredients());

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

               return fuel;
            }
         }
      }
   }

   public boolean apply(Fuel fuel, ItemStack itemStack, boolean simulate) {
      if (ItemUtils.isEmpty(itemStack)) {
         return false;
      } else {
         List<InputIngredient<?>> ingredients = Collections.singletonList(ItemStackInputIngredient.of(itemStack));
         if (fuel == null) {
            return false;
         } else if (ingredients.size() != fuel.getInputIngredients().size()) {
            return false;
         } else {
            Queue<InputIngredient<?>> queueA = new ArrayDeque(fuel.getInputIngredients());

            for(InputIngredient<?> entry : ingredients) {
               queueA.removeIf((temp) -> {
                  return temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount();
               });
            }

            if (!queueA.isEmpty()) {
               return false;
            } else {
               if (!simulate) {
                  Queue<InputIngredient<?>> queueB = new ArrayDeque(fuel.getInputIngredients());

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

   public boolean apply(Fuel fuel, Collection<ItemStack> itemStacks, boolean simulate) {
      Queue<InputIngredient<?>> ingredients = new ArrayDeque();

      for(ItemStack stack : itemStacks) {
         if (!ItemUtils.isEmpty(stack)) {
            ingredients.add(ItemStackInputIngredient.of(stack));
         }
      }

      if (ingredients.isEmpty()) {
         return false;
      } else if (fuel == null) {
         return false;
      } else if (ingredients.size() != fuel.getInputIngredients().size()) {
         return false;
      } else {
         Queue<InputIngredient<?>> queueA = new ArrayDeque(fuel.getInputIngredients());

         for(InputIngredient<?> entry : ingredients) {
            queueA.removeIf((temp) -> {
               return temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount();
            });
         }

         if (!queueA.isEmpty()) {
            return false;
         } else {
            if (!simulate) {
               Queue<InputIngredient<?>> queueB = new ArrayDeque(fuel.getInputIngredients());

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

   public boolean apply2(Fuel fuel, FluidStack fluidStack, boolean simulate) {
      if (fluidStack.amount <= 0) {
         return false;
      } else {
         List<InputIngredient<?>> ingredients = Collections.singletonList(FluidStackInputIngredient.of(fluidStack));
         if (fuel == null) {
            return false;
         } else if (ingredients.size() != fuel.getInputIngredients().size()) {
            return false;
         } else {
            Queue<InputIngredient<?>> queueA = new ArrayDeque(fuel.getInputIngredients());

            for(InputIngredient<?> entry : ingredients) {
               queueA.removeIf((temp) -> {
                  return temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount();
               });
            }

            if (!queueA.isEmpty()) {
               return false;
            } else {
               if (!simulate) {
                  Queue<InputIngredient<?>> queueB = new ArrayDeque(fuel.getInputIngredients());

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

   public boolean apply2(Fuel fuel, Collection<FluidStack> fluidStacks, boolean simulate) {
      Queue<InputIngredient<?>> ingredients = new ArrayDeque();

      for(FluidStack stack : fluidStacks) {
         if (stack.amount <= 0) {
            ingredients.add(FluidStackInputIngredient.of(stack));
         }
      }

      if (ingredients.isEmpty()) {
         return false;
      } else if (fuel == null) {
         return false;
      } else if (ingredients.size() != fuel.getInputIngredients().size()) {
         return false;
      } else {
         Queue<InputIngredient<?>> queueA = new ArrayDeque(fuel.getInputIngredients());

         for(InputIngredient<?> entry : ingredients) {
            queueA.removeIf((temp) -> {
               return temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount();
            });
         }

         if (!queueA.isEmpty()) {
            return false;
         } else {
            if (!simulate) {
               Queue<InputIngredient<?>> queueB = new ArrayDeque(fuel.getInputIngredients());

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

   public boolean apply3(Fuel fuel, Collection<ItemStack> itemStacks, Collection<FluidStack> fluidStacks, boolean simulate) {
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
      } else if (fuel == null) {
         return false;
      } else if (ingredients.size() != fuel.getInputIngredients().size()) {
         return false;
      } else {
         Queue<InputIngredient<?>> queueA = new ArrayDeque(fuel.getInputIngredients());

         for(InputIngredient<?> entry : ingredients) {
            queueA.removeIf((temp) -> {
               return temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount();
            });
         }

         if (!queueA.isEmpty()) {
            return false;
         } else {
            if (!simulate) {
               Queue<InputIngredient<?>> queueB = new ArrayDeque(fuel.getInputIngredients());

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

   public boolean removeFuel(Fuel fuel) {
      if (fuel == null) {
         return false;
      } else {
         this.cachedFuels.invalidate(fuel);
         return this.fuels.remove(fuel);
      }
   }

   public boolean removeFuel(Collection<InputIngredient<?>> ingredients) {
      Fuel fuel = this.getFuel(ingredients);
      if (fuel == null) {
         return false;
      } else {
         this.cachedFuels.invalidate(ingredients);
         return this.fuels.remove(fuel);
      }
   }

   public Collection<Fuel> getFuels() {
      return this.fuels;
   }
}
