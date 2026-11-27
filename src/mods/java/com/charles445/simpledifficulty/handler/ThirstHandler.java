package com.charles445.simpledifficulty.handler;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.config.json.JsonConsumableThirst;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.compat.CompatRightClick;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.network.MessageDrinkWater;
import com.charles445.simpledifficulty.network.PacketHandler;
import com.charles445.simpledifficulty.util.SoundUtil;
import com.charles445.simpledifficulty.util.internal.ThirstUtilInternal;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Finish;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickEmpty;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.Loader;

public class ThirstHandler {
   private final boolean harvestcraftLoaded = Loader.isModLoaded("harvestcraft");

   public ThirstHandler() {
   }

   public void register(EventBus bus) {
      bus.register(Finish.class, this::onLivingEntityUseItemFinish);
      bus.register(RightClickBlock.class, this::onRightClickBlock);
      bus.register(RightClickEmpty.class, this::onRightClickEmpty);
      bus.register(AttackEntityEvent.class, this::onAttackEntity);
      bus.register(BreakEvent.class, this::onBlockBreak);
      bus.register(LivingHurtEvent.class, this::onLivingHurt);
      bus.register(LivingJumpEvent.class, this::onLivingJump);
   }

   public void onLivingEntityUseItemFinish(Finish event) {
      if (QuickConfig.isThirstEnabled()) {
         if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.getEntityLiving();
            if (player.world.isRemote) {
               return;
            }

            ItemStack stack = event.getItem();
            List<JsonConsumableThirst> consumableList = (List)JsonConfig.consumableThirst.get(stack.getItem().getRegistryName().toString());
            if (consumableList != null) {
               for(JsonConsumableThirst jct : consumableList) {
                  if (jct != null && jct.matches(stack)) {
                     ThirstUtil.takeDrink(player, jct.amount, jct.saturation, jct.thirstyChance);
                     return;
                  }
               }
            }

            if (stack.getItem().equals(Items.POTIONITEM)) {
               PotionType potionType = PotionUtils.getPotionFromItem(stack);
               if (potionType.getRegistryName() != null) {
                  String modDomain = potionType.getRegistryName().getResourceDomain();
                  if (modDomain.equals("minecraft")) {
                     if (potionType.equals(PotionTypes.WATER) || potionType.equals(PotionTypes.AWKWARD) || potionType.equals(PotionTypes.MUNDANE) || potionType.equals(PotionTypes.THICK)) {
                        ThirstUtil.takeDrink(player, ThirstEnum.NORMAL);
                        return;
                     }

                     if (!potionType.equals(PotionTypes.EMPTY)) {
                        ThirstUtil.takeDrink(player, ThirstEnum.POTION);
                        return;
                     }
                  } else if (SDPotions.potionTypes.containsValue(potionType)) {
                     ThirstUtil.takeDrink(player, ThirstEnum.POTION);
                     return;
                  }
               }
            }
         }

      }
   }

   public void onRightClickBlock(RightClickBlock event) {
      if (QuickConfig.isThirstEnabled()) {
         if (event.getWorld().isRemote) {
            if (event.getHand() == EnumHand.MAIN_HAND && this.clientCheckWater(event.getEntityPlayer())) {
               this.clientSendDrinkMessageAndPlaySound(event.getEntityPlayer());
            }
         } else {
            EntityPlayer player = event.getEntityPlayer();
            EnumHand hand = event.getHand();
            if (hand == EnumHand.MAIN_HAND) {
               World world = event.getWorld();
               BlockPos pos = event.getPos();
               IBlockState state = world.getBlockState(pos);
               if (state.getBlock() == Blocks.CAULDRON) {
                  CompatRightClick.cauldronHandler.process(event, world, pos, state, player);
               }
            }
         }

      }
   }

   public void onRightClickEmpty(RightClickEmpty event) {
      if (QuickConfig.isThirstEnabled()) {
         if (event.getHand() == EnumHand.MAIN_HAND && this.clientCheckWater(event.getEntityPlayer())) {
            this.clientSendDrinkMessageAndPlaySound(event.getEntityPlayer());
         }

      }
   }

   private boolean clientCheckWater(EntityPlayer player) {
      if (player.isSneaking() && QuickConfig.isThirstEnabled()) {
         return ThirstUtilInternal.traceWaterToDrink(player) != null;
      } else {
         return false;
      }
   }

   private void clientSendDrinkMessageAndPlaySound(EntityPlayer player) {
      MessageDrinkWater message = new MessageDrinkWater();
      PacketHandler.instance.sendToServer(message);
      player.swingArm(EnumHand.MAIN_HAND);
      SoundUtil.commonPlayPlayerSound(player, SoundEvents.ENTITY_GENERIC_DRINK);
   }

   public void onAttackEntity(AttackEntityEvent event) {
      if (QuickConfig.isThirstEnabled()) {
         World world = event.getEntity().world;
         if (!world.isRemote) {
            EntityPlayer player = event.getEntityPlayer();
            if (!this.shouldSkipThirst(player)) {
               Entity monster = event.getTarget();
               if (monster.canBeAttackedWithItem() && !monster.hitByEntity(player)) {
                  this.addExhaustion(player, (float)ModConfig.server.thirst.thirstAttacking);
               }
            }

         }
      }
   }

   public void onBlockBreak(BreakEvent event) {
      if (QuickConfig.isThirstEnabled()) {
         World world = event.getWorld();
         if (!world.isRemote) {
            EntityPlayer player = event.getPlayer();
            if (!this.shouldSkipThirst(player) && event.getState().getBlock().canHarvestBlock(world, event.getPos(), player)) {
               this.addExhaustion(player, (float)ModConfig.server.thirst.thirstBreakBlock);
            }

         }
      }
   }

   public void onLivingHurt(LivingHurtEvent event) {
      if (QuickConfig.isThirstEnabled()) {
         World world = event.getEntity().world;
         if (!world.isRemote && event.getAmount() != 0.0F) {
            if (event.getEntity() instanceof EntityPlayer) {
               EntityPlayer player = (EntityPlayer)event.getEntity();
               if (!this.shouldSkipThirst(player)) {
                  this.addExhaustion(player, event.getSource().getHungerDamage());
               }
            }

         }
      }
   }

   public void onLivingJump(LivingJumpEvent event) {
      if (QuickConfig.isThirstEnabled()) {
         World world = event.getEntity().world;
         if (!world.isRemote) {
            if (event.getEntity() instanceof EntityPlayer) {
               EntityPlayer player = (EntityPlayer)event.getEntity();
               if (!this.shouldSkipThirst(player)) {
                  if (player.isSprinting()) {
                     this.addExhaustion(player, (float)ModConfig.server.thirst.thirstSprintJump);
                  } else {
                     this.addExhaustion(player, (float)ModConfig.server.thirst.thirstJump);
                  }
               }
            }

         }
      }
   }

   private boolean shouldSkipThirst(EntityPlayer player) {
      return player.isCreative() || player.isSpectator();
   }

   private void addExhaustion(EntityPlayer player, float exhaustion) {
      IThirstCapability capability = SDCapabilities.getThirstData(player);
      capability.addThirstExhaustion(exhaustion);
   }
}
