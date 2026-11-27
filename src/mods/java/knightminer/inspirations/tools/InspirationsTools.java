package knightminer.inspirations.tools;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.shared.InspirationsShared;
import knightminer.inspirations.tools.client.BarometerGetter;
import knightminer.inspirations.tools.client.NorthCompassGetter;
import knightminer.inspirations.tools.client.PhotometerGetter;
import knightminer.inspirations.tools.enchantment.EnchantmentAxeDamage;
import knightminer.inspirations.tools.enchantment.EnchantmentAxeLooting;
import knightminer.inspirations.tools.enchantment.EnchantmentExtendedFire;
import knightminer.inspirations.tools.enchantment.EnchantmentExtendedKnockback;
import knightminer.inspirations.tools.enchantment.EnchantmentShieldProtection;
import knightminer.inspirations.tools.enchantment.EnchantmentShieldThorns;
import knightminer.inspirations.tools.entity.EntityModArrow;
import knightminer.inspirations.tools.item.ItemCrook;
import knightminer.inspirations.tools.item.ItemEnchantableShield;
import knightminer.inspirations.tools.item.ItemModArrow;
import knightminer.inspirations.tools.item.ItemRedstoneCharger;
import knightminer.inspirations.tools.item.ItemWaypointCompass;
import knightminer.inspirations.tools.recipe.WaypointCompassCopyRecipe;
import knightminer.inspirations.utility.block.BlockRedstoneCharge;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.enchantment.EnchantmentProtection.Type;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.init.Items;
import net.minecraft.init.Bootstrap.BehaviorDispenseOptional;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.registries.IForgeRegistry;

public class InspirationsTools extends PulseBase {
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

   public static final String pulseID = "InspirationsTools";
   public static CommonProxy proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new knightminer.inspirations.tools.ToolsClientProxy() : new knightminer.inspirations.common.CommonProxy();
   public static Item redstoneCharger;
   public static Item woodenCrook;
   public static Item stoneCrook;
   public static Item boneCrook;
   public static Item blazeCrook;
   public static Item witherCrook;
   public static Item northCompass;
   public static Item barometer;
   public static Item photometer;
   public static Item waypointCompass;
   public static ToolMaterial bone;
   public static ToolMaterial blaze;
   public static ToolMaterial wither;
   public static Block redstoneCharge;
   public static ItemArrow arrow;

   public InspirationsTools() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      proxy.preInit();
      if (Config.separateCrook) {
         bone = EnumHelper.addToolMaterial(Util.prefix("bone"), 1, 225, 4.0F, 1.5F, 10);
         if (Config.netherCrooks) {
            blaze = EnumHelper.addToolMaterial(Util.prefix("blaze"), 2, 300, 6.0F, 2.0F, 20);
            wither = EnumHelper.addToolMaterial(Util.prefix("wither"), 2, 375, 6.0F, 1.5F, 10);
         }
      }

   }

   public void register(EventBus bus) {
      bus.registerGeneric(Register.class, Block.class, this::registerBlocks);
      bus.registerGeneric(Register.class, Item.class, this::registerItems);
      bus.registerGeneric(Register.class, EntityEntry.class, this::registerEntities);
      bus.registerGeneric(Register.class, IRecipe.class, this::registerRecipes);
      bus.registerGeneric(Register.class, Enchantment.class, this::registerEnchantments);
   }

   public void registerBlocks(Register<Block> event) {
      IForgeRegistry<Block> r = event.getRegistry();
      if (Config.enableRedstoneCharge || Config.enableChargedArrow) {
         redstoneCharge = registerBlock(r, new BlockRedstoneCharge(), "redstone_charge");
      }

   }

   public void registerItems(Register<Item> event) {
      IForgeRegistry<Item> r = event.getRegistry();
      arrow = (ItemArrow)registerItem(r, new ItemModArrow(), "arrow");
      if (Config.enableRedstoneCharge) {
         redstoneCharger = registerItem(r, new ItemRedstoneCharger(), "redstone_charger");
      }

      if (Config.separateCrook) {
         woodenCrook = registerItem(r, new ItemCrook(ToolMaterial.WOOD), "wooden_crook");
         stoneCrook = registerItem(r, new ItemCrook(ToolMaterial.STONE), "stone_crook");
         boneCrook = registerItem(r, new ItemCrook(bone), "bone_crook");
         if (Config.netherCrooks) {
            blazeCrook = registerItem(r, new ItemCrook(blaze), "blaze_crook");
            witherCrook = registerItem(r, new ItemCrook(wither), "wither_crook");
         }
      }

      if (Config.enableNorthCompass) {
         northCompass = registerItem(r, (new Item()).setCreativeTab(CreativeTabs.TOOLS), "north_compass");
         northCompass.addPropertyOverride(new ResourceLocation("angle"), new NorthCompassGetter());
         if (Config.renameVanillaCompass) {
            Items.COMPASS.setUnlocalizedName(Util.prefix("origin_compass"));
         }
      }

      if (Config.enableBarometer) {
         barometer = registerItem(r, (new Item()).setCreativeTab(CreativeTabs.TOOLS), "barometer");
         barometer.addPropertyOverride(new ResourceLocation("height"), new BarometerGetter());
      }

      if (Config.enablePhotometer) {
         photometer = registerItem(r, (new Item()).setCreativeTab(CreativeTabs.TOOLS), "photometer");
         photometer.addPropertyOverride(new ResourceLocation("light"), new PhotometerGetter());
      }

      if (Config.enableWaypointCompass) {
         waypointCompass = registerItem(r, new ItemWaypointCompass(), "waypoint_compass");
      }

      if (Config.shieldEnchantmentTable) {
         register(r, new ItemEnchantableShield(), new ResourceLocation("shield"));
      }

   }

   public void registerEntities(Register<EntityEntry> event) {
      IForgeRegistry<EntityEntry> r = event.getRegistry();
      r.register(getEntityBuilder(EntityModArrow.class, "arrow", 0).factory(EntityModArrow::new).tracker(64, 1, false).build());
   }

   public void registerRecipes(Register<IRecipe> event) {
      IForgeRegistry<IRecipe> r = event.getRegistry();
      if (Config.copyWaypointCompass) {
         register(r, new WaypointCompassCopyRecipe(), "waypoint_compass_copy");
      }

   }

   public void registerEnchantments(Register<Enchantment> event) {
      IForgeRegistry<Enchantment> r = event.getRegistry();
      if (Config.moreShieldEnchantments) {
         EntityEquipmentSlot[] slots = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
         register(r, new EnchantmentShieldProtection(Rarity.COMMON, Type.ALL, slots), new ResourceLocation("protection"));
         register(r, new EnchantmentShieldProtection(Rarity.UNCOMMON, Type.FIRE, slots), new ResourceLocation("fire_protection"));
         register(r, new EnchantmentShieldProtection(Rarity.UNCOMMON, Type.PROJECTILE, slots), new ResourceLocation("projectile_protection"));
         register(r, new EnchantmentShieldProtection(Rarity.RARE, Type.EXPLOSION, slots), new ResourceLocation("blast_protection"));
         register(r, new EnchantmentShieldThorns(Rarity.VERY_RARE, slots), new ResourceLocation("thorns"));
      }

      if (Config.moreShieldEnchantments || Config.axeWeaponEnchants) {
         EntityEquipmentSlot[] slots = new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND};
         register(r, new EnchantmentExtendedKnockback(Rarity.UNCOMMON, slots), new ResourceLocation("knockback"));
         register(r, new EnchantmentExtendedFire(Rarity.RARE, slots), new ResourceLocation("fire_aspect"));
         if (Config.axeWeaponEnchants) {
            register(r, new EnchantmentAxeLooting(Rarity.RARE, EnumEnchantmentType.WEAPON, slots), new ResourceLocation("looting"));
         }
      }

      if (Config.axeEnchantmentTable) {
         EntityEquipmentSlot[] slots = new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND};
         register(r, new EnchantmentAxeDamage(Rarity.COMMON, 0, slots), new ResourceLocation("sharpness"));
         register(r, new EnchantmentAxeDamage(Rarity.UNCOMMON, 1, slots), new ResourceLocation("smite"));
         register(r, new EnchantmentAxeDamage(Rarity.UNCOMMON, 2, slots), new ResourceLocation("bane_of_arthropods"));
      }

   }

   public void init(FMLInitializationEvent event) {
      proxy.init();
      if (Config.separateCrook) {
         bone.setRepairItem(new ItemStack(Items.BONE));
         if (Config.netherCrooks) {
            blaze.setRepairItem(new ItemStack(Items.BLAZE_ROD));
            wither.setRepairItem(InspirationsShared.witherBone);
         }
      }

      this.registerDispenserBehavior();
   }

   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit();
      ToolsEvents.register(MinecraftForge.EVENT_BUS);
   }

   private void registerDispenserBehavior() {
      registerDispenserBehavior(arrow, new BehaviorProjectileDispense() {
         protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack stack) {
            EntityModArrow arrow = new EntityModArrow(world, position.getX(), position.getY(), position.getZ(), stack.getMetadata());
            arrow.pickupStatus = PickupStatus.ALLOWED;
            return arrow;
         }
      });
      registerDispenserBehavior(redstoneCharger, new BehaviorDispenseOptional() {
         protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            this.successful = true;
            World world = source.getWorld();
            EnumFacing facing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
            BlockPos pos = source.getBlockPos().offset(facing);
            if (InspirationsTools.redstoneCharge.canPlaceBlockAt(world, pos)) {
               world.setBlockState(pos, InspirationsTools.redstoneCharge.getDefaultState().withProperty(BlockRedstoneCharge.FACING, facing));
               if (stack.attemptDamageItem(1, world.rand, (EntityPlayerMP)null)) {
                  stack.setCount(0);
               }
            } else {
               this.successful = false;
            }

            return stack;
         }
      });
   }
}
