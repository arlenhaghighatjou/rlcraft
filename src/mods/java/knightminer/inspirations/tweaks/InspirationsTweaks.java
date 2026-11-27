package knightminer.inspirations.tweaks;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import java.util.Iterator;
import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.util.ReflectionUtil;
import knightminer.inspirations.shared.InspirationsShared;
import knightminer.inspirations.tweaks.block.BlockBetterFlowerPot;
import knightminer.inspirations.tweaks.block.BlockCactusCrop;
import knightminer.inspirations.tweaks.block.BlockFittedCarpet;
import knightminer.inspirations.tweaks.block.BlockSugarCaneCrop;
import knightminer.inspirations.tweaks.item.ItemSeed;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockTallGrass.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.registries.IForgeRegistry;

public class InspirationsTweaks extends PulseBase {
   public void handlePulseEvent(Object evt) {
      if (evt instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)evt);
      }

      if (evt instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)evt);
      }

      if (evt instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)evt);
      }

   }

   public static final String pulseID = "InspirationsTweaks";
   public static CommonProxy proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new knightminer.inspirations.tweaks.TweaksClientProxy() : new knightminer.inspirations.common.CommonProxy();
   public static Block carpet;
   public static Block flowerPot;
   public static BlockCrops cactusCrop;
   public static BlockCrops sugarCaneCrop;
   public static Item potatoSeeds;
   public static Item carrotSeeds;
   public static Item sugarCaneSeeds;
   public static Item cactusSeeds;
   public static PotionType haste;
   public static PotionType strongHaste;
   public static PotionType fatigue;
   public static PotionType strongFatigue;
   public static PotionType hunger;
   public static PotionType strongHunger;
   public static PotionType resistance;
   public static PotionType longResistance;
   public static PotionType levitation;
   public static PotionType longLevitation;
   public static PotionType blindness;
   public static PotionType longBlindness;
   public static PotionType decay;
   public static PotionType strongDecay;
   public static PotionType longHunger;
   private static final ResourceLocation SILVERFISH_TABLE = new ResourceLocation("entities/silverfish");
   private static final ResourceLocation CAVE_SPIDER_TABLE = new ResourceLocation("entities/cave_spider");
   private static final ResourceLocation SKELETON_TABLE = new ResourceLocation("entities/skeleton");
   private static final IBehaviorDispenseItem DEFAULT = new BehaviorDefaultDispenseItem();

   public InspirationsTweaks() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      proxy.preInit();
   }

   public void register(EventBus bus) {
      bus.registerGeneric(Register.class, Block.class, this::registerBlocks);
      bus.registerGeneric(Register.class, Item.class, this::registerItem);
      bus.registerGeneric(Register.class, PotionType.class, this::registerPotionTypes);
      bus.register(LootTableLoadEvent.class, this::onLootTableLoad);
   }

   public void registerBlocks(Register<Block> event) {
      IForgeRegistry<Block> r = event.getRegistry();
      if (Config.enableFittedCarpets) {
         carpet = (Block)register(r, new BlockFittedCarpet(), new ResourceLocation("carpet"));
      }

      if (Config.betterFlowerPot) {
         flowerPot = (Block)register(r, new BlockBetterFlowerPot(), new ResourceLocation("flower_pot"));
      }

      if (Config.enableMoreSeeds) {
         cactusCrop = (BlockCrops)register(r, new BlockCactusCrop(), "cactus_crop");
         sugarCaneCrop = (BlockCrops)register(r, new BlockSugarCaneCrop(), "sugar_cane_crop");
      }

   }

   public void registerItem(Register<Item> event) {
      IForgeRegistry<Item> r = event.getRegistry();
      if (Config.enableMoreSeeds) {
         cactusSeeds = registerItem(r, new ItemSeed(cactusCrop, EnumPlantType.Desert), "cactus_seeds");
         sugarCaneSeeds = registerItem(r, new ItemSeed(sugarCaneCrop, EnumPlantType.Beach), "sugar_cane_seeds");
         carrotSeeds = registerItem(r, new ItemSeeds(Blocks.CARROTS, Blocks.FARMLAND), "carrot_seeds");
         potatoSeeds = registerItem(r, new ItemSeeds(Blocks.POTATOES, Blocks.FARMLAND), "potato_seeds");
      }

   }

   public void registerPotionTypes(Register<PotionType> event) {
      IForgeRegistry<PotionType> r = event.getRegistry();
      if (Config.brewMissingPotions) {
         haste = (PotionType)register(r, new PotionType(new PotionEffect[]{new PotionEffect(MobEffects.HASTE, 3600)}), "haste");
         strongHaste = (PotionType)register(r, new PotionType("haste", new PotionEffect[]{new PotionEffect(MobEffects.HASTE, 1800, 1)}), "strong_haste");
         fatigue = (PotionType)register(r, new PotionType(new PotionEffect[]{new PotionEffect(MobEffects.MINING_FATIGUE, 1200)}), "fatigue");
         strongFatigue = (PotionType)register(r, new PotionType("fatigue", new PotionEffect[]{new PotionEffect(MobEffects.MINING_FATIGUE, 600, 1)}), "strong_fatigue");
         hunger = (PotionType)register(r, new PotionType(new PotionEffect[]{new PotionEffect(MobEffects.HUNGER, 900)}), "hunger");
         longHunger = (PotionType)register(r, new PotionType("hunger", new PotionEffect[]{new PotionEffect(MobEffects.HUNGER, 1800)}), "long_hunger");
         strongHunger = (PotionType)register(r, new PotionType("hunger", new PotionEffect[]{new PotionEffect(MobEffects.HUNGER, 450, 1)}), "strong_hunger");
         resistance = (PotionType)register(r, new PotionType(new PotionEffect[]{new PotionEffect(MobEffects.RESISTANCE, 2400)}), "resistance");
         longResistance = (PotionType)register(r, new PotionType("resistance", new PotionEffect[]{new PotionEffect(MobEffects.RESISTANCE, 6000)}), "long_resistance");
         levitation = (PotionType)register(r, new PotionType(new PotionEffect[]{new PotionEffect(MobEffects.LEVITATION, 160)}), "levitation");
         longLevitation = (PotionType)register(r, new PotionType("levitation", new PotionEffect[]{new PotionEffect(MobEffects.LEVITATION, 300)}), "long_levitation");
         blindness = (PotionType)register(r, new PotionType(new PotionEffect[]{new PotionEffect(MobEffects.BLINDNESS, 600)}), "blindness");
         longBlindness = (PotionType)register(r, new PotionType("blindness", new PotionEffect[]{new PotionEffect(MobEffects.BLINDNESS, 1200)}), "long_blindness");
         decay = (PotionType)register(r, new PotionType(new PotionEffect[]{new PotionEffect(MobEffects.WITHER, 600)}), "decay");
         strongDecay = (PotionType)register(r, new PotionType("decay", new PotionEffect[]{new PotionEffect(MobEffects.WITHER, 300, 1)}), "long_decay");
      }

   }

   public void init(FMLInitializationEvent event) {
      proxy.init();
      if (Config.addGrassDrops) {
         MinecraftForge.addGrassSeed(new ItemStack(carrotSeeds), 4);
         MinecraftForge.addGrassSeed(new ItemStack(potatoSeeds), 3);
      }

      if (Config.brewHeartbeet) {
         Ingredient heartbeet = Ingredient.fromStacks(new ItemStack[]{InspirationsShared.heartbeet});
         PotionHelper.addMix(PotionTypes.WATER, heartbeet, PotionTypes.MUNDANE);
         PotionHelper.addMix(PotionTypes.AWKWARD, heartbeet, PotionTypes.REGENERATION);
      }

      if (Config.betterFlowerPot) {
         InspirationsRegistry.registerFlower(Blocks.CACTUS, 0, 15);
         InspirationsRegistry.registerFlower(Blocks.TALLGRASS, EnumType.FERN.getMeta(), 4);
      }

      if (Config.brewMissingPotions) {
         Iterator<?> iterator = PotionHelper.POTION_TYPE_CONVERSIONS.iterator();

         while(iterator.hasNext()) {
            PotionType input = ReflectionUtil.getMixPredicateInput(iterator.next());
            if (input == PotionTypes.LEAPING || input == PotionTypes.LONG_LEAPING) {
               iterator.remove();
            }
         }

         Ingredient redstone = Ingredient.fromItem(Items.REDSTONE);
         Ingredient glowstone = Ingredient.fromItem(Items.GLOWSTONE_DUST);
         Ingredient spiderEye = Ingredient.fromItem(Items.FERMENTED_SPIDER_EYE);
         Ingredient silverfishPowder = Ingredient.fromStacks(new ItemStack[]{InspirationsShared.silverfishPowder});
         PotionHelper.addMix(PotionTypes.WATER, silverfishPowder, PotionTypes.MUNDANE);
         PotionHelper.addMix(PotionTypes.AWKWARD, silverfishPowder, haste);
         PotionHelper.addMix(haste, glowstone, strongHaste);
         PotionHelper.addMix(haste, spiderEye, fatigue);
         PotionHelper.addMix(strongHaste, spiderEye, strongFatigue);
         PotionHelper.addMix(fatigue, glowstone, strongFatigue);
         PotionHelper.addMix(PotionTypes.REGENERATION, spiderEye, hunger);
         PotionHelper.addMix(PotionTypes.LONG_REGENERATION, spiderEye, longHunger);
         PotionHelper.addMix(PotionTypes.STRONG_REGENERATION, spiderEye, strongHunger);
         PotionHelper.addMix(hunger, redstone, longHunger);
         PotionHelper.addMix(hunger, glowstone, strongHunger);
         Ingredient skulkerShell = Ingredient.fromItem(Items.SHULKER_SHELL);
         PotionHelper.addMix(PotionTypes.WATER, skulkerShell, PotionTypes.MUNDANE);
         PotionHelper.addMix(PotionTypes.AWKWARD, skulkerShell, resistance);
         PotionHelper.addMix(resistance, redstone, longResistance);
         PotionHelper.addMix(PotionTypes.LEAPING, spiderEye, levitation);
         PotionHelper.addMix(PotionTypes.LONG_LEAPING, spiderEye, longLevitation);
         PotionHelper.addMix(levitation, redstone, longLevitation);
         Ingredient inkSac = Ingredient.fromStacks(new ItemStack[]{new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage())});
         PotionHelper.addMix(PotionTypes.WATER, inkSac, PotionTypes.MUNDANE);
         PotionHelper.addMix(PotionTypes.AWKWARD, inkSac, blindness);
         PotionHelper.addMix(blindness, redstone, longBlindness);
         Ingredient witherBone = new OreIngredient("boneWithered");
         PotionHelper.addMix(PotionTypes.WATER, witherBone, PotionTypes.MUNDANE);
         PotionHelper.addMix(PotionTypes.AWKWARD, witherBone, decay);
         PotionHelper.addMix(decay, glowstone, strongDecay);
      }

      this.registerDispenserBehavior();
   }

   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit();
      TweaksEvents.register(MinecraftForge.EVENT_BUS);
   }

   public void onLootTableLoad(LootTableLoadEvent event) {
      if (Config.brewMissingPotions && SILVERFISH_TABLE.equals(event.getName())) {
         addToVanillaLoot(event, SILVERFISH_TABLE.getResourcePath());
      } else if (Config.caveSpiderDrops && CAVE_SPIDER_TABLE.equals(event.getName())) {
         addToVanillaLoot(event, CAVE_SPIDER_TABLE.getResourcePath());
      } else if (Config.skeletonSkull && SKELETON_TABLE.equals(event.getName())) {
         addToVanillaLoot(event, SKELETON_TABLE.getResourcePath());
      }

   }

   private void registerDispenserBehavior() {
      if (Config.dispensersPlaceAnvils) {
         registerDispenserBehavior(Blocks.ANVIL, (source, stack) -> {
            EnumFacing facing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
            World world = source.getWorld();
            BlockPos pos = source.getBlockPos().offset(facing);
            if (!Blocks.ANVIL.canPlaceBlockAt(world, pos)) {
               return DEFAULT.dispense(source, stack);
            } else {
               int meta = stack.getMetadata();
               if (meta > 3 || meta < 0) {
                  meta = 3;
               }

               EnumFacing anvilFacing = facing.getAxis().isVertical() ? EnumFacing.NORTH : facing.rotateY();
               IBlockState state = Blocks.ANVIL.getDefaultState().withProperty(BlockAnvil.DAMAGE, Integer.valueOf(meta)).withProperty(BlockAnvil.FACING, anvilFacing);
               world.setBlockState(pos, state);
               stack.shrink(1);
               return stack;
            }
         });
      }

   }
}
