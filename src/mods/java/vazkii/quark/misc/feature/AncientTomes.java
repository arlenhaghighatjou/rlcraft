package vazkii.quark.misc.feature;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.misc.item.ItemAncientTome;

public class AncientTomes extends Feature {
   public static Item ancient_tome;
   public static final List<Enchantment> validEnchants = new ArrayList();
   private String[] enchantNames;
   public static int dungeonWeight;
   public static int libraryWeight;
   public static int itemQuality;
   public static int mergeTomeCost;
   public static int applyTomeCost;

   public AncientTomes() {
   }

   public static NBTTagList getEnchantmentsForStack(NBTTagList previous, ItemStack stack) {
      return !stack.isEmpty() && stack.getItem() == ancient_tome ? ItemEnchantedBook.getEnchantments(stack) : previous;
   }

   public void setupConfig() {
      this.enchantNames = this.loadPropStringList("Valid Enchantments", "", this.generateDefaultEnchantmentList());
      dungeonWeight = this.loadPropInt("Dungeon loot weight", "", 8);
      libraryWeight = this.loadPropInt("Stronghold Library loot weight", "", 12);
      itemQuality = this.loadPropInt("Item quality for loot", "", 2);
      mergeTomeCost = this.loadPropInt("Cost to apply tome", "", 35);
      applyTomeCost = this.loadPropInt("Cost to apply upgraded book to item", "", 35);
   }

   public void preInit(FMLPreInitializationEvent event) {
      ancient_tome = new ItemAncientTome();
      LootFunctionManager.registerFunction(new AncientTomes.EnchantTomeFunction.Serializer());
   }

   public void postInit() {
      this.initializeEnchantmentList(this.enchantNames, validEnchants);
   }

   public boolean hasSubscriptions() {
      return true;
   }

   @SubscribeEvent
   public void onLootTableLoad(LootTableLoadEvent event) {
      if (event.getName().equals(LootTableList.CHESTS_STRONGHOLD_LIBRARY)) {
         event.getTable().getPool("main").addEntry(new LootEntryItem(ancient_tome, libraryWeight, itemQuality, new LootFunction[]{new AncientTomes.EnchantTomeFunction()}, new LootCondition[0], "quark:ancient_tome"));
      } else if (event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON)) {
         event.getTable().getPool("main").addEntry(new LootEntryItem(ancient_tome, dungeonWeight, itemQuality, new LootFunction[]{new AncientTomes.EnchantTomeFunction()}, new LootCondition[0], "quark:ancient_tome"));
      }

   }

   @SubscribeEvent
   public void onAnvilUpdate(AnvilUpdateEvent event) {
      ItemStack left = event.getLeft();
      ItemStack right = event.getRight();
      if (!left.isEmpty() && !right.isEmpty()) {
         if (left.getItem() == Items.ENCHANTED_BOOK && right.getItem() == ancient_tome) {
            this.handleTome(left, right, event);
         } else if (right.getItem() == Items.ENCHANTED_BOOK && left.getItem() == ancient_tome) {
            this.handleTome(right, left, event);
         } else if (right.getItem() == Items.ENCHANTED_BOOK) {
            Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(right);
            Map<Enchantment, Integer> currentEnchants = EnchantmentHelper.getEnchantments(left);
            boolean hasOverLevel = false;
            boolean hasMatching = false;

            for(Entry<Enchantment, Integer> entry : enchants.entrySet()) {
               Enchantment enchantment = (Enchantment)entry.getKey();
               if (enchantment != null) {
                  int level = ((Integer)entry.getValue()).intValue();
                  if (level > enchantment.getMaxLevel()) {
                     hasOverLevel = true;
                     if (enchantment.canApply(left)) {
                        hasMatching = true;
                        Iterator<Enchantment> iterator = currentEnchants.keySet().iterator();

                        while(iterator.hasNext()) {
                           Enchantment comparingEnchantment = (Enchantment)iterator.next();
                           if (comparingEnchantment != enchantment && !comparingEnchantment.isCompatibleWith(enchantment)) {
                              iterator.remove();
                           }
                        }

                        currentEnchants.put(enchantment, Integer.valueOf(level));
                     }
                  } else if (enchantment.canApply(left)) {
                     boolean compatible = true;

                     for(Enchantment comparingEnchantment : currentEnchants.keySet()) {
                        if (comparingEnchantment != enchantment && comparingEnchantment != null && !comparingEnchantment.isCompatibleWith(enchantment)) {
                           compatible = false;
                           break;
                        }
                     }

                     if (compatible) {
                        currentEnchants.put(enchantment, Integer.valueOf(level));
                     }
                  }
               }
            }

            if (hasOverLevel) {
               if (hasMatching) {
                  ItemStack out = left.copy();
                  EnchantmentHelper.setEnchantments(currentEnchants, out);
                  String name = event.getName();
                  int cost = applyTomeCost;
                  if (name != null && !name.isEmpty()) {
                     out.setStackDisplayName(name);
                     ++cost;
                  }

                  event.setOutput(out);
                  event.setCost(cost);
               } else {
                  event.setCanceled(true);
               }
            }
         }
      }

   }

   private void handleTome(ItemStack book, ItemStack tome, AnvilUpdateEvent event) {
      Map<Enchantment, Integer> enchantsBook = EnchantmentHelper.getEnchantments(book);
      Map<Enchantment, Integer> enchantsTome = EnchantmentHelper.getEnchantments(tome);

      for(Entry<Enchantment, Integer> entry : enchantsTome.entrySet()) {
         if (!((Integer)enchantsBook.getOrDefault(entry.getKey(), Integer.valueOf(0))).equals(entry.getValue())) {
            return;
         }

         enchantsBook.put(entry.getKey(), Integer.valueOf(Math.min(((Integer)entry.getValue()).intValue(), ((Enchantment)entry.getKey()).getMaxLevel()) + 1));
      }

      ItemStack output = ProxyRegistry.newStack(Items.ENCHANTED_BOOK);

      for(Entry<Enchantment, Integer> entry : enchantsBook.entrySet()) {
         ItemEnchantedBook.addEnchantment(output, new EnchantmentData((Enchantment)entry.getKey(), ((Integer)entry.getValue()).intValue()));
      }

      event.setOutput(output);
      event.setCost(mergeTomeCost);
   }

   private String[] generateDefaultEnchantmentList() {
      Enchantment[] enchants = new Enchantment[]{Enchantments.FEATHER_FALLING, Enchantments.THORNS, Enchantments.SHARPNESS, Enchantments.SMITE, Enchantments.BANE_OF_ARTHROPODS, Enchantments.KNOCKBACK, Enchantments.FIRE_ASPECT, Enchantments.LOOTING, Enchantments.SWEEPING, Enchantments.EFFICIENCY, Enchantments.UNBREAKING, Enchantments.FORTUNE, Enchantments.POWER, Enchantments.PUNCH, Enchantments.LUCK_OF_THE_SEA, Enchantments.LURE};
      List<String> strings = new ArrayList();

      for(Enchantment e : enchants) {
         if (e != null && e.getRegistryName() != null) {
            strings.add(e.getRegistryName().toString());
         }
      }

      return (String[])strings.toArray(new String[0]);
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public static class EnchantTomeFunction extends LootFunction {
      protected EnchantTomeFunction() {
         super(new LootCondition[0]);
      }

      @Nonnull
      public ItemStack apply(@Nonnull ItemStack stack, @Nonnull Random rand, @Nonnull LootContext context) {
         Enchantment enchantment = (Enchantment)AncientTomes.validEnchants.get(rand.nextInt(AncientTomes.validEnchants.size()));
         ItemEnchantedBook.addEnchantment(stack, new EnchantmentData(enchantment, enchantment.getMaxLevel()));
         return stack;
      }

      public static class Serializer extends net.minecraft.world.storage.loot.functions.LootFunction.Serializer<AncientTomes.EnchantTomeFunction> {
         protected Serializer() {
            super(new ResourceLocation("quark", "enchant_tome"), AncientTomes.EnchantTomeFunction.class);
         }

         public void serialize(@Nonnull JsonObject object, @Nonnull AncientTomes.EnchantTomeFunction functionClazz, @Nonnull JsonSerializationContext serializationContext) {
         }

         @Nonnull
         public AncientTomes.EnchantTomeFunction deserialize(@Nonnull JsonObject object, @Nonnull JsonDeserializationContext deserializationContext, @Nonnull LootCondition[] conditionsIn) {
            return new AncientTomes.EnchantTomeFunction();
         }
      }
   }
}
