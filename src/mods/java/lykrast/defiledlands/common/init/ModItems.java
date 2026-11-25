package lykrast.defiledlands.common.init;

import baubles.api.BaubleType;
import java.util.ArrayList;
import java.util.List;
import lykrast.defiledlands.common.entity.boss.EntityDestroyer;
import lykrast.defiledlands.common.entity.boss.EntityMourner;
import lykrast.defiledlands.common.item.ItemArmorGeneric;
import lykrast.defiledlands.common.item.ItemAxeFoiled;
import lykrast.defiledlands.common.item.ItemAxeGeneric;
import lykrast.defiledlands.common.item.ItemBauble;
import lykrast.defiledlands.common.item.ItemBlackHeart;
import lykrast.defiledlands.common.item.ItemBlastemFruit;
import lykrast.defiledlands.common.item.ItemBlastemFruitBlazing;
import lykrast.defiledlands.common.item.ItemBlastemSeed;
import lykrast.defiledlands.common.item.ItemBookWyrmAnalyzer;
import lykrast.defiledlands.common.item.ItemBossSummoner;
import lykrast.defiledlands.common.item.ItemConcussionSmasher;
import lykrast.defiledlands.common.item.ItemDefilementPowder;
import lykrast.defiledlands.common.item.ItemFoulCandy;
import lykrast.defiledlands.common.item.ItemHephaestite;
import lykrast.defiledlands.common.item.ItemHoeGeneric;
import lykrast.defiledlands.common.item.ItemPellet;
import lykrast.defiledlands.common.item.ItemPickaxeFoiled;
import lykrast.defiledlands.common.item.ItemPickaxeGeneric;
import lykrast.defiledlands.common.item.ItemRavager;
import lykrast.defiledlands.common.item.ItemRazorScarlite;
import lykrast.defiledlands.common.item.ItemScuttlerEye;
import lykrast.defiledlands.common.item.ItemShovelFoiled;
import lykrast.defiledlands.common.item.ItemShovelGeneric;
import lykrast.defiledlands.common.item.ItemSwordGeneric;
import lykrast.defiledlands.common.item.ItemSwordScarlite;
import lykrast.defiledlands.common.item.ItemTears;
import lykrast.defiledlands.common.item.ItemUmbraBlaster;
import lykrast.defiledlands.common.util.CreativeTabDL;
import lykrast.defiledlands.common.util.LocUtils;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSimpleFoiled;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber
public class ModItems {
   public static Item tenebraDoor = registerItem(new ItemDoor(ModBlocks.tenebraDoor), "tenebra_door");
   public static Item hephaestite = registerItem(new ItemHephaestite(), "hephaestite");
   public static Item umbriumIngot = registerItem(new Item(), "umbrium_ingot");
   public static Item umbriumNugget = registerItem(new Item(), "umbrium_nugget");
   public static Item scarlite = registerItem(new Item(), "scarlite");
   public static Item blastemSeed = registerItem(new ItemBlastemSeed(), "blastem_seed");
   public static Item blastemFruit = registerItem(new ItemBlastemFruit(), "blastem_fruit");
   public static Item blastemFruitBlazing = registerItem(new ItemBlastemFruitBlazing(), "blastem_fruit_blazing");
   public static Item defilementPowder = registerItem(new ItemDefilementPowder(), "defilement_powder");
   public static Item scuttlerEye = registerItem(new ItemScuttlerEye(), "scuttler_eye");
   public static Item blackHeart = registerItem(new ItemBlackHeart(), "black_heart");
   public static Item foulSlime = registerItem(new Item(), "foul_slime");
   public static Item foulCandy = registerItem(new ItemFoulCandy(), "foul_candy");
   public static Item bookWyrmRaw = registerItem((new ItemFood(3, 0.3F, true)).setPotionEffect(new PotionEffect(MobEffects.HUNGER, 600, 0), 0.5F), "book_wyrm_raw");
   public static Item bookWyrmCooked = registerItem(new ItemFood(8, 0.8F, true), "book_wyrm_cooked");
   public static Item bookWyrmScale = registerItem(new Item(), "book_wyrm_scale");
   public static Item bookWyrmScaleGolden = registerItem(new Item(), "book_wyrm_scale_golden");
   public static Item axeUmbrium;
   public static Item hoeUmbrium;
   public static Item pickaxeUmbrium;
   public static Item shovelUmbrium;
   public static Item swordUmbrium;
   public static Item swordScarlite;
   public static Item razorScarlite;
   public static Item umbraBlaster = registerItem(new ItemUmbraBlaster(465), "umbra_blaster");
   public static Item concussionSmasher = registerItem(new ItemConcussionSmasher(178), "concussion_smasher");
   public static Item umbriumHelmet;
   public static Item umbriumChestplate;
   public static Item umbriumLeggings;
   public static Item umbriumBoots;
   public static Item scaleHelmet;
   public static Item scaleChestplate;
   public static Item scaleLeggings;
   public static Item scaleBoots;
   public static Item scaleGoldenHelmet;
   public static Item scaleGoldenChestplate;
   public static Item scaleGoldenLeggings;
   public static Item scaleGoldenBoots;
   public static Item bookWyrmAnalyzer = registerItem(new ItemBookWyrmAnalyzer(), "book_wyrm_analyzer");
   public static Item scarliteRing = registerItem(new ItemBauble(BaubleType.RING) {
      public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
         if (player.ticksExisted % 200 == 0) {
            player.heal(1.0F);
         }

      }
   }, "scarlite_ring");
   public static Item phytoprostasiaAmulet = registerItem(new ItemBauble(BaubleType.AMULET), "phytoprostasia_amulet");
   public static Item callingStone = registerItem(new ItemBossSummoner() {
      protected Entity getBoss(World worldIn) {
         EntityDestroyer boss = new EntityDestroyer(worldIn);
         boss.ignite();
         return boss;
      }
   }, "calling_stone");
   public static Item essenceDestroyer = registerItem(new ItemSimpleFoiled(), "essence_destroyer");
   public static Item ravagingIngot = registerItem(new ItemSimpleFoiled(), "ravaging_ingot");
   public static Item axeRavaging;
   public static Item pickaxeRavaging;
   public static Item shovelRavaging;
   public static Item theRavager = registerItem(new ItemRavager(2107), "the_ravager");
   public static Item pelletUmbrium = registerItem(new ItemPellet(10), "pellet_umbrium");
   public static Item pelletSpiked = registerItem(new ItemPellet.Spiked(10), "pellet_spiked");
   public static Item pelletRavaging = registerItem(new ItemPellet.Ravaging(20), "pellet_ravaging");
   public static Item idolSorrow = registerItem(new ItemBossSummoner() {
      protected Entity getBoss(World worldIn) {
         EntityMourner boss = new EntityMourner(worldIn);
         boss.ignite();
         return boss;
      }
   }, "idol_sorrow");
   public static Item essenceMourner = registerItem(new ItemSimpleFoiled(), "essence_mourner");
   public static Item remorsefulGem = registerItem(new ItemSimpleFoiled(), "remorseful_gem");
   public static Item tearsFlame = registerItem(new ItemTears.Flame(2107), "tears_flame");
   public static Item tearsShulker = registerItem(new ItemTears.Shulker(2107), "tears_shulker");
   private static List<Item> itemList = new ArrayList();
   static List<Item> itemBlockList = new ArrayList();
   public static ToolMaterial materialUmbrium = EnumHelper.addToolMaterial("umbrium", 2, 302, 5.0F, 2.0F, 20).setRepairItem(new ItemStack(umbriumIngot));
   public static ToolMaterial materialScarlite = EnumHelper.addToolMaterial("scarlite", 3, 1561, 6.0F, 2.0F, 14).setRepairItem(new ItemStack(scarlite));
   public static ToolMaterial materialScarliteRazor = EnumHelper.addToolMaterial("scarlite_razor", 3, 31, 6.0F, -1.0F, 14).setRepairItem(new ItemStack(umbriumIngot));
   public static ToolMaterial materialRavaging = EnumHelper.addToolMaterial("ravaging", 0, 2107, 60.0F, 3.0F, 1).setRepairItem(new ItemStack(ravagingIngot));
   public static ArmorMaterial materialUmbriumA = EnumHelper.addArmorMaterial("umbrium", "umbrium", 18, new int[]{1, 4, 5, 2}, 20, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F);
   public static ArmorMaterial materialScales = EnumHelper.addArmorMaterial("bookWyrmScales", "book_wyrm_scale", 10, new int[]{1, 3, 4, 2}, 17, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F);
   public static ArmorMaterial materialScalesGolden = EnumHelper.addArmorMaterial("bookWyrmScalesGolden", "book_wyrm_scale_golden", 10, new int[]{1, 3, 4, 2}, 25, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.0F);

   public ModItems() {
   }

   @SubscribeEvent
   public static void registerItems(Register<Item> event) {
      for(Item i : itemBlockList) {
         event.getRegistry().register(i);
      }

      for(Item i : itemList) {
         event.getRegistry().register(i);
      }

      ModRecipes.initOreDict();
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public static void registerModels(ModelRegistryEvent evt) {
      for(Item i : itemBlockList) {
         initModel(i);
      }

      for(Item i : itemList) {
         initModel(i);
      }

      itemBlockList = null;
      itemList = null;
   }

   @SideOnly(Side.CLIENT)
   public static void initModel(Item i) {
      ModelLoader.setCustomModelResourceLocation(i, 0, new ModelResourceLocation(i.getRegistryName(), "inventory"));
   }

   public static Item registerItem(Item item, String name) {
      return registerItem(item, name, CreativeTabDL.instance);
   }

   public static Item registerItem(Item item, String name, CreativeTabs tab) {
      item.setRegistryName(name);
      item.setUnlocalizedName(LocUtils.prefix(name));
      if (tab != null) {
         item.setCreativeTab(tab);
      }

      itemList.add(item);
      return item;
   }

   static {
      axeUmbrium = registerItem(new ItemAxeGeneric(materialUmbrium, 8.0F, -3.1F), "umbrium_axe");
      hoeUmbrium = registerItem(new ItemHoeGeneric(materialUmbrium), "umbrium_hoe");
      pickaxeUmbrium = registerItem(new ItemPickaxeGeneric(materialUmbrium), "umbrium_pickaxe");
      shovelUmbrium = registerItem(new ItemShovelGeneric(materialUmbrium), "umbrium_shovel");
      swordUmbrium = registerItem(new ItemSwordGeneric(materialUmbrium), "umbrium_sword");
      swordScarlite = registerItem(new ItemSwordScarlite(materialScarlite), "scarlite_reaver");
      razorScarlite = registerItem(new ItemRazorScarlite(materialScarliteRazor), "scarlite_razor");
      materialUmbriumA.setRepairItem(new ItemStack(umbriumIngot));
      umbriumHelmet = registerItem(new ItemArmorGeneric("umbrium", materialUmbriumA, EntityEquipmentSlot.HEAD), "umbrium_helmet");
      umbriumChestplate = registerItem(new ItemArmorGeneric("umbrium", materialUmbriumA, EntityEquipmentSlot.CHEST), "umbrium_chestplate");
      umbriumLeggings = registerItem(new ItemArmorGeneric("umbrium", materialUmbriumA, EntityEquipmentSlot.LEGS), "umbrium_leggings");
      umbriumBoots = registerItem(new ItemArmorGeneric("umbrium", materialUmbriumA, EntityEquipmentSlot.FEET), "umbrium_boots");
      materialScales.setRepairItem(new ItemStack(bookWyrmScale));
      scaleHelmet = registerItem(new ItemArmorGeneric("book_wyrm_scale", materialScales, EntityEquipmentSlot.HEAD), "scale_helmet");
      scaleChestplate = registerItem(new ItemArmorGeneric("book_wyrm_scale", materialScales, EntityEquipmentSlot.CHEST), "scale_chestplate");
      scaleLeggings = registerItem(new ItemArmorGeneric("book_wyrm_scale", materialScales, EntityEquipmentSlot.LEGS), "scale_leggings");
      scaleBoots = registerItem(new ItemArmorGeneric("book_wyrm_scale", materialScales, EntityEquipmentSlot.FEET), "scale_boots");
      materialScalesGolden.setRepairItem(new ItemStack(bookWyrmScaleGolden));
      scaleGoldenHelmet = registerItem(new ItemArmorGeneric("book_wyrm_scale_golden", materialScalesGolden, EntityEquipmentSlot.HEAD), "scale_golden_helmet");
      scaleGoldenChestplate = registerItem(new ItemArmorGeneric("book_wyrm_scale_golden", materialScalesGolden, EntityEquipmentSlot.CHEST), "scale_golden_chestplate");
      scaleGoldenLeggings = registerItem(new ItemArmorGeneric("book_wyrm_scale_golden", materialScalesGolden, EntityEquipmentSlot.LEGS), "scale_golden_leggings");
      scaleGoldenBoots = registerItem(new ItemArmorGeneric("book_wyrm_scale_golden", materialScalesGolden, EntityEquipmentSlot.FEET), "scale_golden_boots");
      axeRavaging = registerItem(new ItemAxeFoiled(materialRavaging, 8.0F, -3.0F), "ravaging_axe");
      pickaxeRavaging = registerItem(new ItemPickaxeFoiled(materialRavaging), "ravaging_pickaxe");
      shovelRavaging = registerItem(new ItemShovelFoiled(materialRavaging), "ravaging_shovel");
   }
}
