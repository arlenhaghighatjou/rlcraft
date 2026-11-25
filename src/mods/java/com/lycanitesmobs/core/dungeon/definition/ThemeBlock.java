package com.lycanitesmobs.core.dungeon.definition;

import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ThemeBlock {
   protected Block block = null;
   public String blockId;
   public int metadata = 0;
   public int weight = 8;

   public ThemeBlock() {
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("blockId")) {
         this.blockId = json.get("blockId").getAsString();
      }

      if (json.has("metadata")) {
         this.metadata = json.get("metadata").getAsInt();
      }

      if (json.has("weight")) {
         this.weight = json.get("weight").getAsInt();
      }

   }

   public IBlockState getBlockState() {
      if (this.block == null) {
         this.block = (Block)GameRegistry.findRegistry(Block.class).getValue(new ResourceLocation(this.blockId));
      }

      if (this.block == null) {
         return Blocks.AIR.getDefaultState();
      } else {
         return this.metadata <= 0 ? this.block.getDefaultState() : this.block.getStateFromMeta(this.metadata);
      }
   }
}
