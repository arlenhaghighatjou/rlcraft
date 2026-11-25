package com.charles445.rltweaker.config.json;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.util.ErrorUtil;
import com.google.gson.annotations.SerializedName;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class JsonBlockState {
   public static final JsonBlockState AIR = new JsonBlockState("minecraft:air");
   @SerializedName("id")
   public String registryName;
   public int metadata;
   private IBlockState asBlockState;

   public JsonBlockState(String registryName) {
      this.registryName = registryName;
      this.metadata = -1;
      this.asBlockState = this.getAsBlockState();
      this.metadata = this.asBlockState.getBlock().getMetaFromState(this.asBlockState);
   }

   public JsonBlockState(String registryName, int metadata) {
      this.registryName = registryName;
      this.metadata = metadata;
      this.asBlockState = this.getAsBlockState();
   }

   public IBlockState getAsBlockState() {
      if (this.asBlockState == null) {
         Block block = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation(this.registryName));
         if (block == null) {
            RLTweaker.logger.warn("Couldn't getAsBlockState resource: " + this.registryName);
            ErrorUtil.logSilent("JsonBlockState getAsBlockState");
            return Blocks.AIR.getDefaultState();
         } else {
            return this.metadata != -1 ? block.getStateFromMeta(this.metadata) : block.getDefaultState();
         }
      } else {
         return this.asBlockState;
      }
   }
}
