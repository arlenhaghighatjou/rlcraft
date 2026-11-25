package org.jwaresoftware.mcmods.lib;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;

public final class SkinsHelper {
   public static final String NBT_TAGNAME = LibInfo.rstring("Skin");
   public static final String _PROPERTY_SKIN = "skin";

   public static final int getSkinUnbounded(@Nonnull ItemStack stack, int default_skin) {
      int skin = default_skin;
      if (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_TAGNAME, 3)) {
         int x = stack.getTagCompound().getInteger(NBT_TAGNAME);
         if (x >= 0) {
            skin = x;
         }
      }

      return skin;
   }

   public static final int getSkin(@Nonnull ItemStack stack, int num_skins, int default_skin) {
      int skin = default_skin;
      if (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_TAGNAME, 3)) {
         int x = stack.getTagCompound().getInteger(NBT_TAGNAME);
         if (x > 0 && x < num_skins) {
            skin = x;
         }
      }

      return skin;
   }

   public static final void initSkinUnbounded(ItemStack stack, @Nonnull Random rand, int[] skins, int default_skin) {
      int f = skins.length > 0 ? skins[rand.nextInt(skins.length)] : default_skin;
      if (f != default_skin) {
         ItemStacks.getTagCompoundNonNull(stack).setInteger(NBT_TAGNAME, f);
      }

   }

   public static final void initSkin(ItemStack stack, @Nonnull Random rand, int num_skins, int default_skin) {
      int f = rand.nextInt(num_skins);
      if (f != default_skin) {
         ItemStacks.getTagCompoundNonNull(stack).setInteger(NBT_TAGNAME, f);
      }

   }

   public static final void initSkinConditionally(ItemStack stack, @Nonnull Random rand, int[] skins, int default_skin) {
      int f = getSkinUnbounded(stack, default_skin);
      if (f == default_skin) {
         initSkinUnbounded(stack, rand, skins, default_skin);
      }

   }

   public static final void initSkinConditionally(ItemStack stack, @Nonnull Random rand, int num_skins, int default_skin) {
      int f = getSkin(stack, num_skins, default_skin);
      if (f == default_skin) {
         initSkin(stack, rand, num_skins, default_skin);
      }

   }

   public static final void setSkin(ItemStack stack, int skin_id) {
      ItemStacks.getTagCompoundNonNull(stack).setInteger(NBT_TAGNAME, skin_id);
   }

   private SkinsHelper() {
   }

   public static final void init() {
   }
}
