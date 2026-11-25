package c4.champions.common;

import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.init.ChampionsRegistry;
import c4.champions.common.util.ChampionHelper;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootContext.Builder;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.ExplosionEvent.Start;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandlerCommon {
   private static final ResourceLocation CHAMPION_LOOT = new ResourceLocation("champions", "champion_loot");

   public EventHandlerCommon() {
   }

   @SubscribeEvent
   public void livingDeath(LivingDeathEvent evt) {
      EntityLivingBase entityLivingBase = evt.getEntityLiving();
      boolean flag = entityLivingBase.world.getGameRules().getBoolean("showDeathMessages");
      Entity source = evt.getSource().getTrueSource();
      if (source instanceof EntityPlayer) {
         if (flag && !entityLivingBase.world.isRemote && ChampionHelper.isValidChampion(entityLivingBase)) {
            IChampionship chp = CapabilityChampionship.getChampionship((EntityLiving)entityLivingBase);
            int messageTier = ConfigHandler.deathMessageTier;
            if (messageTier > 0 && chp != null && ChampionHelper.isElite(chp.getRank()) && chp.getRank().getTier() >= messageTier) {
               entityLivingBase.getServer().getPlayerList().sendMessage((new TextComponentTranslation("champions.identifier", new Object[0])).appendSibling(new TextComponentString(" ")).appendSibling(entityLivingBase.getCombatTracker().getDeathMessage()));
            }
         }

      }
   }

   @SubscribeEvent
   public void livingDrops(LivingDropsEvent evt) {
      EntityLivingBase entity = evt.getEntityLiving();
      if (entity.world.getGameRules().getBoolean("doMobLoot") && (ConfigHandler.lootFake || !(evt.getSource().getTrueSource() instanceof FakePlayer))) {
         if (ChampionHelper.isValidChampion(entity)) {
            IChampionship chp = CapabilityChampionship.getChampionship((EntityLiving)entity);
            if (chp != null && ChampionHelper.isElite(chp.getRank()) && entity.world instanceof WorldServer) {
               if (ConfigHandler.lootSource != ConfigHandler.LootSource.CONFIG) {
                  WorldServer world = (WorldServer)entity.world;
                  LootTable table = world.getLootTableManager().getLootTableFromLocation(CHAMPION_LOOT);
                  DamageSource source = evt.getSource();
                  Builder builder = (new Builder(world)).withDamageSource(evt.getSource()).withLootedEntity(entity);
                  if (source.getTrueSource() instanceof EntityPlayer) {
                     EntityPlayer player = (EntityPlayer)source.getTrueSource();
                     builder.withPlayer(player).withLuck(player.getLuck());
                  }

                  LootContext ctx = builder.build();

                  for(ItemStack stack : table.generateLootForPools(world.rand, ctx)) {
                     EntityItem entityitem = new EntityItem(entity.world, entity.posX, entity.posY, entity.posZ, stack);
                     entityitem.setDefaultPickupDelay();
                     evt.getDrops().add(entityitem);
                  }
               }

               if (ConfigHandler.lootSource != ConfigHandler.LootSource.LOOT_TABLE) {
                  List<ItemStack> loot = ChampionHelper.getLootDrops(chp.getRank().getTier());
                  if (!loot.isEmpty()) {
                     loot.forEach((lootStack) -> {
                        if (!lootStack.isEmpty()) {
                           EntityItem entityitem = new EntityItem(entity.world, entity.posX, entity.posY, entity.posZ, lootStack);
                           entityitem.setDefaultPickupDelay();
                           evt.getDrops().add(entityitem);
                        }

                     });
                  }
               }
            }
         }

      }
   }

   @SubscribeEvent
   public void livingXP(LivingExperienceDropEvent evt) {
      EntityLivingBase entity = evt.getEntityLiving();
      if (ChampionHelper.isValidChampion(entity)) {
         IChampionship chp = CapabilityChampionship.getChampionship((EntityLiving)entity);
         if (chp != null && ChampionHelper.isElite(chp.getRank())) {
            evt.setDroppedExperience((int)((double)chp.getRank().getGrowthFactor() * ConfigHandler.growth.exp * (double)evt.getOriginalExperience()));
         }
      }

   }

   @SubscribeEvent
   public void livingDamage(LivingHurtEvent evt) {
      if (evt.getSource().getTrueSource() instanceof EntityLiving) {
         EntityLiving entity = (EntityLiving)evt.getSource().getTrueSource();
         if (entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) == null && ChampionHelper.isValidChampion(entity)) {
            IChampionship chp = CapabilityChampionship.getChampionship(entity);
            if (chp != null && ChampionHelper.isElite(chp.getRank())) {
               evt.setAmount(evt.getAmount() * (float)(1.0D + ConfigHandler.growth.attackDamage * (double)chp.getRank().getTier()));
            }
         }
      }

   }

   @SubscribeEvent
   public void onExplosion(Start evt) {
      Explosion explosion = evt.getExplosion();
      EntityLivingBase entityLivingBase = explosion.getExplosivePlacedBy();
      if (entityLivingBase instanceof EntityCreeper && !entityLivingBase.world.isRemote) {
         IChampionship chp = CapabilityChampionship.getChampionship((EntityCreeper)entityLivingBase);
         if (chp != null && ChampionHelper.isElite(chp.getRank())) {
            explosion.size += (float)(ConfigHandler.growth.creeperStrength * chp.getRank().getTier());
         }
      }

   }

   @SubscribeEvent
   public void injuredPotionHealHandler(LivingHealEvent evt) {
      EntityLivingBase entity = evt.getEntityLiving();
      if (entity.isPotionActive(ChampionsRegistry.injured)) {
         evt.setAmount(evt.getAmount() * 0.6F);
      }

   }

   @SubscribeEvent
   public void injuredPotionHurtHandler(LivingDamageEvent evt) {
      if (evt.getEntityLiving().isPotionActive(ChampionsRegistry.injured)) {
         evt.setAmount(evt.getAmount() * 1.4F);
      }

   }
}
