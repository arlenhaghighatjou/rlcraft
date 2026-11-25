package svenhjol.charm.brewing.feature;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import svenhjol.charm.Charm;
import svenhjol.charm.brewing.block.BlockFlavoredCake;
import svenhjol.charm.brewing.message.MessageCakeImbue;
import svenhjol.charm.crafting.feature.Composter;
import svenhjol.meson.Feature;
import svenhjol.meson.Meson;
import svenhjol.meson.handler.NetworkHandler;
import svenhjol.meson.helper.ForgeHelper;
import svenhjol.meson.helper.ItemHelper;
import svenhjol.meson.helper.SoundHelper;

public class FlavoredCake extends Feature {
   public static Map<String, BlockFlavoredCake> cakes = new HashMap();
   public static Map<PotionType, BlockFlavoredCake> cakeTypes = new HashMap();
   public static String[] validPotions;
   public static double multiplier;

   public FlavoredCake() {
   }

   public String getDescription() {
      return "Right-click a Long Potion on a cake make a Flavored Cake that gives a potion effect after eating each slice.";
   }

   public void configure() {
      super.configure();
      multiplier = this.propDouble("Effect duration multiplier", "Duration multiplier of the potion effect when eating a single slice of cake.", 0.5D);
      validPotions = this.propStringList("Effect potion types", "List of Long Potions that can be used to make a cake.", new String[]{"swiftness", "strength", "leaping", "regeneration", "fire_resistance", "water_breathing", "invisibility", "night_vision", "quark:danger_sight", "quark:haste", "quark:resistance", "quark:resilience"});
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);

      for(String name : validPotions) {
         String modName = "";
         String baseName = name;
         if (name.contains(":")) {
            String[] split = name.split(":");
            modName = split[0];
            baseName = split[1];
         }

         if (modName.isEmpty() || ForgeHelper.areModsLoaded(modName)) {
            BlockFlavoredCake cake = new BlockFlavoredCake(baseName);
            cakes.put(name, cake);
            if (Charm.hasFeature(Composter.class)) {
               Composter.inputs.put(((ResourceLocation)Objects.requireNonNull(cake.getRegistryName())).toString(), Float.valueOf(1.0F));
            }
         }
      }

      BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.POTIONITEM, new FlavoredCake.DispenseHandlerImbue());
      NetworkHandler.register(MessageCakeImbue.class, Side.CLIENT);
   }

   public void init(FMLInitializationEvent event) {
      for(String name : cakes.keySet()) {
         PotionType type = null;
         BlockFlavoredCake cake = (BlockFlavoredCake)cakes.get(name);
         int duration = 0;
         String longName;
         String strongName;
         if (name.contains(":")) {
            String[] split = name.split(":");
            String modName = split[0];
            longName = modName + ":long_" + split[1];
            strongName = modName + ":strong_" + split[1];
         } else {
            longName = "long_" + name;
            strongName = "strong_" + name;
         }

         String[] names = new String[]{longName, strongName, name};

         for(String n : names) {
            type = PotionType.getPotionTypeForName(n);
            if (type != null && !type.getEffects().isEmpty()) {
               break;
            }
         }

         if (type != null && !type.getEffects().isEmpty()) {
            ItemStack potionItem = ItemHelper.getPotionBottle(1, type);

            for(PotionEffect effect : PotionUtils.getEffectsFromStack(potionItem)) {
               duration = Math.max(duration, effect.getDuration());
            }

            if (duration == 0) {
               duration = 10;
            }

            cake.setPotionItem(potionItem, duration);
            cakeTypes.put(type, cake);
         } else {
            Meson.log("Cannot find PotionType for " + name + ", skipping");
         }
      }

   }

   @SubscribeEvent
   public void onUse(RightClickBlock event) {
      ItemStack held = event.getEntityPlayer().getHeldItem(event.getHand());
      boolean result = this.imbue(event.getWorld(), event.getPos(), held);
      if (result) {
         event.setCanceled(true);
      }

   }

   private boolean imbue(World world, BlockPos pos, ItemStack item) {
      if (world.getBlockState(pos).getBlock() == Blocks.CAKE) {
         PotionType potionItem = PotionUtils.getPotionFromItem(item);
         if (cakeTypes.containsKey(potionItem)) {
            IBlockState current = world.getBlockState(pos);
            IBlockState imbued = ((BlockFlavoredCake)cakeTypes.get(potionItem)).getDefaultState().withProperty(BlockFlavoredCake.BITES, current.getValue(BlockFlavoredCake.BITES));
            world.setBlockState(pos, imbued, 2);
            item.shrink(1);
            if (!world.isRemote) {
               NetworkHandler.INSTANCE.sendToAll(new MessageCakeImbue(pos));
            }

            return true;
         }
      }

      return false;
   }

   @SideOnly(Side.CLIENT)
   public static void cakeImbued(BlockPos pos) {
      World world = Minecraft.getMinecraft().world;
      SoundHelper.playSoundAtPos(world, pos, SoundEvents.ITEM_BOTTLE_EMPTY, 0.5F, 1.1F);

      for(int i = 0; i < 8; ++i) {
         double d0 = world.rand.nextGaussian() * 0.02D;
         double d1 = world.rand.nextGaussian() * 0.02D;
         double d2 = world.rand.nextGaussian() * 0.02D;
         double dx = (double)((float)pos.getX() + MathHelper.clamp(world.rand.nextFloat(), 0.25F, 0.75F));
         double dy = (double)((float)pos.getY() + 0.65F);
         double dz = (double)((float)pos.getZ() + MathHelper.clamp(world.rand.nextFloat(), 0.25F, 0.75F));
         world.spawnParticle(EnumParticleTypes.SPELL_WITCH, dx, dy, dz, d0, d1, d2, new int[0]);
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }

   public class DispenseHandlerImbue extends BehaviorDefaultDispenseItem {
      public DispenseHandlerImbue() {
      }

      protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
         World world = source.getWorld();
         BlockPos pos = source.getBlockPos().offset((EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING));
         boolean result = FlavoredCake.this.imbue(world, pos, stack);
         return !result ? super.dispenseStack(source, stack) : stack;
      }
   }
}
