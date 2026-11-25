package org.orecruncher.dsurround.client.handlers.effects;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.effects.EntityEffect;
import org.orecruncher.dsurround.client.effects.IEntityEffectFactory;
import org.orecruncher.dsurround.client.effects.IEntityEffectFactoryFilter;
import org.orecruncher.dsurround.client.effects.IEntityEffectHandlerState;
import org.orecruncher.dsurround.client.sound.ISoundInstance;
import org.orecruncher.dsurround.client.sound.SoundEffect;
import org.orecruncher.dsurround.registry.item.IItemData;
import org.orecruncher.dsurround.registry.item.ItemUtils;

@SideOnly(Side.CLIENT)
public class PlayerToolBarSoundEffect extends EntityEffect {
   protected final PlayerToolBarSoundEffect.MainHandTracker mainHand;
   protected final PlayerToolBarSoundEffect.HandTracker offHand;
   public static final IEntityEffectFactoryFilter DEFAULT_FILTER = (e, eei) -> {
      return eei.effects.contains("toolbar");
   };

   public PlayerToolBarSoundEffect(@Nonnull EntityPlayer player) {
      this.mainHand = new PlayerToolBarSoundEffect.MainHandTracker(player);
      this.offHand = new PlayerToolBarSoundEffect.HandTracker(player);
   }

   @Nonnull
   public String name() {
      return "Toolbar";
   }

   public void update(@Nonnull Entity subject) {
      if (ModOptions.sound.enableEquipSound) {
         this.mainHand.update(this.getState());
         this.offHand.update(this.getState());
      }

   }

   public static class Factory implements IEntityEffectFactory {
      public Factory() {
      }

      @Nonnull
      public List<EntityEffect> create(@Nonnull Entity entity) {
         return ImmutableList.of(new PlayerToolBarSoundEffect((EntityPlayer)entity));
      }
   }

   protected static class HandTracker {
      protected final EnumHand hand;
      protected Item lastHeld;

      public HandTracker(@Nonnull EntityPlayer player) {
         this(player, EnumHand.OFF_HAND);
      }

      protected HandTracker(@Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
         this.hand = hand;
         this.lastHeld = this.getItemForHand(player, hand);
      }

      protected Item getItemForHand(EntityPlayer player, EnumHand hand) {
         ItemStack stack = player.getHeldItem(hand);
         return stack.getItem();
      }

      protected boolean triggerNewEquipSound(@Nonnull EntityPlayer player) {
         Item heldItem = this.getItemForHand(player, this.hand);
         return heldItem != this.lastHeld;
      }

      protected void clearState() {
         this.lastHeld = null;
      }

      public void update(@Nonnull IEntityEffectHandlerState state) {
         Optional<Entity> e = state.subject();
         if (e.isPresent()) {
            EntityPlayer player = (EntityPlayer)e.get();
            if (this.triggerNewEquipSound(player)) {
               this.clearState();
               ItemStack currentStack = player.getHeldItem(this.hand);
               IItemData data = ItemUtils.getItemData(currentStack.getItem());
               SoundEffect soundEffect = data.getEquipSound(currentStack);
               if (soundEffect != null) {
                  ISoundInstance snd = state.createSound(soundEffect, player);
                  state.playSound(snd);
                  this.lastHeld = currentStack.getItem();
               }
            }
         }

      }
   }

   protected static class MainHandTracker extends PlayerToolBarSoundEffect.HandTracker {
      protected int lastSlot;

      public MainHandTracker(@Nonnull EntityPlayer player) {
         super(player, EnumHand.MAIN_HAND);
         this.lastSlot = player.inventory.currentItem;
      }

      protected boolean triggerNewEquipSound(@Nonnull EntityPlayer player) {
         return this.lastSlot != player.inventory.currentItem || super.triggerNewEquipSound(player);
      }

      public void update(@Nonnull IEntityEffectHandlerState state) {
         super.update(state);
         state.subject().ifPresent((e) -> {
            this.lastSlot = ((EntityPlayer)e).inventory.currentItem;
         });
      }
   }
}
