package com.ferreusveritas.dynamictrees.trees;

import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenHugeMushroom;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

public class Mushroom extends Species {
   protected final boolean redcap;

   public Mushroom(boolean redcap) {
      this.redcap = redcap;
      this.setRegistryName(new ResourceLocation("dynamictrees", "mushroom" + (redcap ? "red" : "brn")));
      this.setStandardSoils();
      this.addGenFeature(new FeatureGenHugeMushroom(redcap ? Blocks.RED_MUSHROOM_BLOCK : Blocks.BROWN_MUSHROOM_BLOCK), 1);
   }

   public boolean isTransformable() {
      return false;
   }
}
