package wiresegal.classyhats.client.render;

import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wiresegal.classyhats.ClassyHatsContent;
import wiresegal.classyhats.event.CapabilityEvents;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u000f\u0012\b\u0010\u0003\u001a\u0004\u0018\u00010\u0004¢\u0006\u0002\u0010\u0005JH\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\nH\u0016J\b\u0010\u0011\u001a\u00020\u0012H\u0016R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0013"},
   d2 = {"Lwiresegal/classyhats/client/render/EntityLayerRendererHat;", "Lnet/minecraft/client/renderer/entity/layers/LayerRenderer;", "Lnet/minecraft/entity/EntityLivingBase;", "modelRenderer", "Lnet/minecraft/client/model/ModelRenderer;", "(Lnet/minecraft/client/model/ModelRenderer;)V", "doRenderLayer", "", "player", "limbSwing", "", "limbSwingAmount", "partialTicks", "ageInTicks", "netHeadYaw", "headPitch", "scale", "shouldCombineTextures", "", "classyhats"}
)
public final class EntityLayerRendererHat implements LayerRenderer<EntityLivingBase> {
   private final ModelRenderer modelRenderer;

   public void doRenderLayer(@NotNull EntityLivingBase player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      Intrinsics.checkParameterIsNotNull(player, "player");
      ItemStack stack = new ItemStack((Item)ClassyHatsContent.INSTANCE.getHAT());
      NBTTagCompound data = player.getEntityData();
      if (data.hasKey(CapabilityEvents.INSTANCE.getCustomKey())) {
         String str = data.getString(CapabilityEvents.INSTANCE.getCustomKey());
         Intrinsics.checkExpressionValueIsNotNull(str, "str");
         CharSequence flag = (CharSequence)str;
         if (flag.length() > 0) {
            NBTHelper.setString(stack, "hat", str);
            GlStateManager.pushMatrix();
            if (player.isSneaking()) {
               GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            boolean flag = player instanceof EntityVillager || player instanceof EntityZombieVillager;
            if (player.isChild() && !(player instanceof EntityVillager)) {
               GlStateManager.translate(0.0F, 0.5F * scale, 0.0F);
               GlStateManager.scale(0.7F, 0.7F, 0.7F);
               GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
            }

            if (flag) {
               GlStateManager.translate(0.0F, 0.1875F, 0.0F);
            }

            ModelRenderer var10000 = this.modelRenderer;
            if (this.modelRenderer != null) {
               var10000.postRender(0.0625F);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.translate(0.0F, -0.25F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(0.625F, -0.625F, -0.625F);
            Minecraft var14 = Minecraft.getMinecraft();
            Intrinsics.checkExpressionValueIsNotNull(var14, "Minecraft.getMinecraft()");
            var14.getItemRenderer().renderItem(player, stack, TransformType.HEAD);
            GlStateManager.popMatrix();
         }
      }

   }

   public boolean shouldCombineTextures() {
      return false;
   }

   public EntityLayerRendererHat(@Nullable ModelRenderer modelRenderer) {
      this.modelRenderer = modelRenderer;
   }
}
