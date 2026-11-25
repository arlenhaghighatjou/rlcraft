package cursedflames.bountifulbaubles.item;

import cursedflames.bountifulbaubles.BountifulBaubles;
import cursedflames.bountifulbaubles.item.base.GenericItemBB;
import cursedflames.bountifulbaubles.util.Config;
import cursedflames.bountifulbaubles.wormhole.TeleporterRecall;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.api.ICustomEnchantColor;

public class ItemMagicMirror extends GenericItemBB implements ICustomEnchantColor {
   static Property interdimensional;

   public ItemMagicMirror(String name) {
      super(name, BountifulBaubles.TAB);
      this.setMaxStackSize(1);
      this.addPropertyOverride(new ResourceLocation("using"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
         }
      });
      if (interdimensional == null) {
         BountifulBaubles.config.addPropBoolean(this.getRegistryName() + ".interdimensional", "Items", "Can magic/wormhole mirrors and recall potions recall interdimensionally?", false, Config.EnumPropSide.SYNCED);
         interdimensional = BountifulBaubles.config.getSyncedProperty(this.getRegistryName() + ".interdimensional");
      }

   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      int dim = player.getSpawnDimension();
      if (world.provider.getDimension() != dim && !interdimensional.getBoolean(false)) {
         player.sendStatusMessage(new TextComponentTranslation(ModItems.magicMirror.getUnlocalizedName() + ".wrongdim", new Object[0]), true);
         return new ActionResult(EnumActionResult.FAIL, player.getHeldItem(hand));
      } else {
         player.setActiveHand(hand);
         return new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
      }
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.NONE;
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return 72000;
   }

   public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) {
      count = 72000 - count;
      World world = entity.world;
      if (world.isRemote && count > 0 && count < 20 && entity instanceof EntityPlayer) {
         for(int i = count != 15 && count != 16 ? 5 : 15; i > 0; --i) {
            Vec3d vel = (new Vec3d(Math.random() - 0.5D, Math.random() - 0.5D, Math.random() - 0.5D)).normalize().scale((Math.random() * 8.0D + 1.0D) * 0.02D);
            Vec3d off = (new Vec3d(Math.random() - 0.5D, Math.random() - 0.5D, Math.random() - 0.5D)).normalize().scale(count == 16 ? 1.1D : 0.1D);
            BountifulBaubles.proxy.spawnParticleGradient(world, entity.posX + off.x, entity.posY + 1.0D + off.y, entity.posZ + off.z, (float)(0.8470588326454163D - Math.random() / 10.0D), 1.0F, 1.0F, (float)(0.27450981736183167D + Math.random() / 10.0D), (float)(0.5882353186607361D + Math.random() / 10.0D), (float)(0.8627451062202454D + Math.random() / 10.0D), entity.world.rand.nextInt(25) + 5, vel.x, vel.y, vel.z);
         }

         if (count == 15 || count == 16) {
            entity.lastTickPosX = entity.posX;
            entity.lastTickPosY = entity.posY;
            entity.lastTickPosZ = entity.posZ;
         }
      }

      if (count == 15 && entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         teleportPlayerToSpawn(world, player);
      }

   }

   public static void teleportPlayerToSpawn(World world, EntityPlayer player) {
      int dim = player.getSpawnDimension();
      World world1 = world;
      if (world.provider.getDimension() != dim) {
         if (!interdimensional.getBoolean(false)) {
            return;
         }

         world1 = DimensionManager.getWorld(dim);
      }

      if (world1 != null) {
         if (player.isPlayerSleeping()) {
            return;
         }

         BountifulBaubles.logger.info(player.getPosition());
         if (player.isRiding()) {
            player.dismountRidingEntity();
         }

         BountifulBaubles.logger.info(player.getPosition());
         if (!world.isRemote) {
            if (world != world1) {
               player.changeDimension(dim, new TeleporterRecall());
            } else {
               BlockPos spawnPoint = player.getBedLocation(dim);
               if (spawnPoint != null) {
                  spawnPoint = EntityPlayer.getBedSpawnLocation(world1, spawnPoint, player.isSpawnForced(dim));
               }

               if (spawnPoint == null) {
                  spawnPoint = world1.provider.getRandomizedSpawnPoint();
               }

               if (spawnPoint != null) {
                  player.setPositionAndUpdate((double)spawnPoint.getX() + 0.5D, (double)spawnPoint.getY(), (double)spawnPoint.getZ() + 0.5D);
                  BountifulBaubles.logger.info(player.getPosition());
                  if (player.fallDistance > 0.0F) {
                     player.fallDistance = 0.0F;
                  }

                  player.lastTickPosX = player.posX;
                  player.lastTickPosY = player.posY;
                  player.lastTickPosZ = player.posZ;
                  player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
               }
            }
         }
      }

   }

   public boolean hasEffect(ItemStack stack) {
      return true;
   }

   public int getEnchantEffectColor(ItemStack arg0) {
      return 4152925;
   }
}
