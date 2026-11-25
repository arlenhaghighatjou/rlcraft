package moe.plushie.armourers_workshop.api.client.render;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkin;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.item.ItemStack;

public interface ISkinRenderHandler {
   boolean renderSkinWithHelper(ItemStack var1);

   boolean renderSkinWithHelper(ItemStack var1, ModelBiped var2);

   boolean renderSkinWithHelper(ItemStack var1, float var2, float var3, float var4, float var5, float var6);

   boolean renderSkinWithHelper(ISkinDescriptor var1);

   boolean renderSkinWithHelper(ISkinDescriptor var1, ModelBiped var2);

   boolean renderSkinWithHelper(ISkinDescriptor var1, float var2, float var3, float var4, float var5, float var6);

   boolean renderSkin(ItemStack var1);

   boolean renderSkin(ISkinDescriptor var1);

   boolean renderSkinPart(ISkinDescriptor var1, ISkinPartType var2);

   boolean isSkinInModelCache(ItemStack var1);

   boolean isSkinInModelCache(ISkinDescriptor var1);

   void requestSkinModelFromSever(ItemStack var1);

   void requestSkinModelFromSever(ISkinDescriptor var1);

   ModelBase getArmourerHandModel();

   ISkin getSkinFromModelCache(ISkinDescriptor var1);
}
