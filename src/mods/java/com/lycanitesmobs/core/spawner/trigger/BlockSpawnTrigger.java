package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.helpers.JSONHelper;
import com.lycanitesmobs.core.spawner.Spawner;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public class BlockSpawnTrigger extends SpawnTrigger {
   public boolean playerOnly = false;
   public boolean onBreak = true;
   public boolean onHarvest = true;
   public boolean onPlace = false;
   public List<Block> blocks = new ArrayList();
   public String blocksListType = "whitelist";
   public List<Material> blockMaterials = new ArrayList();
   public String blockMaterialsListType = "whitelist";
   public String silkTouch = "ignore";
   public double fortuneMin = -1.0D;
   public double fortuneMax = -1.0D;

   public BlockSpawnTrigger(Spawner spawner) {
      super(spawner);
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("playerOnly")) {
         this.playerOnly = json.get("playerOnly").getAsBoolean();
      }

      if (json.has("onBreak")) {
         this.onBreak = json.get("onBreak").getAsBoolean();
      }

      if (json.has("onHarvest")) {
         this.onHarvest = json.get("onHarvest").getAsBoolean();
      }

      if (json.has("onPlace")) {
         this.onPlace = json.get("onPlace").getAsBoolean();
      }

      this.blocks = JSONHelper.getJsonBlocks(json);
      if (json.has("blocksListType")) {
         this.blocksListType = json.get("blocksListType").getAsString();
      }

      this.blockMaterials = JSONHelper.getJsonMaterials(json);
      if (json.has("blockMaterialsListType")) {
         this.blockMaterialsListType = json.get("blockMaterialsListType").getAsString();
      }

      if (json.has("silkTouch")) {
         this.silkTouch = json.get("silkTouch").getAsString();
      }

      if (json.has("fortuneMin")) {
         this.fortuneMin = (double)json.get("fortuneMin").getAsInt();
      }

      if (json.has("fortuneMax")) {
         this.fortuneMax = (double)json.get("fortuneMax").getAsInt();
      }

      super.loadFromJSON(json);
   }

   public void onBlockBreak(World world, EntityPlayer player, BlockPos breakPos, IBlockState blockState, int chain) {
      if (this.onBreak) {
         this.onBlockTriggered(world, player, breakPos, blockState, chain, 0);
      }
   }

   public void onBlockHarvest(World world, EntityPlayer player, BlockPos breakPos, IBlockState blockState, int chain, int fortune, boolean silkTouch) {
      if (this.onHarvest) {
         if (this.fortuneMin < 0.0D || (double)fortune >= this.fortuneMin) {
            if (this.fortuneMax <= this.fortuneMin || (double)fortune <= this.fortuneMax) {
               if (!silkTouch || !"deny".equals(this.silkTouch)) {
                  if (silkTouch || !"require".equals(this.silkTouch)) {
                     this.onBlockTriggered(world, player, breakPos, blockState, chain, fortune);
                  }
               }
            }
         }
      }
   }

   public void onBlockPlace(World world, EntityPlayer player, BlockPos breakPos, IBlockState blockState, int chain) {
      if (this.onPlace) {
         this.onBlockTriggered(world, player, breakPos, blockState, chain, 0);
      }
   }

   public void onBlockTriggered(World world, EntityPlayer player, BlockPos blockPos, IBlockState blockState, int chain, int fortune) {
      if (!this.playerOnly || player != null && !(player instanceof FakePlayer)) {
         if (this.isTriggerBlock(blockState, world, blockPos, fortune)) {
            if (this.chance >= 1.0D || world.rand.nextDouble() <= this.chance) {
               this.trigger(world, player, blockPos, this.getBlockLevel(blockState, world, blockPos), chain);
            }
         }
      }
   }

   public boolean isTriggerBlock(IBlockState blockState, World world, BlockPos blockPos, int fortune) {
      if (this.blocks.size() > 0) {
         Block block = blockState.getBlock();
         if (this.blocks.contains(block)) {
            return !"blacklist".equalsIgnoreCase(this.blocksListType);
         }
      }

      if (this.blockMaterials.size() > 0) {
         Material material = blockState.getMaterial();
         if (this.blockMaterials.contains(material)) {
            return !"blacklist".equalsIgnoreCase(this.blockMaterialsListType);
         }
      }

      if (this.blocks.isEmpty() && this.blockMaterials.isEmpty()) {
         return true;
      } else {
         return "blacklist".equalsIgnoreCase(this.blocksListType) && "blacklist".equalsIgnoreCase(this.blockMaterialsListType);
      }
   }

   public int getBlockLevel(IBlockState blockState, World world, BlockPos blockPos) {
      return 0;
   }
}
