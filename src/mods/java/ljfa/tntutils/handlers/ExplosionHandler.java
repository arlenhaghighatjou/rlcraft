package ljfa.tntutils.handlers;

import ljfa.tntutils.Config;
import ljfa.tntutils.asm.HooksExplosion;
import ljfa.tntutils.util.ListHelper;
import ljfa.tntutils.util.Predicate;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.world.ExplosionEvent.Detonate;
import net.minecraftforge.event.world.ExplosionEvent.Start;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ExplosionHandler {
   public ExplosionHandler() {
   }

   @SubscribeEvent
   public void onExplosionStart(Start event) {
      if (Config.disableExplosions) {
         event.setCanceled(true);
      } else {
         Explosion var10000 = event.getExplosion();
         var10000.size *= Config.sizeMultiplier;
      }

   }

   @SubscribeEvent
   public void onExplosionDetonate(Detonate event) {
      final World world = event.getWorld();
      if (!world.isRemote) {
         if (!Config.disableBlockDamage && (!Config.disableCreeperBlockDamage || !(event.getExplosion().exploder instanceof EntityCreeper))) {
            if (Config.spareTileEntities || Config.blacklistActive) {
               ListHelper.removeIf(event.getAffectedBlocks(), new Predicate<BlockPos>() {
                  public boolean test(BlockPos pos) {
                     return ExplosionHandler.shouldBePreserved(world.getBlockState(pos));
                  }
               });
            }

            if (Config.preventChainExpl) {
               for(BlockPos pos : event.getAffectedBlocks()) {
                  IBlockState state = world.getBlockState(pos);
                  if (state.getBlock() instanceof BlockTNT) {
                     state.getBlock().dropBlockAsItemWithChance(world, pos, state, HooksExplosion.getDropChance(event.getExplosion()), 0);
                     world.setBlockToAir(pos);
                  }
               }
            }
         } else {
            event.getAffectedBlocks().clear();
         }

         if (Config.disableEntityDamage) {
            event.getAffectedEntities().clear();
         } else if (Config.disablePlayerDamage || Config.disableItemDamage || Config.disableNPCDamage || Config.preventChainExpl) {
            ListHelper.removeIf(event.getAffectedEntities(), new Predicate<Entity>() {
               public boolean test(Entity ent) {
                  return Config.disableNPCDamage && ent instanceof EntityLivingBase && !(ent instanceof EntityPlayer) || Config.disablePlayerDamage && ent instanceof EntityPlayer || Config.disableItemDamage && ent instanceof EntityItem || Config.preventChainExpl && ent instanceof EntityMinecartTNT;
               }
            });
         }

      }
   }

   public static boolean shouldBePreserved(IBlockState state) {
      if (Config.spareTileEntities && state.getBlock().hasTileEntity(state)) {
         return true;
      } else {
         Integer mask = (Integer)Config.blackWhiteList.get(state.getBlock());
         boolean matches = mask != null && (mask.intValue() & 1 << state.getBlock().getMetaFromState(state)) != 0;
         return Config.listIsWhitelist ? !matches : matches;
      }
   }
}
