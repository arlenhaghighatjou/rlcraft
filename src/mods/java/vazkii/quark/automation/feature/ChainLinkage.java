package vazkii.quark.automation.feature;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.minecart.MinecartUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.base.ChainHandler;
import vazkii.quark.world.client.render.ChainRenderer;
import vazkii.quark.world.item.ItemChain;

public class ChainLinkage extends Feature {
   public static boolean craftsArmor;
   public static Item chain;
   private static final TIntObjectMap<EaglercraftUUID> AWAIT_MAP = new TIntObjectHashMap();

   public ChainLinkage() {
   }

   public void setupConfig() {
      craftsArmor = this.loadPropBool("Crafts Armor", "Can vehicle-linking chains be used for crafting chain armor?", true);
   }

   public void preInit(FMLPreInitializationEvent event) {
      chain = new ItemChain();
      RecipeHandler.addShapedRecipe(ProxyRegistry.newStack(chain, 3), new Object[]{"NN ", "NI ", "  I", 'N', "nuggetIron", 'I', "ingotIron"});
      if (craftsArmor) {
         RecipeHandler.addShapedRecipe(ProxyRegistry.newStack(Items.CHAINMAIL_HELMET), new Object[]{"CCC", "C C", 'C', ProxyRegistry.newStack(chain)});
         RecipeHandler.addShapedRecipe(ProxyRegistry.newStack(Items.CHAINMAIL_CHESTPLATE), new Object[]{"C C", "CCC", "CCC", 'C', ProxyRegistry.newStack(chain)});
         RecipeHandler.addShapedRecipe(ProxyRegistry.newStack(Items.CHAINMAIL_LEGGINGS), new Object[]{"CCC", "C C", "C C", 'C', ProxyRegistry.newStack(chain)});
         RecipeHandler.addShapedRecipe(ProxyRegistry.newStack(Items.CHAINMAIL_BOOTS), new Object[]{"C C", "C C", 'C', ProxyRegistry.newStack(chain)});
      }

   }

   public static void queueChainUpdate(int vehicle, EaglercraftUUID other) {
      if (other != null) {
         AWAIT_MAP.put(vehicle, other);
      }

   }

   public static void onEntityUpdate(Entity vehicle) {
      if (ChainHandler.canBeLinkedTo(vehicle)) {
         ChainHandler.adjustVehicle(vehicle);
      }

   }

   public static void drop(Entity vehicle) {
      if (ChainHandler.getLinked(vehicle) != null) {
         vehicle.entityDropItem(new ItemStack(chain), 0.0F);
      }

   }

   @SubscribeEvent
   public void onInteract(EntityInteract event) {
      EntityPlayer player = event.getEntityPlayer();
      ItemStack stack = event.getItemStack();
      Entity entity = event.getTarget();
      Entity link = ChainHandler.getLinked(entity);
      boolean sneaking = player.isSneaking();
      List<Entity> linkedToPlayer = new ArrayList();

      for(Entity linkCandidate : entity.world.getEntitiesWithinAABB(Entity.class, player.getEntityBoundingBox().grow(8.0D))) {
         if (ChainHandler.getLinked(linkCandidate) == player) {
            linkedToPlayer.add(linkCandidate);
         }
      }

      if (ChainHandler.canBeLinked(entity) && linkedToPlayer.isEmpty() && !stack.isEmpty() && stack.getItem() == chain && link == null) {
         if (!entity.world.isRemote) {
            ChainHandler.setLink(entity, player.getUniqueID(), true);
            if (!player.isCreative()) {
               stack.shrink(1);
            }
         }

         event.setCancellationResult(EnumActionResult.SUCCESS);
         event.setCanceled(true);
      } else if (link == player) {
         if (!entity.world.isRemote) {
            if (!player.isCreative()) {
               entity.entityDropItem(new ItemStack(chain), 0.0F);
            }

            ChainHandler.setLink(entity, (EaglercraftUUID)null, true);
         }

         event.setCancellationResult(EnumActionResult.SUCCESS);
         event.setCanceled(true);
      } else if (ChainHandler.canBeLinked(entity) && !linkedToPlayer.isEmpty()) {
         if (!entity.world.isRemote) {
            for(Entity linked : linkedToPlayer) {
               ChainHandler.setLink(linked, entity.getUniqueID(), true);
            }
         }

         event.setCancellationResult(EnumActionResult.SUCCESS);
         event.setCanceled(true);
      } else if (link != null && sneaking) {
         if (!entity.world.isRemote) {
            if (!player.isCreative()) {
               entity.entityDropItem(new ItemStack(chain), 0.0F);
            }

            ChainHandler.setLink(entity, (EaglercraftUUID)null, true);
         }

         event.setCancellationResult(EnumActionResult.SUCCESS);
         event.setCanceled(true);
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void clientUpdateTick(ClientTickEvent event) {
      if (event.side == Side.CLIENT && event.phase == Phase.START) {
         ChainRenderer.updateTick();
      }

   }

   @SubscribeEvent
   public void onVehicleSeen(StartTracking event) {
      if (ChainHandler.canBeLinked(event.getTarget())) {
         ChainHandler.updateLinkState(event.getTarget(), ChainHandler.getLink(event.getTarget()), event.getEntityPlayer());
      }

   }

   @SubscribeEvent
   public void onVehicleArrive(EntityJoinWorldEvent event) {
      Entity target = event.getEntity();
      if (event.getWorld().isRemote && ChainHandler.canBeLinked(target)) {
         int id = target.getEntityId();
         if (AWAIT_MAP.containsKey(id)) {
            target.getEntityData().setUniqueId("Quark:VehicleLink", (EaglercraftUUID)AWAIT_MAP.get(id));
         }

         AWAIT_MAP.remove(id);
      }

   }

   @SubscribeEvent
   public void onMinecartUpdate(MinecartUpdateEvent event) {
      onEntityUpdate(event.getMinecart());
   }

   public boolean hasSubscriptions() {
      return true;
   }
}
