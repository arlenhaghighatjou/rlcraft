package mezz.jei.gui;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableBuilder;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.gui.elements.DrawableAnimated;
import mezz.jei.gui.elements.DrawableBlank;
import mezz.jei.gui.elements.DrawableBuilder;
import mezz.jei.gui.elements.DrawableIngredient;
import mezz.jei.gui.elements.DrawableNineSliceTexture;
import mezz.jei.gui.elements.DrawableSprite;
import mezz.jei.gui.textures.TextureInfo;
import mezz.jei.gui.textures.Textures;
import mezz.jei.util.ErrorUtil;
import net.minecraft.util.ResourceLocation;

public class GuiHelper implements IGuiHelper {
   private final IIngredientRegistry ingredientRegistry;
   private final IDrawableStatic slotDrawable;
   private final IDrawableStatic tabSelected;
   private final IDrawableStatic tabUnselected;
   private final IDrawableStatic shapelessIcon;
   private final IDrawableStatic arrowPrevious;
   private final IDrawableStatic arrowNext;
   private final IDrawableStatic recipeTransfer;
   private final IDrawableStatic configButtonIcon;
   private final IDrawableStatic configButtonCheatIcon;
   private final IDrawableStatic bookmarkButtonDisabledIcon;
   private final IDrawableStatic bookmarkButtonEnabledIcon;
   private final DrawableNineSliceTexture buttonDisabled;
   private final DrawableNineSliceTexture buttonEnabled;
   private final DrawableNineSliceTexture buttonHighlight;
   private final DrawableNineSliceTexture guiBackground;
   private final DrawableNineSliceTexture recipeBackground;
   private final DrawableNineSliceTexture searchBackground;
   private final DrawableNineSliceTexture catalystTab;
   private final DrawableNineSliceTexture nineSliceSlot;
   private final IDrawableStatic infoIcon;
   private final IDrawableStatic flameIcon;

   public GuiHelper(IIngredientRegistry ingredientRegistry, Textures textures) {
      this.ingredientRegistry = ingredientRegistry;
      this.slotDrawable = this.createDrawable(textures.slot);
      this.nineSliceSlot = this.createNineSliceDrawable(textures.slot);
      this.tabSelected = this.createDrawable(textures.tabSelected);
      this.tabUnselected = this.createDrawable(textures.tabUnselected);
      this.buttonDisabled = this.createNineSliceDrawable(textures.buttonDisabled);
      this.buttonEnabled = this.createNineSliceDrawable(textures.buttonEnabled);
      this.buttonHighlight = this.createNineSliceDrawable(textures.buttonHighlight);
      this.guiBackground = this.createNineSliceDrawable(textures.guiBackground);
      this.recipeBackground = this.createNineSliceDrawable(textures.recipeBackground);
      this.searchBackground = this.createNineSliceDrawable(textures.searchBackground);
      this.catalystTab = this.createNineSliceDrawable(textures.catalystTab);
      this.shapelessIcon = this.createDrawable(textures.shapelessIcon);
      this.arrowPrevious = this.createDrawable(textures.arrowPrevious);
      this.arrowNext = this.createDrawable(textures.arrowNext);
      this.recipeTransfer = this.createDrawable(textures.recipeTransfer);
      this.configButtonIcon = this.createDrawable(textures.configButtonIcon);
      this.configButtonCheatIcon = this.createDrawable(textures.configButtonCheatIcon);
      this.bookmarkButtonDisabledIcon = this.createDrawable(textures.bookmarkButtonDisabledIcon);
      this.bookmarkButtonEnabledIcon = this.createDrawable(textures.bookmarkButtonEnabledIcon);
      this.infoIcon = this.createDrawable(textures.infoIcon);
      this.flameIcon = this.createDrawable(textures.flameIcon);
   }

   public IDrawableBuilder drawableBuilder(ResourceLocation resourceLocation, int u, int v, int width, int height) {
      return new DrawableBuilder(resourceLocation, u, v, width, height);
   }

   public IDrawableAnimated createAnimatedDrawable(IDrawableStatic drawable, int ticksPerCycle, IDrawableAnimated.StartDirection startDirection, boolean inverted) {
      ErrorUtil.checkNotNull(drawable, "drawable");
      ErrorUtil.checkNotNull(startDirection, "startDirection");
      return new DrawableAnimated(drawable, ticksPerCycle, startDirection, inverted);
   }

   public IDrawableStatic getSlotDrawable() {
      return this.slotDrawable;
   }

   public IDrawableStatic createBlankDrawable(int width, int height) {
      return new DrawableBlank(width, height);
   }

   public <V> IDrawable createDrawableIngredient(V ingredient) {
      IIngredientRenderer<V> ingredientRenderer = this.ingredientRegistry.getIngredientRenderer(ingredient);
      return new DrawableIngredient(ingredient, ingredientRenderer);
   }

   public ICraftingGridHelper createCraftingGridHelper(int craftInputSlot1, int craftOutputSlot) {
      return new CraftingGridHelper(craftInputSlot1, craftOutputSlot);
   }

   public ITickTimer createTickTimer(int ticksPerCycle, int maxValue, boolean countDown) {
      return new TickTimer(ticksPerCycle, maxValue, countDown);
   }

   private IDrawableStatic createDrawable(TextureInfo textureInfo) {
      return new DrawableSprite(textureInfo);
   }

   private DrawableNineSliceTexture createNineSliceDrawable(TextureInfo textureInfo) {
      return new DrawableNineSliceTexture(textureInfo);
   }

   public IDrawableStatic getTabSelected() {
      return this.tabSelected;
   }

   public IDrawableStatic getTabUnselected() {
      return this.tabUnselected;
   }

   public IDrawableStatic getShapelessIcon() {
      return this.shapelessIcon;
   }

   public IDrawableStatic getArrowPrevious() {
      return this.arrowPrevious;
   }

   public IDrawableStatic getArrowNext() {
      return this.arrowNext;
   }

   public IDrawableStatic getRecipeTransfer() {
      return this.recipeTransfer;
   }

   public IDrawableStatic getConfigButtonIcon() {
      return this.configButtonIcon;
   }

   public IDrawableStatic getConfigButtonCheatIcon() {
      return this.configButtonCheatIcon;
   }

   public IDrawableStatic getBookmarkButtonDisabledIcon() {
      return this.bookmarkButtonDisabledIcon;
   }

   public IDrawableStatic getBookmarkButtonEnabledIcon() {
      return this.bookmarkButtonEnabledIcon;
   }

   public DrawableNineSliceTexture getButtonDisabled() {
      return this.buttonDisabled;
   }

   public DrawableNineSliceTexture getButtonEnabled() {
      return this.buttonEnabled;
   }

   public DrawableNineSliceTexture getButtonHighlight() {
      return this.buttonHighlight;
   }

   public DrawableNineSliceTexture getButtonForState(int state) {
      if (state == 0) {
         return this.getButtonDisabled();
      } else {
         return state == 2 ? this.getButtonHighlight() : this.getButtonEnabled();
      }
   }

   public DrawableNineSliceTexture getGuiBackground() {
      return this.guiBackground;
   }

   public DrawableNineSliceTexture getRecipeBackground() {
      return this.recipeBackground;
   }

   public DrawableNineSliceTexture getSearchBackground() {
      return this.searchBackground;
   }

   public IDrawableStatic getInfoIcon() {
      return this.infoIcon;
   }

   public DrawableNineSliceTexture getCatalystTab() {
      return this.catalystTab;
   }

   public DrawableNineSliceTexture getNineSliceSlot() {
      return this.nineSliceSlot;
   }

   public IDrawableStatic getFlameIcon() {
      return this.flameIcon;
   }
}
