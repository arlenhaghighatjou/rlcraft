package org.orecruncher.dsurround.client.handlers.effects;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.effects.EntityEffect;
import org.orecruncher.dsurround.client.effects.IEntityEffectFactory;
import org.orecruncher.dsurround.client.effects.IEntityEffectFactoryFilter;
import org.orecruncher.dsurround.client.sound.ISoundInstance;
import org.orecruncher.dsurround.client.sound.SoundEffect;
import org.orecruncher.dsurround.registry.item.IItemData;
import org.orecruncher.dsurround.registry.item.ItemClass;
import org.orecruncher.dsurround.registry.item.ItemUtils;
import org.orecruncher.lib.ItemStackUtil;

@SideOnly(Side.CLIENT)
public class EntityBowSoundEffect extends EntityEffect {
   private ItemStack lastActiveStack = ItemStack.EMPTY;
   public static final IEntityEffectFactoryFilter DEFAULT_FILTER = (e, eei) -> {
      return eei.effects.contains("bow");
   };

   public EntityBowSoundEffect() {
   }

   @Nonnull
   public String name() {
      return "Bow Sound";
   }

   public void update(@Nonnull Entity subject) {
      EntityLivingBase entity = (EntityLivingBase)subject;
      ItemStack currentStack = entity.getActiveItemStack();
      if (ItemStackUtil.isValidItemStack(currentStack)) {
         if (!ItemStack.areItemStacksEqual(currentStack, this.lastActiveStack)) {
            IItemData data = ItemUtils.getItemData(currentStack.getItem());
            ItemClass itemClass = data.getItemClass();
            if (itemClass == ItemClass.BOW || itemClass == ItemClass.SHIELD) {
               SoundEffect soundEffect = data.getUseSound(currentStack);
               if (soundEffect != null) {
                  ISoundInstance fx = this.getState().createSound(soundEffect, entity);
                  this.getState().playSound(fx);
               }
            }

            this.lastActiveStack = currentStack;
         }
      } else {
         this.lastActiveStack = ItemStack.EMPTY;
      }

   }

   public static class Factory implements IEntityEffectFactory {
      public Factory() {
      }

      @Nonnull
      public List<EntityEffect> create(@Nonnull Entity entity) {
         return ImmutableList.of(new EntityBowSoundEffect());
      }
   }
}
