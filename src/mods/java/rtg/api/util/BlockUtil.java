package rtg.api.util;

import com.google.common.base.Optional;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockSponge;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.BlockDoublePlant.EnumPlantType;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.block.BlockStone.EnumType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

public final class BlockUtil {
   public static final String GROUP_RESLOC = "resloc";
   public static final String GROUP_PROPERTIES = "props";
   public static final String BLOCKSTATE_PATTERN = "^(?<resloc>[a-z0-9_]*:[a-z0-9_]+)(?:(?:\\[)(?<props>[a-z0-9_]+=[a-z0-9_]+(?:,[a-z0-9_]+=[a-z0-9_]+)*)?(?:\\]))?.*$";
   public static final Pattern BLOCKSTATE_MATCHER = Pattern.compile("^(?<resloc>[a-z0-9_]*:[a-z0-9_]+)(?:(?:\\[)(?<props>[a-z0-9_]+=[a-z0-9_]+(?:,[a-z0-9_]+=[a-z0-9_]+)*)?(?:\\]))?.*$");

   private BlockUtil() {
   }

   @Nullable
   public static Block getBlock(@Nullable String resourceLocation) {
      return !StringUtils.isEmpty(resourceLocation) ? getBlock(new ResourceLocation(resourceLocation)) : null;
   }

   @Nullable
   public static Block getBlock(@Nullable ResourceLocation resourceLocation) {
      return resourceLocation != null && Block.REGISTRY.containsKey(resourceLocation) ? (Block)Block.REGISTRY.getObject(resourceLocation) : null;
   }

   public static Block getBlock(@Nullable String resourceLocation, Block fallback) {
      return !StringUtils.isEmpty(resourceLocation) ? getBlock(new ResourceLocation(resourceLocation), fallback) : fallback;
   }

   public static Block getBlock(@Nullable ResourceLocation resourceLocation, Block fallback) {
      Block block = getBlock(resourceLocation);
      return block != null ? block : fallback;
   }

   public static IBlockState getBlockStateFromCfgString(@Nullable String configString, IBlockState fallback) {
      if (StringUtils.isEmpty(configString)) {
         return fallback;
      } else {
         IBlockState blockState = getBlockStateFromCfgString(configString);
         return blockState != null ? blockState : fallback;
      }
   }

   @Nullable
   public static IBlockState getBlockStateFromCfgString(@Nullable String configString) {
      if (StringUtils.isEmpty(configString)) {
         return null;
      } else {
         Matcher m = BLOCKSTATE_MATCHER.matcher(configString.replace(" ", ""));
         if (!m.find()) {
            return null;
         } else {
            ResourceLocation resloc = new ResourceLocation(m.group("resloc").trim());
            if (!Block.REGISTRY.containsKey(resloc)) {
               return null;
            } else {
               Block block = (Block)Block.REGISTRY.getObject(resloc);
               IBlockState ret = block.getDefaultState();
               String proplist = m.group("props");
               if (proplist == null) {
                  return ret;
               } else {
                  for(String entry : proplist.split(",")) {
                     String prop = entry.split("=")[0];
                     String val = entry.split("=")[1];
                     IProperty<?> property = block.getBlockState().getProperty(prop);
                     if (property != null) {
                        ret = getPossibleState(ret, property, val);
                     }
                  }

                  return ret;
               }
            }
         }
      }
   }

   public static IBlockState getPossibleState(Block block, String propertyName, String valueName, IBlockState fallback) {
      if (!StringUtils.isEmpty(propertyName) && !StringUtils.isEmpty(valueName)) {
         IBlockState bs = getPossibleState(block, propertyName, valueName);
         return bs != null ? bs : fallback;
      } else {
         return fallback;
      }
   }

   @Nullable
   public static IBlockState getPossibleState(Block block, String propertyName, String valueName) {
      if (!StringUtils.isEmpty(propertyName) && !StringUtils.isEmpty(valueName)) {
         IProperty<?> property = block.getBlockState().getProperty(propertyName);
         return property != null ? getPossibleState(block.getDefaultState(), property, valueName) : null;
      } else {
         return null;
      }
   }

   @Nullable
   public static IBlockState getPossibleState(IBlockState blockState, String propertyName, String valueName) {
      if (!StringUtils.isEmpty(propertyName) && !StringUtils.isEmpty(valueName)) {
         IProperty<?> property = blockState.getBlock().getBlockState().getProperty(propertyName);
         return property != null ? getPossibleState(blockState, property, valueName) : null;
      } else {
         return null;
      }
   }

   public static <T extends Comparable<T>> IBlockState getPossibleState(IBlockState blockstate, IProperty<T> propname, String valueName) {
      if (StringUtils.isEmpty(valueName)) {
         return blockstate;
      } else {
         Optional<T> value = propname.parseValue(valueName);
         return value.isPresent() ? blockstate.withProperty(propname, (Comparable)value.get()) : blockstate;
      }
   }

   public static IBlockState getStateStone(EnumType variant) {
      switch(variant) {
      case STONE:
         return Blocks.STONE.getDefaultState();
      case GRANITE:
         return Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.GRANITE);
      case GRANITE_SMOOTH:
         return Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.GRANITE_SMOOTH);
      case DIORITE:
         return Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.DIORITE);
      case DIORITE_SMOOTH:
         return Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.DIORITE_SMOOTH);
      case ANDESITE:
         return Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.ANDESITE);
      case ANDESITE_SMOOTH:
         return Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.ANDESITE_SMOOTH);
      default:
         return Blocks.STONE.getDefaultState();
      }
   }

   public static IBlockState getStateDirt(DirtType variant) {
      switch(variant) {
      case DIRT:
         return Blocks.DIRT.getDefaultState();
      case COARSE_DIRT:
         return Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.COARSE_DIRT);
      case PODZOL:
         return Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.PODZOL);
      default:
         return Blocks.DIRT.getDefaultState();
      }
   }

   public static IBlockState getStateSand(net.minecraft.block.BlockSand.EnumType variant) {
      switch(variant) {
      case SAND:
         return Blocks.SAND.getDefaultState();
      case RED_SAND:
         return Blocks.SAND.getDefaultState().withProperty(BlockSand.VARIANT, net.minecraft.block.BlockSand.EnumType.RED_SAND);
      default:
         return Blocks.SAND.getDefaultState();
      }
   }

   public static IBlockState getStateClay(EnumDyeColor color) {
      switch(color) {
      case WHITE:
         return Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.WHITE);
      case ORANGE:
         return Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE);
      case MAGENTA:
         return Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.MAGENTA);
      case LIGHT_BLUE:
         return Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.LIGHT_BLUE);
      case YELLOW:
         return Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.YELLOW);
      case LIME:
         return Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.LIME);
      case PINK:
         return Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.PINK);
      case GRAY:
         return Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.GRAY);
      case SILVER:
         return Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.SILVER);
      case CYAN:
         return Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.CYAN);
      case PURPLE:
         return Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.PURPLE);
      case BLUE:
         return Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLUE);
      case BROWN:
         return Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BROWN);
      case GREEN:
         return Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.GREEN);
      case RED:
         return Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.RED);
      case BLACK:
         return Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLACK);
      default:
         return Blocks.HARDENED_CLAY.getDefaultState();
      }
   }

   public static IBlockState getStateLog(net.minecraft.block.BlockPlanks.EnumType variant) {
      switch(variant) {
      case OAK:
         return Blocks.LOG.getDefaultState();
      case SPRUCE:
         return Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, net.minecraft.block.BlockPlanks.EnumType.SPRUCE);
      case BIRCH:
         return Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, net.minecraft.block.BlockPlanks.EnumType.BIRCH);
      case JUNGLE:
         return Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, net.minecraft.block.BlockPlanks.EnumType.JUNGLE);
      case ACACIA:
         return Blocks.LOG2.getDefaultState();
      case DARK_OAK:
         return Blocks.LOG2.getDefaultState().withProperty(BlockNewLog.VARIANT, net.minecraft.block.BlockPlanks.EnumType.DARK_OAK);
      default:
         return Blocks.LOG.getDefaultState();
      }
   }

   public static IBlockState getStateLeaf(net.minecraft.block.BlockPlanks.EnumType variant) {
      switch(variant) {
      case OAK:
         return Blocks.LEAVES.getDefaultState();
      case SPRUCE:
         return Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, net.minecraft.block.BlockPlanks.EnumType.SPRUCE);
      case BIRCH:
         return Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, net.minecraft.block.BlockPlanks.EnumType.BIRCH);
      case JUNGLE:
         return Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, net.minecraft.block.BlockPlanks.EnumType.JUNGLE);
      case ACACIA:
         return Blocks.LEAVES2.getDefaultState();
      case DARK_OAK:
         return Blocks.LEAVES2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, net.minecraft.block.BlockPlanks.EnumType.DARK_OAK);
      default:
         return Blocks.LEAVES.getDefaultState();
      }
   }

   public static IBlockState getStateSapling(net.minecraft.block.BlockPlanks.EnumType type) {
      switch(type) {
      case OAK:
         return Blocks.SAPLING.getDefaultState();
      case SPRUCE:
         return Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, net.minecraft.block.BlockPlanks.EnumType.SPRUCE);
      case BIRCH:
         return Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, net.minecraft.block.BlockPlanks.EnumType.BIRCH);
      case JUNGLE:
         return Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, net.minecraft.block.BlockPlanks.EnumType.JUNGLE);
      case ACACIA:
         return Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, net.minecraft.block.BlockPlanks.EnumType.ACACIA);
      case DARK_OAK:
         return Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, net.minecraft.block.BlockPlanks.EnumType.DARK_OAK);
      default:
         return Blocks.SAPLING.getDefaultState();
      }
   }

   public static IBlockState getSponge(boolean wet) {
      return wet ? Blocks.SPONGE.getDefaultState().withProperty(BlockSponge.WET, Boolean.valueOf(true)) : Blocks.SPONGE.getDefaultState();
   }

   public static IBlockState getStateFlower(EnumFlowerType type) {
      switch(type) {
      case DANDELION:
         return Blocks.YELLOW_FLOWER.getDefaultState().withProperty(Blocks.YELLOW_FLOWER.getTypeProperty(), type);
      case POPPY:
      case BLUE_ORCHID:
      case ALLIUM:
      case HOUSTONIA:
      case RED_TULIP:
      case ORANGE_TULIP:
      case WHITE_TULIP:
      case PINK_TULIP:
      case OXEYE_DAISY:
         return Blocks.RED_FLOWER.getDefaultState().withProperty(Blocks.RED_FLOWER.getTypeProperty(), type);
      default:
         return Blocks.RED_FLOWER.getDefaultState();
      }
   }

   public static IBlockState getStateDoublePlant(EnumPlantType variant) {
      switch(variant) {
      case SUNFLOWER:
         return Blocks.DOUBLE_PLANT.getDefaultState().withProperty(BlockDoublePlant.VARIANT, EnumPlantType.SUNFLOWER);
      case SYRINGA:
         return Blocks.DOUBLE_PLANT.getDefaultState().withProperty(BlockDoublePlant.VARIANT, EnumPlantType.SYRINGA);
      case GRASS:
         return Blocks.DOUBLE_PLANT.getDefaultState().withProperty(BlockDoublePlant.VARIANT, EnumPlantType.GRASS);
      case FERN:
         return Blocks.DOUBLE_PLANT.getDefaultState().withProperty(BlockDoublePlant.VARIANT, EnumPlantType.FERN);
      case ROSE:
         return Blocks.DOUBLE_PLANT.getDefaultState().withProperty(BlockDoublePlant.VARIANT, EnumPlantType.ROSE);
      case PAEONIA:
         return Blocks.DOUBLE_PLANT.getDefaultState().withProperty(BlockDoublePlant.VARIANT, EnumPlantType.PAEONIA);
      default:
         return Blocks.DOUBLE_PLANT.getDefaultState();
      }
   }

   public static IBlockState getSaplingFromLeaves(IBlockState leaves, IBlockState fallback) {
      IBlockState ret;
      return (ret = getSaplingFromLeaves(leaves)) != null ? ret : fallback;
   }

   @Nullable
   public static IBlockState getSaplingFromLeaves(IBlockState leaves) {
      if (!(leaves.getBlock() instanceof BlockLeaves)) {
         return null;
      } else {
         net.minecraft.block.BlockPlanks.EnumType type = (net.minecraft.block.BlockPlanks.EnumType)leaves.getProperties().get(BlockOldLeaf.VARIANT);
         if (type == null) {
            type = (net.minecraft.block.BlockPlanks.EnumType)leaves.getProperties().get(BlockNewLeaf.VARIANT);
         }

         if (type != null) {
            switch(type) {
            case OAK:
               return Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, net.minecraft.block.BlockPlanks.EnumType.OAK);
            case SPRUCE:
               return Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, net.minecraft.block.BlockPlanks.EnumType.SPRUCE);
            case BIRCH:
               return Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, net.minecraft.block.BlockPlanks.EnumType.BIRCH);
            case JUNGLE:
               return Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, net.minecraft.block.BlockPlanks.EnumType.JUNGLE);
            case ACACIA:
               return Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, net.minecraft.block.BlockPlanks.EnumType.ACACIA);
            case DARK_OAK:
               return Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, net.minecraft.block.BlockPlanks.EnumType.DARK_OAK);
            }
         }

         return null;
      }
   }

   @Nullable
   public static net.minecraft.block.BlockPlanks.EnumType getTypeFromSapling(IBlockState sapling) {
      if (!(sapling.getBlock() instanceof BlockSapling)) {
         return null;
      } else {
         net.minecraft.block.BlockPlanks.EnumType ret = null;

         try {
            ret = (net.minecraft.block.BlockPlanks.EnumType)sapling.getValue(BlockSapling.TYPE);
         } catch (IllegalArgumentException var3) {
            ;
         }

         return ret;
      }
   }

   public static boolean isBlockAir(IBlockState blockState) {
      return blockState.getBlock() == Blocks.AIR;
   }

   public static boolean isBlockAir(Block block) {
      return block == Blocks.AIR;
   }

   public static boolean isMaterialAir(IBlockState blockState) {
      return blockState.getMaterial() == Material.AIR;
   }

   public static boolean isMaterialAir(Block block) {
      return block.getDefaultState().getMaterial() == Material.AIR;
   }

   public static boolean isMaterialAir(Material material) {
      return material == Material.AIR;
   }

   public static boolean isBlock(World world, BlockPos origin, Block block) {
      return world.getBlockState(origin).getBlock() == block;
   }

   public static boolean isBlockOneOf(World world, BlockPos origin, Block... validBlocks) {
      return isBlockOneOf(world, origin, Collections.unmodifiableCollection(Arrays.asList(validBlocks)));
   }

   public static boolean isBlockOneOf(World world, BlockPos origin, Collection<Block> validBlocks) {
      Block checkBlock = world.getBlockState(origin).getBlock();
      return validBlocks.isEmpty() ? isBlockAir(checkBlock) : validBlocks.contains(checkBlock);
   }

   public static boolean isMaterial(World world, BlockPos origin, Material material) {
      return world.getBlockState(origin).getMaterial() == material;
   }

   public static boolean isMaterialOneOf(World world, BlockPos origin, Material... validMaterials) {
      return isMaterialOneOf(world, origin, Collections.unmodifiableCollection(Arrays.asList(validMaterials)));
   }

   public static boolean isMaterialOneOf(World world, BlockPos origin, Collection<Material> validMaterials) {
      Material checkMaterial = world.getBlockState(origin).getMaterial();
      return validMaterials.isEmpty() ? isMaterialAir(checkMaterial) : validMaterials.contains(checkMaterial);
   }

   public static boolean isReplaceable(World world, BlockPos origin) {
      return world.getBlockState(origin).getMaterial().isReplaceable();
   }

   public static boolean checkAdjacentBlocks(BlockUtil.MatchType matchType, World world, BlockPos origin, Block... validBlocks) {
      return checkAdjacentBlocks(matchType, world, origin, Collections.unmodifiableCollection(Arrays.asList(validBlocks)));
   }

   public static boolean checkAdjacentBlocks(BlockUtil.MatchType matchType, World world, BlockPos origin, Collection<Block> validBlocks) {
      boolean ignoreReplaceable = matchType == BlockUtil.MatchType.ALL_IGNORE_REPLACEABLE;
      switch(matchType) {
      case ALL_IGNORE_REPLACEABLE:
      case ALL:
         EnumFacing[] var11 = EnumFacing.HORIZONTALS;
         int var13 = var11.length;
         int var15 = 0;

         while(true) {
            if (var15 >= var13) {
               return true;
            }

            EnumFacing direction = var11[var15];
            IBlockState bs = world.getBlockState(origin.offset(direction));
            if ((!ignoreReplaceable || bs.getMaterial().isReplaceable()) && !validBlocks.isEmpty()) {
               if (!validBlocks.contains(bs.getBlock())) {
                  break;
               }
            } else if (!isBlockAir(bs)) {
               break;
            }

            ++var15;
         }

         return false;
      case ANY:
         EnumFacing[] var10 = EnumFacing.HORIZONTALS;
         int var12 = var10.length;
         int var14 = 0;

         while(true) {
            if (var14 >= var12) {
               return false;
            }

            EnumFacing direction = var10[var14];
            Block block = world.getBlockState(origin.offset(direction)).getBlock();
            if (validBlocks.isEmpty()) {
               if (isBlockAir(block)) {
                  break;
               }
            } else if (!validBlocks.contains(block)) {
               break;
            }

            ++var14;
         }

         return true;
      case NONE:
         EnumFacing[] var5 = EnumFacing.HORIZONTALS;
         int var6 = var5.length;
         int var7 = 0;

         while(true) {
            if (var7 >= var6) {
               return true;
            }

            EnumFacing direction = var5[var7];
            Block block = world.getBlockState(origin.offset(direction)).getBlock();
            if (validBlocks.isEmpty()) {
               if (isBlockAir(block)) {
                  break;
               }
            } else if (!validBlocks.contains(block)) {
               break;
            }

            ++var7;
         }

         return false;
      default:
         return false;
      }
   }

   public static boolean checkAdjacentMaterials(BlockUtil.MatchType matchType, World world, BlockPos origin, Material... validMaterials) {
      return checkAdjacentMaterials(matchType, world, origin, Collections.unmodifiableCollection(Arrays.asList(validMaterials)));
   }

   public static boolean checkAdjacentMaterials(BlockUtil.MatchType matchType, World world, BlockPos origin, Collection<Material> validMaterials) {
      boolean ignoreReplaceable = matchType == BlockUtil.MatchType.ALL_IGNORE_REPLACEABLE;
      switch(matchType) {
      case ALL_IGNORE_REPLACEABLE:
      case ALL:
         EnumFacing[] var11 = EnumFacing.HORIZONTALS;
         int var13 = var11.length;
         int var15 = 0;

         while(true) {
            if (var15 >= var13) {
               return true;
            }

            EnumFacing direction = var11[var15];
            Material material = world.getBlockState(origin.offset(direction)).getMaterial();
            if ((!ignoreReplaceable || material.isReplaceable()) && !validMaterials.isEmpty()) {
               if (!validMaterials.contains(material)) {
                  break;
               }
            } else if (!isMaterialAir(material)) {
               break;
            }

            ++var15;
         }

         return false;
      case ANY:
         EnumFacing[] var10 = EnumFacing.HORIZONTALS;
         int var12 = var10.length;
         int var14 = 0;

         while(true) {
            if (var14 >= var12) {
               return false;
            }

            EnumFacing direction = var10[var14];
            Material material = world.getBlockState(origin.offset(direction)).getMaterial();
            if (validMaterials.isEmpty()) {
               if (isMaterialAir(material)) {
                  break;
               }
            } else if (validMaterials.contains(material)) {
               break;
            }

            ++var14;
         }

         return true;
      case NONE:
         EnumFacing[] var5 = EnumFacing.HORIZONTALS;
         int var6 = var5.length;
         int var7 = 0;

         while(true) {
            if (var7 >= var6) {
               return true;
            }

            EnumFacing direction = var5[var7];
            Material material = world.getBlockState(origin.offset(direction)).getMaterial();
            if (validMaterials.isEmpty()) {
               if (isMaterialAir(material)) {
                  break;
               }
            } else if (validMaterials.contains(material)) {
               break;
            }

            ++var7;
         }

         return false;
      default:
         return false;
      }
   }

   public static boolean checkVerticalBlocks(BlockUtil.MatchType matchType, World world, BlockPos origin, int distance, Block... validBlocks) {
      return checkVerticalBlocks(matchType, world, origin, distance, Collections.unmodifiableCollection(Arrays.asList(validBlocks)));
   }

   public static boolean checkVerticalBlocks(BlockUtil.MatchType matchType, World world, BlockPos origin, int distance, Collection<Block> validBlocks) {
      int ox = origin.getX();
      int oy = origin.getY();
      int oz = origin.getZ();
      int dis = Math.abs(distance);
      boolean invert = distance < 0;
      boolean ignoreReplaceable = matchType == BlockUtil.MatchType.ALL_IGNORE_REPLACEABLE;
      MutableBlockPos mpos = new MutableBlockPos();
      switch(matchType) {
      case ALL_IGNORE_REPLACEABLE:
      case ALL:
         int i = dis;

         while(true) {
            if (i <= 0) {
               return true;
            }

            IBlockState bs = world.getBlockState(mpos.setPos(ox, oy + (invert ? -i : i), oz));
            if ((!ignoreReplaceable || bs.getMaterial().isReplaceable()) && !validBlocks.isEmpty()) {
               if (!validBlocks.contains(bs.getBlock())) {
                  break;
               }
            } else if (!isBlockAir(bs)) {
               break;
            }

            --i;
         }

         return false;
      case ANY:
         int i = dis;

         while(true) {
            if (i <= 0) {
               return false;
            }

            Block block = world.getBlockState(mpos.setPos(ox, oy + (invert ? -i : i), oz)).getBlock();
            if (validBlocks.isEmpty()) {
               if (isBlockAir(block)) {
                  break;
               }
            } else if (validBlocks.contains(block)) {
               break;
            }

            --i;
         }

         return true;
      case NONE:
         int i = dis;

         while(true) {
            if (i <= 0) {
               return true;
            }

            Block block = world.getBlockState(mpos.setPos(ox, oy + (invert ? -i : i), oz)).getBlock();
            if (validBlocks.isEmpty()) {
               if (isBlockAir(block)) {
                  break;
               }
            } else if (validBlocks.contains(block)) {
               break;
            }

            --i;
         }

         return false;
      default:
         return false;
      }
   }

   public static boolean checkVerticalMaterials(BlockUtil.MatchType matchType, World world, BlockPos origin, int distance, Material... validMaterials) {
      return checkVerticalMaterials(matchType, world, origin, distance, Collections.unmodifiableCollection(Arrays.asList(validMaterials)));
   }

   public static boolean checkVerticalMaterials(BlockUtil.MatchType matchType, World world, BlockPos origin, int distance, Collection<Material> validMaterials) {
      int ox = origin.getX();
      int oy = origin.getY();
      int oz = origin.getZ();
      int dis = Math.abs(distance);
      boolean invert = distance < 0;
      boolean ignoreReplaceable = matchType == BlockUtil.MatchType.ALL_IGNORE_REPLACEABLE;
      MutableBlockPos mpos = new MutableBlockPos();
      switch(matchType) {
      case ALL_IGNORE_REPLACEABLE:
      case ALL:
         int i = dis;

         while(true) {
            if (i <= 0) {
               return true;
            }

            Material material = world.getBlockState(mpos.setPos(ox, oy + (invert ? -i : i), oz)).getMaterial();
            if ((!ignoreReplaceable || material.isReplaceable()) && !validMaterials.isEmpty()) {
               if (!validMaterials.contains(material)) {
                  break;
               }
            } else if (!isMaterialAir(material)) {
               break;
            }

            --i;
         }

         return false;
      case ANY:
         int i = dis;

         while(true) {
            if (i <= 0) {
               return false;
            }

            Material material = world.getBlockState(mpos.setPos(ox, oy + (invert ? -i : i), oz)).getMaterial();
            if (validMaterials.isEmpty()) {
               if (isMaterialAir(material)) {
                  break;
               }
            } else if (validMaterials.contains(material)) {
               break;
            }

            --i;
         }

         return true;
      case NONE:
         int i = dis;

         while(true) {
            if (i <= 0) {
               return true;
            }

            Material material = world.getBlockState(mpos.setPos(ox, oy + (invert ? -i : i), oz)).getMaterial();
            if (validMaterials.isEmpty()) {
               if (isMaterialAir(material)) {
                  break;
               }
            } else if (validMaterials.contains(material)) {
               break;
            }

            --i;
         }

         return false;
      default:
         return false;
      }
   }

   public static boolean checkAreaBlocks(BlockUtil.MatchType matchType, World world, BlockPos origin, int checkRadius, Block... validBlocks) {
      return checkAreaBlocks(matchType, world, origin, checkRadius, Collections.unmodifiableCollection(Arrays.asList(validBlocks)));
   }

   public static boolean checkAreaBlocks(BlockUtil.MatchType matchType, World world, BlockPos origin, int checkRadius, Collection<Block> validBlocks) {
      int ox = origin.getX();
      int oy = origin.getY();
      int oz = origin.getZ();
      boolean ignoreReplaceable = matchType == BlockUtil.MatchType.ALL_IGNORE_REPLACEABLE;
      MutableBlockPos mpos = new MutableBlockPos();
      int i = 1;
      switch(matchType) {
      case ALL_IGNORE_REPLACEABLE:
      case ALL:
         label219:
         while(i <= checkRadius) {
            int x = ox + i;
            int z = oz + i;

            while(true) {
               if (x <= ox - i) {
                  while(true) {
                     if (z <= oz - i) {
                        while(true) {
                           if (x >= ox + i) {
                              while(true) {
                                 if (z >= oz + i) {
                                    ++i;
                                    continue label219;
                                 }

                                 ++z;
                                 IBlockState bs = world.getBlockState(mpos.setPos(x, oy, z));
                                 if ((!ignoreReplaceable || bs.getMaterial().isReplaceable()) && !validBlocks.isEmpty()) {
                                    if (!validBlocks.contains(bs.getBlock())) {
                                       break;
                                    }
                                 } else if (!isBlockAir(bs)) {
                                    break;
                                 }
                              }

                              return false;
                           }

                           ++x;
                           IBlockState bs = world.getBlockState(mpos.setPos(x, oy, z));
                           if ((!ignoreReplaceable || bs.getMaterial().isReplaceable()) && !validBlocks.isEmpty()) {
                              if (!validBlocks.contains(bs.getBlock())) {
                                 break;
                              }
                           } else if (!isBlockAir(bs)) {
                              break;
                           }
                        }

                        return false;
                     }

                     --z;
                     IBlockState bs = world.getBlockState(mpos.setPos(x, oy, z));
                     if ((!ignoreReplaceable || bs.getMaterial().isReplaceable()) && !validBlocks.isEmpty()) {
                        if (!validBlocks.contains(bs.getBlock())) {
                           break;
                        }
                     } else if (!isBlockAir(bs)) {
                        break;
                     }
                  }

                  return false;
               }

               --x;
               IBlockState bs = world.getBlockState(mpos.setPos(x, oy, z));
               if ((!ignoreReplaceable || bs.getMaterial().isReplaceable()) && !validBlocks.isEmpty()) {
                  if (!validBlocks.contains(bs.getBlock())) {
                     break;
                  }
               } else if (!isBlockAir(bs)) {
                  break;
               }
            }

            return false;
         }

         return true;
      case ANY:
         label160:
         while(i <= checkRadius) {
            int x = ox + i;
            int z = oz + i;

            while(true) {
               if (x <= ox - i) {
                  while(true) {
                     if (z <= oz - i) {
                        while(true) {
                           if (x >= ox + i) {
                              while(true) {
                                 if (z >= oz + i) {
                                    ++i;
                                    continue label160;
                                 }

                                 ++z;
                                 IBlockState bs = world.getBlockState(mpos.setPos(x, oy, z));
                                 if (validBlocks.isEmpty()) {
                                    if (isBlockAir(bs)) {
                                       break;
                                    }
                                 } else if (validBlocks.contains(bs.getBlock())) {
                                    break;
                                 }
                              }

                              return true;
                           }

                           ++x;
                           IBlockState bs = world.getBlockState(mpos.setPos(x, oy, z));
                           if (validBlocks.isEmpty()) {
                              if (isBlockAir(bs)) {
                                 break;
                              }
                           } else if (validBlocks.contains(bs.getBlock())) {
                              break;
                           }
                        }

                        return true;
                     }

                     --z;
                     IBlockState bs = world.getBlockState(mpos.setPos(x, oy, z));
                     if (validBlocks.isEmpty()) {
                        if (isBlockAir(bs)) {
                           break;
                        }
                     } else if (validBlocks.contains(bs.getBlock())) {
                        break;
                     }
                  }

                  return true;
               }

               --x;
               IBlockState bs = world.getBlockState(mpos.setPos(x, oy, z));
               if (validBlocks.isEmpty()) {
                  if (isBlockAir(bs)) {
                     break;
                  }
               } else if (validBlocks.contains(bs.getBlock())) {
                  break;
               }
            }

            return true;
         }

         return false;
      case NONE:
         while(i <= checkRadius) {
            int x = ox + i;
            int z = oz + i;

            while(x > ox - i) {
               --x;
               if (validBlocks.contains(world.getBlockState(mpos.setPos(x, oy, z)).getBlock())) {
                  return false;
               }
            }

            while(z > oz - i) {
               --z;
               if (validBlocks.contains(world.getBlockState(mpos.setPos(x, oy, z)).getBlock())) {
                  return false;
               }
            }

            while(x < ox + i) {
               ++x;
               if (validBlocks.contains(world.getBlockState(mpos.setPos(x, oy, z)).getBlock())) {
                  return false;
               }
            }

            while(z < oz + i) {
               ++z;
               if (validBlocks.contains(world.getBlockState(mpos.setPos(x, oy, z)).getBlock())) {
                  return false;
               }
            }

            ++i;
         }

         return true;
      default:
         return false;
      }
   }

   public static boolean checkAreaMaterials(BlockUtil.MatchType matchType, World world, BlockPos origin, int checkRadius, Material... validMaterials) {
      return checkAreaMaterials(matchType, world, origin, checkRadius, Collections.unmodifiableCollection(Arrays.asList(validMaterials)));
   }

   public static boolean checkAreaMaterials(BlockUtil.MatchType matchType, World world, BlockPos origin, int checkRadius, Collection<Material> validMaterials) {
      int ox = origin.getX();
      int oy = origin.getY();
      int oz = origin.getZ();
      boolean ignoreReplaceable = matchType == BlockUtil.MatchType.ALL_IGNORE_REPLACEABLE;
      MutableBlockPos mpos = new MutableBlockPos();
      int i = 1;
      switch(matchType) {
      case ALL_IGNORE_REPLACEABLE:
      case ALL:
         label244:
         while(i <= checkRadius) {
            int x = ox + i;
            int z = oz + i;

            while(true) {
               if (x <= ox - i) {
                  while(true) {
                     if (z <= oz - i) {
                        while(true) {
                           if (x >= ox + i) {
                              while(true) {
                                 if (z >= oz + i) {
                                    ++i;
                                    continue label244;
                                 }

                                 ++z;
                                 Material material = world.getBlockState(mpos.setPos(x, oy, z)).getMaterial();
                                 if ((!ignoreReplaceable || material.isReplaceable()) && !validMaterials.isEmpty()) {
                                    if (!validMaterials.contains(material)) {
                                       break;
                                    }
                                 } else if (!isMaterialAir(material)) {
                                    break;
                                 }
                              }

                              return false;
                           }

                           ++x;
                           Material material = world.getBlockState(mpos.setPos(x, oy, z)).getMaterial();
                           if ((!ignoreReplaceable || material.isReplaceable()) && !validMaterials.isEmpty()) {
                              if (!validMaterials.contains(material)) {
                                 break;
                              }
                           } else if (!isMaterialAir(material)) {
                              break;
                           }
                        }

                        return false;
                     }

                     --z;
                     Material material = world.getBlockState(mpos.setPos(x, oy, z)).getMaterial();
                     if ((!ignoreReplaceable || material.isReplaceable()) && !validMaterials.isEmpty()) {
                        if (!validMaterials.contains(material)) {
                           break;
                        }
                     } else if (!isMaterialAir(material)) {
                        break;
                     }
                  }

                  return false;
               }

               --x;
               Material material = world.getBlockState(mpos.setPos(x, oy, z)).getMaterial();
               if ((!ignoreReplaceable || material.isReplaceable()) && !validMaterials.isEmpty()) {
                  if (!validMaterials.contains(material)) {
                     break;
                  }
               } else if (!isMaterialAir(material)) {
                  break;
               }
            }

            return false;
         }

         return true;
      case ANY:
         label185:
         while(i <= checkRadius) {
            int x = ox + i;
            int z = oz + i;

            while(true) {
               if (x <= ox - i) {
                  while(true) {
                     if (z <= oz - i) {
                        while(true) {
                           if (x >= ox + i) {
                              while(true) {
                                 if (z >= oz + i) {
                                    ++i;
                                    continue label185;
                                 }

                                 ++z;
                                 Material material = world.getBlockState(mpos.setPos(x, oy, z)).getMaterial();
                                 if (validMaterials.isEmpty()) {
                                    if (isMaterialAir(material)) {
                                       break;
                                    }
                                 } else if (validMaterials.contains(material)) {
                                    break;
                                 }
                              }

                              return true;
                           }

                           ++x;
                           Material material = world.getBlockState(mpos.setPos(x, oy, z)).getMaterial();
                           if (validMaterials.isEmpty()) {
                              if (isMaterialAir(material)) {
                                 break;
                              }
                           } else if (validMaterials.contains(material)) {
                              break;
                           }
                        }

                        return true;
                     }

                     --z;
                     Material material = world.getBlockState(mpos.setPos(x, oy, z)).getMaterial();
                     if (validMaterials.isEmpty()) {
                        if (isMaterialAir(material)) {
                           break;
                        }
                     } else if (validMaterials.contains(material)) {
                        break;
                     }
                  }

                  return true;
               }

               --x;
               Material material = world.getBlockState(mpos.setPos(x, oy, z)).getMaterial();
               if (validMaterials.isEmpty()) {
                  if (isMaterialAir(material)) {
                     break;
                  }
               } else if (validMaterials.contains(material)) {
                  break;
               }
            }

            return true;
         }

         return false;
      case NONE:
         label146:
         while(i <= checkRadius) {
            int x = ox + i;
            int z = oz + i;

            while(true) {
               if (x <= ox - i) {
                  while(true) {
                     if (z <= oz - i) {
                        while(true) {
                           if (x >= ox + i) {
                              while(true) {
                                 if (z >= oz + i) {
                                    ++i;
                                    continue label146;
                                 }

                                 ++z;
                                 Material material = world.getBlockState(mpos.setPos(x, oy, z)).getMaterial();
                                 if (validMaterials.isEmpty()) {
                                    if (isMaterialAir(material)) {
                                       break;
                                    }
                                 } else if (validMaterials.contains(material)) {
                                    break;
                                 }
                              }

                              return false;
                           }

                           ++x;
                           Material material = world.getBlockState(mpos.setPos(x, oy, z)).getMaterial();
                           if (validMaterials.isEmpty()) {
                              if (isMaterialAir(material)) {
                                 break;
                              }
                           } else if (validMaterials.contains(material)) {
                              break;
                           }
                        }

                        return false;
                     }

                     --z;
                     Material material = world.getBlockState(mpos.setPos(x, oy, z)).getMaterial();
                     if (validMaterials.isEmpty()) {
                        if (isMaterialAir(material)) {
                           break;
                        }
                     } else if (validMaterials.contains(material)) {
                        break;
                     }
                  }

                  return false;
               }

               --x;
               Material material = world.getBlockState(mpos.setPos(x, oy, z)).getMaterial();
               if (validMaterials.isEmpty()) {
                  if (isMaterialAir(material)) {
                     break;
                  }
               } else if (validMaterials.contains(material)) {
                  break;
               }
            }

            return false;
         }

         return true;
      default:
         return false;
      }
   }

   public static boolean checkVolumeBlocks(BlockUtil.MatchType matchType, World world, BlockPos origin, int checkRadius, int checkHeight, Block... validBlocks) {
      return checkVolumeBlocks(matchType, world, origin, checkRadius, checkHeight, Collections.unmodifiableCollection(Arrays.asList(validBlocks)));
   }

   public static boolean checkVolumeBlocks(BlockUtil.MatchType matchType, World world, BlockPos origin, int checkRadius, int checkHeight, Collection<Block> validBlocks) {
      int ox = origin.getX();
      int oy = origin.getY();
      int oz = origin.getZ();
      int height = Math.abs(checkHeight);
      boolean invert = checkHeight < 0;
      MutableBlockPos mpos = new MutableBlockPos();
      if (!checkVerticalBlocks(matchType, world, origin, checkHeight, validBlocks)) {
         return false;
      } else {
         for(int i = 0; i < height; ++i) {
            if (!checkAreaBlocks(matchType, world, mpos.setPos(ox, oy + (invert ? -i : i), oz), checkRadius, validBlocks)) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean checkVolumeMaterials(BlockUtil.MatchType matchType, World world, BlockPos origin, int checkRadius, int checkHeight, Material... validMaterials) {
      return checkVolumeMaterials(matchType, world, origin, checkRadius, checkHeight, Collections.unmodifiableCollection(Arrays.asList(validMaterials)));
   }

   public static boolean checkVolumeMaterials(BlockUtil.MatchType matchType, World world, BlockPos origin, int checkRadius, int checkHeight, Collection<Material> validMaterials) {
      int ox = origin.getX();
      int oy = origin.getY();
      int oz = origin.getZ();
      int height = Math.abs(checkHeight);
      boolean invert = checkHeight < 0;
      MutableBlockPos mpos = new MutableBlockPos();
      if (!checkVerticalMaterials(matchType, world, origin, checkHeight, validMaterials)) {
         return false;
      } else {
         for(int i = 0; i < height; ++i) {
            if (!checkAreaMaterials(matchType, world, mpos.setPos(ox, oy + (invert ? -i : i), oz), checkRadius, validMaterials)) {
               return false;
            }
         }

         return true;
      }
   }

   public static enum EnumMaterial {
      AIR(Material.AIR),
      ANVIL(Material.ANVIL),
      BARRIER(Material.BARRIER),
      CACTUS(Material.CACTUS),
      CAKE(Material.CAKE),
      CARPET(Material.CARPET),
      CIRCUITS(Material.CIRCUITS),
      CLAY(Material.CLAY),
      CLOTH(Material.CLOTH),
      CORAL(Material.CORAL),
      CRAFTED_SNOW(Material.CRAFTED_SNOW),
      DRAGON_EGG(Material.DRAGON_EGG),
      FIRE(Material.FIRE),
      GLASS(Material.GLASS),
      GOURD(Material.GOURD),
      GRASS(Material.GRASS),
      GROUND(Material.GROUND),
      ICE(Material.ICE),
      IRON(Material.IRON),
      LAVA(Material.LAVA),
      LEAVES(Material.LEAVES),
      PACKED_ICE(Material.PACKED_ICE),
      PISTON(Material.PISTON),
      PLANTS(Material.PLANTS),
      PORTAL(Material.PORTAL),
      REDSTONE_LIGHT(Material.REDSTONE_LIGHT),
      ROCK(Material.ROCK),
      SAND(Material.SAND),
      SNOW(Material.SNOW),
      SPONGE(Material.SPONGE),
      STRUCTURE_VOID(Material.STRUCTURE_VOID),
      TNT(Material.TNT),
      VINE(Material.VINE),
      WATER(Material.WATER),
      WEB(Material.WEB),
      WOOD(Material.WOOD);

      private final Material material;

      private EnumMaterial(Material material) {
         this.material = material;
      }

      public static Collection<Material> getMaterials() {
         return Collections.unmodifiableCollection((Collection)Arrays.stream(values()).map(BlockUtil.EnumMaterial::getMaterial).collect(Collectors.toSet()));
      }

      @Nullable
      public static Material getByName(String name) {
         return (Material)Arrays.stream(values()).filter((material) -> {
            return material.name().equals(name.toUpperCase(Locale.ENGLISH));
         }).findFirst().map(BlockUtil.EnumMaterial::getMaterial).orElse((Object)null);
      }

      @Nullable
      public static BlockUtil.EnumMaterial getByMaterial(Material material) {
         return (BlockUtil.EnumMaterial)Arrays.stream(values()).filter((value) -> {
            return value.getMaterial() == material;
         }).findFirst().orElse((Object)null);
      }

      public static Collection<Material> getReplaceable() {
         return Collections.unmodifiableCollection((Collection)Arrays.stream(values()).filter(BlockUtil.EnumMaterial::isReplaceable).map(BlockUtil.EnumMaterial::getMaterial).collect(Collectors.toList()));
      }

      public Material getMaterial() {
         return this.material;
      }

      public boolean isReplaceable() {
         return this.material.isReplaceable();
      }
   }

   public static enum MatchType {
      ALL_IGNORE_REPLACEABLE,
      ALL,
      ANY,
      NONE;

      private MatchType() {
      }
   }
}
