package net.ilexiconn.llibrary.client.util;

import javax.annotation.Nonnull;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum ItemTESRContext {
   INSTANCE;

   @Nonnull
   private ItemStack currentStack = ItemStack.EMPTY;
   @Nonnull
   private TransformType currentTransform = TransformType.GROUND;

   private ItemTESRContext() {
   }

   public void provideStackContext(@Nonnull ItemStack stack) {
      this.currentStack = stack;
   }

   public void providePerspectiveContext(@Nonnull TransformType transform) {
      this.currentTransform = transform;
   }

   @Nonnull
   public ItemStack getCurrentStack() {
      return this.currentStack;
   }

   @Nonnull
   public TransformType getCurrentTransform() {
      return this.currentTransform;
   }
}
