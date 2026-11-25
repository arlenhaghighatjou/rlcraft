package org.orecruncher.dsurround.client.handlers.effects;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.effects.EntityEffect;
import org.orecruncher.dsurround.client.effects.IEntityEffectFactory;
import org.orecruncher.dsurround.client.effects.IEntityEffectFactoryFilter;
import org.orecruncher.dsurround.client.sound.ISoundInstance;
import org.orecruncher.dsurround.client.sound.SoundEffect;
import org.orecruncher.dsurround.registry.item.IItemData;
import org.orecruncher.dsurround.registry.item.ItemUtils;
import org.orecruncher.lib.math.RayTrace;

@SideOnly(Side.CLIENT)
public class EntitySwingEffect extends EntityEffect {
   protected int swingProgress = 0;
   protected boolean isSwinging = false;
   public static final IEntityEffectFactoryFilter DEFAULT_FILTER = (e, eei) -> {
      return eei.effects.contains("swing");
   };

   public EntitySwingEffect() {
   }

   @Nonnull
   public String name() {
      return "Item Swing";
   }

   public void update(@Nonnull Entity subject) {
      if (ModOptions.sound.enableSwingSounds) {
         EntityLivingBase entity = (EntityLivingBase)subject;
         if (!(entity.getRidingEntity() instanceof EntityBoat)) {
            if (entity.swingingHand != null && entity.swingProgressInt > this.swingProgress) {
               if (!this.isSwinging) {
                  ItemStack currentItem = entity.getHeldItem(entity.swingingHand);
                  IItemData data = ItemUtils.getItemData(currentItem.getItem());
                  SoundEffect soundEffect = data.getSwingSound(currentItem);
                  if (soundEffect != null) {
                     RayTraceResult whatImHitting = RayTrace.trace(entity);
                     if (whatImHitting == null || whatImHitting.typeOfHit != Type.BLOCK) {
                        ISoundInstance snd = this.getState().createSound(soundEffect, entity);
                        this.getState().playSound(snd);
                     }
                  }
               }

               this.isSwinging = true;
            } else {
               this.isSwinging = false;
            }

            this.swingProgress = entity.swingProgressInt;
         }
      }
   }

   public static class Factory implements IEntityEffectFactory {
      public Factory() {
      }

      @Nonnull
      public List<EntityEffect> create(@Nonnull Entity entity) {
         return ImmutableList.of(new EntitySwingEffect());
      }
   }
}
