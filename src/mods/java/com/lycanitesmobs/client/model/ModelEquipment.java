package com.lycanitesmobs.client.model;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.client.renderer.EquipmentRenderer;
import com.lycanitesmobs.client.renderer.IItemModelRenderer;
import com.lycanitesmobs.client.renderer.layer.LayerItem;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class ModelEquipment implements IItemModelRenderer {
   protected List<LayerItem> renderLayers = new ArrayList();
   protected List<ModelItemBase> renderedModels = new ArrayList();

   public ModelEquipment() {
   }

   public void render(ItemStack itemStack, EnumHand hand, EquipmentRenderer renderer, float loop) {
      if (itemStack.getItem() instanceof ItemEquipment) {
         ItemEquipment itemEquipment = (ItemEquipment)itemStack.getItem();
         NonNullList<ItemStack> equipmentPartStacks = itemEquipment.getEquipmentPartStacks(itemStack);
         int slotId = -1;
         ModelItemBase modelPartBase = null;
         ModelItemBase modelPartHead = null;
         Iterator var10 = equipmentPartStacks.iterator();

         while(true) {
            ItemStack partStack;
            while(true) {
               if (!var10.hasNext()) {
                  for(ModelItemBase modelItemBase : this.renderedModels) {
                     modelItemBase.clearAnimationFrames();
                  }

                  this.renderedModels.clear();
                  return;
               }

               partStack = (ItemStack)var10.next();
               ++slotId;
               if (slotId == 0) {
                  modelPartBase = this.renderPart(partStack, hand, renderer, (ModelObjPart)null, loop);
                  break;
               }

               if (slotId == 1) {
                  if (modelPartBase != null && modelPartBase.animationParts.containsKey("head")) {
                     modelPartHead = this.renderPart(partStack, hand, renderer, (ModelObjPart)modelPartBase.animationParts.get("head"), loop);
                     break;
                  }
               } else if (slotId == 2) {
                  if (modelPartHead != null && modelPartHead.animationParts.containsKey("tipa")) {
                     this.renderPart(partStack, hand, renderer, (ModelObjPart)modelPartHead.animationParts.get("tipa"), loop);
                     break;
                  }
               } else if (slotId == 3) {
                  if (modelPartHead != null && modelPartHead.animationParts.containsKey("tipb")) {
                     this.renderPart(partStack, hand, renderer, (ModelObjPart)modelPartHead.animationParts.get("tipb"), loop);
                     break;
                  }
               } else {
                  if (slotId != 4) {
                     break;
                  }

                  if (modelPartHead != null && modelPartHead.animationParts.containsKey("tipc")) {
                     this.renderPart(partStack, hand, renderer, (ModelObjPart)modelPartHead.animationParts.get("tipc"), loop);
                     break;
                  }
               }
            }

            if (slotId == 5 && modelPartBase != null && modelPartBase.animationParts.containsKey("pommel")) {
               this.renderPart(partStack, hand, renderer, (ModelObjPart)modelPartBase.animationParts.get("pommel"), loop);
            }
         }
      }
   }

   public ModelItemBase renderPart(ItemStack partStack, EnumHand hand, EquipmentRenderer renderer, ModelObjPart offsetPart, float loop) {
      if (!partStack.isEmpty() && partStack.getItem() instanceof ItemEquipmentPart) {
         ItemEquipmentPart itemEquipmentPart = (ItemEquipmentPart)partStack.getItem();
         ModelItemBase modelItemBase = AssetManager.getItemModel(itemEquipmentPart.itemName);
         if (modelItemBase.animationParts.containsKey("base")) {
            ((ModelObjPart)modelItemBase.animationParts.get("base")).setOffset(offsetPart);
         }

         this.renderLayers.clear();
         modelItemBase.addCustomLayers(this);
         modelItemBase.generateAnimationFrames(partStack, (LayerItem)null, loop, offsetPart);
         modelItemBase.render(partStack, hand, renderer, offsetPart, (LayerItem)null, loop, false);

         for(LayerItem renderLayer : this.renderLayers) {
            modelItemBase.render(partStack, hand, renderer, offsetPart, renderLayer, loop, false);
         }

         this.renderedModels.add(modelItemBase);
         return modelItemBase;
      } else {
         return null;
      }
   }

   public void bindItemTexture(ResourceLocation location) {
   }

   public List<LayerItem> addLayer(LayerItem renderLayer) {
      if (!this.renderLayers.contains(renderLayer)) {
         this.renderLayers.add(renderLayer);
      }

      return this.renderLayers;
   }
}
