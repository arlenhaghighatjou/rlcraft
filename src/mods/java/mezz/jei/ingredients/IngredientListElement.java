package mezz.jei.ingredients;

import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.startup.IModIdHelper;
import mezz.jei.util.LegacyUtil;
import mezz.jei.util.Log;
import mezz.jei.util.Translator;

public class IngredientListElement<V> implements IIngredientListElement<V> {
   private static final Pattern SPACE_PATTERN = Pattern.compile("\\s");
   private final V ingredient;
   private final int orderIndex;
   private final IIngredientHelper<V> ingredientHelper;
   private final IIngredientRenderer<V> ingredientRenderer;
   private final String displayName;
   private final List<String> modIds;
   private final List<String> modNames;
   private final String resourceId;
   private boolean visible = true;

   @Nullable
   public static <V> IngredientListElement<V> create(V ingredient, IIngredientHelper<V> ingredientHelper, IIngredientRenderer<V> ingredientRenderer, IModIdHelper modIdHelper, int orderIndex) {
      try {
         return new IngredientListElement<V>(ingredient, orderIndex, ingredientHelper, ingredientRenderer, modIdHelper);
      } catch (RuntimeException var8) {
         RuntimeException e = var8;

         try {
            String ingredientInfo = ingredientHelper.getErrorInfo(ingredient);
            Log.get().warn("Found a broken ingredient {}", ingredientInfo, e);
         } catch (RuntimeException var7) {
            Log.get().warn("Found a broken ingredient.", var7);
         }

         return null;
      }
   }

   protected IngredientListElement(V ingredient, int orderIndex, IIngredientHelper<V> ingredientHelper, IIngredientRenderer<V> ingredientRenderer, IModIdHelper modIdHelper) {
      this.ingredient = ingredient;
      this.orderIndex = orderIndex;
      this.ingredientHelper = ingredientHelper;
      this.ingredientRenderer = ingredientRenderer;
      String displayModId = ingredientHelper.getDisplayModId(ingredient);
      String modId = ingredientHelper.getModId(ingredient);
      this.modIds = new ArrayList();
      this.modIds.add(displayModId);
      if (!modId.equals(displayModId)) {
         this.modIds.add(modId);
      }

      this.modNames = (List)this.modIds.stream().map(modIdHelper::getModNameForModId).collect(Collectors.toList());
      this.displayName = IngredientInformation.getDisplayName(ingredient, ingredientHelper);
      this.resourceId = LegacyUtil.getResourceId(ingredient, ingredientHelper);
   }

   public final V getIngredient() {
      return this.ingredient;
   }

   public int getOrderIndex() {
      return this.orderIndex;
   }

   public IIngredientHelper<V> getIngredientHelper() {
      return this.ingredientHelper;
   }

   public IIngredientRenderer<V> getIngredientRenderer() {
      return this.ingredientRenderer;
   }

   public final String getDisplayName() {
      return this.displayName;
   }

   public String getModNameForSorting() {
      return (String)this.modNames.get(0);
   }

   public Set<String> getModNameStrings() {
      Set<String> modNameStrings = new HashSet();

      for(int i = 0; i < this.modIds.size(); ++i) {
         String modId = (String)this.modIds.get(i);
         String modName = (String)this.modNames.get(i);
         addModNameStrings(modNameStrings, modId, modName);
      }

      return modNameStrings;
   }

   private static void addModNameStrings(Set<String> modNames, String modId, String modName) {
      String modNameLowercase = modName.toLowerCase(Locale.ENGLISH);
      String modNameNoSpaces = SPACE_PATTERN.matcher(modNameLowercase).replaceAll("");
      String modIdNoSpaces = SPACE_PATTERN.matcher(modId).replaceAll("");
      modNames.add(modId);
      modNames.add(modNameNoSpaces);
      modNames.add(modIdNoSpaces);
   }

   public final List<String> getTooltipStrings() {
      String modName = (String)this.modNames.get(0);
      String modId = (String)this.modIds.get(0);
      String modNameLowercase = modName.toLowerCase(Locale.ENGLISH);
      String displayNameLowercase = Translator.toLowercaseWithLocale(this.displayName);
      return IngredientInformation.getTooltipStrings(this.ingredient, this.ingredientRenderer, ImmutableSet.of(modId, modNameLowercase, displayNameLowercase, this.resourceId));
   }

   public Collection<String> getOreDictStrings() {
      Collection<String> oreDictNames = this.ingredientHelper.getOreDictNames(this.ingredient);
      return (Collection)oreDictNames.stream().map((s) -> {
         return s.toLowerCase(Locale.ENGLISH);
      }).collect(Collectors.toList());
   }

   public Collection<String> getCreativeTabsStrings() {
      Collection<String> creativeTabsStrings = this.ingredientHelper.getCreativeTabNames(this.ingredient);
      return (Collection)creativeTabsStrings.stream().map(Translator::toLowercaseWithLocale).collect(Collectors.toList());
   }

   public Collection<String> getColorStrings() {
      return IngredientInformation.getColorStrings(this.ingredient, this.ingredientHelper);
   }

   public String getResourceId() {
      return this.resourceId;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
   }
}
