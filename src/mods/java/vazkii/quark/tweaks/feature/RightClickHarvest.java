package vazkii.quark.tweaks.feature;

import com.google.common.collect.Maps;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.Feature;
import vazkii.quark.tweaks.base.BlockStack;

public class RightClickHarvest extends Feature {
   public static boolean hoeHarvest;
   public static boolean emptyHandHarvest;
   public static boolean harvestingCostsDurability;
   public static boolean doHarvestingSearch;
   public static String[] harvestableBlocks;
   public static final Map<BlockStack, BlockStack> crops = Maps.newHashMap();
   private static boolean hasInit;
   private static Method getSeed;

   public RightClickHarvest() {
   }

   public void setupConfig() {
      hoeHarvest = this.loadPropBool("Hoe Harvest", "Can hoes harvest crops with right click?", true);
      emptyHandHarvest = this.loadPropBool("Empty Hand Harvest", "Can players harvest crops with empty hand clicks?", true);
      harvestingCostsDurability = this.loadPropBool("Harvesting Costs Durability", "Does harvesting crops with a hoe cost durability?", false);
      doHarvestingSearch = this.loadPropBool("Add Harvestable Crops", "Should Quark look for (nonvanilla) crops, and handle them?", true);
      harvestableBlocks = this.loadPropStringList("Harvestable Crops", "Which crops can be harvested?\nFormat is: \"harvestState[,afterHarvest]\", i.e. \"minecraft:wheat:7\" or \"minecraft:cocoa:11,minecraft:cocoa:3\"", new String[]{"minecraft:wheat:7", "minecraft:carrots:7", "minecraft:potatoes:7", "minecraft:beetroots:3", "minecraft:nether_wart:3", "minecraft:cocoa:11,minecraft:cocoa:3", "minecraft:cocoa:10,minecraft:cocoa:2", "minecraft:cocoa:9,minecraft:cocoa:1", "minecraft:cocoa:8,minecraft:cocoa:0"});
      if (hasInit) {
         this.fillCropList();
      }

   }

   public void init() {
      hasInit = true;
      this.fillCropList();
   }

   private void fillCropList() {
      crops.clear();
      if (doHarvestingSearch) {
         ForgeRegistries.BLOCKS.getValuesCollection().stream().filter((b) -> {
            return !isVanilla(b) && b instanceof BlockCrops;
         }).forEach((b) -> {
            BlockStack var10000 = (BlockStack)crops.put(new BlockStack(b, ((BlockCrops)b).getMaxAge()), new BlockStack(b));
         });
      }

      for(String harvestKey : harvestableBlocks) {
         String[] split = harvestKey.split(",", 2);
         BlockStack initial = BlockStack.fromString(split[0]);
         BlockStack result;
         if (split.length > 1) {
            result = BlockStack.fromString(split[1]);
         } else {
            result = new BlockStack(initial.getBlock());
         }

         crops.put(initial, result);
      }

   }

   private static void replant(World world, BlockPos pos, BlockStack inWorld, EntityPlayer player) {
      ItemStack mainHand = player.getHeldItemMainhand();
      boolean isHoe = !mainHand.isEmpty() && mainHand.getItem() instanceof ItemHoe;
      BlockStack newBlock = (BlockStack)crops.get(inWorld);
      NonNullList<ItemStack> drops = NonNullList.create();
      int fortune = HoeSickle.canFortuneApply(Enchantments.FORTUNE, mainHand) && isHoe ? EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, mainHand) : 0;
      inWorld.getBlock().getDrops(drops, world, pos, inWorld.getState(), fortune);

      for(ItemStack stack : drops) {
         if (!stack.isEmpty() && (stack.getItem() instanceof IPlantable || stack.getItem() == Items.DYE && stack.getMetadata() == EnumDyeColor.BROWN.getDyeDamage())) {
            stack.shrink(1);
            break;
         }
      }

      ForgeEventFactory.fireBlockHarvesting(drops, world, pos, inWorld.getState(), fortune, 1.0F, false, player);
      boolean seedNotNull = true;
      if (inWorld.getBlock() instanceof BlockCrops) {
         try {
            if (getSeed == null) {
               getSeed = ObfuscationReflectionHelper.findMethod(BlockCrops.class, "getSeed", Item.class, new Class[0]);
            }

            Item seed = (Item)getSeed.invoke(inWorld.getBlock());
            seedNotNull = seed != null && seed != Items.AIR;
         } catch (InvocationTargetException | IllegalAccessException var13) {
            Quark.LOG.error("Failed to reflect BlockCrops", var13);
         }
      }

      if (seedNotNull) {
         if (!world.isRemote) {
            world.playEvent(2001, pos, Block.getStateId(newBlock.getState()));
            world.setBlockState(pos, newBlock.getState());

            for(ItemStack stack : drops) {
               EntityItem entityItem = new EntityItem(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, stack);
               entityItem.setPickupDelay(10);
               world.spawnEntity(entityItem);
            }
         }
      } else {
         Quark.LOG.warn("Crop definition {},{} does not work! Couldn't find a seed definition.", inWorld, newBlock);
      }

   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onCropClick(RightClickBlock event) {
      if (event.getHand() == EnumHand.MAIN_HAND) {
         ItemStack mainHand = event.getEntityPlayer().getHeldItemMainhand();
         boolean isHoe = !mainHand.isEmpty() && mainHand.getItem() instanceof ItemHoe;
         if (emptyHandHarvest || isHoe) {
            int range = HoeSickle.getRange(mainHand);
            int harvests = 0;

            for(int x = 1 - range; x < range; ++x) {
               for(int z = 1 - range; z < range; ++z) {
                  BlockPos pos = event.getPos().add(x, 0, z);
                  BlockStack worldBlock = BlockStack.getStackFromPos(event.getWorld(), pos);
                  if (crops.containsKey(worldBlock)) {
                     replant(event.getWorld(), pos, worldBlock, event.getEntityPlayer());
                     ++harvests;
                  }
               }
            }

            if (harvests > 0) {
               if (harvestingCostsDurability && isHoe && !event.getWorld().isRemote) {
                  mainHand.damageItem(harvests, event.getEntityPlayer());
               }

               event.getEntityPlayer().swingArm(EnumHand.MAIN_HAND);
               event.setCanceled(true);
               event.setCancellationResult(EnumActionResult.SUCCESS);
            }

         }
      }
   }

   public boolean hasSubscriptions() {
      return true;
   }

   public String[] getIncompatibleMods() {
      return new String[]{"harvest"};
   }
}
