package com.yyon.grapplinghook;

import com.google.common.base.Predicate;
import com.yyon.grapplinghook.blocks.BlockGrappleModifier;
import com.yyon.grapplinghook.blocks.TileEntityGrappleModifier;
import com.yyon.grapplinghook.controllers.grappleController;
import com.yyon.grapplinghook.enchantments.DoublejumpEnchantment;
import com.yyon.grapplinghook.enchantments.SlidingEnchantment;
import com.yyon.grapplinghook.enchantments.WallrunEnchantment;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.items.KeypressItem;
import com.yyon.grapplinghook.items.LongFallBoots;
import com.yyon.grapplinghook.items.grappleBow;
import com.yyon.grapplinghook.items.launcherItem;
import com.yyon.grapplinghook.items.repeller;
import com.yyon.grapplinghook.items.alternategrapple.DoubleMotorHook;
import com.yyon.grapplinghook.items.alternategrapple.EnderHook;
import com.yyon.grapplinghook.items.alternategrapple.MagnetHook;
import com.yyon.grapplinghook.items.alternategrapple.MotorHook;
import com.yyon.grapplinghook.items.alternategrapple.RocketDoubleMotorHook;
import com.yyon.grapplinghook.items.alternategrapple.RocketHook;
import com.yyon.grapplinghook.items.alternategrapple.SmartHook;
import com.yyon.grapplinghook.items.upgrades.BaseUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.DoubleUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.ForcefieldUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.LimitsUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.MagnetUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.MotorUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.RocketUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.RopeUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.StaffUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.SwingUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.ThrowUpgradeItem;
import com.yyon.grapplinghook.network.DetachSingleHookMessage;
import com.yyon.grapplinghook.network.GrappleAttachMessage;
import com.yyon.grapplinghook.network.GrappleAttachPosMessage;
import com.yyon.grapplinghook.network.GrappleDetachMessage;
import com.yyon.grapplinghook.network.GrappleEndMessage;
import com.yyon.grapplinghook.network.GrappleModifierMessage;
import com.yyon.grapplinghook.network.KeypressMessage;
import com.yyon.grapplinghook.network.LoggedInMessage;
import com.yyon.grapplinghook.network.PlayerMovementMessage;
import com.yyon.grapplinghook.network.SegmentMessage;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
   modid = "grapplemod",
   version = "1.12.2-v12.3"
)
public class grapplemod {
   public static final String MODID = "grapplemod";
   public static final String VERSION = "1.12.2-v12.3";
   public static Item grapplebowitem;
   public static Item motorhookitem;
   public static Item smarthookitem;
   public static Item doublemotorhookitem;
   public static Item rocketdoublemotorhookitem;
   public static Item enderhookitem;
   public static Item magnethookitem;
   public static Item rockethookitem;
   public static Item launcheritem;
   public static Item repelleritem;
   public static Item baseupgradeitem;
   public static Item doubleupgradeitem;
   public static Item forcefieldupgradeitem;
   public static Item magnetupgradeitem;
   public static Item motorupgradeitem;
   public static Item ropeupgradeitem;
   public static Item staffupgradeitem;
   public static Item swingupgradeitem;
   public static Item throwupgradeitem;
   public static Item limitsupgradeitem;
   public static Item rocketupgradeitem;
   public static Item longfallboots;
   public static final EnumEnchantmentType GRAPPLEENCHANTS_FEET = EnumHelper.addEnchantmentType("GRAPPLEENCHANTS_FEET", (item) -> {
      return item instanceof ItemArmor && ((ItemArmor)item).armorType == EntityEquipmentSlot.FEET;
   });
   public static WallrunEnchantment wallrunenchantment;
   public static DoublejumpEnchantment doublejumpenchantment;
   public static SlidingEnchantment slidingenchantment;
   public static Object instance;
   public static SimpleNetworkWrapper network;
   public static HashMap<Integer, grappleController> controllers = new HashMap();
   public static HashMap<BlockPos, grappleController> controllerpos = new HashMap();
   public static HashSet<Integer> attached = new HashSet();
   public static HashMap<Integer, HashSet<grappleArrow>> allarrows = new HashMap();
   private static int controllerid = 0;
   public static int GRAPPLEID = controllerid++;
   public static int REPELID = controllerid++;
   public static int AIRID = controllerid++;
   public static boolean anyblocks = true;
   public static HashSet<Block> grapplingblocks;
   public static boolean removeblocks = false;
   public static HashSet<Block> grapplingbreaksblocks;
   public static boolean anybreakblocks = false;
   public static HashSet<Block> grapplingignoresblocks;
   public static boolean anyignoresblocks = false;
   public static Block blockGrappleModifier;
   public static ItemBlock itemBlockGrappleModifier;
   public ResourceLocation resourceLocation;
   public static final CreativeTabs tabGrapplemod = new CreativeTabs("tabGrapplemod") {
      public void displayAllRelevantItems(NonNullList<ItemStack> items) {
         super.displayAllRelevantItems(items);
         final Item[] allitems = grapplemod.getAllItems();
         Collections.sort(items, new Comparator<ItemStack>() {
            public int compare(ItemStack arg0, ItemStack arg1) {
               return this.getIndex(arg0.getItem()) - this.getIndex(arg1.getItem());
            }

            public int getIndex(Item item) {
               int i = 0;

               for(Item item2 : allitems) {
                  if (item == item2) {
                     return i;
                  }

                  ++i;
               }

               return i;
            }
         });
      }

      public ItemStack getTabIconItem() {
         return new ItemStack(grapplemod.grapplebowitem);
      }
   };
   @SidedProxy(
      clientSide = "com.yyon.grapplinghook.ClientProxyClass",
      serverSide = "com.yyon.grapplinghook.ServerProxyClass"
   )
   public static CommonProxyClass proxy;
   int entityID = 0;

   public grapplemod() {
   }

   @EventHandler
   public void load(FMLInitializationEvent event) {
   }

   public void registerRenderers() {
   }

   public void generateNether(World world, EaglercraftRandom random, int chunkX, int chunkZ) {
   }

   public void generateSurface(World world, EaglercraftRandom random, int chunkX, int chunkZ) {
   }

   public int addFuel(ItemStack fuel) {
      return 0;
   }

   public void serverLoad(FMLServerStartingEvent event) {
   }

   public static HashSet<Block> stringToBlocks(String s) {
      HashSet<Block> blocks = new HashSet();
      if (!s.equals("") && !s.equals("none") && !s.equals("any")) {
         String[] blockstr = s.split(",");

         for(String str : blockstr) {
            str = str.trim();
            String modid;
            String name;
            if (str.contains(":")) {
               String[] splitstr = str.split(":");
               modid = splitstr[0];
               name = splitstr[1];
            } else {
               modid = "minecraft";
               name = str;
            }

            Block b = (Block)Block.REGISTRY.getObject(new ResourceLocation(modid, name));
            blocks.add(b);
         }

         return blocks;
      } else {
         return blocks;
      }
   }

   public static void updateGrapplingBlocks() {
      String s = GrappleConfig.getconf().grapplingBlocks;
      if (!s.equals("any") && !s.equals("")) {
         anyblocks = false;
         removeblocks = false;
      } else {
         s = GrappleConfig.getconf().grapplingNonBlocks;
         if (!s.equals("none") && !s.equals("")) {
            anyblocks = false;
            removeblocks = true;
         } else {
            anyblocks = true;
         }
      }

      if (!anyblocks) {
         grapplingblocks = stringToBlocks(s);
      }

      grapplingbreaksblocks = stringToBlocks(GrappleConfig.getconf().grappleBreakBlocks);
      anybreakblocks = grapplingbreaksblocks.size() != 0;
      grapplingignoresblocks = stringToBlocks(GrappleConfig.getconf().grappleIgnoreBlocks);
      anyignoresblocks = grapplingignoresblocks.size() != 0;
   }

   @SubscribeEvent
   public void registerItems(Register<Item> event) {
      event.getRegistry().registerAll(getAllItems());
   }

   public static Item[] getAllItems() {
      return new Item[]{grapplebowitem, itemBlockGrappleModifier, enderhookitem, magnethookitem, rockethookitem, motorhookitem, smarthookitem, doublemotorhookitem, rocketdoublemotorhookitem, launcheritem, repelleritem, longfallboots, baseupgradeitem, ropeupgradeitem, throwupgradeitem, motorupgradeitem, swingupgradeitem, staffupgradeitem, forcefieldupgradeitem, magnetupgradeitem, doubleupgradeitem, rocketupgradeitem, limitsupgradeitem};
   }

   @SubscribeEvent
   public void registerEnchantments(Register<Enchantment> event) {
      event.getRegistry().registerAll(new Enchantment[]{wallrunenchantment, doublejumpenchantment, slidingenchantment});
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      grapplebowitem = new grappleBow();
      grapplebowitem.setRegistryName("grapplinghook");
      motorhookitem = new MotorHook();
      motorhookitem.setRegistryName("motorhook");
      smarthookitem = new SmartHook();
      smarthookitem.setRegistryName("smarthook");
      doublemotorhookitem = new DoubleMotorHook();
      doublemotorhookitem.setRegistryName("doublemotorhook");
      rocketdoublemotorhookitem = new RocketDoubleMotorHook();
      rocketdoublemotorhookitem.setRegistryName("rocketdoublemotorhook");
      enderhookitem = new EnderHook();
      enderhookitem.setRegistryName("enderhook");
      magnethookitem = new MagnetHook();
      magnethookitem.setRegistryName("magnethook");
      rockethookitem = new RocketHook();
      rockethookitem.setRegistryName("rockethook");
      launcheritem = new launcherItem();
      launcheritem.setRegistryName("launcheritem");
      longfallboots = new LongFallBoots(ArmorMaterial.DIAMOND, 3);
      longfallboots.setRegistryName("longfallboots");
      repelleritem = new repeller();
      repelleritem.setRegistryName("repeller");
      baseupgradeitem = new BaseUpgradeItem();
      baseupgradeitem.setRegistryName("baseupgradeitem");
      doubleupgradeitem = new DoubleUpgradeItem();
      doubleupgradeitem.setRegistryName("doubleupgradeitem");
      doubleupgradeitem.setContainerItem(doubleupgradeitem);
      forcefieldupgradeitem = new ForcefieldUpgradeItem();
      forcefieldupgradeitem.setRegistryName("forcefieldupgradeitem");
      forcefieldupgradeitem.setContainerItem(forcefieldupgradeitem);
      magnetupgradeitem = new MagnetUpgradeItem();
      magnetupgradeitem.setRegistryName("magnetupgradeitem");
      magnetupgradeitem.setContainerItem(magnetupgradeitem);
      motorupgradeitem = new MotorUpgradeItem();
      motorupgradeitem.setRegistryName("motorupgradeitem");
      motorupgradeitem.setContainerItem(motorupgradeitem);
      ropeupgradeitem = new RopeUpgradeItem();
      ropeupgradeitem.setRegistryName("ropeupgradeitem");
      ropeupgradeitem.setContainerItem(ropeupgradeitem);
      staffupgradeitem = new StaffUpgradeItem();
      staffupgradeitem.setRegistryName("staffupgradeitem");
      staffupgradeitem.setContainerItem(staffupgradeitem);
      swingupgradeitem = new SwingUpgradeItem();
      swingupgradeitem.setRegistryName("swingupgradeitem");
      swingupgradeitem.setContainerItem(swingupgradeitem);
      throwupgradeitem = new ThrowUpgradeItem();
      throwupgradeitem.setRegistryName("throwupgradeitem");
      throwupgradeitem.setContainerItem(throwupgradeitem);
      limitsupgradeitem = new LimitsUpgradeItem();
      limitsupgradeitem.setRegistryName("limitsupgradeitem");
      limitsupgradeitem.setContainerItem(limitsupgradeitem);
      rocketupgradeitem = new RocketUpgradeItem();
      rocketupgradeitem.setRegistryName("rocketupgradeitem");
      rocketupgradeitem.setContainerItem(rocketupgradeitem);
      wallrunenchantment = new WallrunEnchantment();
      wallrunenchantment.setRegistryName("wallrunenchantment");
      doublejumpenchantment = new DoublejumpEnchantment();
      doublejumpenchantment.setRegistryName("doublejumpenchantment");
      slidingenchantment = new SlidingEnchantment();
      slidingenchantment.setRegistryName("slidingenchantment");
      this.resourceLocation = new ResourceLocation("grapplemod", "grapplemod");
      this.registerEntity(grappleArrow.class, "grappleArrow");
      network = NetworkRegistry.INSTANCE.newSimpleChannel("grapplemodchannel");
      byte id = 0;
      network.registerMessage(PlayerMovementMessage.Handler.class, PlayerMovementMessage.class, id++, Side.SERVER);
      network.registerMessage(GrappleAttachMessage.Handler.class, GrappleAttachMessage.class, id++, Side.CLIENT);
      network.registerMessage(GrappleEndMessage.Handler.class, GrappleEndMessage.class, id++, Side.SERVER);
      network.registerMessage(GrappleDetachMessage.Handler.class, GrappleDetachMessage.class, id++, Side.CLIENT);
      network.registerMessage(DetachSingleHookMessage.Handler.class, DetachSingleHookMessage.class, id++, Side.CLIENT);
      network.registerMessage(GrappleAttachPosMessage.Handler.class, GrappleAttachPosMessage.class, id++, Side.CLIENT);
      network.registerMessage(SegmentMessage.Handler.class, SegmentMessage.class, id++, Side.CLIENT);
      network.registerMessage(GrappleModifierMessage.Handler.class, GrappleModifierMessage.class, id++, Side.SERVER);
      network.registerMessage(LoggedInMessage.Handler.class, LoggedInMessage.class, id++, Side.CLIENT);
      network.registerMessage(KeypressMessage.Handler.class, KeypressMessage.class, id++, Side.SERVER);
      blockGrappleModifier = (BlockGrappleModifier)(new BlockGrappleModifier()).setUnlocalizedName("block_grapple_modifier");
      blockGrappleModifier.setHardness(10.0F);
      blockGrappleModifier.setRegistryName("block_grapple_modifier");
      ForgeRegistries.BLOCKS.register(blockGrappleModifier);
      itemBlockGrappleModifier = new ItemBlock(blockGrappleModifier);
      itemBlockGrappleModifier.setRegistryName(blockGrappleModifier.getRegistryName());
      GameRegistry.registerTileEntity(TileEntityGrappleModifier.class, new ResourceLocation("grapplemod", "tile_entity_grapple_modifier"));
      MinecraftForge.EVENT_BUS.register(this);
      proxy.preInit(event);
      tabGrapplemod.setRelevantEnchantmentTypes(new EnumEnchantmentType[]{GRAPPLEENCHANTS_FEET});
   }

   @EventHandler
   public void Init(FMLInitializationEvent event) {
      proxy.init(event, this);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit(event);
      updateGrapplingBlocks();
   }

   public void registerEntity(Class<? extends Entity> entityClass, String name) {
      EntityRegistry.registerModEntity(this.resourceLocation, entityClass, name, this.entityID++, this, 900, 1, true);
   }

   public static void registerController(int entityId, grappleController controller) {
      if (controllers.containsKey(Integer.valueOf(entityId))) {
         ((grappleController)controllers.get(Integer.valueOf(entityId))).unattach();
      }

      controllers.put(Integer.valueOf(entityId), controller);
   }

   public static void unregisterController(int entityId) {
      controllers.remove(Integer.valueOf(entityId));
   }

   public static void receiveGrappleDetach(int id) {
      grappleController controller = (grappleController)controllers.get(Integer.valueOf(id));
      if (controller != null) {
         controller.receiveGrappleDetach();
      } else {
         System.out.println("Couldn't find controller");
      }

   }

   public static void receiveGrappleDetachHook(int id, int hookid) {
      grappleController controller = (grappleController)controllers.get(Integer.valueOf(id));
      if (controller != null) {
         controller.receiveGrappleDetachHook(hookid);
      } else {
         System.out.println("Couldn't find controller");
      }

   }

   public static void receiveEnderLaunch(int id, double x, double y, double z) {
      grappleController controller = (grappleController)controllers.get(Integer.valueOf(id));
      if (controller != null) {
         controller.receiveEnderLaunch(x, y, z);
      } else {
         System.out.println("Couldn't find controller");
      }

   }

   public static void sendtocorrectclient(IMessage message, int playerid, World w) {
      Entity entity = w.getEntityByID(playerid);
      if (entity instanceof EntityPlayerMP) {
         network.sendTo(message, (EntityPlayerMP)entity);
      } else {
         System.out.println("ERROR! couldn't find player");
      }

   }

   public static void removesubarrow(int id) {
      HashSet<Integer> arrowIds = new HashSet();
      arrowIds.add(Integer.valueOf(id));
      network.sendToServer(new GrappleEndMessage(-1, arrowIds));
   }

   public static void receiveGrappleEnd(int id, World world, HashSet<Integer> arrowIds) {
      if (attached.contains(Integer.valueOf(id))) {
         attached.remove(new Integer(id));
      }

      Iterator entity = arrowIds.iterator();

      while(entity.hasNext()) {
         int arrowid = ((Integer)entity.next()).intValue();
         Entity grapple = world.getEntityByID(arrowid);
         if (grapple instanceof grappleArrow) {
            ((grappleArrow)grapple).removeServer();
         }
      }

      Entity entity = world.getEntityByID(id);
      if (entity != null) {
         entity.fallDistance = 0.0F;
      }

      removeallmultihookarrows(id);
   }

   public static void addarrow(int id, grappleArrow arrow) {
      if (!allarrows.containsKey(Integer.valueOf(id))) {
         allarrows.put(Integer.valueOf(id), new HashSet());
      }

      ((HashSet)allarrows.get(Integer.valueOf(id))).add(arrow);
   }

   public static void removeallmultihookarrows(int id) {
      if (!allarrows.containsKey(Integer.valueOf(id))) {
         allarrows.put(Integer.valueOf(id), new HashSet());
      }

      for(grappleArrow arrow : (HashSet)allarrows.get(Integer.valueOf(id))) {
         if (arrow != null && !arrow.isDead) {
            arrow.removeServer();
         }
      }

      allarrows.put(Integer.valueOf(id), new HashSet());
   }

   public static NBTTagCompound getstackcompound(ItemStack stack, String key) {
      if (!stack.hasTagCompound()) {
         stack.setTagCompound(new NBTTagCompound());
      }

      NBTTagCompound basecompound = stack.getTagCompound();
      if (basecompound.hasKey(key, 10)) {
         return basecompound.getCompoundTag(key);
      } else {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         stack.setTagInfo(key, nbttagcompound);
         return nbttagcompound;
      }
   }

   @SubscribeEvent
   public void onConfigChanged(OnConfigChangedEvent eventArgs) {
      if (eventArgs.getModID().equals("grapplemod")) {
         System.out.println("grapplemod config updated");
         ConfigManager.sync("grapplemod", Type.INSTANCE);
         updateGrapplingBlocks();
      }

   }

   public static void receiveKeypress(EntityPlayer player, KeypressItem.Keys key, boolean isDown) {
      if (player != null) {
         ItemStack stack = player.getHeldItemMainhand();
         if (stack != null) {
            Item item = stack.getItem();
            if (item instanceof KeypressItem) {
               if (isDown) {
                  ((KeypressItem)item).onCustomKeyDown(stack, player, key, true);
               } else {
                  ((KeypressItem)item).onCustomKeyUp(stack, player, key, true);
               }

               return;
            }
         }

         stack = player.getHeldItemOffhand();
         if (stack != null) {
            Item item = stack.getItem();
            if (item instanceof KeypressItem) {
               if (isDown) {
                  ((KeypressItem)item).onCustomKeyDown(stack, player, key, false);
               } else {
                  ((KeypressItem)item).onCustomKeyUp(stack, player, key, false);
               }

               return;
            }
         }
      }

   }

   public static Rarity getRarityFromInt(int rarity_int) {
      Rarity[] rarities = new Rarity[]{Rarity.VERY_RARE, Rarity.RARE, Rarity.UNCOMMON, Rarity.COMMON};
      if (rarity_int < 0) {
         rarity_int = 0;
      }

      if (rarity_int >= rarities.length) {
         rarity_int = rarities.length - 1;
      }

      return rarities[rarity_int];
   }

   public static enum upgradeCategories {
      ROPE("Rope"),
      THROW("Hook Thrower"),
      MOTOR("Motor"),
      SWING("Swing Speed"),
      STAFF("Ender Staff"),
      FORCEFIELD("Forcefield"),
      MAGNET("Hook Magnet"),
      DOUBLE("Double Hook"),
      LIMITS("Limits"),
      ROCKET("Rocket");

      public String description;

      private upgradeCategories(String desc) {
         this.description = desc;
      }

      public static grapplemod.upgradeCategories fromInt(int i) {
         return values()[i];
      }

      public int toInt() {
         for(int i = 0; i < size(); ++i) {
            if (values()[i] == this) {
               return i;
            }
         }

         return -1;
      }

      public static int size() {
         return values().length;
      }

      public Item getItem() {
         if (this == ROPE) {
            return grapplemod.ropeupgradeitem;
         } else if (this == THROW) {
            return grapplemod.throwupgradeitem;
         } else if (this == MOTOR) {
            return grapplemod.motorupgradeitem;
         } else if (this == SWING) {
            return grapplemod.swingupgradeitem;
         } else if (this == STAFF) {
            return grapplemod.staffupgradeitem;
         } else if (this == FORCEFIELD) {
            return grapplemod.forcefieldupgradeitem;
         } else if (this == MAGNET) {
            return grapplemod.magnetupgradeitem;
         } else if (this == DOUBLE) {
            return grapplemod.doubleupgradeitem;
         } else if (this == LIMITS) {
            return grapplemod.limitsupgradeitem;
         } else {
            return this == ROCKET ? grapplemod.rocketupgradeitem : null;
         }
      }
   }
}
