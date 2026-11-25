package com.charles445.simpledifficulty.compat;

import com.charles445.simpledifficulty.api.SDCompatibility;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.json.JsonItemIdentity;
import com.charles445.simpledifficulty.api.config.json.JsonPropertyTemperature;
import com.charles445.simpledifficulty.api.config.json.JsonPropertyValue;
import com.charles445.simpledifficulty.util.CompatUtil;
import com.charles445.simpledifficulty.util.OreDictUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class JsonCompatDefaults {
   public static JsonCompatDefaults instance = new JsonCompatDefaults();

   public JsonCompatDefaults() {
   }

   public void populate() {
      this.populateBaubles();
      this.populateBiomesOPlenty();
      this.populateHarvestCraft();
      this.populateLycanitesMobs();
      this.populatePyrotech();
      this.populateRealisticTorches();
      this.populateRustic();
      this.populateSimpleCampfire();
      this.populateTinkersConstruct();
   }

   public boolean populate(String modid) {
      byte var3 = -1;
      switch(modid.hashCode()) {
      case -1894911324:
         if (modid.equals("realistictorches")) {
            var3 = 5;
         }
         break;
      case -1742494237:
         if (modid.equals("tconstruct")) {
            var3 = 8;
         }
         break;
      case -919651330:
         if (modid.equals("rustic")) {
            var3 = 6;
         }
         break;
      case -424745242:
         if (modid.equals("biomesoplenty")) {
            var3 = 1;
         }
         break;
      case -330846322:
         if (modid.equals("baubles")) {
            var3 = 0;
         }
         break;
      case -139769801:
         if (modid.equals("campfire")) {
            var3 = 7;
         }
         break;
      case 904941655:
         if (modid.equals("harvestcraft")) {
            var3 = 2;
         }
         break;
      case 1009814940:
         if (modid.equals("pyrotech")) {
            var3 = 4;
         }
         break;
      case 1652168751:
         if (modid.equals("lycanitesmobs")) {
            var3 = 3;
         }
      }

      switch(var3) {
      case 0:
         return this.populateBaubles();
      case 1:
         return this.populateBiomesOPlenty();
      case 2:
         return this.populateHarvestCraft();
      case 3:
         return this.populateLycanitesMobs();
      case 4:
         return this.populatePyrotech();
      case 5:
         return this.populateRealisticTorches();
      case 6:
         return this.populateRustic();
      case 7:
         return this.populateSimpleCampfire();
      case 8:
         return this.populateTinkersConstruct();
      default:
         return false;
      }
   }

   private boolean populateBaubles() {
      if (!this.canUseModJsonDefaults("baubles")) {
         return false;
      } else {
         JsonConfig.registerArmorTemperature("baubles:ring", -0.25F);
         return true;
      }
   }

   private boolean populateBiomesOPlenty() {
      if (!this.canUseModJsonDefaults("biomesoplenty")) {
         return false;
      } else {
         this.addFluidTemperature("hot_spring_water", 3.0F);
         return true;
      }
   }

   private boolean populateHarvestCraft() {
      if (!this.canUseModJsonDefaults("harvestcraft")) {
         return false;
      } else {
         for(ItemStack stack : OreDictUtil.listAlljuice) {
            ResourceLocation loc = stack.getItem().getRegistryName();
            if (loc.getResourceDomain().equals("harvestcraft")) {
               this.addDrink(loc.toString(), 6, 5.0F);
            }
         }

         for(ItemStack stack : OreDictUtil.listAllsoda) {
            ResourceLocation loc = stack.getItem().getRegistryName();
            if (loc.getResourceDomain().equals("harvestcraft")) {
               this.addDrink(loc.toString(), 9, 7.0F);
            }
         }

         for(ItemStack stack : OreDictUtil.listAllsmoothie) {
            ResourceLocation loc = stack.getItem().getRegistryName();
            if (loc.getResourceDomain().equals("harvestcraft")) {
               this.addDrink(loc.toString(), 9, 7.0F);
            }
         }

         this.addDrink("harvestcraft:teaitem", 6, 5.0F);
         this.addDrink("harvestcraft:coffeeitem", 6, 5.0F);
         this.addDrink("harvestcraft:hotchocolateitem", 6, 5.0F);
         this.addDrink("harvestcraft:lemonaideitem", 9, 7.0F);
         this.addDrink("harvestcraft:raspberryicedteaitem", 9, 7.0F);
         this.addDrink("harvestcraft:chaiteaitem", 9, 7.0F);
         this.addDrink("harvestcraft:espressoitem", 12, 9.0F);
         this.addDrink("harvestcraft:coffeeconlecheitem", 18, 15.0F);
         this.addDrink("harvestcraft:coconutmilkitem", 3, 3.0F);
         this.addDrink("harvestcraft:chocolatemilkitem", 6, 5.0F);
         this.addDrink("harvestcraft:fruitpunchitem", 6, 5.0F);
         this.addDrink("harvestcraft:pinacoladaitem", 6, 5.0F);
         this.addDrink("harvestcraft:eggnogitem", 15, 12.0F);
         this.addDrink("harvestcraft:soymilkitem", 3, 3.0F);
         this.addDrink("harvestcraft:applecideritem", 6, 5.0F);
         this.addDrink("harvestcraft:energydrinkitem", 15, 12.0F);
         this.addDrink("harvestcraft:greenteaitem", 6, 5.0F);
         this.addDrink("harvestcraft:earlgreyteaitem", 6, 5.0F);
         this.addDrink("harvestcraft:bubbleteaitem", 12, 9.0F);
         this.addDrink("harvestcraft:rosepetalteaitem", 6, 5.0F);
         this.addDrink("harvestcraft:cherryslushieitem", 9, 7.0F);
         this.addDrink("harvestcraft:lycheeteaitem", 9, 7.0F);
         this.addDrink("harvestcraft:dandelionteaitem", 9, 7.0F);
         this.addDrink("harvestcraft:raspberrymilkshakeitem", 15, 12.0F);
         this.addDrink("harvestcraft:pumpkinspicelatteitem", 20, 19.0F);
         this.addDrink("harvestcraft:rootbeerfloatitem", 18, 15.0F);
         this.addDrink("harvestcraft:hotcocoaitem", 15, 12.0F);
         this.addDrink("harvestcraft:strawberrymilkshakeitem", 9, 7.0F);
         this.addDrink("harvestcraft:chocolatemilkshakeitem", 9, 7.0F);
         this.addDrink("harvestcraft:bananamilkshakeitem", 9, 7.0F);
         this.addDrink("harvestcraft:gooseberrymilkshakeitem", 9, 7.0F);
         this.addDrink("harvestcraft:durianmilkshakeitem", 9, 7.0F);
         this.addDrink("harvestcraft:cookiesandmilkitem", 8, 7.0F);
         this.addDrink("harvestcraft:sundayhighteaitem", 12, 10.0F);
         this.addDrink("harvestcraft:delightedmealitem", 14, 13.0F);
         this.addDrink("harvestcraft:weekendpicnicitem", 20, 20.0F);
         this.addDrink("harvestcraft:theatreboxitem", 16, 15.0F);
         this.addDrink("harvestcraft:friedfeastitem", 20, 20.0F);
         this.addDrink("harvestcraft:bbqplatteritem", 20, 20.0F);

         for(int i = 1; i <= 16; ++i) {
            this.addHeldItemTemperature("harvestcraft:candledeco" + i, 1.0F);
            this.addBlockTemperature("harvestcraft:candledeco" + i, 1.0F);
         }

         return true;
      }
   }

   private boolean populateLycanitesMobs() {
      if (!this.canUseModJsonDefaults("lycanitesmobs")) {
         return false;
      } else {
         this.addBlockTemperature("lycanitesmobs:purelava", 12.5F);
         this.addBlockTemperature("lycanitesmobs:moglava", 12.5F);
         this.addBlockTemperature("lycanitesmobs:ooze", -12.5F);
         this.addBlockTemperature("lycanitesmobs:rabbitooze", -12.5F);
         this.addBlockTemperature("lycanitesmobs:frostfire", -5.0F);
         this.addBlockTemperature("lycanitesmobs:icefire", -8.0F);
         return true;
      }
   }

   private boolean populatePyrotech() {
      if (!this.canUseModJsonDefaults("pyrotech")) {
         return false;
      } else {
         this.addBlockTemperature("pyrotech:torch_fiber", 3.0F, new JsonPropertyValue("type", "LIT"));
         this.addBlockTemperature("pyrotech:torch_stone", 3.0F, new JsonPropertyValue("type", "LIT"));
         this.addBlockTemperature("pyrotech:campfire", 7.0F, new JsonPropertyValue("variant", "LIT"));
         this.addBlockTemperature("pyrotech:kiln_pit", 6.0F, new JsonPropertyValue("variant", "ACTIVE"));
         this.addBlockTemperature("pyrotech:bloomery", 6.0F, new JsonPropertyValue("type", "BottomLit"));
         this.addBlockTemperature("pyrotech:wither_forge", 7.0F, new JsonPropertyValue("type", "BottomLit"));
         this.addBlockTemperature("pyrotech:pile_slag", 4.0F, new JsonPropertyValue("molten", "true"));
         float pyrotechStone = 5.0F;
         float pyroTechBrick = 4.0F;
         this.addBlockTemperature("pyrotech:stone_kiln", pyrotechStone, new JsonPropertyValue("type", "BOTTOM_LIT"));
         this.addBlockTemperature("pyrotech:stone_oven", pyrotechStone, new JsonPropertyValue("type", "BOTTOM_LIT"));
         this.addBlockTemperature("pyrotech:stone_sawmill", pyrotechStone, new JsonPropertyValue("type", "BOTTOM_LIT"));
         this.addBlockTemperature("pyrotech:stone_crucible", pyrotechStone, new JsonPropertyValue("type", "BOTTOM_LIT"));
         this.addBlockTemperature("pyrotech:brick_kiln", pyroTechBrick, new JsonPropertyValue("type", "BOTTOM_LIT"));
         this.addBlockTemperature("pyrotech:brick_oven", pyroTechBrick, new JsonPropertyValue("type", "BOTTOM_LIT"));
         this.addBlockTemperature("pyrotech:brick_sawmill", pyroTechBrick, new JsonPropertyValue("type", "BOTTOM_LIT"));
         this.addBlockTemperature("pyrotech:brick_crucible", pyroTechBrick, new JsonPropertyValue("type", "BOTTOM_LIT"));
         return true;
      }
   }

   private boolean populateRealisticTorches() {
      if (!this.canUseModJsonDefaults("realistictorches")) {
         return false;
      } else {
         this.addHeldItemTemperature("realistictorches:torch_lit", 1.0F);
         this.addBlockTemperature("realistictorches:torch_lit", 1.0F);
         return true;
      }
   }

   private boolean populateRustic() {
      if (!this.canUseModJsonDefaults("rustic")) {
         return false;
      } else {
         this.addHeldItemTemperature("rustic:candle", 1.0F);
         this.addBlockTemperature("rustic:candle", 1.0F);
         this.addHeldItemTemperature("rustic:candle_gold", 1.0F);
         this.addBlockTemperature("rustic:candle_gold", 1.0F);
         int boozeThirst = 5;
         float boozeSaturation = 5.0F;
         float boozeSickness = 0.25F;
         int juiceThirst = 6;
         float juiceSaturation = 5.0F;
         this.addDrink("rustic:elixir", 4, 0.4F);
         this.addDrink("rustic:fluid_bottle", 2, 0.1F, 0.9F, this.rusticFluid("oliveoil"));
         this.addDrink("rustic:fluid_bottle", 2, 8.0F, this.rusticFluid("ironberryjuice"));
         this.addDrink("rustic:fluid_bottle", juiceThirst, juiceSaturation, this.rusticFluid("wildberryjuice"));
         this.addDrink("rustic:fluid_bottle", juiceThirst, juiceSaturation, this.rusticFluid("grapejuice"));
         this.addDrink("rustic:fluid_bottle", juiceThirst, juiceSaturation, this.rusticFluid("applejuice"));
         this.addDrink("rustic:fluid_bottle", boozeThirst, boozeSaturation, boozeSickness, this.rusticFluid("alewort"));
         this.addDrink("rustic:fluid_bottle", 4, 3.0F, this.rusticFluid("honey"));
         this.addDrink("rustic:fluid_bottle", boozeThirst, boozeSaturation, boozeSickness, this.rusticFluid("ale"));
         this.addDrink("rustic:fluid_bottle", boozeThirst, boozeSaturation, boozeSickness, this.rusticFluid("cider"));
         this.addDrink("rustic:fluid_bottle", boozeThirst, boozeSaturation, boozeSickness, this.rusticFluid("ironwine"));
         this.addDrink("rustic:fluid_bottle", boozeThirst, boozeSaturation, boozeSickness, this.rusticFluid("mead"));
         this.addDrink("rustic:fluid_bottle", boozeThirst, boozeSaturation, boozeSickness, this.rusticFluid("wildberrywine"));
         this.addDrink("rustic:fluid_bottle", boozeThirst, boozeSaturation, boozeSickness, this.rusticFluid("wine"));
         return true;
      }
   }

   private String rusticFluid(String name) {
      return "{Fluid:{FluidName:\"" + name + "\"}}";
   }

   private boolean populateSimpleCampfire() {
      if (!this.canUseModJsonDefaults("campfire")) {
         return false;
      } else {
         this.addBlockTemperature("campfire:campfire", 7.0F);
         return true;
      }
   }

   private boolean populateTinkersConstruct() {
      if (!this.canUseModJsonDefaults("tconstruct")) {
         return false;
      } else {
         float moltenTemp = 12.5F;
         this.addBlockTemperature("tconstruct:molten_alubrass", moltenTemp);
         this.addBlockTemperature("tconstruct:molten_aluminum", moltenTemp);
         this.addBlockTemperature("tconstruct:molten_ardite", moltenTemp);
         this.addBlockTemperature("tconstruct:molten_brass", moltenTemp);
         this.addBlockTemperature("tconstruct:molten_bronze", moltenTemp);
         this.addBlockTemperature("tconstruct:molten_clay", moltenTemp);
         this.addBlockTemperature("tconstruct:molten_cobalt", moltenTemp);
         this.addBlockTemperature("tconstruct:molten_copper", moltenTemp);
         this.addBlockTemperature("tconstruct:molten_dirt", moltenTemp);
         this.addBlockTemperature("tconstruct:molten_electrum", moltenTemp);
         this.addBlockTemperature("tconstruct:molten_emerald", moltenTemp);
         this.addBlockTemperature("tconstruct:molten_glass", moltenTemp);
         this.addBlockTemperature("tconstruct:molten_gold", moltenTemp);
         this.addBlockTemperature("tconstruct:molten_iron", moltenTemp);
         this.addBlockTemperature("tconstruct:molten_knightslime", moltenTemp);
         this.addBlockTemperature("tconstruct:molten_lead", moltenTemp);
         this.addBlockTemperature("tconstruct:molten_manyullyn", moltenTemp);
         this.addBlockTemperature("tconstruct:molten_nickel", moltenTemp);
         this.addBlockTemperature("tconstruct:molten_obsidian", moltenTemp);
         this.addBlockTemperature("tconstruct:molten_pigiron", moltenTemp);
         this.addBlockTemperature("tconstruct:molten_silver", moltenTemp);
         this.addBlockTemperature("tconstruct:molten_steel", moltenTemp);
         this.addBlockTemperature("tconstruct:molten_stone", moltenTemp);
         this.addBlockTemperature("tconstruct:molten_tin", moltenTemp);
         this.addBlockTemperature("tconstruct:molten_zinc", moltenTemp);
         float lavawood = 5.0F;
         float firewood = 7.0F;
         this.addBlockTemperature("tconstruct:firewood", lavawood, new JsonPropertyValue("type", "LAVAWOOD"));
         this.addBlockTemperature("tconstruct:firewood", firewood, new JsonPropertyValue("type", "FIREWOOD"));
         this.addBlockTemperature("tconstruct:firewood_stairs", firewood);
         this.addBlockTemperature("tconstruct:lavawood_stairs", lavawood);
         this.addBlockTemperature("tconstruct:firewood_slab", lavawood, new JsonPropertyValue("type", "LAVAWOOD"));
         this.addBlockTemperature("tconstruct:firewood_slab", firewood, new JsonPropertyValue("type", "FIREWOOD"));
         float heldWood = 2.0F;
         this.addHeldItemTemperature("tconstruct:firewood", 0, heldWood);
         this.addHeldItemTemperature("tconstruct:firewood", 1, heldWood);
         this.addHeldItemTemperature("tconstruct:firewood_stairs", 0, heldWood);
         this.addHeldItemTemperature("tconstruct:lavawood_stairs", 0, heldWood);
         this.addHeldItemTemperature("tconstruct:firewood_slab", 0, heldWood);
         this.addHeldItemTemperature("tconstruct:firewood_slab", 1, heldWood);
         this.addHeldItemTemperature("tconstruct:stone_torch", 0, 1.0F);
         return true;
      }
   }

   private void addBlockTemperature(String registryName, float temperature, JsonPropertyValue... properties) {
      JsonConfig.registerBlockTemperature(registryName, temperature, properties);
   }

   private void addDrink(String registryName, int amount, float saturation) {
      this.addDrink(registryName, amount, saturation, 0.0F);
   }

   private void addDrink(String registryName, int amount, float saturation, float thirstyChance) {
      JsonConfig.registerConsumableThirst(registryName, amount, saturation, thirstyChance, new JsonItemIdentity(-1));
   }

   private void addDrink(String registryName, int amount, float saturation, String nbt) {
      JsonConfig.registerConsumableThirst(registryName, amount, saturation, 0.0F, new JsonItemIdentity(-1, nbt));
   }

   private void addDrink(String registryName, int amount, float saturation, float thirstyChance, String nbt) {
      JsonConfig.registerConsumableThirst(registryName, amount, saturation, thirstyChance, new JsonItemIdentity(-1, nbt));
   }

   private void addFluidTemperature(String fluidName, float temperature) {
      JsonConfig.registerFluidTemperature(fluidName, temperature);
   }

   private void addHeldItemTemperature(String registryName, float temperature) {
      JsonConfig.registerHeldItem(registryName, temperature, new JsonItemIdentity(-1));
   }

   private void addHeldItemTemperature(String registryName, int metadata, float temperature) {
      JsonConfig.registerHeldItem(registryName, temperature, new JsonItemIdentity(metadata));
   }

   private boolean canUseModJsonDefaults(String modid) {
      return this.canUseModJsonDefaults(modid, true);
   }

   private boolean canUseModJsonDefaults(String modid, boolean modBool) {
      return modBool && CompatUtil.canUseMod(modid) && !SDCompatibility.disabledDefaultJson.contains(modid);
   }

   private JsonPropertyTemperature propTemp(float temp) {
      return new JsonPropertyTemperature(temp, new JsonPropertyValue[0]);
   }

   private JsonPropertyTemperature propTemp(float temp, JsonPropertyValue... props) {
      return new JsonPropertyTemperature(temp, props);
   }
}
