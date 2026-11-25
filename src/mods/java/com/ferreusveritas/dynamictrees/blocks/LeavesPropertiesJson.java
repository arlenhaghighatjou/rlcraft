package com.ferreusveritas.dynamictrees.blocks;

import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.api.cells.ICellKit;
import com.ferreusveritas.dynamictrees.api.treedata.ILeavesProperties;
import com.ferreusveritas.dynamictrees.client.BlockColorMultipliers;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.command.CommandBase;
import net.minecraft.command.InvalidBlockStateException;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LeavesPropertiesJson extends LeavesProperties {
   static List<LeavesPropertiesJson> resolutionList = new ArrayList();
   private int lightRequirement;
   private int smotherLeavesMax;
   private JsonObject jsonObj;
   private boolean connectAnyRadius;
   private int flammability;
   private int fireSpreadSpeed;
   protected LeavesPropertiesJson.ILeavesUpdate leavesUpdate;
   protected static Map<String, Function<JsonElement, LeavesPropertiesJson.PrimitiveLeavesComponents>> blockstateFinderMap = new HashMap();
   private JsonPrimitive colorPrimitive;
   @SideOnly(Side.CLIENT)
   private IBlockColor colorMultiplier;

   public LeavesPropertiesJson(String jsonData) {
      this(getJsonObject(jsonData));
   }

   public LeavesPropertiesJson(JsonObject jsonObj) {
      super(ModBlocks.blockStates.air, ItemStack.EMPTY);
      this.lightRequirement = 13;
      this.smotherLeavesMax = 4;
      this.jsonObj = null;
      this.connectAnyRadius = false;
      this.flammability = 60;
      this.fireSpreadSpeed = 30;
      this.leavesUpdate = (w, p, s, r, l) -> {
         return true;
      };
      this.colorPrimitive = null;
      this.jsonObj = jsonObj;
      resolutionList.add(this);
   }

   public void resolve() {
      if (this.jsonObj != null) {
         for(Entry<String, JsonElement> entry : this.jsonObj.entrySet()) {
            String key = (String)entry.getKey();
            JsonElement element = (JsonElement)entry.getValue();
            if ("color".equals(key)) {
               if (element.isJsonPrimitive()) {
                  this.colorPrimitive = element.getAsJsonPrimitive();
               }
            } else if ("leaves".equals(key)) {
               if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                  getPrimitiveLeaves(element.getAsString()).assignTo(this);
               } else if (element.isJsonObject()) {
                  getPrimitiveLeaves(element.getAsJsonObject()).assignTo(this);
               }
            } else if ("cellkit".equals(key)) {
               ICellKit kit = TreeRegistry.findCellKit(element.getAsString());
               if (kit != null) {
                  this.cellKit = kit;
               }
            } else if ("smother".equals(key)) {
               this.smotherLeavesMax = MathHelper.clamp(element.getAsInt(), 0, 64);
            } else if ("light".equals(key)) {
               this.lightRequirement = MathHelper.clamp(element.getAsInt(), 0, 15);
            } else if ("connectAny".equals(key)) {
               if (element.isJsonPrimitive()) {
                  this.connectAnyRadius = element.getAsBoolean();
               }
            } else if ("flammability".equals(key)) {
               if (element.isJsonPrimitive()) {
                  this.flammability = element.getAsInt();
               }
            } else if ("fireSpreadSpeed".equals(key) && element.isJsonPrimitive()) {
               this.fireSpreadSpeed = element.getAsInt();
            }
         }

         this.jsonObj = null;
      }

   }

   private static LeavesPropertiesJson.PrimitiveLeavesComponents getPrimitiveLeaves(String leavesDesc) {
      IBlockState state = getBlockState(leavesDesc);
      ItemStack stack = ItemStack.EMPTY;
      if (state.getBlock() != Blocks.AIR) {
         int meta = state.getBlock().damageDropped(state);
         if (state.getBlock() == Blocks.LEAVES2 && meta >= 4) {
            meta -= 4;
         }

         stack = new ItemStack(Item.getItemFromBlock(state.getBlock()), 1, meta);
      }

      return new LeavesPropertiesJson.PrimitiveLeavesComponents(state, stack);
   }

   private static LeavesPropertiesJson.PrimitiveLeavesComponents getPrimitiveLeaves(JsonObject jsonObj) {
      for(Entry<String, JsonElement> entry : jsonObj.entrySet()) {
         String name = (String)entry.getKey();
         LeavesPropertiesJson.PrimitiveLeavesComponents comp = findPrimitiveComponents(name, (JsonElement)entry.getValue());
         if (comp != null) {
            return comp;
         }
      }

      return new LeavesPropertiesJson.PrimitiveLeavesComponents(Blocks.AIR.getDefaultState(), ItemStack.EMPTY);
   }

   private static IBlockState getBlockState(String blockStateDesc) {
      String blockString = blockStateDesc;
      String argString = "default";
      String[] args = blockStateDesc.split(" ");
      if (args.length == 2) {
         blockString = args[0];
         argString = args[1];
      }

      Block block = (Block)Block.REGISTRY.getObject(new ResourceLocation(blockString));

      try {
         return CommandBase.convertArgToBlockState(block, argString);
      } catch (InvalidBlockStateException | NumberInvalidException var6) {
         var6.printStackTrace();
         return block.getDefaultState();
      }
   }

   public static JsonObject getJsonObject(String jsonData) {
      try {
         JsonParser parser = new JsonParser();
         JsonElement je = parser.parse(jsonData.replace('`', '"'));
         return je.getAsJsonObject();
      } catch (Exception var3) {
         System.err.println(var3);
         return null;
      }
   }

   public static void resolveAll() {
      for(LeavesPropertiesJson res : resolutionList) {
         res.resolve();
      }

   }

   public static void cleanUp() {
      resolutionList = null;
      blockstateFinderMap = null;
   }

   public int getLightRequirement() {
      return this.lightRequirement;
   }

   public int getSmotherLeavesMax() {
      return this.smotherLeavesMax;
   }

   public int getRadiusForConnection(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, BlockBranch from, EnumFacing side, int fromRadius) {
      return (fromRadius == 1 || this.connectAnyRadius) && from.getFamily().isCompatibleDynamicLeaves(blockAccess.getBlockState(pos), blockAccess, pos) ? 1 : 0;
   }

   public int getFlammability() {
      return this.flammability;
   }

   public int getFireSpreadSpeed() {
      return this.fireSpreadSpeed;
   }

   public void setLeavesUpdate(LeavesPropertiesJson.ILeavesUpdate leavesUpdate) {
      this.leavesUpdate = leavesUpdate;
   }

   public boolean updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      return this.leavesUpdate.updateTick(worldIn, pos, state, rand, this);
   }

   public static void addLeavesFinderFunction(String finderName, Function<JsonElement, LeavesPropertiesJson.PrimitiveLeavesComponents> function) {
      blockstateFinderMap.put(finderName, function);
   }

   public static LeavesPropertiesJson.PrimitiveLeavesComponents findPrimitiveComponents(String finderName, JsonElement jsonElement) {
      Function<JsonElement, LeavesPropertiesJson.PrimitiveLeavesComponents> function = (Function)blockstateFinderMap.get(finderName);
      return function != null ? (LeavesPropertiesJson.PrimitiveLeavesComponents)function.apply(jsonElement) : new LeavesPropertiesJson.PrimitiveLeavesComponents(Blocks.AIR.getDefaultState(), ItemStack.EMPTY);
   }

   @SideOnly(Side.CLIENT)
   public int foliageColorMultiplier(IBlockState state, IBlockAccess world, BlockPos pos) {
      return this.colorMultiplier.colorMultiplier(state, world, pos, -1);
   }

   @SideOnly(Side.CLIENT)
   private IBlockColor processColor(JsonPrimitive primitive) {
      int color = -1;
      if (primitive.isNumber()) {
         color = ((Integer)primitive.getAsNumber()).intValue();
      } else if (primitive.isString()) {
         String code = primitive.getAsString();
         if (code.startsWith("@")) {
            code = code.substring(1);
            if ("biome".equals(code)) {
               return (state, world, pos, t) -> {
                  return world.getBiome(pos).getModdedBiomeFoliageColor(super.foliageColorMultiplier(state, world, pos));
               };
            }

            IBlockColor blockColor = BlockColorMultipliers.find(code);
            if (blockColor != null) {
               return blockColor;
            }

            System.err.println("Error: ColorMultiplier resource \"" + code + "\" could not be found.");
         } else {
            color = Color.decode(code).getRGB();
         }
      }

      return (s, w, p, t) -> {
         return c;
      };
   }

   @SideOnly(Side.CLIENT)
   public void resolveClient() {
      if (this.colorPrimitive != null) {
         this.colorMultiplier = this.processColor(this.colorPrimitive);
         this.colorPrimitive = null;
      } else {
         this.colorMultiplier = (s, w, p, t) -> {
            return super.foliageColorMultiplier(s, w, p);
         };
      }

   }

   @SideOnly(Side.CLIENT)
   public static void postInitClient() {
      for(LeavesPropertiesJson res : resolutionList) {
         res.resolveClient();
      }

   }

   public interface ILeavesUpdate {
      boolean updateTick(World var1, BlockPos var2, IBlockState var3, EaglercraftRandom var4, ILeavesProperties var5);
   }

   public static class PrimitiveLeavesComponents {
      public IBlockState state;
      public ItemStack stack;

      public PrimitiveLeavesComponents(IBlockState state, ItemStack stack) {
         this.state = state;
         this.stack = stack;
      }

      public void assignTo(LeavesPropertiesJson lpJson) {
         lpJson.primitiveLeaves = this.state;
         lpJson.primitiveLeavesItemStack = this.stack;
      }
   }
}
