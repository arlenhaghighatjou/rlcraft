package vazkii.quark.tweaks.feature;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import vazkii.quark.base.module.Feature;

public class MinecartInteraction extends Feature {
   public static final Map<Item, Function<EntityMinecartEmpty, EntityMinecart>> inserters = new HashMap();
   public static boolean enableCommandAndSpawner;

   public MinecartInteraction() {
   }

   public void setupConfig() {
      enableCommandAndSpawner = this.loadPropBool("Enable Command Block and Mob Spawner", "", true);
   }

   public void init() {
      inserters.put(Item.getItemFromBlock(Blocks.CHEST), (e) -> {
         return this.getMinecart(new ResourceLocation("minecraft", "chest_minecart"), e.getEntityWorld(), e.posX, e.posY, e.posZ);
      });
      inserters.put(Item.getItemFromBlock(Blocks.TNT), (e) -> {
         return this.getMinecart(new ResourceLocation("minecraft", "tnt_minecart"), e.getEntityWorld(), e.posX, e.posY, e.posZ);
      });
      inserters.put(Item.getItemFromBlock(Blocks.FURNACE), (e) -> {
         return this.getMinecart(new ResourceLocation("minecraft", "furnace_minecart"), e.getEntityWorld(), e.posX, e.posY, e.posZ);
      });
      inserters.put(Item.getItemFromBlock(Blocks.HOPPER), (e) -> {
         return this.getMinecart(new ResourceLocation("minecraft", "hopper_minecart"), e.getEntityWorld(), e.posX, e.posY, e.posZ);
      });
      if (enableCommandAndSpawner) {
         inserters.put(Item.getItemFromBlock(Blocks.COMMAND_BLOCK), (e) -> {
            return this.getMinecart(new ResourceLocation("minecraft", "commandblock_minecart"), e.getEntityWorld(), e.posX, e.posY, e.posZ);
         });
         inserters.put(Item.getItemFromBlock(Blocks.MOB_SPAWNER), (e) -> {
            return this.getMinecart(new ResourceLocation("minecraft", "spawner_minecart"), e.getEntityWorld(), e.posX, e.posY, e.posZ);
         });
      }

   }

   private EntityMinecart getMinecart(ResourceLocation rl, World world, double x, double y, double z) {
      try {
         EntityEntry entry = (EntityEntry)ForgeRegistries.ENTITIES.getValue(rl);
         if (entry != null) {
            Class<? extends Entity> minecartClass = entry.getEntityClass();
            return (EntityMinecart)minecartClass.getConstructor(World.class, Double.TYPE, Double.TYPE, Double.TYPE).newInstance(world, x, y, z);
         }
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException var11) {
         var11.printStackTrace();
      }

      return null;
   }

   @SubscribeEvent
   public void onEntityInteract(EntityInteract event) {
      Entity target = event.getTarget();
      if (target instanceof EntityMinecartEmpty && target.getPassengers().isEmpty()) {
         EntityPlayer player = event.getEntityPlayer();
         EnumHand hand = EnumHand.MAIN_HAND;
         ItemStack stack = player.getHeldItemMainhand();
         if (stack.isEmpty() || !inserters.containsKey(stack.getItem())) {
            stack = player.getHeldItemOffhand();
            hand = EnumHand.OFF_HAND;
         }

         if (!stack.isEmpty() && inserters.containsKey(stack.getItem())) {
            player.swingArm(hand);
            if (!event.getWorld().isRemote) {
               EntityMinecart minecart = (EntityMinecart)((Function)inserters.get(stack.getItem())).apply((EntityMinecartEmpty)target);
               if (minecart != null) {
                  target.setDead();
                  event.getWorld().spawnEntity(minecart);
                  event.setCanceled(true);
                  if (!player.capabilities.isCreativeMode) {
                     stack.shrink(1);
                     if (stack.getCount() <= 0) {
                        player.setHeldItem(hand, ItemStack.EMPTY);
                     }
                  }
               }
            }
         }
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public String getFeatureIngameConfigName() {
      return "Minecart Block Placement";
   }
}
