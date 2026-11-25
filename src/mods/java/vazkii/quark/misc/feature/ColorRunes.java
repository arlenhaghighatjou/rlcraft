package vazkii.quark.misc.feature;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetMetadata;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.api.ICustomEnchantColor;
import vazkii.quark.api.capability.IEnchantColorProvider;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.misc.item.ItemRune;

public class ColorRunes extends Feature {
   public static final String TAG_RUNE_ATTACHED = "Quark:RuneAttached";
   public static final String TAG_RUNE_COLOR = "Quark:RuneColor";
   private static ItemStack targetStack;
   public static Item rune;
   public static int dungeonWeight;
   public static int netherFortressWeight;
   public static int jungleTempleWeight;
   public static int desertTempleWeight;
   public static int itemQuality;
   public static int applyCost;
   public static boolean enableRainbowRuneCrafting;
   public static boolean enableRainbowRuneChests;
   public static boolean stackable;

   public ColorRunes() {
   }

   public void setupConfig() {
      dungeonWeight = this.loadPropInt("Dungeon loot weight", "", 20);
      netherFortressWeight = this.loadPropInt("Nether Fortress loot weight", "", 15);
      jungleTempleWeight = this.loadPropInt("Jungle Temple loot weight", "", 15);
      desertTempleWeight = this.loadPropInt("Desert Temple loot weight", "", 15);
      itemQuality = this.loadPropInt("Item quality for loot", "", 0);
      applyCost = this.loadPropInt("Cost to apply rune", "", 15);
      enableRainbowRuneCrafting = this.loadPropBool("Enable Rainbow Rune Crafting", "", true);
      enableRainbowRuneChests = this.loadPropBool("Enable Rainbow Rune in Chests", "", false);
      stackable = this.loadPropBool("Stackable Runes", "", true);
   }

   public void preInit(FMLPreInitializationEvent event) {
      rune = new ItemRune(stackable);
      if (enableRainbowRuneCrafting) {
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(rune, 7, 16), new Object[]{"345", "2G6", "1W7", 'G', ProxyRegistry.newStack(Blocks.GLASS), 'W', ProxyRegistry.newStack(rune, 1, 0), '1', ProxyRegistry.newStack(rune, 1, 14), '2', ProxyRegistry.newStack(rune, 1, 1), '3', ProxyRegistry.newStack(rune, 1, 4), '4', ProxyRegistry.newStack(rune, 1, 5), '5', ProxyRegistry.newStack(rune, 1, 3), '6', ProxyRegistry.newStack(rune, 1, 11), '7', ProxyRegistry.newStack(rune, 1, 2)});
      }

   }

   @SubscribeEvent
   public void onLootTableLoad(LootTableLoadEvent event) {
      LootFunction[] funcs = new LootFunction[]{new SetMetadata(new LootCondition[0], new RandomValueRange(0.0F, enableRainbowRuneChests ? 16.0F : 15.0F))};
      if (event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON)) {
         event.getTable().getPool("main").addEntry(new LootEntryItem(rune, dungeonWeight, itemQuality, funcs, new LootCondition[0], "quark:rune"));
      } else if (event.getName().equals(LootTableList.CHESTS_NETHER_BRIDGE)) {
         event.getTable().getPool("main").addEntry(new LootEntryItem(rune, netherFortressWeight, itemQuality, funcs, new LootCondition[0], "quark:rune"));
      } else if (event.getName().equals(LootTableList.CHESTS_JUNGLE_TEMPLE)) {
         event.getTable().getPool("main").addEntry(new LootEntryItem(rune, jungleTempleWeight, itemQuality, funcs, new LootCondition[0], "quark:rune"));
      } else if (event.getName().equals(LootTableList.CHESTS_DESERT_PYRAMID)) {
         event.getTable().getPool("main").addEntry(new LootEntryItem(rune, desertTempleWeight, itemQuality, funcs, new LootCondition[0], "quark:rune"));
      }

   }

   @SubscribeEvent
   public void onAnvilUpdate(AnvilUpdateEvent event) {
      ItemStack left = event.getLeft();
      ItemStack right = event.getRight();
      if (!left.isEmpty() && !right.isEmpty() && left.isItemEnchanted() && right.getItem() == rune) {
         ItemStack out = left.copy();
         ItemNBTHelper.setBoolean(out, "Quark:RuneAttached", true);
         ItemNBTHelper.setInt(out, "Quark:RuneColor", right.getItemDamage());
         event.setOutput(out);
         event.setCost(applyCost);
         event.setMaterialCost(1);
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public static void setTargetStack(ItemStack stack) {
      targetStack = stack;
   }

   public static void setTargetStack(EntityLivingBase entity, EntityEquipmentSlot slot) {
      setTargetStack(entity.getItemStackFromSlot(slot));
   }

   public static int getColor(int original) {
      return ModuleLoader.isFeatureEnabled(ColorRunes.class) && (doesStackHaveRune(targetStack) || targetStack.isEmpty() || targetStack.getItem() instanceof ICustomEnchantColor) ? getColorFromStack(targetStack) : original;
   }

   public static void applyColor() {
      if (ModuleLoader.isFeatureEnabled(ColorRunes.class) && doesStackHaveRune(targetStack)) {
         int color = getColorFromStack(targetStack);
         float r = (float)(color >> 16 & 255) / 255.0F;
         float g = (float)(color >> 8 & 255) / 255.0F;
         float b = (float)(color & 255) / 255.0F;
         GlStateManager.color(r, g, b, 1.0F);
      }
   }

   public static int getColorFromStack(ItemStack stack) {
      if (stack.isEmpty()) {
         return 16777215;
      } else {
         int retColor = 16777215;
         boolean truncate = true;
         if (IEnchantColorProvider.hasProvider(stack)) {
            IEnchantColorProvider provider = IEnchantColorProvider.getProvider(stack);
            int color = provider.getEnchantEffectColor();
            truncate = provider.shouldTruncateColorBrightness();
            retColor = -16777216 | color;
         } else if (doesStackHaveRune(stack)) {
            int color = ItemRune.getColor(ItemNBTHelper.getInt(targetStack, "Quark:RuneColor", 0));
            retColor = -16777216 | color;
         }

         if (truncate) {
            int r = retColor >> 16 & 255;
            int g = retColor >> 8 & 255;
            int b = retColor & 255;
            int t = r + g + b;
            if (t > 396) {
               float mul = 396.0F / (float)t;
               r = (int)((float)r * mul);
               g = (int)((float)g * mul);
               b = (int)((float)b * mul);
               retColor = -16777216 + (r << 16) + (g << 8) + b;
            }
         }

         return retColor;
      }
   }

   public static boolean doesStackHaveRune(ItemStack stack) {
      return !stack.isEmpty() && stack.hasTagCompound() && ItemNBTHelper.getBoolean(stack, "Quark:RuneAttached", false);
   }
}
