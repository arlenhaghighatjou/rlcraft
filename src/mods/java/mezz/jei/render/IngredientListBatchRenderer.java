package mezz.jei.render;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import mezz.jei.api.ingredients.ISlowRenderItem;
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.input.ClickedIngredient;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.Log;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class IngredientListBatchRenderer {
   private final List<IngredientListSlot> slots = new ArrayList();
   private final List<ItemStackFastRenderer> renderItems2d = new ArrayList();
   private final List<ItemStackFastRenderer> renderItems3d = new ArrayList();
   private final List<IngredientRenderer> renderOther = new ArrayList();
   private int blocked = 0;

   public IngredientListBatchRenderer() {
   }

   public void clear() {
      this.slots.clear();
      this.renderItems2d.clear();
      this.renderItems3d.clear();
      this.renderOther.clear();
      this.blocked = 0;
   }

   public int size() {
      return this.slots.size() - this.blocked;
   }

   public void add(IngredientListSlot ingredientListSlot) {
      this.slots.add(ingredientListSlot);
   }

   public List<IngredientListSlot> getAllGuiIngredientSlots() {
      return this.slots;
   }

   public void set(int startIndex, List<IIngredientListElement> ingredientList) {
      this.renderItems2d.clear();
      this.renderItems3d.clear();
      this.renderOther.clear();
      this.blocked = 0;
      int i = startIndex;

      for(IngredientListSlot ingredientListSlot : this.slots) {
         if (ingredientListSlot.isBlocked()) {
            ingredientListSlot.clear();
            ++this.blocked;
         } else {
            if (i >= ingredientList.size()) {
               ingredientListSlot.clear();
            } else {
               IIngredientListElement<?> element = (IIngredientListElement)ingredientList.get(i);
               this.set(ingredientListSlot, element);
            }

            ++i;
         }
      }

   }

   private <V> void set(IngredientListSlot ingredientListSlot, IIngredientListElement<V> element) {
      ingredientListSlot.clear();
      V ingredient = element.getIngredient();
      if (ingredient instanceof ItemStack) {
         ItemStack itemStack = (ItemStack)element.getIngredient();
         ItemModelMesher itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

         IBakedModel var11;
         try {
            IBakedModel bakedModel = itemModelMesher.getItemModel(itemStack);
            var11 = bakedModel.getOverrides().handleItemState(bakedModel, itemStack, (World)null, (EntityLivingBase)null);
            Preconditions.checkNotNull(var11, "IBakedModel must not be null.");
         } catch (Throwable var10) {
            String stackInfo = ErrorUtil.getItemStackInfo(itemStack);
            Log.get().error("ItemStack crashed getting IBakedModel. {}", stackInfo, var10);
            return;
         }

         if (!var11.isBuiltInRenderer() && !(itemStack.getItem() instanceof ISlowRenderItem)) {
            ItemStackFastRenderer renderer = new ItemStackFastRenderer((IIngredientListElement<ItemStack>)element);
            ingredientListSlot.setIngredientRenderer(renderer);
            if (var11.isGui3d()) {
               this.renderItems3d.add(renderer);
            } else {
               this.renderItems2d.add(renderer);
            }

            return;
         }
      }

      IngredientRenderer<V> renderer = new IngredientRenderer<V>(element);
      ingredientListSlot.setIngredientRenderer(renderer);
      this.renderOther.add(renderer);
   }

   @Nullable
   public ClickedIngredient<?> getIngredientUnderMouse(int mouseX, int mouseY) {
      IngredientRenderer hovered = this.getHovered(mouseX, mouseY);
      if (hovered != null) {
         IIngredientListElement element = hovered.getElement();
         return ClickedIngredient.create(element.getIngredient(), hovered.getArea());
      } else {
         return null;
      }
   }

   @Nullable
   public IngredientRenderer getHovered(int mouseX, int mouseY) {
      for(IngredientListSlot slot : this.slots) {
         if (slot.isMouseOver(mouseX, mouseY)) {
            return slot.getIngredientRenderer();
         }
      }

      return null;
   }

   public void render(Minecraft minecraft) {
      RenderHelper.enableGUIStandardItemLighting();
      RenderItem renderItem = minecraft.getRenderItem();
      TextureManager textureManager = minecraft.getTextureManager();
      renderItem.zLevel += 50.0F;
      textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableLighting();

      for(ItemStackFastRenderer slot : this.renderItems3d) {
         slot.renderItemAndEffectIntoGUI();
      }

      GlStateManager.disableLighting();

      for(ItemStackFastRenderer slot : this.renderItems2d) {
         slot.renderItemAndEffectIntoGUI();
      }

      GlStateManager.disableAlpha();
      GlStateManager.disableBlend();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableLighting();
      textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
      renderItem.zLevel -= 50.0F;

      for(ItemStackFastRenderer slot : this.renderItems3d) {
         slot.renderOverlay();
      }

      for(ItemStackFastRenderer slot : this.renderItems2d) {
         slot.renderOverlay();
      }

      GlStateManager.disableLighting();

      for(IngredientRenderer slot : this.renderOther) {
         slot.renderSlow();
      }

      RenderHelper.disableStandardItemLighting();
   }
}
