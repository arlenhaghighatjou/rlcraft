package cursedflames.bountifulbaubles.item;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import cursedflames.bountifulbaubles.BountifulBaubles;
import cursedflames.bountifulbaubles.item.base.AGenericItemBauble;
import cursedflames.bountifulbaubles.util.Config;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemTrinketBrokenHeart extends AGenericItemBauble {
   public static final EaglercraftUUID MODIFIER_UUID = EaglercraftUUID.fromString("554f3929-4193-4ae5-a4da-4b528a89ca32");
   static Property regenHearts;

   public ItemTrinketBrokenHeart() {
      super("trinketBrokenHeart", BountifulBaubles.TAB);
      if (regenHearts == null) {
         BountifulBaubles.config.addPropBoolean(this.getRegistryName() + ".regenheartcontainers", "Items", "Whether sleeping regenerates heart containers. If disabled, any broken heart max health decrease will be permanent (until the player dies)", true, Config.EnumPropSide.SYNCED);
         regenHearts = BountifulBaubles.config.getSyncedProperty(this.getRegistryName() + ".regenheartcontainers");
      }

   }

   public BaubleType getBaubleType(ItemStack arg0) {
      return BaubleType.TRINKET;
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onDamage(LivingDamageEvent event) {
      if (event.getEntity() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getEntity();
         if (BaublesApi.isBaubleEquipped(player, ModItems.trinketBrokenHeart) != -1) {
            float healthAfterDamage = player.getHealth() - event.getAmount();
            if (healthAfterDamage < 1.0F) {
               double maxHealthDamage = 1.0D - (double)healthAfterDamage;
               if ((double)player.getMaxHealth() > maxHealthDamage) {
                  IAttributeInstance maxHealth = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
                  AttributeModifier modifier = maxHealth.getModifier(MODIFIER_UUID);
                  double prevMaxHealthDamage = 0.0D;
                  if (modifier != null) {
                     prevMaxHealthDamage = modifier.getAmount();
                     maxHealth.removeModifier(modifier);
                  }

                  modifier = new AttributeModifier(MODIFIER_UUID, "Broken Heart MaxHP drain", prevMaxHealthDamage - maxHealthDamage, 0);
                  maxHealth.applyModifier(modifier);
                  if ((double)event.getAmount() - maxHealthDamage < 0.1D) {
                     event.setCanceled(true);
                  }

                  event.setAmount((float)Math.max((double)event.getAmount() - maxHealthDamage, 0.0D));
                  player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_IRONGOLEM_HURT, SoundCategory.PLAYERS, 0.7F, (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.1F + 0.8F);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerWake(PlayerWakeUpEvent event) {
      if (regenHearts.getBoolean(true)) {
         EntityPlayer player = event.getEntityPlayer();
         IAttributeInstance maxHealth = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
         AttributeModifier modifier = maxHealth.getModifier(MODIFIER_UUID);
         if (modifier != null) {
            maxHealth.removeModifier(modifier);
         }

      }
   }
}
