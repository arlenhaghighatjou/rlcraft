package svenhjol.charm.world.feature;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmLootTables;
import svenhjol.charm.world.client.render.RenderChargedEmerald;
import svenhjol.charm.world.entity.EntityChargedEmerald;
import svenhjol.charm.world.item.ItemChargedEmerald;
import svenhjol.meson.Feature;
import svenhjol.meson.helper.LootHelper;

public class ChargedEmeralds extends Feature {
   public static ItemChargedEmerald emerald;

   public ChargedEmeralds() {
   }

   public String getDescription() {
      return "If you are holding an emerald and are struck by lightning, the emerald will become charged and you will take no damage.\nRight-click a Charged Emerald to throw it, creating a lightning strike at the point of impact.";
   }

   public void configure() {
      super.configure();
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      emerald = new ItemChargedEmerald();
      String name = "charm:charged_emerald";
      EntityRegistry.registerModEntity(new ResourceLocation(name), EntityChargedEmerald.class, name, 1, Charm.instance, 64, 10, true);
      BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(emerald, new BehaviorProjectileDispense() {
         protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
            return new EntityChargedEmerald(worldIn, position.getX(), position.getY(), position.getZ());
         }
      });
   }

   @SideOnly(Side.CLIENT)
   public void preInitClient(FMLPreInitializationEvent event) {
      RenderingRegistry.registerEntityRenderingHandler(EntityChargedEmerald.class, RenderChargedEmerald.FACTORY);
   }

   @SubscribeEvent
   public void onLootTableLoad(LootTableLoadEvent event) {
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
         LootHelper.addToLootTable(event.getTable(), emerald, weight, quality, functions, conditions);
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }
}
