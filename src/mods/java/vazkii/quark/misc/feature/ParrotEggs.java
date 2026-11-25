package vazkii.quark.misc.feature;

import java.util.Objects;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.misc.client.render.RenderParrotEgg;
import vazkii.quark.misc.client.render.RenderParrotKoto;
import vazkii.quark.misc.entity.EntityParrotEgg;
import vazkii.quark.misc.item.ItemParrotEgg;

public class ParrotEggs extends Feature {
   private static final ResourceLocation KOTO = new ResourceLocation("quark", "textures/entity/kotobirb.png");
   private static final String TAG_EGG_TIMER = "quark:parrot_egg_timer";
   public static Item parrot_egg;
   public static Item item;
   public static double chance;
   public static int eggTime;
   public static boolean enableKotobirb;

   public ParrotEggs() {
   }

   @SideOnly(Side.CLIENT)
   public static ResourceLocation getTextureForParrot(ResourceLocation previous, NBTTagCompound parrot) {
      if (ModuleLoader.isFeatureEnabled(ParrotEggs.class) && enableKotobirb) {
         EaglercraftUUID uuid = parrot.getUniqueId("EaglercraftUUID");
         Class<?> entityClass = EntityList.getClassFromName(parrot.getString("id"));
         return entityClass != EntityParrot.class || parrot.getInteger("Variant") != 4 || uuid != null && uuid.getLeastSignificantBits() % 20L != 0L ? previous : KOTO;
      } else {
         return previous;
      }
   }

   public void setupConfig() {
      item = Items.BEETROOT_SEEDS;
      String itemName = this.loadPropString("Feed Item", "", Objects.toString(item.getRegistryName()));
      Item targetItem = (Item)Item.REGISTRY.getObject(new ResourceLocation(itemName));
      if (targetItem != null) {
         item = targetItem;
      }

      chance = this.loadLegacyPropChance("Success Percentage Chance", "Success Chance", "If this is X, the parrot will, on average, start making an egg in every 1/X seeds fed", 0.05D);
      eggTime = this.loadPropInt("Egg Creation Time", "", 12000);
      enableKotobirb = this.loadPropBool("Enable Special Awesome Parrot", "", true);
   }

   public void preInit(FMLPreInitializationEvent event) {
      parrot_egg = new ItemParrotEgg();
      String eggName = "quark:parrot_egg";
      EntityRegistry.registerModEntity(new ResourceLocation(eggName), EntityParrotEgg.class, eggName, 14, Quark.instance, 64, 10, true);
   }

   @SideOnly(Side.CLIENT)
   public void preInitClient() {
      RenderingRegistry.registerEntityRenderingHandler(EntityParrotEgg.class, RenderParrotEgg.factory());
      if (enableKotobirb) {
         RenderingRegistry.registerEntityRenderingHandler(EntityParrot.class, RenderParrotKoto.factory());
      }

   }

   @SubscribeEvent
   public void entityInteract(EntityInteract event) {
      if (event.getTarget() != null) {
         Entity e = event.getTarget();
         if (e instanceof EntityParrot && e.getEntityData().getInteger("quark:parrot_egg_timer") == 0) {
            EntityParrot parrot = (EntityParrot)e;
            if (!parrot.isTamed()) {
               return;
            }

            EntityPlayer player = event.getEntityPlayer();
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.isEmpty() || stack.getItem() != item) {
               stack = player.getHeldItemOffhand();
            }

            if (!stack.isEmpty() && stack.getItem() == item) {
               event.setCanceled(true);
               if (e.world.isRemote || event.getHand() == EnumHand.OFF_HAND) {
                  return;
               }

               if (!player.isCreative()) {
                  stack.shrink(1);
               }

               WorldServer ws = (WorldServer)e.world;
               ws.playSound((EntityPlayer)null, e.posX, e.posY, e.posZ, SoundEvents.ENTITY_PARROT_EAT, SoundCategory.NEUTRAL, 1.0F, 1.0F + (ws.rand.nextFloat() - ws.rand.nextFloat()) * 0.2F);
               if (e.world.rand.nextDouble() < chance) {
                  e.getEntityData().setInteger("quark:parrot_egg_timer", eggTime);
                  ws.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, e.posX, e.posY, e.posZ, 10, (double)e.width, (double)e.height, (double)e.width, 0.0D, new int[0]);
               } else {
                  ws.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, e.posX, e.posY, e.posZ, 10, (double)e.width, (double)e.height, (double)e.width, 0.0D, new int[0]);
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void entityUpdate(LivingUpdateEvent event) {
      Entity e = event.getEntity();
      if (e instanceof EntityParrot) {
         int time = e.getEntityData().getInteger("quark:parrot_egg_timer");
         if (time > 0) {
            if (time == 1) {
               e.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (e.world.rand.nextFloat() - e.world.rand.nextFloat()) * 0.2F + 1.0F);
               e.entityDropItem(new ItemStack(parrot_egg, 1, this.getResultingEggColor((EntityParrot)e)), 0.0F);
            }

            e.getEntityData().setInteger("quark:parrot_egg_timer", time - 1);
         }
      }

   }

   private int getResultingEggColor(EntityParrot parrot) {
      int color = parrot.getVariant();
      EaglercraftRandom rand = parrot.world.rand;
      return rand.nextBoolean() ? color : rand.nextInt(5);
   }

   public boolean hasSubscriptions() {
      return true;
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
