package com.teammetallurgy.aquaculture.items;

import com.teammetallurgy.aquaculture.Aquaculture;
import com.teammetallurgy.aquaculture.handlers.Config;
import com.teammetallurgy.aquaculture.items.meta.MetaItem;
import com.teammetallurgy.aquaculture.items.meta.MetaItemFood;
import com.teammetallurgy.aquaculture.items.meta.SubItem;
import com.teammetallurgy.aquaculture.items.meta.SubItemFood;
import com.teammetallurgy.aquaculture.loot.BiomeType;
import com.teammetallurgy.aquaculture.loot.FishLoot;
import java.util.Locale;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

public class AquacultureItems {
   public static final Item woodenFishingRod = (new ItemAquacultureFishingRod(50, ToolMaterial.WOOD)).setUnlocalizedName("fishingRod").setCreativeTab(Aquaculture.tab);
   public static final Item ironFishingRod = (new ItemAquacultureFishingRod(75, ToolMaterial.IRON)).setUnlocalizedName("IronFishingRod").setCreativeTab(Aquaculture.tab);
   public static final Item goldFishingRod = (new ItemAquacultureFishingRod(50, ToolMaterial.GOLD)).setUnlocalizedName("GoldFishingRod").setCreativeTab(Aquaculture.tab);
   public static final Item diamondFishingRod = (new ItemAquacultureFishingRod(250, ToolMaterial.DIAMOND)).setUnlocalizedName("DiamondFishingRod").setCreativeTab(Aquaculture.tab);
   public static final Item adminFishingRod = (new ItemAdminFishingRod(75)).setUnlocalizedName("AdminFishingRod").setCreativeTab(Aquaculture.tab);
   public static final MetaItemFood metaFoodItem = (MetaItemFood)(new MetaItemFood()).setUnlocalizedName("food").setCreativeTab(Aquaculture.tab);
   public static final MetaItem metaLootItem = (MetaItem)(new MetaItem()).setUnlocalizedName("loot").setCreativeTab(Aquaculture.tab);
   public static final SubItemFood seaweed = (new SubItemFood(metaFoodItem, 2, 0.0F)).setTranslationKey("Seaweed").setCreativeTab(Aquaculture.tab);
   public static final SubItemFood algae = (new SubItemFood(metaFoodItem, 2, 0.0F)).setTranslationKey("Algae").setCreativeTab(Aquaculture.tab);
   public static final SubItemFood whaleSteak = (new SubItemFood(metaFoodItem, 2, 0.3F)).setTranslationKey("RawWhaleSteak").setCreativeTab(Aquaculture.tab);
   public static final SubItemFood fishFillet = (new SubItemFood(metaFoodItem, 2, 0.3F)).setTranslationKey("RawFishFillet").setCreativeTab(Aquaculture.tab);
   public static final SubItemFood cookedFillet = (new SubItemFood(metaFoodItem, 5, 0.6F)).setTranslationKey("CookedFishFillet").setCreativeTab(Aquaculture.tab);
   public static final SubItemFood cookedWhaleSteak = (new SubItemFood(metaFoodItem, 10, 0.8F)).setTranslationKey("CookedWhaleSteak").setCreativeTab(Aquaculture.tab);
   public static final SubItemFood whaleBurger = (new SubItemFood(metaFoodItem, 20, 0.8F)).setTranslationKey("Whaleburger").setCreativeTab(Aquaculture.tab);
   public static final SubItemFood frogLegs = (new SubItemFood(metaFoodItem, 2, 0.3F)).setTranslationKey("FrogLegs").setCreativeTab(Aquaculture.tab);
   public static final SubItemFood cookedFrogLegs = (new SubItemFood(metaFoodItem, 3, 0.6F)).setTranslationKey("CookedFrogLegs").setCreativeTab(Aquaculture.tab);
   public static final SubItemFood turtleSoup = (new SubItemFood(metaFoodItem, 6, 0.6F)).setTranslationKey("TurtleSoup").setCreativeTab(Aquaculture.tab);
   public static final SubItemFood sushi = (new SubItemFood(metaFoodItem, 4, 0.6F)).setTranslationKey("Sushi").setCreativeTab(Aquaculture.tab);
   public static final SubItem driftwood = (new SubItem(metaLootItem)).setTranslationKey("Driftwood").setCreativeTab(Aquaculture.tab);
   public static final SubItem neptuniumBar = (new SubItem(metaLootItem)).setTranslationKey("NeptuniumIngot").setCreativeTab(Aquaculture.tab);
   public static final SubItem tinCan = (new SubItem(metaLootItem)).setTranslationKey("TinCan").setCreativeTab(Aquaculture.tab);
   public static final SubItem nessageInABottle = (new ItemMessageInABottle(metaLootItem)).setTranslationKey("MessageInABottle").setCreativeTab(Aquaculture.tab);
   public static final SubItem box = (new ItemBox(metaLootItem)).setTranslationKey("Box").setCreativeTab(Aquaculture.tab);
   public static final SubItem lockbox = (new ItemLockbox(metaLootItem)).setTranslationKey("Lockbox").setCreativeTab(Aquaculture.tab);
   public static final SubItem treasureChest = (new ItemTreasureChest(metaLootItem)).setTranslationKey("TreasureChest").setCreativeTab(Aquaculture.tab);
   public static final SubItem neptunesBounty = (new ItemNeptunesBounty(metaLootItem)).setTranslationKey("NeptunesBounty").setCreativeTab(Aquaculture.tab);
   public static final ItemFish fish = (ItemFish)(new ItemFish()).setUnlocalizedName("Fish").setCreativeTab(Aquaculture.tab);
   private static ToolMaterial neptuniumEnum = EnumHelper.addToolMaterial("Neptunium", 3, 2500, 9.0F, 6.0F, 15);
   public static final Item neptuniumPickaxe = (new AquaItemPickaxe(neptuniumEnum)).setUnlocalizedName("NeptuniumPickaxe").setCreativeTab(Aquaculture.tab);
   public static final Item neptuniumShovel = (new AquaItemSpade(neptuniumEnum)).setUnlocalizedName("NeptuniumShovel").setCreativeTab(Aquaculture.tab);
   public static final Item neptuniumAxe = (new AquaItemAxe(neptuniumEnum)).setUnlocalizedName("NeptuniumAxe").setCreativeTab(Aquaculture.tab);
   public static final Item neptuniumHoe = (new AquaItemHoe(neptuniumEnum)).setUnlocalizedName("NeptuniumHoe").setCreativeTab(Aquaculture.tab);
   public static final Item neptuniumSword = (new AquaItemSword(neptuniumEnum)).setUnlocalizedName("NeptuniumSword").setCreativeTab(Aquaculture.tab);
   private static ArmorMaterial neptuniumArmorEnum = EnumHelper.addArmorMaterial("Neptunium", "", 75, new int[]{3, 6, 8, 3}, 15, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0F);
   public static final Item neptuniumHelmet = (new NeptuniumArmor(neptuniumArmorEnum, 0, EntityEquipmentSlot.HEAD)).setArmorTexture("neptunium_layer_1").setUnlocalizedName("NeptuniumHelmet").setCreativeTab(Aquaculture.tab);
   public static final Item neptuniumPlate = (new NeptuniumArmor(neptuniumArmorEnum, 0, EntityEquipmentSlot.CHEST)).setArmorTexture("neptunium_layer_1").setUnlocalizedName("NeptuniumChestplate").setCreativeTab(Aquaculture.tab);
   public static final Item neptuniumLegs = (new NeptuniumArmor(neptuniumArmorEnum, 0, EntityEquipmentSlot.LEGS)).setArmorTexture("neptunium_layer_2").setUnlocalizedName("NeptuniumLeggings").setCreativeTab(Aquaculture.tab);
   public static final Item neptuniumBoots = (new NeptuniumArmor(neptuniumArmorEnum, 0, EntityEquipmentSlot.FEET)).setArmorTexture("neptunium_layer_1").setUnlocalizedName("NeptuniumBoots").setCreativeTab(Aquaculture.tab);

   public AquacultureItems() {
   }

   public void register() {
      this.register(woodenFishingRod);
      this.register(ironFishingRod);
      this.register(goldFishingRod);
      this.register(diamondFishingRod);
      this.register(adminFishingRod);
      this.register(metaLootItem);
      this.register(metaFoodItem);
      this.register(neptuniumPickaxe);
      this.register(neptuniumShovel);
      this.register(neptuniumAxe);
      this.register(neptuniumHoe);
      this.register(neptuniumSword);
      this.register(neptuniumHelmet);
      this.register(neptuniumPlate);
      this.register(neptuniumLegs);
      this.register(neptuniumBoots);
      this.register(fish);
      fish.addFish("Bluegill", 1, 1, 5, BiomeType.freshwater, ((Integer)Config.fishRarity.get("Bluegill")).intValue());
      fish.addFish("Perch", 1, 1, 5, BiomeType.freshwater, ((Integer)Config.fishRarity.get("Perch")).intValue());
      fish.addFish("Gar", 2, 1, 10, BiomeType.freshwater, ((Integer)Config.fishRarity.get("Gar")).intValue());
      fish.addFish("Bass", 3, 1, 25, BiomeType.freshwater, ((Integer)Config.fishRarity.get("Bass")).intValue());
      fish.addFish("Muskellunge", 3, 1, 35, BiomeType.freshwater, ((Integer)Config.fishRarity.get("Muskellunge")).intValue());
      fish.addFish("Brown Trout", 3, 1, 40, BiomeType.freshwater, ((Integer)Config.fishRarity.get("Brown Trout")).intValue());
      fish.addFish("Catfish", 4, 1, 50, BiomeType.freshwater, ((Integer)Config.fishRarity.get("Catfish")).intValue());
      fish.addFish("Carp", 5, 1, 100, BiomeType.freshwater, ((Integer)Config.fishRarity.get("Carp")).intValue());
      fish.addFish("Blowfish", 3, 1, 25, BiomeType.saltwater, ((Integer)Config.fishRarity.get("Blowfish")).intValue());
      fish.addFish("Red Grouper", 4, 1, 50, BiomeType.saltwater, ((Integer)Config.fishRarity.get("Red Grouper")).intValue());
      fish.addFish("Salmon", 5, 1, 100, BiomeType.saltwater, ((Integer)Config.fishRarity.get("Salmon")).intValue());
      fish.addFish("Tuna", 5, 1, 135, BiomeType.saltwater, ((Integer)Config.fishRarity.get("Tuna")).intValue());
      fish.addFish("Swordfish", 7, 1, 1400, BiomeType.saltwater, ((Integer)Config.fishRarity.get("Swordfish")).intValue());
      fish.addFish("Shark", 8, 1, 5000, BiomeType.saltwater, ((Integer)Config.fishRarity.get("Shark")).intValue());
      fish.addFish("Whale", 0, 1, 190000, BiomeType.saltwater, ((Integer)Config.fishRarity.get("Whale")).intValue());
      fish.addFish("Squid", 0, 1, 1000, BiomeType.saltwater, ((Integer)Config.fishRarity.get("Squid")).intValue());
      fish.addFish("Jellyfish", 0, 1, 500, BiomeType.saltwater, ((Integer)Config.fishRarity.get("Jellyfish")).intValue());
      fish.addFish("Frog", 0, 1, 1, BiomeType.brackish, ((Integer)Config.fishRarity.get("Frog")).intValue());
      fish.addFish("Turtle", 0, 1, 5, BiomeType.brackish, ((Integer)Config.fishRarity.get("Turtle")).intValue());
      fish.addFish("Leech", 0, 1, 1, BiomeType.brackish, ((Integer)Config.fishRarity.get("Leech")).intValue());
      fish.addFish("Pirahna", 1, 1, 8, BiomeType.tropical, ((Integer)Config.fishRarity.get("Pirahna")).intValue());
      fish.addFish("Electric Eel", 3, 1, 45, BiomeType.tropical, ((Integer)Config.fishRarity.get("Electric Eel")).intValue());
      fish.addFish("Tambaqui", 4, 1, 75, BiomeType.tropical, ((Integer)Config.fishRarity.get("Tambaqui")).intValue());
      fish.addFish("Arapaima", 6, 1, 220, BiomeType.tropical, ((Integer)Config.fishRarity.get("Arapaima")).intValue());
      fish.addFish("Cod", 6, 1, 210, BiomeType.arctic, ((Integer)Config.fishRarity.get("Cod")).intValue());
      fish.addFish("Pollock", 3, 1, 45, BiomeType.arctic, ((Integer)Config.fishRarity.get("Pollock")).intValue());
      fish.addFish("Herring", 1, 1, 3, BiomeType.arctic, ((Integer)Config.fishRarity.get("Herring")).intValue());
      fish.addFish("Halibut", 7, 1, 700, BiomeType.arctic, ((Integer)Config.fishRarity.get("Halibut")).intValue());
      fish.addFish("Pink Salmon", 5, 1, 100, BiomeType.arctic, ((Integer)Config.fishRarity.get("Pink Salmon")).intValue());
      fish.addFish("Rainbow Trout", 4, 1, 50, BiomeType.arctic, ((Integer)Config.fishRarity.get("Rainbow Trout")).intValue());
      fish.addFish("Blackfish", 2, 1, 10, BiomeType.arctic, ((Integer)Config.fishRarity.get("Blackfish")).intValue());
      fish.addFish("Capitaine", 4, 1, 450, BiomeType.arid, ((Integer)Config.fishRarity.get("Capitaine")).intValue());
      fish.addFish("Boulti", 2, 1, 10, BiomeType.arid, ((Integer)Config.fishRarity.get("Boulti")).intValue());
      fish.addFish("Bagrid", 3, 1, 25, BiomeType.arid, ((Integer)Config.fishRarity.get("Bagrid")).intValue());
      fish.addFish("Syndontis", 1, 1, 3, BiomeType.arid, ((Integer)Config.fishRarity.get("Syndontis")).intValue());
      fish.addFish("Red Shrooma", 0, 1, 5, BiomeType.mushroom, ((Integer)Config.fishRarity.get("Red Shrooma")).intValue());
      fish.addFish("Brown Shrooma", 0, 1, 5, BiomeType.mushroom, ((Integer)Config.fishRarity.get("Brown Shrooma")).intValue());
      fish.addFish("Goldfish", 0, 1, 4, new BiomeType[0], 1);
      fish.addFish("Fish Bones", 0, 1, 1, new BiomeType[0], 1);
      FishLoot.instance().addJunkLoot(seaweed.getItemStack(), BiomeType.saltwater, ((Integer)Config.junkRarity.get("Seaweed")).intValue());
      FishLoot.instance().addJunkLoot(algae.getItemStack(), new BiomeType[]{BiomeType.arctic, BiomeType.arid, BiomeType.brackish, BiomeType.freshwater, BiomeType.mushroom, BiomeType.tropical}, ((Integer)Config.junkRarity.get("Algae")).intValue());
      FishLoot.instance().addJunkLoot(driftwood.getItemStack(), ((Integer)Config.junkRarity.get("Driftwood")).intValue());
      FishLoot.instance().addJunkLoot(tinCan.getItemStack(), ((Integer)Config.junkRarity.get("Tin Can")).intValue());
      FishLoot.instance().addJunkLoot(box.getItemStack(), ((Integer)Config.junkRarity.get("Box")).intValue());
      FishLoot.instance().addJunkLoot(lockbox.getItemStack(), ((Integer)Config.junkRarity.get("Lockbox")).intValue());
      FishLoot.instance().addJunkLoot(treasureChest.getItemStack(), ((Integer)Config.junkRarity.get("Treasure Chest")).intValue());
      FishLoot.instance().addJunkLoot(new ItemStack(Items.STICK), ((Integer)Config.junkRarity.get("Stick")).intValue());
      FishLoot.instance().addJunkLoot(new ItemStack(Items.BONE), ((Integer)Config.junkRarity.get("Bone")).intValue());
      FishLoot.instance().addJunkLoot(new ItemStack(Items.LEATHER_BOOTS), ((Integer)Config.junkRarity.get("Leather Boots")).intValue());
      FishLoot.instance().addJunkLoot(new ItemStack(Items.APPLE), ((Integer)Config.junkRarity.get("Apple")).intValue());
      FishLoot.instance().addJunkLoot(fish.getItemStackFish("Goldfish"), ((Integer)Config.junkRarity.get("Goldfish")).intValue());
      FishLoot.instance().addJunkLoot(nessageInABottle.getItemStack(), ((Integer)Config.junkRarity.get("Message In A Bottle")).intValue());
      if (Config.enableNeptuniumLoot) {
         FishLoot.instance().addJunkLoot(neptunesBounty.getItemStack(), ((Integer)Config.junkRarity.get("Neptunes Bounty")).intValue());
      }

      OreDictionary.registerOre("listAllfishraw", fishFillet.getItemStack());
      OreDictionary.registerOre("listAllfishcooked", cookedFillet.getItemStack());
      OreDictionary.registerOre("cropSeaweed", seaweed.getItemStack());
   }

   public void register(Item item) {
      String name = item.getUnlocalizedName();
      name = name.replaceAll("item.", "");
      name = name.replaceAll("([A-Za-z][a-z\\d]+)(?=([A-Z][a-z\\d]+))", "$1_").toLowerCase(Locale.US);
      item.setRegistryName(name);
      ForgeRegistries.ITEMS.register(item);
   }
}
