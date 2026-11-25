package svenhjol.charm.world.feature;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import svenhjol.charm.Charm;
import svenhjol.charm.world.entity.EntityEndermitePowder;
import svenhjol.charm.world.item.ItemEndermitePowder;
import svenhjol.meson.Feature;

public class EndermitePowder extends Feature {
   public static ItemEndermitePowder endermitePowder;
   public static double dropChance;
   public static double lootingBoost;

   public EndermitePowder() {
   }

   public String getDescription() {
      return "Endermite Powder has a chance of being dropped from an Endermite.\nUse it in the End to help locate an End City.";
   }

   public void configure() {
      super.configure();
      dropChance = this.propDouble("Drop chance", "Chance (out of 1.0) of an endermite dropping Endermite Powder when killed by the player.", 0.5D);
      lootingBoost = 0.1D;
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      endermitePowder = new ItemEndermitePowder();
      String name = "charm:endermite_powder";
      EntityRegistry.registerModEntity(new ResourceLocation(name), EntityEndermitePowder.class, name, 0, Charm.instance, 80, 10, false);
   }

   @SubscribeEvent
   public void onDrops(LivingDropsEvent event) {
      if (!event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof EntityEndermite && event.getSource().getTrueSource() instanceof EntityPlayer && (double)event.getEntityLiving().world.rand.nextFloat() <= dropChance + lootingBoost * (double)event.getLootingLevel()) {
         Entity entity = event.getEntity();
         ItemStack item = new ItemStack(endermitePowder);
         event.getDrops().add(new EntityItem(entity.getEntityWorld(), entity.posX, entity.posY, entity.posZ, item));
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }
}
