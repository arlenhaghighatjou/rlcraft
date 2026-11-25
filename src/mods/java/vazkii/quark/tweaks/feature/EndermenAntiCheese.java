package vazkii.quark.tweaks.feature;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class EndermenAntiCheese extends Feature {
   public static int minimumDifficulty = 2;
   public static boolean oldBehaviour;
   public static int delay;
   public static int lowerBound;
   public static boolean ignoreMobGriefing;

   public EndermenAntiCheese() {
   }

   public void setupConfig() {
      minimumDifficulty = this.loadPropInt("Minimum Difficulty", "The minimum difficulty in which this effect should take place. (1: easy, 2: normal, 3: hard)", 3);
      oldBehaviour = this.loadPropBool("Use Old Behaviour", "Set this to true to use the old behaviour, where the endermen would teleport the player to them", false);
      delay = this.loadPropInt("Delay", "The delay (in ticks) between how often an enderman can break a block.", 10);
      lowerBound = this.loadPropInt("HP Lower Bound", "A value of health for which endermen will stop doing anti-cheese when below. Set to 0 to disable.", 3);
      ignoreMobGriefing = this.loadPropBool("Ignore mobGriefing Gamerule", "", true);
   }

   @SubscribeEvent
   public void onUpdate(LivingUpdateEvent event) {
      if (event.getEntityLiving() instanceof EntityEnderman && event.getEntityLiving().getEntityWorld().getDifficulty().getDifficultyId() >= minimumDifficulty) {
         EntityEnderman entity = (EntityEnderman)event.getEntityLiving();
         if (entity.getHealth() < (float)lowerBound) {
            return;
         }

         BlockPos ourPos = entity.getPosition().up(2);
         IBlockState ourState = entity.getEntityWorld().getBlockState(ourPos);
         if (ourState.getCollisionBoundingBox(entity.getEntityWorld(), ourPos) != null) {
            return;
         }

         EntityLivingBase target = entity.getAttackTarget();
         if (target instanceof EntityPlayer && target.onGround) {
            BlockPos pos = target.getPosition().up(2);
            if (pos.getDistance(ourPos.getX(), ourPos.getY(), ourPos.getZ()) > 5.0D) {
               return;
            }

            if (oldBehaviour) {
               this.teleportPlayer(entity, target, pos);
            } else {
               this.pickupDefense(entity, target, ourPos);
            }
         }
      }

   }

   private void teleportPlayer(EntityEnderman entity, EntityLivingBase target, BlockPos pos) {
      IBlockState state = entity.getEntityWorld().getBlockState(pos);
      if (state.getCollisionBoundingBox(entity.getEntityWorld(), pos) != null) {
         for(int i = 0; i < 16 && !target.attemptTeleport(entity.posX + (Math.random() - 0.5D) * 2.0D, entity.posY + 0.5D, entity.posZ + (Math.random() - 0.5D) * 2.0D); ++i) {
            ;
         }

         target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 30, 0));
         target.getEntityWorld().playSound((EntityPlayer)null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ENDERMEN_SCREAM, SoundCategory.HOSTILE, 1.0F, 1.0F);
         target.getEntityWorld().playSound((EntityPlayer)null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 1.0F, 1.0F);
      }

   }

   private void pickupDefense(EntityEnderman entity, EntityLivingBase target, BlockPos pos) {
      if (entity.ticksExisted % delay == 0 || !ignoreMobGriefing && entity.world.getGameRules().getBoolean("mobGriefing")) {
         Vec3d look = entity.getLookVec();
         pos = pos.add((int)(look.x * 1.2D), 0, (int)(look.z * 1.2D));
         entity.swingArm(EnumHand.MAIN_HAND);
         IBlockState state = entity.world.getBlockState(pos);
         Block block = state.getBlock();
         boolean unbreakable = state.getBlockHardness(entity.world, pos) == -1.0F || !block.canEntityDestroy(state, entity.world, pos, entity);
         if (!unbreakable && state.getCollisionBoundingBox(entity.getEntityWorld(), pos) != null) {
            NonNullList<ItemStack> drops = NonNullList.create();
            block.getDrops(drops, entity.world, pos, state, 0);
            entity.world.setBlockToAir(pos);
            entity.world.playEvent(2001, pos, Block.getStateId(state));
            if (!target.world.isRemote) {
               for(ItemStack drop : drops) {
                  entity.world.spawnEntity(new EntityItem(entity.world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, drop));
               }
            }
         }

      }
   }

   public boolean hasSubscriptions() {
      return true;
   }
}
