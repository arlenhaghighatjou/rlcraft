package xzeroair.trinkets.util.interfaces;

import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import xzeroair.trinkets.api.ITrinketInterface;

public interface IAccessoryInterface extends ITrinketInterface {
   default void eventClientTick(ItemStack stack, EntityPlayer player) {
   }

   default void eventPlayerTick(ItemStack stack, EntityPlayer player) {
   }

   default void eventLivingUpdateTick(ItemStack stack, EntityLivingBase entity) {
   }

   default void eventPlayerLogin(ItemStack stack, EntityPlayer player) {
   }

   default void eventPlayerLogout(ItemStack stack, EntityPlayer player) {
   }

   default void eventPlayerChangedDimension(ItemStack stack, EntityPlayer player, int fromDimension, int toDimension) {
   }

   default void eventLivingJump(ItemStack stack, EntityLivingBase entity) {
   }

   default void eventLivingFall(ItemStack stack, EntityLivingBase entity, LivingFallEvent event) {
   }

   default void eventSetAttackTarget(ItemStack stack, EntityLivingBase target, EntityLivingBase targeter) {
   }

   default void eventLivingHurtAttacked(@Nonnull ItemStack stack, @Nonnull EntityLivingBase attacked, @Nonnull LivingHurtEvent event) {
   }

   default void eventLivingHurtAttacker(@Nonnull ItemStack stack, @Nonnull EntityLivingBase attacker, @Nonnull LivingHurtEvent event) {
   }

   default void eventLivingDamageAttacked(ItemStack stack, EntityLivingBase attacked, LivingDamageEvent event) {
   }

   default void eventLivingDamageAttacker(ItemStack stack, EntityLivingBase attacker, LivingDamageEvent event) {
   }

   default void eventLivingExperienceDrops(ItemStack stack, EntityPlayer player, LivingExperienceDropEvent event) {
   }

   default void eventLivingDrops(ItemStack stack, EntityLivingBase attacker, LivingDropsEvent event) {
   }

   default void eventBreakSpeed(ItemStack stack, EntityPlayer player, BreakSpeed event) {
   }

   default void eventBlockBreak(ItemStack stack, EntityPlayer player, BreakEvent event) {
   }

   default void eventBlockDrops(ItemStack stack, EntityLivingBase player, HarvestDropsEvent event) {
   }

   default void eventPotionApplicable(ItemStack stack, EntityLivingBase entity, PotionApplicableEvent event) {
   }

   default void onEntityArmorTick(World world, EntityLivingBase entity, ItemStack stack) {
   }

   default void onAccessoryEquipped(ItemStack stack, EntityLivingBase entity) {
   }

   default void onAccessoryUnequipped(ItemStack stack, EntityLivingBase entity) {
   }

   default boolean canEquipAccessory(ItemStack stack, EntityLivingBase entity) {
      return true;
   }

   default boolean canUnequipAccessory(ItemStack stack, EntityLivingBase entity) {
      return true;
   }

   default void playerRenderLayer(ItemStack stack, EntityLivingBase player, RenderPlayer renderer, boolean isSlim, float partialTicks, float scale) {
   }

   default boolean ItemEnabled() {
      return true;
   }
}
