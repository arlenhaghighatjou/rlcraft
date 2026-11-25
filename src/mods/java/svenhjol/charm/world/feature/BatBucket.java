package svenhjol.charm.world.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import svenhjol.charm.base.CharmLootTables;
import svenhjol.charm.world.item.ItemBatBucket;
import svenhjol.charm.world.message.MessageGlowing;
import svenhjol.meson.Feature;
import svenhjol.meson.handler.NetworkHandler;
import svenhjol.meson.helper.LootHelper;
import svenhjol.meson.helper.PlayerHelper;

public class BatBucket extends Feature {
   public static ItemBatBucket batBucket;
   public static int maxSeconds;
   public static int range;
   public static boolean addToLoot;
   public static int clientExistingTicks;
   public static double clientRange;
   public static List<Entity> clientEntities = new ArrayList();

   public BatBucket() {
   }

   public String getDescription() {
      return "Right-click a bat with a bucket to capture it.\nRight-click your Bat in a Bucket to release the bat and help locate entities around you.";
   }

   public void configure() {
      super.configure();
      maxSeconds = this.propInt("Maximum time", "Maximum time (in seconds) that the glowing effect will last.", 10);
      range = this.propInt("Viewing range", "Range (in blocks) in which entities will glow when the Bat in a Bucket is held.", 24);
      addToLoot = this.propBoolean("Add to loot", "Add the Bat in a Bucket to dungeon loot.", true);
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      batBucket = new ItemBatBucket();
      NetworkHandler.register(MessageGlowing.class, Side.CLIENT);
   }

   @SubscribeEvent
   public void onInteract(EntityInteract event) {
      if (!event.isCanceled() && !event.getWorld().isRemote && event.getTarget() instanceof EntityBat && ((EntityBat)event.getTarget()).getHealth() > 0.0F) {
         EntityPlayer player = event.getEntityPlayer();
         EnumHand hand = event.getHand();
         ItemStack stack = player.getHeldItem(hand);
         if (!stack.isEmpty() && stack.getItem() == Items.BUCKET) {
            ItemStack out = new ItemStack(batBucket);
            PlayerHelper.setHeldItem(player, hand, out);
            event.getTarget().setDead();
         }
      }

      if (event.getWorld().isRemote) {
         event.getEntityPlayer().swingArm(event.getHand());
      }

   }

   @SubscribeEvent
   public void onDeath(LivingDeathEvent event) {
      if (event.getEntityLiving() instanceof EntityPlayer && event.getEntityLiving().world.isRemote) {
         clientExistingTicks = 0;
         this.setGlowing(false);
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onClientTick(ClientTickEvent event) {
      if (clientExistingTicks > 0 && clientRange > 0.0D && event.phase == Phase.START && Minecraft.getMinecraft().player != null) {
         EntityPlayer player = Minecraft.getMinecraft().player;
         if (clientExistingTicks % 10 == 0 || clientEntities.isEmpty()) {
            this.setGlowing(false);
            this.setNearbyEntities(player);
            this.setGlowing(true);
         }

         if (--clientExistingTicks <= 0) {
            this.setGlowing(false);
         }
      }

   }

   @SideOnly(Side.CLIENT)
   private void setNearbyEntities(EntityPlayer player) {
      clientEntities.clear();
      AxisAlignedBB area = player.getEntityBoundingBox().grow(clientRange, clientRange / 2.0D, clientRange);
      Predicate<Entity> selector = (entity) -> {
         return true;
      };
      World var10000 = player.world;
      selector.getClass();
      clientEntities = var10000.getEntitiesWithinAABB(Entity.class, area, selector::test);
   }

   @SideOnly(Side.CLIENT)
   private void setGlowing(boolean glowing) {
      for(Entity entity : clientEntities) {
         entity.setGlowing(glowing);
      }

   }

   @SubscribeEvent
   public void onLootTableLoad(LootTableLoadEvent event) {
      if (addToLoot) {
         int weight = 0;
         int quality = 0;
         LootFunction[] functions = new LootFunction[0];
         LootCondition[] conditions = new LootCondition[0];
         if (event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON)) {
            weight = 10;
         }

         if (event.getName().equals(CharmLootTables.TREASURE_UNCOMMON)) {
            weight = 10;
         }

         if (weight > 0) {
            LootHelper.addToLootTable(event.getTable(), batBucket, weight, quality, functions, conditions);
         }

      }
   }

   public boolean hasSubscriptions() {
      return true;
   }
}
