package me.ichun.mods.ichunutil.client.model.item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

public interface IModelBase {
   @Nonnull
   ResourceLocation getTexture();

   void renderModel(float var1);

   void postRender();

   @Nonnull
   ModelBase getModel();

   ItemCameraTransforms getCameraTransforms();

   Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType var1, Pair<? extends IBakedModel, Matrix4f> var2);

   boolean useVanillaCameraTransform();

   void handleBlockState(@Nullable IBlockState var1, @Nullable EnumFacing var2, long var3);

   void handleItemState(ItemStack var1, World var2, EntityLivingBase var3);
}
