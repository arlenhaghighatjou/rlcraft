package vazkii.quark.base.command;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class ConfigCategoryDescriptor {
   @Nullable
   public final ConfigCategory parentCategory;
   @Nonnull
   public final Configuration config;
   @Nonnull
   public final String key;
   @Nullable
   public final String parentKey;

   public ConfigCategoryDescriptor(@Nullable ConfigCategory parentCategory, @Nonnull Configuration config, @Nonnull String key, @Nullable String parentKey) {
      this.parentCategory = parentCategory;
      this.config = config;
      this.key = key;
      this.parentKey = parentKey;
   }

   public ConfigCategoryDescriptor(@Nullable ConfigCategory parentCategory, @Nonnull Configuration config, @Nonnull String key) {
      this(parentCategory, config, key, (String)null);
   }

   @Nullable
   public ConfigCategory category() {
      return this.parentCategory == null ? this.config.getCategory(this.key) : (ConfigCategory)this.parentCategory.getChildren().stream().filter((category) -> {
         return category.getName().equals(this.key);
      }).findFirst().orElse((Object)null);
   }

   public Set<String> subCategories() {
      return this.parentCategory == null ? this.config.getCategoryNames() : (Set)this.parentCategory.getChildren().stream().map(ConfigCategory::getName).collect(Collectors.toSet());
   }

   public String getFullKey() {
      return this.parentKey == null ? this.key : this.parentKey + "." + this.key;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ConfigCategoryDescriptor that = (ConfigCategoryDescriptor)o;
         return Objects.equals(this.parentCategory, that.parentCategory) && Objects.equals(this.config, that.config) && Objects.equals(this.key, that.key) && Objects.equals(this.parentKey, that.parentKey);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.parentCategory, this.config, this.key, this.parentKey});
   }
}
