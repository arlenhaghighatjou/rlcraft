package svenhjol.charm.world.feature;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import svenhjol.charm.Charm;
import svenhjol.charm.crafting.feature.Lantern;
import svenhjol.charm.crafting.feature.SuspiciousSoup;
import svenhjol.charm.enchanting.feature.CurseBreak;
import svenhjol.charm.enchanting.feature.Salvage;
import svenhjol.meson.Feature;
import svenhjol.meson.helper.EnchantmentHelper;
import svenhjol.meson.registry.VillagerRegistry;

public class VillagerTrades extends Feature {
   public VillagerTrades() {
   }

   public String getDescription() {
      return "Villagers have additional trades.  Librarians can buy back enchanted books, tool smiths repair very damaged anvils for a small iron cost, and zombie flesh is more useful.";
   }

   @SubscribeEvent
   public void onRegisterVillagers(Register<VillagerProfession> event) {
      if (Charm.hasFeature(SuspiciousSoup.class)) {
         VillagerRegistry.farmer.addTrade(2, new ITradeList[]{new VillagerTrades.SuspiciousSoupTrade()});
         VillagerRegistry.fisherman.addTrade(2, new ITradeList[]{new VillagerTrades.SuspiciousSoupTrade()});
      }

      VillagerRegistry.librarian.addTrade(1, new ITradeList[]{new VillagerTrades.EnchantedBookTrade()});
      VillagerRegistry.cleric.addTrade(3, new ITradeList[]{new VillagerTrades.CurseBreakTrade()});
      VillagerRegistry.butcher.addTrade(1, new ITradeList[]{new VillagerTrades.ZombieFlesh(new Item[]{Items.BEEF, Items.PORKCHOP, Items.RABBIT, Items.MUTTON})});
      VillagerRegistry.leatherWorker.addTrade(1, new ITradeList[]{new VillagerTrades.ZombieFlesh(new Item[]{Items.LEATHER})});
      VillagerRegistry.smith.addTrade(1, new ITradeList[]{new VillagerTrades.DamagedAnvilsTrade()});
      VillagerRegistry.smith.addTrade(2, new ITradeList[]{new VillagerTrades.LanternsTrade()});
      VillagerRegistry.smith.addTrade(3, new ITradeList[]{new VillagerTrades.SalvageTrade()});
      VillagerRegistry.weapon.addTrade(1, new ITradeList[]{new VillagerTrades.DamagedAnvilsTrade()});
      VillagerRegistry.weapon.addTrade(2, new ITradeList[]{new VillagerTrades.LanternsTrade()});
      VillagerRegistry.weapon.addTrade(3, new ITradeList[]{new VillagerTrades.SalvageTrade()});
      VillagerRegistry.tool.addTrade(1, new ITradeList[]{new VillagerTrades.DamagedAnvilsTrade()});
      VillagerRegistry.tool.addTrade(2, new ITradeList[]{new VillagerTrades.LanternsTrade()});
      VillagerRegistry.tool.addTrade(3, new ITradeList[]{new VillagerTrades.SalvageTrade()});
      VillagerRegistry.tool.addTrade(4, new ITradeList[]{new VillagerTrades.FortuneShovelTrade()});
   }

   public boolean hasSubscriptions() {
      return true;
   }

   public static class CurseBreakTrade implements ITradeList {
      public CurseBreakTrade() {
      }

      public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, EaglercraftRandom random) {
         int emeraldCount = 2 * (random.nextInt(15) + 5);
         ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
         EnchantmentHelper.setEnchantments(new HashMap<Enchantment, Integer>() {
            {
               this.put(CurseBreak.enchantment, Integer.valueOf(1));
            }
         }, book);
         recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, emeraldCount), new ItemStack(Items.BOOK), book));
      }
   }

   public static class DamagedAnvilsTrade implements ITradeList {
      public DamagedAnvilsTrade() {
      }

      public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, EaglercraftRandom random) {
         int ironCount = random.nextInt(4) + 4;
         recipeList.add(new MerchantRecipe(new ItemStack(Blocks.ANVIL, 1, 2), new ItemStack(Items.IRON_INGOT, ironCount), new ItemStack(Blocks.ANVIL)));
      }
   }

   public static class EnchantedBookTrade implements ITradeList {
      public EnchantedBookTrade() {
      }

      public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, EaglercraftRandom random) {
         ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
         int emeraldCount = random.nextInt(4) + 1;
         recipeList.add(new MerchantRecipe(book, new ItemStack(Items.EMERALD, emeraldCount)));
      }
   }

   public static class FortuneShovelTrade implements ITradeList {
      public FortuneShovelTrade() {
      }

      public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, EaglercraftRandom random) {
         int emeraldCount = random.nextInt(5) + 12;
         ItemStack shovel = new ItemStack(Items.DIAMOND_SHOVEL);
         EnchantmentHelper.setEnchantments(new HashMap<Enchantment, Integer>() {
            {
               this.put(Enchantments.FORTUNE, Integer.valueOf(3));
            }
         }, shovel);
         recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, emeraldCount), shovel));
      }
   }

   public static class LanternsTrade implements ITradeList {
      public LanternsTrade() {
      }

      public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, EaglercraftRandom random) {
         int emeraldCount = random.nextInt(1) + 1;
         int lanternCount = random.nextInt(2) + 2;
         ItemStack lanterns = new ItemStack(Lantern.getDefaultLantern(), lanternCount);
         recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, emeraldCount), lanterns));
      }
   }

   public static class SalvageTrade implements ITradeList {
      public SalvageTrade() {
      }

      public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, EaglercraftRandom random) {
         int emeraldCount = random.nextInt(15) + 5;
         ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
         EnchantmentHelper.setEnchantments(new HashMap<Enchantment, Integer>() {
            {
               this.put(Salvage.enchantment, Integer.valueOf(1));
            }
         }, book);
         recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, emeraldCount), new ItemStack(Items.BOOK), book));
      }
   }

   public static class SuspiciousSoupTrade implements ITradeList {
      public SuspiciousSoupTrade() {
      }

      public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, EaglercraftRandom random) {
         int beetrootCount = random.nextInt(10) + 5;
         ItemStack soup = new ItemStack(SuspiciousSoup.suspiciousSoup, 1, random.nextInt(SuspiciousSoup.maxTypes));
         recipeList.add(new MerchantRecipe(new ItemStack(Items.BEETROOT, beetrootCount), soup));
      }
   }

   public static class ZombieFlesh implements ITradeList {
      public final Item[] forWhat;

      public ZombieFlesh(Item... forWhat) {
         this.forWhat = forWhat;
      }

      public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, EaglercraftRandom random) {
         int fleshCount = random.nextInt(10) + 5;
         int tradeCount = Math.min(random.nextInt(2) + 2, fleshCount);
         List<Item> items = Arrays.asList(this.forWhat);
         ItemStack zombieFlesh = new ItemStack(Items.ROTTEN_FLESH, fleshCount);
         ItemStack traded = new ItemStack((Item)items.get(random.nextInt(items.size())), tradeCount);
         recipeList.add(new MerchantRecipe(zombieFlesh, traded));
      }
   }
}
