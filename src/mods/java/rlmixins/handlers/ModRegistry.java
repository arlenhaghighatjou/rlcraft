package rlmixins.handlers;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlmixins.item.ItemScarliteArmor;
import rlmixins.item.ItemSteelArmor;
import rlmixins.potion.PotionCow;
import rlmixins.potion.PotionCurseBreak;
import rlmixins.potion.PotionDelayedLaunch;
import rlmixins.potion.PotionEncumbered;
import rlmixins.potion.PotionLesserFireResistance;
import rlmixins.recipe.RecipeCurseBreak;
import rlmixins.recipe.RecipeFlameIceWeapon;

@EventBusSubscriber(
   modid = "rlmixins"
)
public class ModRegistry {
   public static ArmorMaterial STEEL_ARMOR = EnumHelper.addArmorMaterial("steel_armor", "rlmixins:steel_armor", 26, new int[]{3, 5, 7, 3}, 10, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F);
   public static ArmorMaterial SCARLITE_ARMOR = EnumHelper.addArmorMaterial("scarlite_armor", "rlmixins:scarlite_armor", 36, new int[]{3, 6, 8, 3}, 20, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0F);
   public static Item steelHelmet = new ItemSteelArmor("steel_helmet", STEEL_ARMOR, 2, EntityEquipmentSlot.HEAD);
   public static Item steelChestplate = new ItemSteelArmor("steel_chestplate", STEEL_ARMOR, 1, EntityEquipmentSlot.CHEST);
   public static Item steelLeggings = new ItemSteelArmor("steel_leggings", STEEL_ARMOR, 2, EntityEquipmentSlot.LEGS);
   public static Item steelBoots = new ItemSteelArmor("steel_boots", STEEL_ARMOR, 1, EntityEquipmentSlot.FEET);
   public static Item scarliteHelmet = new ItemScarliteArmor("scarlite_helmet", SCARLITE_ARMOR, 2, EntityEquipmentSlot.HEAD);
   public static Item scarliteChestplate = new ItemScarliteArmor("scarlite_chestplate", SCARLITE_ARMOR, 1, EntityEquipmentSlot.CHEST);
   public static Item scarliteLeggings = new ItemScarliteArmor("scarlite_leggings", SCARLITE_ARMOR, 2, EntityEquipmentSlot.LEGS);
   public static Item scarliteBoots = new ItemScarliteArmor("scarlite_boots", SCARLITE_ARMOR, 1, EntityEquipmentSlot.FEET);
   public static Item cleansingTalisman = ((Item)(new Item()).setRegistryName("rlmixins", "cleansing_talisman")).setUnlocalizedName("cleansing_talisman").setCreativeTab(CreativeTabs.MATERIALS);
   public static PotionType curseBreakPotion = (PotionType)(new PotionType("curse_break", new PotionEffect[]{new PotionEffect(PotionCurseBreak.INSTANCE)})).setRegistryName(new ResourceLocation("rlmixins", "curse_break"));
   public static SoundEvent FLARE_SHOOT;
   public static SoundEvent FLARE_BURN;
   public static SoundEvent CRITICAL_STRIKE;
   public static SoundEvent ATOMIC_DECONSTRUCT;
   public static SoundEvent PANDORA_REMOVAL;
   public static SoundEvent COW;

   public ModRegistry() {
   }

   public static void init() {
      FLARE_SHOOT = (SoundEvent)(new SoundEvent(new ResourceLocation("rlmixins", "flare_shoot"))).setRegistryName("flare_shoot");
      FLARE_BURN = (SoundEvent)(new SoundEvent(new ResourceLocation("rlmixins", "flare_burn"))).setRegistryName("flare_burn");
      CRITICAL_STRIKE = (SoundEvent)(new SoundEvent(new ResourceLocation("rlmixins", "critical_strike"))).setRegistryName("critical_strike");
      ATOMIC_DECONSTRUCT = (SoundEvent)(new SoundEvent(new ResourceLocation("rlmixins", "atomic_deconstruct"))).setRegistryName("atomic_deconstruct");
      PANDORA_REMOVAL = (SoundEvent)(new SoundEvent(new ResourceLocation("rlmixins", "pandora_removal"))).setRegistryName("pandora_removal");
      COW = (SoundEvent)(new SoundEvent(new ResourceLocation("rlmixins", "cow"))).setRegistryName("cow");
   }

   @SubscribeEvent
   public static void registerItemEvent(Register<Item> event) {
      if (ForgeConfigHandler.server.registerSteelArmor) {
         event.getRegistry().registerAll(new Item[]{steelHelmet, steelChestplate, steelLeggings, steelBoots});
      }

      if (ForgeConfigHandler.server.registerScarliteArmor) {
         event.getRegistry().registerAll(new Item[]{scarliteHelmet, scarliteChestplate, scarliteLeggings, scarliteBoots});
      }

      if (ForgeConfigHandler.server.registerCleansingTalisman) {
         event.getRegistry().register(cleansingTalisman);
      }

   }

   @SubscribeEvent
   public static void registerRecipeEvent(Register<IRecipe> event) {
      if (ForgeConfigHandler.server.registerCleansingTalisman) {
         event.getRegistry().register((new RecipeCurseBreak()).setRegistryName(new ResourceLocation("rlmixins", "cursebreak")));
      }

      if (ForgeConfigHandler.server.registerEnchantmentSensitiveFlameIceWeapon) {
         event.getRegistry().register((new RecipeFlameIceWeapon()).setRegistryName(new ResourceLocation("rlmixins", "flameiceweapon")));
      }

   }

   @SubscribeEvent
   public static void registerSoundEvent(Register<SoundEvent> event) {
      event.getRegistry().register(FLARE_SHOOT);
      event.getRegistry().register(FLARE_BURN);
      event.getRegistry().register(CRITICAL_STRIKE);
      event.getRegistry().register(ATOMIC_DECONSTRUCT);
      event.getRegistry().register(PANDORA_REMOVAL);
      event.getRegistry().register(COW);
   }

   @SubscribeEvent
   public static void registerPotionEvent(Register<Potion> event) {
      if (ForgeConfigHandler.server.registerEncumbered) {
         event.getRegistry().register(PotionEncumbered.INSTANCE);
      }

      if (ForgeConfigHandler.mixinConfig.delayedLaunch) {
         event.getRegistry().register(PotionDelayedLaunch.INSTANCE);
      }

      if (ForgeConfigHandler.server.registerLesserFireResistance) {
         event.getRegistry().register(PotionLesserFireResistance.INSTANCE);
      }

      if (ForgeConfigHandler.server.registerCleansingTalisman) {
         event.getRegistry().register(PotionCurseBreak.INSTANCE);
      }

      if (ForgeConfigHandler.server.registerCowPotion) {
         event.getRegistry().register(PotionCow.INSTANCE);
      }

   }

   @SubscribeEvent
   public static void registerPotionTypeEvent(Register<PotionType> event) {
      if (ForgeConfigHandler.server.registerCleansingTalisman) {
         event.getRegistry().register(curseBreakPotion);
         PotionHelper.addMix(PotionTypes.THICK, cleansingTalisman, curseBreakPotion);
      }

   }
}
