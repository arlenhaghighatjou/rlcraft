package cursedflames.bountifulbaubles.item;

import cursedflames.bountifulbaubles.BountifulBaubles;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class ItemWormholeMirror extends ItemMagicMirror {
   public ItemWormholeMirror(String name) {
      super(name);
   }

   public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) {
      count = 72000 - count;
      if (entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         World world = entity.world;
         if (world.isRemote && count > 0 && count < 20) {
            for(int i = count != 15 && count != 16 ? 5 : 15; i > 0; --i) {
               Vec3d vel = (new Vec3d(Math.random() - 0.5D, Math.random() - 0.5D, Math.random() - 0.5D)).normalize().scale((Math.random() * 8.0D + 1.0D) * 0.02D);
               Vec3d off = (new Vec3d(Math.random() - 0.5D, Math.random() - 0.5D, Math.random() - 0.5D)).normalize().scale(count == 16 ? 1.1D : 0.1D);
               BountifulBaubles.proxy.spawnParticleGradient(world, entity.posX + off.x, entity.posY + 1.0D + off.y, entity.posZ + off.z, (float)(0.8470588326454163D - Math.random() / 10.0D), 1.0F, 1.0F, (float)(0.27450981736183167D + Math.random() / 10.0D), (float)(0.5882353186607361D + Math.random() / 10.0D), (float)(0.8627451062202454D + Math.random() / 10.0D), entity.world.rand.nextInt(25) + 5, vel.x, vel.y, vel.z);
            }
         }

         boolean sneaking = player.isSneaking();
         if (!world.isRemote && count == 15 && !sneaking) {
            int dim = player.getSpawnDimension();
            if (world.provider.getDimension() != dim && !interdimensional.getBoolean(false)) {
               player.sendStatusMessage(new TextComponentTranslation(ModItems.magicMirror.getUnlocalizedName() + ".wrongdim", new Object[0]), true);
            } else {
               teleportPlayerToSpawn(world, player);
            }
         }

         if (!world.isRemote && count == 16 && sneaking) {
            if (world.playerEntities.size() < 2) {
               player.sendStatusMessage(new TextComponentTranslation(ModItems.potionWormhole.getUnlocalizedName() + ".nootherplayers", new Object[0]), true);
            } else {
               player.openGui(BountifulBaubles.instance, 2, world, (int)player.posX, (int)player.posY, (int)player.posZ);
            }
         }

      }
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      player.setActiveHand(hand);
      return new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
   }
}
