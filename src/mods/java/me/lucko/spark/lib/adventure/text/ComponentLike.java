package me.lucko.spark.lib.adventure.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ComponentLike {
   @NotNull
   static List<Component> asComponents(@NotNull List<? extends ComponentLike> likes) {
      return asComponents(likes, (Predicate)null);
   }

   @NotNull
   static List<Component> asComponents(@NotNull List<? extends ComponentLike> likes, @Nullable Predicate<? super Component> filter) {
      if (likes.isEmpty()) {
         return Collections.emptyList();
      } else {
         int size = likes.size();
         ArrayList<Component> components = null;

         for(int i = 0; i < size; ++i) {
            ComponentLike like = (ComponentLike)likes.get(i);
            Component component = like.asComponent();
            if (filter == null || filter.test(component)) {
               if (components == null) {
                  components = new ArrayList(size);
               }

               components.add(component);
            }
         }

         if (components != null) {
            components.trimToSize();
         }

         if (components == null) {
            return Collections.emptyList();
         } else {
            return Collections.unmodifiableList(components);
         }
      }
   }

   @Nullable
   static Component unbox(@Nullable ComponentLike like) {
      return like != null ? like.asComponent() : null;
   }

   @Contract(
      pure = true
   )
   @NotNull
   Component asComponent();
}
