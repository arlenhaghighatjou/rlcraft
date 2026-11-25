package vazkii.quark.misc.feature;

import javax.annotation.Nonnull;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.Feature;
import vazkii.quark.misc.entity.EntityDragonBreathBottle;

public class ThrowableDragonBreath extends Feature {
   public static int blocksPerBottle;

   public ThrowableDragonBreath() {
   }

   public void setupConfig() {
      blocksPerBottle = this.loadPropInt("Blocks per Bottle", "", 64);
   }

   public void preInit(FMLPreInitializationEvent event) {
      String bottleName = "quark:dragon_breath_bottle";
      EntityRegistry.registerModEntity(new ResourceLocation(bottleName), EntityDragonBreathBottle.class, bottleName, 11, Quark.instance, 64, 10, true);
   }

   @SideOnly(Side.CLIENT)
   public void preInitClient() {
      RenderingRegistry.registerEntityRenderingHandler(EntityDragonBreathBottle.class, (renderManager) -> {
         return new RenderSnowball(renderManager, Items.DRAGON_BREATH, Minecraft.getMinecraft().getRenderItem());
      });
   }

   public void postInit() {
      BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.DRAGON_BREATH, new BehaviorProjectileDispense() {
         @Nonnull
         protected IProjectile getProjectileEntity(@Nonnull World worldIn, @Nonnull IPosition position, @Nonnull ItemStack stackIn) {
            return new EntityDragonBreathBottle(worldIn, position.getX(), position.getY(), position.getZ());
         }

         protected float getProjectileInaccuracy() {
            return super.getProjectileInaccuracy() * 0.5F;
         }

         protected float getProjectileVelocity() {
            return super.getProjectileVelocity() * 1.25F;
         }
      });
   }

   @SubscribeEvent
   public void playerRightClick(RightClickItem event) {
      EntityPlayer player = event.getEntityPlayer();
      World world = player.world;
      ItemStack stack = event.getItemStack();
      if (stack.getItem() == Items.DRAGON_BREATH) {
         if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
         }

         world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
         if (!world.isRemote) {
            EntityDragonBreathBottle b = new EntityDragonBreathBottle(world, player);
            b.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
            world.spawnEntity(b);
         } else {
            player.swingArm(event.getHand());
         }

         event.setCancellationResult(EnumActionResult.SUCCESS);
         event.setCanceled(true);
      }
   }

   public boolean hasSubscriptions() {
      return true;
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
