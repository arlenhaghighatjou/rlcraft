package xzeroair.trinkets.init;

import java.util.HashMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.items.base.BasePotion;
import xzeroair.trinkets.items.potions.IceResistance;
import xzeroair.trinkets.items.potions.PotionObject;
import xzeroair.trinkets.items.potions.TransformationPotion;
import xzeroair.trinkets.races.EntityRace;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.helpers.StringUtils;

public class ModPotionTypes {
   public static final String baseSparkling = "sparkling";
   public static final String enhancedGlittering = "glittering";
   public static final String advancedGlowing = "glowing";
   public static final String restore = "restorative";
   public static final String iceResist = "ice_resistance";
   public static HashMap<String, Potion> TrinketPotions = new HashMap();
   public static HashMap<String, PotionType> TrinketPotionTypes = new HashMap();
   public static HashMap<String, PotionObject> TrinketPotionObjects = new HashMap();
   public static HashMap<String, PotionObject> TrinketRacePotionObjects = new HashMap();

   public ModPotionTypes() {
   }

   protected static PotionObject createBasePotion(String potionName, int color, int duration, Ingredient craftingIngredient) {
      return createBasePotion(potionName, color, duration, false, craftingIngredient);
   }

   protected static PotionObject createBasePotion(String potionName, int color, int duration, boolean isBadEffect, Ingredient craftingIngredient) {
      return createBasePotion(potionName, color, duration, duration * 3, isBadEffect, craftingIngredient);
   }

   protected static PotionObject createBasePotion(String potionName, int color, int duration, int extendedDuration, Ingredient craftingIngredient) {
      return createBasePotion(potionName, color, duration, extendedDuration, false, craftingIngredient);
   }

   protected static PotionObject createBasePotion(String potionName, int color, int duration, int extendedDuration, boolean isBadEffect, Ingredient craftingIngredient) {
      return createBasePotion("xat", potionName, color, duration, extendedDuration, isBadEffect, craftingIngredient);
   }

   public static PotionObject createBasePotion(String modid, String potionName, int color, int duration, boolean isBadEffect, Ingredient craftingIngredient) {
      Potion potion = new BasePotion(potionName, color, duration, isBadEffect);
      return getBasePotionObject(potion, modid, potionName, color, duration, craftingIngredient);
   }

   public static PotionObject createBasePotion(String modid, String potionName, int color, int duration, int extendedDuration, boolean isBadEffect, Ingredient craftingIngredient) {
      Potion potion = new BasePotion(potionName, color, duration, isBadEffect);
      return getBasePotionObject(potion, modid, potionName, color, duration, extendedDuration, craftingIngredient);
   }

   public static PotionObject getBasePotionObject(Potion potionProduct, String modid, String potionName, int color, int duration, Ingredient craftingIngredient) {
      return getBasePotionObject(potionProduct, modid, potionName, color, duration, duration * 3, craftingIngredient);
   }

   public static PotionObject getBasePotionObject(Potion potionProduct, String modid, String potionName, int color, int duration, int extendedDuration, Ingredient craftingIngredient) {
      PotionObject obj = new PotionObject(potionProduct, modid, potionName, color, duration, extendedDuration, craftingIngredient);
      boolean isInternal = modid.contentEquals("xat");
      if (isInternal) {
         TrinketPotionObjects.put(obj.getName(), obj);
      }

      return obj;
   }

   protected static PotionObject createCompoundPotion(PotionType craftingBase, String potionName, int color, int duration, Ingredient craftingIngredient) {
      Potion potion = new BasePotion(potionName, color, duration, false);
      return createCompoundPotion(potion, craftingBase, "xat", potionName, color, duration, duration * 3, craftingIngredient);
   }

   protected static PotionObject createCompoundPotion(PotionType craftingBase, String potionName, int color, int duration, int extendedDuration, Ingredient craftingIngredient) {
      Potion potion = new BasePotion(potionName, color, duration, false);
      return createCompoundPotion(potion, craftingBase, "xat", potionName, color, duration, extendedDuration, craftingIngredient);
   }

   protected static PotionObject createCompoundPotion(Potion potionProduct, PotionType craftingBase, String potionName, int color, int duration, Ingredient craftingIngredient) {
      return createCompoundPotion(potionProduct, craftingBase, "xat", potionName, color, duration, duration * 3, craftingIngredient);
   }

   protected static PotionObject createCompoundPotion(Potion potionProduct, PotionType craftingBase, String potionName, int color, int duration, int extendedDuration, Ingredient craftingIngredient) {
      return createCompoundPotion(potionProduct, craftingBase, "xat", potionName, color, duration, extendedDuration, craftingIngredient);
   }

   public static PotionObject createCompoundPotion(Potion potionProduct, PotionType craftingBase, String modid, String potionName, int color, int duration, Ingredient craftingIngredient) {
      return createCompoundPotion(potionProduct, craftingBase, modid, potionName, color, duration, duration * 3, craftingIngredient);
   }

   public static PotionObject createCompoundPotion(Potion potionProduct, PotionType craftingBase, String modid, String potionName, int color, int duration, int extendedDuration, Ingredient craftingIngredient) {
      PotionObject obj = new PotionObject(potionProduct, craftingBase, modid, potionName, color, duration, extendedDuration, craftingIngredient);
      boolean isInternal = modid.contentEquals("xat");
      if (isInternal) {
         TrinketPotionObjects.put(obj.getName(), obj);
      }

      return obj;
   }

   public static PotionObject createRacePotion(EntityRace race, PotionType craftingBase, int duration, Ingredient craftingIngredient) {
      return createRacePotion(race, craftingBase, duration, duration * 3, craftingIngredient);
   }

   public static PotionObject createRacePotion(EntityRace race, PotionType craftingBase, int duration, int extendedDuration, Ingredient craftingIngredient) {
      return createRacePotion(race, craftingBase, duration, extendedDuration, false, craftingIngredient);
   }

   public static PotionObject createRacePotion(EntityRace race, PotionType craftingBase, int duration, int extendedDuration, boolean isBadEffect, Ingredient craftingIngredient) {
      String modid = race.getRegistryName().getResourceDomain().toString().toLowerCase();
      String name = race.getRegistryName().getResourcePath().toString().toLowerCase();
      int color = race.getPrimaryColor();
      Potion potion = new TransformationPotion(modid, name, color, duration, race.getUUID().toString(), false);
      PotionObject obj = createCompoundPotion(potion, craftingBase, modid, name, color, duration, extendedDuration, craftingIngredient);
      TrinketRacePotionObjects.put(obj.getName(), obj);
      return obj;
   }

   public static void registerPotionTypes() {
      Trinkets.log.info("Generating Potions");
      createBasePotion("sparkling", 16777160, 0, Ingredient.fromItem(ModItems.crafting.glowing_powder)).registerWithPotion();
      createCompoundPotion(((PotionObject)TrinketPotionObjects.get("sparkling")).getPotionType(), "glittering", 16777120, 0, Ingredient.fromItem(ModItems.crafting.glowing_ingot)).registerWithPotion();
      createCompoundPotion(((PotionObject)TrinketPotionObjects.get("glittering")).getPotionType(), "glowing", 16777080, 0, Ingredient.fromItem(ModItems.crafting.glowing_gem)).registerWithPotion();
      createCompoundPotion(new IceResistance("ice_resistance", 3600, 15132390, false), ((PotionObject)TrinketPotionObjects.get("sparkling")).getPotionType(), "ice_resistance", 15132390, 3600, 9600, Ingredient.fromItem(Item.getItemFromBlock(Blocks.SNOW))).registerWithPotion();
      createRacePotion(EntityRaces.human, ((PotionObject)TrinketPotionObjects.get("sparkling")).getPotionType(), TrinketsConfig.SERVER.Potion.human.Duration, getCatalyst(TrinketsConfig.SERVER.Potion.human.catalyst)).registerWithPotion();
      createRacePotion(EntityRaces.fairy, ((PotionObject)TrinketPotionObjects.get("glowing")).getPotionType(), TrinketsConfig.SERVER.Potion.fairy.Duration, getCatalyst(TrinketsConfig.SERVER.Potion.fairy.catalyst)).registerWithPotion();
      createRacePotion(EntityRaces.dwarf, ((PotionObject)TrinketPotionObjects.get("glittering")).getPotionType(), TrinketsConfig.SERVER.Potion.dwarf.Duration, getCatalyst(TrinketsConfig.SERVER.Potion.dwarf.catalyst)).registerWithPotion();
      createRacePotion(EntityRaces.titan, ((PotionObject)TrinketPotionObjects.get("glowing")).getPotionType(), TrinketsConfig.SERVER.Potion.titan.Duration, getCatalyst(TrinketsConfig.SERVER.Potion.titan.catalyst)).registerWithPotion();
      createRacePotion(EntityRaces.goblin, ((PotionObject)TrinketPotionObjects.get("sparkling")).getPotionType(), TrinketsConfig.SERVER.Potion.goblin.Duration, getCatalyst(TrinketsConfig.SERVER.Potion.goblin.catalyst)).registerWithPotion();
      createRacePotion(EntityRaces.elf, ((PotionObject)TrinketPotionObjects.get("glittering")).getPotionType(), TrinketsConfig.SERVER.Potion.elf.Duration, getCatalyst(TrinketsConfig.SERVER.Potion.elf.catalyst)).registerWithPotion();
      createRacePotion(EntityRaces.faelis, ((PotionObject)TrinketPotionObjects.get("glittering")).getPotionType(), TrinketsConfig.SERVER.Potion.faelis.Duration, getCatalyst(TrinketsConfig.SERVER.Potion.faelis.catalyst)).registerWithPotion();
      createRacePotion(EntityRaces.dragon, ((PotionObject)TrinketPotionObjects.get("glowing")).getPotionType(), TrinketsConfig.SERVER.Potion.dragon.Duration, getCatalyst(TrinketsConfig.SERVER.Potion.dragon.catalyst)).registerWithPotion();
      Trinkets.log.info("Finished Generating Potions");
   }

   private static Ingredient getCatalyst(String catalyst) {
      String[] itemConfig = catalyst.replace(";", ":").split(":");
      String modIDString = StringUtils.getStringFromArray(itemConfig, 0);
      String itemIDString = StringUtils.getStringFromArray(itemConfig, 1);
      String metaString = StringUtils.getStringFromArray(itemConfig, 2).replaceAll("[^\\d]", "");
      int meta = 32767;

      try {
         if (!metaString.isEmpty()) {
            meta = Integer.parseInt(metaString);
         }
      } catch (Exception var7) {
         Trinkets.log.warn("Invalid catalyst meta from | " + catalyst);
      }

      return Ingredient.fromStacks(new ItemStack[]{new ItemStack(Item.getByNameOrId(modIDString + ":" + itemIDString), 1, meta)});
   }
}
