package com.lycanitesmobs.core.info.projectile.behaviours;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.helpers.JSONHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ProjectileBehaviourRandomEffect extends ProjectileBehaviour {
   public List<String> effects = new ArrayList();
   public int duration = 100;
   public int amplifier = 0;

   public ProjectileBehaviourRandomEffect() {
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("effects")) {
         this.effects = JSONHelper.getJsonStrings(json.get("effects").getAsJsonArray());
      }

      if (json.has("effectDuration")) {
         this.duration = json.get("effectDuration").getAsInt();
      }

      if (json.has("effectAmplifier")) {
         this.amplifier = json.get("effectAmplifier").getAsInt();
      }

   }

   public void onProjectileDamage(BaseProjectileEntity projectile, World world, EntityLivingBase target, float damage) {
      if (projectile.getThrower() != null && damage > 0.0F) {
         if (this.duration > 0 && this.amplifier >= 0) {
            int randomIndex = world.rand.nextInt(this.effects.size());
            Potion effect = (Potion)GameRegistry.findRegistry(Potion.class).getValue(new ResourceLocation((String)this.effects.get(randomIndex)));
            if (effect != null) {
               target.addPotionEffect(new PotionEffect(effect, this.duration, this.amplifier));
            }

         }
      }
   }
}
