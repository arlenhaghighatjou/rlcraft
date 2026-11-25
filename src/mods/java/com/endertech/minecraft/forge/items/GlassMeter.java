package com.endertech.minecraft.forge.items;

import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.forge.units.IUnitWithMeshDefinition;
import com.endertech.minecraft.forge.units.UnitId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber
public abstract class GlassMeter extends EquipmentItem implements IUnitWithMeshDefinition {
   protected final Map<ItemStack, Entity> carriers = new ConcurrentHashMap();
   private final TextFormatting textFormatting;

   public GlassMeter(ForgeMod mod, UnitConfig config, String name, CreativeTabs tab, int durability, EntityEquipmentSlot equipSlot, TextFormatting textFormating) {
      super(mod, config, name, tab, durability, UnitId.from("paneGlass"), equipSlot, SoundEvents.BLOCK_GLASS_HIT);
      this.textFormatting = textFormating;
   }

   @SubscribeEvent
   public static void onLeftClickBlock(LeftClickBlock event) {
      EntityPlayer player = event.getEntityPlayer();
      ItemStack stack = player.getHeldItemMainhand();
      Item item = stack.getItem();
      if (item instanceof GlassMeter && !player.isSwingInProgress) {
         ((GlassMeter)item).onHit(stack, player);
      }

   }

   protected void onHit(ItemStack stack, EntityPlayer player) {
      if (notBroken(stack)) {
         damageItem(1, stack, player, false);
         if (isBroken(stack)) {
            player.playSound(SoundEvents.BLOCK_GLASS_BREAK, 1.0F, 1.0F);
         }
      }

   }

   public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
      this.onHit(stack, player);
      return super.onLeftClickEntity(stack, player, entity);
   }

   public boolean onDroppedByPlayer(ItemStack stack, EntityPlayer player) {
      this.onHit(stack, player);
      return super.onDroppedByPlayer(stack, player);
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.BLOCK;
   }

   public TextFormatting getTextFormatting() {
      return this.textFormatting;
   }

   @SideOnly(Side.CLIENT)
   public String getItemStackDisplayName(ItemStack itemStack) {
      return this.getTextFormatting() + super.getItemStackDisplayName(itemStack);
   }
}
