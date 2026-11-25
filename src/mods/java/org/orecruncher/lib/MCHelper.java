package org.orecruncher.lib;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class MCHelper {
   private static final String MATERIAL_CUSTOM = "Custom";
   private static final String MATERIAL_NONE = "None";
   private static final Map<Material, String> materialMap = new Reference2ObjectOpenHashMap();
   private static final Set<Block> hasVariants = new ReferenceOpenHashSet();

   private static boolean variantCheck(@Nonnull Block block) {
      return block.getBlockState().getValidStates().size() > 1;
   }

   protected MCHelper() {
   }

   @Nullable
   public static String nameOf(@Nonnull Block block) {
      ResourceLocation res = (ResourceLocation)Block.REGISTRY.getNameForObject(block);
      return res != null ? res.toString() : null;
   }

   @Nullable
   public static String nameOf(@Nonnull Item item) {
      ResourceLocation res = (ResourceLocation)Item.REGISTRY.getNameForObject(item);
      return res != null ? res.toString() : null;
   }

   @Nullable
   public static Item getItemByName(@Nonnull String itemName) {
      return Item.getByNameOrId(itemName);
   }

   @Nullable
   public static Block getBlockByName(@Nonnull String blockName) {
      return Block.getBlockFromName(blockName);
   }

   @Nullable
   public static SoundType getSoundType(@Nonnull IBlockState state) {
      return getSoundType(state, (World)null);
   }

   @Nullable
   public static SoundType getSoundType(@Nonnull IBlockState state, @Nullable World world) {
      try {
         return state.getBlock().getSoundType(state, world, (BlockPos)null, (Entity)null);
      } catch (Throwable var3) {
         return state.getBlock().getSoundType();
      }
   }

   public static boolean hasVariants(@Nonnull Block block) {
      return hasVariants.contains(block);
   }

   public static boolean hasSpecialMeta(@Nonnull Block block) {
      return block instanceof BlockCrops;
   }

   @Nonnull
   public static String getMaterialName(@Nullable Material material) {
      if (material == null) {
         return "None";
      } else {
         String materialName = (String)materialMap.get(material);
         return materialName == null ? "Custom" : materialName;
      }
   }

   static {
      materialMap.put(Material.AIR, "Air");
      materialMap.put(Material.ANVIL, "Anvil");
      materialMap.put(Material.BARRIER, "Barrier");
      materialMap.put(Material.CACTUS, "Cactus");
      materialMap.put(Material.CAKE, "Cake");
      materialMap.put(Material.CARPET, "Carpet");
      materialMap.put(Material.CIRCUITS, "Circuits");
      materialMap.put(Material.CLAY, "Clay");
      materialMap.put(Material.CLOTH, "Cloth");
      materialMap.put(Material.CORAL, "Coral");
      materialMap.put(Material.CRAFTED_SNOW, "Crafted Snow");
      materialMap.put(Material.DRAGON_EGG, "Dragon Egg");
      materialMap.put(Material.FIRE, "FireJetEffect");
      materialMap.put(Material.GLASS, "Glass");
      materialMap.put(Material.GOURD, "Gourd");
      materialMap.put(Material.GRASS, "Grass");
      materialMap.put(Material.GROUND, "Ground");
      materialMap.put(Material.ICE, "Ice");
      materialMap.put(Material.IRON, "Iron");
      materialMap.put(Material.LAVA, "Lava");
      materialMap.put(Material.LEAVES, "Leaves");
      materialMap.put(Material.PACKED_ICE, "Packed Ice");
      materialMap.put(Material.PISTON, "Piston");
      materialMap.put(Material.PLANTS, "Plants");
      materialMap.put(Material.PORTAL, "Portal");
      materialMap.put(Material.REDSTONE_LIGHT, "Redstone Light");
      materialMap.put(Material.ROCK, "Rock");
      materialMap.put(Material.SAND, "Sand");
      materialMap.put(Material.SNOW, "Snow");
      materialMap.put(Material.SPONGE, "Sponge");
      materialMap.put(Material.STRUCTURE_VOID, "Structure Void");
      materialMap.put(Material.TNT, "TNT");
      materialMap.put(Material.VINE, "Vine");
      materialMap.put(Material.WATER, "Water");
      materialMap.put(Material.WEB, "Web");
      materialMap.put(Material.WOOD, "Wood");
      Set<Block> blocks = (Set)StreamSupport.stream(Block.REGISTRY.spliterator(), false).filter(MCHelper::variantCheck).collect(Collectors.toSet());
      hasVariants.addAll(blocks);
   }
}
