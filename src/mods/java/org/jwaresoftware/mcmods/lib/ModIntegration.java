package org.jwaresoftware.mcmods.lib;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.common.Loader;

public enum ModIntegration {
   ABYSSALCRAFT("abyssalcraft"),
   ACTUALLY_ADDITIONS("actuallyadditions"),
   ADVENTURERS_TOOLBOX("toolbox"),
   AGRICRAFT("agricraft"),
   ANCIENT_WARFARE("ancientwarfare"),
   ANIMANIA("animania"),
   APPLECORE("applecore"),
   APPLESKIN("appleskin"),
   AQUACULTURE("aquaculture"),
   ARMOR_UNDERWEAR("armorunder"),
   ASTRAL_SORCERY("astralsorcery"),
   BAMBOOZLED("bamboozled"),
   BAUBLES("baubles"),
   BAUBLEY_HEARTS("bhc"),
   BASE_METALS("basemetals"),
   BETTER_AGRICULTURE("betteragriculture"),
   BETTER_WITH_MODS("betterwithmods"),
   BIOMESOPLENTY("biomesoplenty"),
   BLOOD_MAGIC("bloodmagic"),
   BULLSEYE("bullseye"),
   CARIBBEAN_FOODPANTRY("caribbeanfoodpantry"),
   CARROTS_LIB("carrots"),
   CAVERN("cavern"),
   CERAMICS("ceramics"),
   CHAMPIONS("champions"),
   CHARCOAL_PIT("charcoal_pit"),
   COMBUSTIVE_FISHIN("combustfish"),
   CREEPER_CONFETTI("creeperconfetti"),
   CUISINE("cuisine"),
   CYCLIC_MAGIC("cyclicmagic"),
   DEFILED_LANDS("defiledlands"),
   DIMENSIONAL_DOORS("dimdoors"),
   DMONSTERS("dmonsters"),
   DUNGEON_MOBS("dungeonmobs"),
   DYNAMIC_TREES("dynamictrees"),
   ELDER_ARSENAL("elderarsenal"),
   ELECTROBLOB_WIZARDRY("ebwizardry"),
   ELEMENTAL_CREEPERS("elementalcreepers"),
   EMBER_ROOT_ZOO("emberroot"),
   ENDER_ZOO("enderzoo"),
   EXOTIC_BIRDS("exoticbirds"),
   EXTRA_ALCHEMY("extraalchemy"),
   FAMILIAR_FAUNA("familiarfauna"),
   FERDINANDS_FLOWERS("ferdinandsflowers"),
   FORESTRY("forestry"),
   GALACTICRAFT("galacticraftcore"),
   GRAVESTONE_EXT("gravestone-extended"),
   GHOSTLY("ghostly"),
   HARVESTCRAFT("harvestcraft"),
   HEARTDROPS("heartdrops"),
   HUNGER_OVERHAUL("hungeroverhaul"),
   ICE_AND_FIRE("iceandfire"),
   INFERNAL_MOBS("infernalmobs"),
   INVENTORY_TWEAKS("inventorytweaks"),
   JEI("jei"),
   LOOT_SLASH_CONQUER("lsc"),
   MAGMA_MONSTERS("magma_monsters"),
   MEGALOOT("megaloot"),
   MILLENAIRE("millenaire"),
   MINECRAFT("minecraft"),
   MOBULTION("mobultion"),
   MOCREATURES("mocreatures"),
   MOWZIE_MOBS("mowziesmobs"),
   MYSTICAL_AGRICULTURE("mysticalagriculture"),
   MYSTICAL_WILDLIFE("mysticalwildlife"),
   NATURA("natura"),
   NETHER_EX("nex"),
   NUTRITION("nutrition"),
   PERSONAL_EFFECTS("personaleffects"),
   PILLAGERS("pillagers"),
   PINKLYSHEEP("pinklysheep"),
   PLANT_MEGAPACK("plantmegapack"),
   PLANTS_MOD("plants2"),
   POTION_CORE("potioncore"),
   PRIMAL("primal"),
   PRIMITIVE_MOBS("primitivemobs"),
   QUARK("quark"),
   RANDOMLOOT("randomloot"),
   REALWORLD("realworld"),
   REFORGED_MOD("reforged"),
   REPTILE_MOD("reptilemod"),
   ROUGH_MOBS("roughmobs"),
   RUSTIC("rustic"),
   SALTY_MOD("saltmod"),
   SCALING_HEALTH("scalinghealth"),
   SHOTGUNS_AND_GLITTER("shotgunsandglitter"),
   SILENT_GEMS("silentgems"),
   SILENT_GEAR("silentgear"),
   SMARTHOPPERS("smarthoppers"),
   SPARTAN_SHIELDS("spartanshields"),
   SPARTAN_WEAPONRY("spartanweaponry"),
   SPECTRITE("spectrite"),
   STACK_UP("stackup"),
   STYLED_BLOCKS("styledblocks"),
   SUPERBLOCKS("superblocks"),
   TEASTORY("teastory"),
   TERRAQUEOUS("terraqueous"),
   TETRA("tetra"),
   THAUMCRAFT("thaumcraft"),
   THE_BENEATH("beneath"),
   THE_BETWEENLANDS("thebetweenlands"),
   TINKERS_CONSTRUCT("tconstruct"),
   TOTEM_ESSENTIALS("totemessentials"),
   TOTEM_EXPANSION("totemexpansion"),
   TOTEMIC("totemic"),
   TOUGH_AS_NAILS("toughasnails"),
   TRAVERSE("traverse"),
   TREASURE2("treasure2"),
   TWILIGHT_FOREST("twilightforest"),
   UPSIZER("upsizer"),
   VANILLA_FOODPANTRY("vanillafoodpantry"),
   WOLF_ARMOR("wolfarmor");

   private final String _modid;

   private ModIntegration(String modid) {
      this._modid = modid;
   }

   public String modid() {
      return this._modid;
   }

   public final boolean isLoaded() {
      return Loader.isModLoaded(this._modid);
   }

   public final boolean is(String modid) {
      return this._modid.equals(modid);
   }

   public final boolean belongsTo(@Nullable ResourceLocation r) {
      return r != null && this._modid.equals(r.getResourceDomain());
   }

   public final boolean matches(@Nullable ResourceLocation r, @Nonnull String item_name) {
      return this.belongsTo(r) && item_name.equals(r.getResourcePath());
   }

   public final boolean matches(@Nullable Entity e, @Nonnull String entity_name) {
      ResourceLocation r = e != null ? EntityList.getKey(e) : null;
      return this.matches(r, entity_name);
   }

   public final boolean matchesAny(@Nullable Entity e, String... names) {
      ResourceLocation r = e != null ? EntityList.getKey(e) : null;
      if (this.belongsTo(r)) {
         String entity_name = r.getResourcePath();

         for(String next_name : names) {
            if (entity_name.equals(next_name)) {
               return true;
            }
         }
      }

      return false;
   }

   public final boolean belongsTo(@Nullable Biome b) {
      return b != null ? this.belongsTo(b.getRegistryName()) : false;
   }

   public final boolean belongsTo(@Nullable Entity e) {
      ResourceLocation r = e != null ? EntityList.getKey(e) : null;
      return this.belongsTo(r);
   }

   public final boolean belongsTo(@Nonnull ItemStack i) {
      ResourceLocation r = i != null && !i.isEmpty() ? i.getItem().getRegistryName() : null;
      return this.belongsTo(r);
   }

   public static final String itemid(@Nonnull String mod_id, @Nonnull String item_name) {
      return "" + mod_id + ":" + item_name;
   }

   public final String itemid(@Nonnull String item_name) {
      return this.modid() + ":" + item_name;
   }

   public final ResourceLocation resource(@Nonnull String res_name) {
      return res_name.indexOf(58) > 0 ? new ResourceLocation(res_name) : new ResourceLocation(this.modid(), res_name);
   }

   @Nullable
   public final Item item(@Nonnull String item_name) {
      Item item = (Item)Item.REGISTRY.getObject(this.resource(item_name));
      return SharedGlue.isDefined(item) ? item : null;
   }

   @Nullable
   public final Block block(@Nonnull String block_name) {
      Block block = (Block)Block.REGISTRY.getObject(this.resource(block_name));
      return block != null && block != Blocks.AIR ? block : null;
   }

   @Nonnull
   public final ItemStack itemstack(@Nonnull String item_name, int meta) {
      Item item = this.item(item_name);
      return item != null ? new ItemStack(item, 1, meta) : ItemStack.EMPTY;
   }

   @Nonnull
   public static final ItemStack itemstack(@Nonnull String mod_id, @Nonnull String item_name, int meta) {
      Item item = (Item)Item.REGISTRY.getObject(new ResourceLocation(mod_id, item_name));
      return SharedGlue.isDefined(item) ? new ItemStack(item, 1, meta) : ItemStack.EMPTY;
   }

   @Nonnull
   public static final ItemStack itemstack(@Nonnull String mod_id, @Nonnull String item_name) {
      return itemstack(mod_id, item_name, 0);
   }

   @Nonnull
   public final ItemStack itemstack(@Nonnull String item_name) {
      return this.itemstack(item_name, 0);
   }

   @Nullable
   public final Potion potion(@Nonnull String potion_name) {
      return (Potion)Potion.REGISTRY.getObject(this.resource(potion_name));
   }

   @Nullable
   public final Enchantment enchantment(@Nonnull String ench_name) {
      return (Enchantment)Enchantment.REGISTRY.getObject(this.resource(ench_name));
   }

   public static final boolean isPresent(@Nonnull String modid) {
      return modid != null && Loader.isModLoaded(modid);
   }

   public static final boolean anyPresent(ModIntegration... mods) {
      for(ModIntegration mod : mods) {
         if (mod.isLoaded()) {
            return true;
         }
      }

      return false;
   }

   public static final boolean isVanillaBaselineReplaced() {
      return PRIMAL.isLoaded() || BETTER_WITH_MODS.isLoaded();
   }

   public static final boolean isUniversalBucket(@Nonnull ItemStack stack) {
      Item item = stack.getItem();
      if (item instanceof UniversalBucket) {
         return true;
      } else {
         ResourceLocation r = item.getRegistryName();
         return CERAMICS.belongsTo(r) && "clay_bucket".equals(r.getResourcePath());
      }
   }

   public static final boolean isCustomMilkBottle(@Nonnull ItemStack stack) {
      ResourceLocation r = stack.getItem().getRegistryName();
      if (r == null) {
         return false;
      } else {
         String item_name = r.getResourcePath();
         if (VANILLA_FOODPANTRY.belongsTo(r)) {
            return "milkdrink".equals(item_name);
         } else if (ANIMANIA.belongsTo(r)) {
            return "milk_bottle".equals(item_name);
         } else {
            if (RUSTIC.belongsTo(r) && "fluid_bottle".equals(item_name)) {
               NBTTagCompound f = stack.getSubCompound("Fluid");
               if (f != null) {
                  return Fluids.isMilk(FluidStack.loadFluidStackFromNBT(f));
               }
            }

            return false;
         }
      }
   }

   private static final boolean isBucketOf(@Nonnull ItemStack stack, @Nonnull Fluid fluid, boolean strict) {
      if (isUniversalBucket(stack)) {
         FluidStack f = FluidUtil.getFluidContained(stack);
         if (f != null) {
            if (f.getFluid() == fluid) {
               return true;
            }

            if (!strict) {
               if (fluid == FluidRegistry.WATER) {
                  return Fluids.isWater(f);
               }

               if (Fluids.isMilk(fluid.getName())) {
                  return Fluids.isMilkLike(f);
               }
            }
         }
      }

      return false;
   }

   public static final boolean isWaterBucket(@Nonnull ItemStack stack) {
      return RID.matches(stack, "itemWaterBucket", Items.WATER_BUCKET) ? true : isBucketOf(stack, FluidRegistry.WATER, false);
   }

   public static final boolean isLavaBucket(@Nonnull ItemStack stack) {
      return RID.matches(stack, "itemLavaBucket", Items.LAVA_BUCKET) ? true : isBucketOf(stack, FluidRegistry.LAVA, true);
   }

   public static final boolean isMilkBucket(@Nonnull ItemStack stack) {
      if (RID.matches(stack, "itemMilkBucket", Items.MILK_BUCKET)) {
         return true;
      } else {
         Fluid mf = FluidRegistry.getFluid("milk");
         return mf == null ? false : isBucketOf(stack, mf, true);
      }
   }

   private static final boolean isSimpleFluid(FluidStack fluid) {
      return fluid.tag == null || fluid.tag.hasNoTags();
   }

   public static final boolean isBucketOf(@Nonnull ItemStack stack, @Nonnull FluidStack fluid) {
      if (isSimpleFluid(fluid)) {
         Fluid f = fluid.getFluid();
         if (f == FluidRegistry.WATER) {
            return isWaterBucket(stack);
         }

         if (f == FluidRegistry.LAVA) {
            return isLavaBucket(stack);
         }

         if ("milk".equals(f.getName())) {
            return isMilkBucket(stack);
         }
      }

      return isUniversalBucket(stack) && fluid.isFluidEqual(stack);
   }

   public static final boolean hasTipsyEffect(@Nonnull Potion potion) {
      ResourceLocation name = potion.getRegistryName();
      if (name == null) {
         return false;
      } else {
         String potion_name = name.getResourcePath();
         if (!"tipsy".equals(potion_name) && !"booze".equals(potion_name) && !"drunk".equals(potion_name)) {
            return RUSTIC.belongsTo(name) ? "tipsy".equals(potion_name) : false;
         } else {
            return true;
         }
      }
   }

   public static final boolean hasBleedingEffect(@Nonnull Potion potion) {
      if (potion.isBadEffect()) {
         ResourceLocation name = potion.getRegistryName();
         if (name == null) {
            return false;
         }

         String potion_name = name.getResourcePath();
         if (potion_name.contains("bleed")) {
            return true;
         }
      }

      return false;
   }

   public static final boolean isDetonateable(Block block) {
      if (block != Blocks.TNT && !(block instanceof BlockTNT)) {
         ResourceLocation r = block.getRegistryName();
         if (r == null) {
            return false;
         } else {
            return ELDER_ARSENAL.belongsTo(r) && "gunpowder_barrel".equals(r.getResourcePath());
         }
      } else {
         return true;
      }
   }

   public static final boolean isOneOfWabbits(String modid) {
      return CARROTS_LIB.is(modid) || VANILLA_FOODPANTRY.is(modid) || PINKLYSHEEP.is(modid) || SMARTHOPPERS.is(modid) || SUPERBLOCKS.is(modid) || CARIBBEAN_FOODPANTRY.is(modid) || STYLED_BLOCKS.is(modid) || ARMOR_UNDERWEAR.is(modid) || PERSONAL_EFFECTS.is(modid) || UPSIZER.is(modid);
   }
}
