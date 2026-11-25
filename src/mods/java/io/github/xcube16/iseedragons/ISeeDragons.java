package io.github.xcube16.iseedragons;

import com.google.common.collect.BiMap;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.oredict.OreDictionary;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "iseedragons",
   version = "1.2.1",
   acceptableRemoteVersions = "*",
   name = "ISeeDragons"
)
public class ISeeDragons {
   public static final String MODID = "iseedragons";
   public static final String NAME = "ISeeDragons";
   public static final String VERSION = "1.2.1";
   public static final Logger logger = ISD.logger;
   @Instance("iseedragons")
   private static ISeeDragons instance;
   @SidedProxy(
      clientSide = "io.github.xcube16.iseedragons.client.ClientProxy",
      serverSide = "io.github.xcube16.iseedragons.DefaultProxy"
   )
   private static DefaultProxy proxy;
   @Nullable
   private Method dragonSetSleeping;
   @Nullable
   private Field dragonCurrentAnimation;
   @Nullable
   private Field dragonAnimationTick;
   @Nullable
   private Field dragon_ANIMATION_SHAKEPREY;
   @Nullable
   private Field cyclopsCurrentAnimation;
   @Nullable
   private Field cyclopsAnimationTick;
   @Nullable
   private Field cyclops_ANIMATION_EATPLAYER;
   private static Map<Block, Integer> dropChances;
   private static Map<Block, Integer> effectChances;
   private static final Set<ResourceLocation> unstoneableEntitys = new HashSet();
   private Map<Item, Float> extraUndeadDamage;

   public ISeeDragons() {
   }

   @EventHandler
   public void preinit(FMLPreInitializationEvent event) {
      MinecraftForge.EVENT_BUS.register(this);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      this.loadConfig();
      logger.info("Fixing sea serpent armor repair...");

      try {
         Class<?> enumSeaSerpent = Class.forName("com.github.alexthe666.iceandfire.enums.EnumSeaSerpent");
         Object[] values = enumSeaSerpent.getDeclaredMethod("values").invoke((Object)null);
         Field armorMaterialField = enumSeaSerpent.getDeclaredField("armorMaterial");
         Field scaleField = enumSeaSerpent.getDeclaredField("scale");

         for(Object serpent : values) {
            ArmorMaterial material = (ArmorMaterial)armorMaterialField.get(serpent);
            material.setRepairItem(new ItemStack((Item)scaleField.get(serpent)));
            logger.info("Fixed " + material);
         }
      } catch (ClassNotFoundException var12) {
         logger.warn("Could not find " + var12.getMessage() + " while trying to fix sea serpent armor");
      } catch (Exception var13) {
         logger.error("Failed to fix sea serpent armor", var13);
      }

      logger.info("Fixing tools/armor repair listed in config...");
      StaticConfig.repairFixes.forEach((toolId, repairItem) -> {
         String[] split = repairItem.split(",");
         if (split.length == 1) {
            this.fixToolRepair(toolId, split[0], 0);
         } else if (split.length == 2) {
            this.fixToolRepair(toolId, split[0], Integer.parseInt(split[1]));
         } else {
            logger.error("Bad item string " + repairItem);
         }

      });
      if (StaticConfig.preventTANAttackEntityEvent && Loader.isModLoaded("toughasnails")) {
         logger.info("Fixing TAN's attack entity event damage problem...");
         boolean found_listener = false;
         ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners = (ConcurrentHashMap)ReflectionHelper.getPrivateValue(EventBus.class, MinecraftForge.EVENT_BUS, new String[]{"listeners"});

         for(Entry<Object, ArrayList<IEventListener>> listener_entry : listeners.entrySet()) {
            if (listener_entry.getKey().getClass().getName().equals("toughasnails.handler.thirst.ThirstStatHandler")) {
               found_listener = true;

               try {
                  MinecraftForge.EVENT_BUS.register(new FakeThirstStatHandler(listener_entry.getKey()));
               } catch (Exception var11) {
                  logger.error("Failed to initialize FakeThirstStatHandler");
                  var11.printStackTrace();
                  break;
               }

               MinecraftForge.EVENT_BUS.unregister(listener_entry.getKey());
               break;
            }
         }

         if (!found_listener) {
            logger.error("Could not find toughasnails ThirstStatHandler event listener");
         }
      }

   }

   private void fixToolRepair(String toolId, String repairItemId, int meta) {
      try {
         Item tool = Item.getByNameOrId(toolId);
         if (tool == null) {
            logger.info("Could not find " + toolId + ", ignoring");
            return;
         }

         if (tool instanceof ItemTool) {
            Field toolMaterialField = ItemTool.class.getDeclaredField("toolMaterial");
            toolMaterialField.setAccessible(true);
            Object toolMaterial = toolMaterialField.get(tool);
            if (toolMaterial instanceof ToolMaterial) {
               Item repairItem = Item.getByNameOrId(repairItemId);
               if (repairItem != null) {
                  ((ToolMaterial)toolMaterial).setRepairItem(new ItemStack(repairItem, 1, meta));
                  logger.info(toolId + " can now be repaired with " + repairItemId);
               } else {
                  logger.error(repairItemId + " does not exist! Failed to fix " + toolId + " repair!");
               }
            } else {
               logger.error(toolId + " has a bad tool material of " + toolMaterial);
            }
         } else if (tool instanceof ItemArmor) {
            Field materialField = ItemArmor.class.getDeclaredField("material");
            materialField.setAccessible(true);
            Object toolMaterial = materialField.get(tool);
            if (toolMaterial instanceof ArmorMaterial) {
               Item repairItem = Item.getByNameOrId(repairItemId);
               if (repairItem != null) {
                  ((ArmorMaterial)toolMaterial).setRepairItem(new ItemStack(repairItem, 1, meta));
                  logger.info(toolId + " can now be repaired with " + repairItemId);
               } else {
                  logger.error(repairItemId + " does not exist! Failed to fix " + toolId + " repair!");
               }
            } else {
               logger.error(toolId + " has a bad armor material of " + toolMaterial);
            }
         } else if (tool instanceof ItemSword) {
            Field materialField = ItemSword.class.getDeclaredField("material");
            materialField.setAccessible(true);
            Object toolMaterial = materialField.get(tool);
            if (toolMaterial instanceof ToolMaterial) {
               Item repairItem = Item.getByNameOrId(repairItemId);
               if (repairItem != null) {
                  ((ToolMaterial)toolMaterial).setRepairItem(new ItemStack(repairItem, 1, meta));
                  logger.info(toolId + " can now be repaired with " + repairItemId);
               } else {
                  logger.error(repairItemId + " does not exist! Failed to fix " + toolId + " repair!");
               }
            } else {
               logger.error(toolId + " has a bad sword material of " + toolMaterial);
            }
         } else {
            logger.info(toolId + " is not a tool, armor, or sword");
         }
      } catch (Exception var8) {
         logger.error("Critical error while fixing tool/armor repair for " + toolId, var8);
      }

   }

   @SubscribeEvent
   public void lightningStruckEntity(EntityStruckByLightningEvent event) {
      if (StaticConfig.disableLightningItemDamage && event.getEntity() instanceof EntityItem) {
         event.setCanceled(true);
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void registerItems(Register<Item> event) {
      logger.info("Fixing Ice and Fire axes");
      OreDictionary.registerOre("toolAxe", Item.getByNameOrId("iceandfire:dragonbone_axe"));
      OreDictionary.registerOre("toolAxe", Item.getByNameOrId("iceandfire:myrmex_desert_axe"));
      OreDictionary.registerOre("toolAxe", Item.getByNameOrId("iceandfire:myrmex_jungle_axe"));
      OreDictionary.registerOre("toolAxe", Item.getByNameOrId("iceandfire:silver_axe"));
      logger.info("Fixing broken Ice and Fire OreDictionary entries. (all that ore dictionary spam that you can now ignore!)");
      OreDictionary.registerOre("ingotSilver", Item.getByNameOrId("iceandfire:silver_ingot"));
      OreDictionary.registerOre("nuggetSilver", Item.getByNameOrId("iceandfire:silver_nugget"));
      OreDictionary.registerOre("oreSilver", Item.getByNameOrId("iceandfire:silver_ore"));
      OreDictionary.registerOre("blockSilver", Item.getByNameOrId("iceandfire:silver_block"));
      OreDictionary.registerOre("gemSapphire", Item.getByNameOrId("iceandfire:sapphire_gem"));
      OreDictionary.registerOre("oreSapphire", Item.getByNameOrId("iceandfire:sapphire_ore"));
      OreDictionary.registerOre("blockSapphire", Item.getByNameOrId("iceandfire:sapphire_block"));
      OreDictionary.registerOre("boneWither", Item.getByNameOrId("iceandfire:witherbone"));
      OreDictionary.registerOre("boneWithered", Item.getByNameOrId("iceandfire:witherbone"));
      OreDictionary.registerOre("boneDragon", Item.getByNameOrId("iceandfire:dragonbone"));

      try {
         Class<?> enumSeaSerpent = Class.forName("com.github.alexthe666.iceandfire.enums.EnumSeaSerpent");
         Object[] values = enumSeaSerpent.getDeclaredMethod("values").invoke((Object)null);
         Field scaleField = enumSeaSerpent.getDeclaredField("scale");

         for(Object serpent : values) {
            OreDictionary.registerOre("seaSerpentScales", (Item)scaleField.get(serpent));
         }
      } catch (ClassNotFoundException var9) {
         logger.warn("Could not find " + var9.getMessage() + " while trying to fix sea serpent scales ore dictionary");
      } catch (Exception var10) {
         logger.error("Failed register sea serpent scales in ore dictionary", var10);
      }

      this.registerEgg(Item.getByNameOrId("iceandfire:hippogryph_egg"));
      this.registerEgg(Item.getByNameOrId("iceandfire:myrmex_jungle_egg"));
      this.registerEgg(Item.getByNameOrId("iceandfire:myrmex_desert_egg"));
      logger.info("Done fixing Ice and Fire ore dictionary");
   }

   @SubscribeEvent
   public void onLivingHurt(LivingHurtEvent e) {
      DamageSource source = e.getSource();
      if (e.getEntityLiving().getCreatureAttribute() == EnumCreatureAttribute.UNDEAD && !source.isExplosion() && !source.isFireDamage() && !source.isMagicDamage() && !source.isProjectile() && (source.getDamageType().equals("player") || source.getDamageType().equals("mob"))) {
         if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityLivingBase) {
            ItemStack weapon = ((EntityLivingBase)source.getTrueSource()).getHeldItemMainhand();
            if (!weapon.isEmpty()) {
               Float extra = (Float)this.extraUndeadDamage.get(weapon.getItem());
               if (extra != null) {
                  e.setAmount(e.getAmount() + extra.floatValue());
               }
            }
         }

      }
   }

   @SubscribeEvent
   public void onConfigChange(OnConfigChangedEvent e) {
      if (e.getModID().equals("iseedragons")) {
         ConfigManager.sync("iseedragons", Type.INSTANCE);
         this.loadConfig();
      }

   }

   private void loadConfig() {
      dropChances = this.loadBlockChanceMapping(StaticConfig.dropChances);
      effectChances = this.loadBlockChanceMapping(StaticConfig.effectChances);
      Map<Item, Float> extraUndeadDamage = new HashMap();

      for(Entry<String, Float> entry : StaticConfig.extraUndeadDamage.entrySet()) {
         Item weapon = Item.getByNameOrId((String)entry.getKey());
         if (weapon != Items.AIR) {
            extraUndeadDamage.put(weapon, entry.getValue());
         } else {
            logger.warn((String)entry.getKey() + " was not found!");
         }
      }

      this.extraUndeadDamage = extraUndeadDamage;
      if (StaticConfig.minBrightness > StaticConfig.maxBrightness) {
         logger.error("Min and max brightness are mixed up!");
         float min = StaticConfig.minBrightness;
         StaticConfig.minBrightness = StaticConfig.maxBrightness;
         StaticConfig.maxBrightness = min;
      }

      proxy.setBrightness(StaticConfig.minBrightness, StaticConfig.maxBrightness);
      unstoneableEntitys.clear();

      for(String str : StaticConfig.unstoneableEntitys) {
         unstoneableEntitys.add(new ResourceLocation(str));
      }

      logger.info("Scanning for Ice and Fire dragons...");
      boolean foundOne = false;

      try {
         Field regField = EntityRegistry.instance().getClass().getDeclaredField("entityClassRegistrations");
         regField.setAccessible(true);
         BiMap<Class<? extends Entity>, EntityRegistration> reg = (BiMap)regField.get(EntityRegistry.instance());

         for(EntityRegistration entity : reg.values()) {
            Optional<Integer> boost = this.getRenderBoost(entity.getRegistryName());
            if (boost.isPresent()) {
               foundOne = true;
               logger.info("Fixed " + entity.getRegistryName() + " tracking distance");
               Field rangeField = entity.getClass().getDeclaredField("trackingRange");
               rangeField.setAccessible(true);
               rangeField.set(entity, boost.get());
            }
         }
      } catch (Exception var9) {
         logger.error("Failed to fix Ice and Fire entity tracking distance", var9);
      }

      if (!foundOne) {
         logger.warn("No Ice and Fire dragons found! Is it installed?");
      }

   }

   private Map<Block, Integer> loadBlockChanceMapping(Map<String, Integer> input) {
      Map<Block, Integer> map = new HashMap();

      for(Entry<String, Integer> entry : input.entrySet()) {
         Block block = (Block)Block.REGISTRY.getObject(new ResourceLocation((String)entry.getKey()));
         if (block == Blocks.AIR) {
            logger.warn("Could not find block \"" + (String)entry.getKey() + "\", ignoring!");
         } else {
            map.put(block, Integer.valueOf(Math.min(Math.max(((Integer)entry.getValue()).intValue(), 0), 100)));
         }
      }

      return map;
   }

   public static boolean dragonBreakBlockHook(World world, BlockPos pos, IBlockState state) {
      Block block = state.getBlock();
      int chance = ((Integer)Optional.ofNullable(dropChances.get(block)).orElse(StaticConfig.defaultDropChance)).intValue();
      boolean shouldDrop = true;
      if (chance == 0) {
         shouldDrop = false;
      } else if (chance != 100) {
         shouldDrop = world.rand.nextInt(100) < chance;
      }

      if (!block.isAir(state, world, pos)) {
         chance = ((Integer)Optional.ofNullable(effectChances.get(block)).orElse(StaticConfig.defaultEffectChance)).intValue();
         if (chance == 100 || chance != 0 && world.rand.nextInt(100) < chance) {
            world.playEvent(2001, pos, Block.getStateId(state));
         }

         if (shouldDrop) {
            block.dropBlockAsItem(world, pos, state, 0);
         }

         world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
      }

      return true;
   }

   public static void dragonShakePrayHook(EntityTameable dragon, Entity prey, int animTick) {
      if (!dragon.world.isRemote) {
         if (animTick == 56) {
            prey.attackEntityFrom(DamageSource.causeMobDamage(dragon), prey instanceof EntityPlayer ? 17.0F : (float)dragon.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * 4.0F);
         } else if (animTick > 60) {
            prey.dismountRidingEntity();
         }

      }
   }

   public static boolean canPlayerDismountHook(EntityPlayer player) {
      Entity mount = player.getRidingEntity();
      return mount != null ? instance.canDismount(mount) : true;
   }

   public static ResourceLocation[] cleanAdvancementRequardsHook(ResourceLocation[] craftable) {
      return (ResourceLocation[])Arrays.stream(craftable).filter((item) -> {
         return CraftingManager.getRecipe(item) != null;
      }).toArray((x$0) -> {
         return new ResourceLocation[x$0];
      });
   }

   public static boolean iceAndFireGenerateHook(World world) {
      return Arrays.stream(StaticConfig.generatorBlacklist).noneMatch((i) -> {
         return i == world.provider.getDimension();
      });
   }

   public static boolean canStoneHook(Entity entity) {
      ResourceLocation id = EntityList.getKey(entity);
      if (id == null) {
         return false;
      } else {
         return !unstoneableEntitys.contains(id);
      }
   }

   private void registerEgg(Item egg) {
      OreDictionary.registerOre("listAllEgg", new ItemStack(egg, 1, 32767));
      OreDictionary.registerOre("objectEgg", new ItemStack(egg, 1, 32767));
      OreDictionary.registerOre("bakingEgg", new ItemStack(egg, 1, 32767));
      OreDictionary.registerOre("egg", new ItemStack(egg, 1, 32767));
      OreDictionary.registerOre("ingredientEgg", new ItemStack(egg, 1, 32767));
      OreDictionary.registerOre("foodSimpleEgg", new ItemStack(egg, 1, 32767));
   }

   @SubscribeEvent
   public void livingEntityAttacked(LivingAttackEvent e) {
      if (this.isDragon(e.getEntity()) && e.getAmount() > 0.0F) {
         try {
            if (this.dragonSetSleeping == null) {
               this.dragonSetSleeping = this.getMethodInHierarchy(e.getEntity().getClass(), "setSleeping", Boolean.TYPE);
               this.dragonSetSleeping.setAccessible(true);
            }

            this.dragonSetSleeping.invoke(e.getEntity(), false);
         } catch (Exception var3) {
            logger.error("Failed to wake up Ice and Fire dragon", var3);
         }
      }

   }

   @SubscribeEvent
   public void onDismount(EntityMountEvent e) {
      if (!e.isCanceled() && e.getEntityMounting() instanceof EntityPlayer) {
         if (e.isDismounting()) {
            if (!this.canDismount(e.getEntityBeingMounted())) {
               e.setCanceled(true);
            } else {
               ((EntityPlayer)e.getEntityMounting()).setJumping(false);
               this.handleDismount(e.getEntityBeingMounted(), e.getEntityMounting());
            }
         } else {
            Entity previousMount = e.getEntityMounting().getRidingEntity();
            if (previousMount != null) {
               if (!this.canDismount(previousMount)) {
                  e.setCanceled(true);
               } else {
                  this.handleDismount(previousMount, e.getEntityMounting());
               }
            }
         }
      }

   }

   private boolean canDismount(Entity dragon) {
      if (!dragon.isDead && (!(dragon instanceof EntityLivingBase) || ((EntityLivingBase)dragon).getHealth() > 0.0F)) {
         if (this.isDragon(dragon)) {
            try {
               if (this.dragonCurrentAnimation == null) {
                  this.dragonCurrentAnimation = this.getFieldInHierarchy(dragon.getClass(), "currentAnimation");
                  this.dragonCurrentAnimation.setAccessible(true);
               }

               if (this.dragonAnimationTick == null) {
                  this.dragonAnimationTick = this.getFieldInHierarchy(dragon.getClass(), "animationTick");
                  this.dragonAnimationTick.setAccessible(true);
               }

               if (this.dragon_ANIMATION_SHAKEPREY == null) {
                  this.dragon_ANIMATION_SHAKEPREY = this.getFieldInHierarchy(dragon.getClass(), "ANIMATION_SHAKEPREY");
               }

               Object animation = this.dragonCurrentAnimation.get(dragon);
               Object aniShakePray = this.dragon_ANIMATION_SHAKEPREY.get(dragon);
               int aniTick = ((Integer)this.dragonAnimationTick.get(dragon)).intValue();
               if (animation == aniShakePray && aniTick <= 60) {
                  return false;
               }
            } catch (Exception var6) {
               logger.error("Failed to check dragon state", var6);
            }
         } else if (this.isCyclops(dragon)) {
            try {
               if (this.cyclopsCurrentAnimation == null) {
                  this.cyclopsCurrentAnimation = this.getFieldInHierarchy(dragon.getClass(), "currentAnimation");
                  this.cyclopsCurrentAnimation.setAccessible(true);
               }

               if (this.cyclopsAnimationTick == null) {
                  this.cyclopsAnimationTick = this.getFieldInHierarchy(dragon.getClass(), "animationTick");
                  this.cyclopsAnimationTick.setAccessible(true);
               }

               if (this.cyclops_ANIMATION_EATPLAYER == null) {
                  this.cyclops_ANIMATION_EATPLAYER = this.getFieldInHierarchy(dragon.getClass(), "ANIMATION_EATPLAYER");
               }

               Object animation = this.cyclopsCurrentAnimation.get(dragon);
               Object aniShakePray = this.cyclops_ANIMATION_EATPLAYER.get(dragon);
               int aniTick = ((Integer)this.cyclopsAnimationTick.get(dragon)).intValue();
               if (animation == aniShakePray && aniTick < 32) {
                  return false;
               }
            } catch (Exception var5) {
               logger.error("Failed to check cyclops state", var5);
            }
         }

         return true;
      } else {
         return true;
      }
   }

   private void handleDismount(Entity mount, Entity rider) {
      if (!mount.isDead && (!(mount instanceof EntityLivingBase) || ((EntityLivingBase)mount).getHealth() > 0.0F)) {
         if (this.isDragon(mount)) {
            try {
               if (this.dragonCurrentAnimation == null) {
                  this.dragonCurrentAnimation = this.getFieldInHierarchy(mount.getClass(), "currentAnimation");
                  this.dragonCurrentAnimation.setAccessible(true);
               }

               if (this.dragon_ANIMATION_SHAKEPREY == null) {
                  this.dragon_ANIMATION_SHAKEPREY = this.getFieldInHierarchy(mount.getClass(), "ANIMATION_SHAKEPREY");
               }

               Object animation = this.dragonCurrentAnimation.get(mount);
               Object aniShakePray = this.dragon_ANIMATION_SHAKEPREY.get(mount);
               if (animation == aniShakePray && !mount.world.isRemote) {
                  float var5 = mount.getRotationYawHead();
               }
            } catch (Exception var6) {
               var6.printStackTrace();
               logger.error("Failed to check dragon state", var6);
            }
         }

      }
   }

   private Optional<Integer> getRenderBoost(@Nullable ResourceLocation id) {
      return id == null ? Optional.empty() : Optional.ofNullable(StaticConfig.distanceOverrides.get(id.toString()));
   }

   private boolean isDragon(Entity entity) {
      ResourceLocation id = EntityList.getKey(entity);
      if (id == null) {
         return false;
      } else {
         return id.getResourceDomain().equals("iceandfire") && (id.getResourcePath().equals("icedragon") || id.getResourcePath().equals("firedragon"));
      }
   }

   private boolean isCyclops(Entity entity) {
      ResourceLocation id = EntityList.getKey(entity);
      if (id == null) {
         return false;
      } else {
         return id.getResourceDomain().equals("iceandfire") && id.getResourcePath().equals("cyclops");
      }
   }

   private Method getMethodInHierarchy(Class<?> clazz, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
      try {
         return clazz.getDeclaredMethod(name, parameterTypes);
      } catch (NoSuchMethodException var5) {
         if (clazz.getSuperclass() != null) {
            return this.getMethodInHierarchy(clazz.getSuperclass(), name, parameterTypes);
         } else {
            throw var5;
         }
      }
   }

   private Field getFieldInHierarchy(Class<?> clazz, String name) throws NoSuchFieldException {
      try {
         return clazz.getDeclaredField(name);
      } catch (NoSuchFieldException var4) {
         if (clazz.getSuperclass() != null) {
            return this.getFieldInHierarchy(clazz.getSuperclass(), name);
         } else {
            throw var4;
         }
      }
   }
}
