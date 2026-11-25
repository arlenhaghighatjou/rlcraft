package com.charles445.rltweaker.config.init;

import java.util.HashMap;
import java.util.Map;
import net.minecraftforge.fml.common.Loader;

public class JsonConfigLessCollisions {
   public JsonConfigLessCollisions() {
   }

   public static Map<String, Double> getDefaults() {
      Map<String, Double> map = new HashMap();
      double dfv = 2.0D;
      if (Loader.isModLoaded("defiledlands")) {
         map.put("lykrast.defiledlands.common.entity.boss.EntityDestroyer", Double.valueOf(dfv));
         map.put("lykrast.defiledlands.common.entity.boss.EntityMourner", Double.valueOf(dfv));
         map.put("lykrast.defiledlands.common.entity.monster.EntityHost", Double.valueOf(dfv));
         map.put("lykrast.defiledlands.common.entity.monster.EntityScuttler", Double.valueOf(dfv));
         map.put("lykrast.defiledlands.common.entity.monster.EntityShambler", Double.valueOf(dfv));
         map.put("lykrast.defiledlands.common.entity.monster.EntityShamblerTwisted", Double.valueOf(dfv));
         map.put("lykrast.defiledlands.common.entity.monster.EntitySlimeDefiled", Double.valueOf(dfv));
         map.put("lykrast.defiledlands.common.entity.passive.EntityBookWyrm", Double.valueOf(dfv));
      }

      if (Loader.isModLoaded("familiarfauna")) {
         map.put("familiarfauna.entities.EntityButterfly", Double.valueOf(dfv));
         map.put("familiarfauna.entities.EntityDeer", Double.valueOf(dfv));
         map.put("familiarfauna.entities.EntityDragonfly", Double.valueOf(dfv));
         map.put("familiarfauna.entities.EntityPixie", Double.valueOf(dfv));
         map.put("familiarfauna.entities.EntitySnail", Double.valueOf(dfv));
         map.put("familiarfauna.entities.EntityTurkey", Double.valueOf(dfv));
      }

      if (Loader.isModLoaded("iceandfire")) {
         map.put("com.github.alexthe666.iceandfire.entity.EntityCyclops", Double.valueOf(dfv));
         map.put("com.github.alexthe666.iceandfire.entity.EntityCyclopsEye", Double.valueOf(dfv));
         map.put("com.github.alexthe666.iceandfire.entity.EntityDeathWormEgg", Double.valueOf(dfv));
         map.put("com.github.alexthe666.iceandfire.entity.EntityDragonEgg", Double.valueOf(dfv));
         map.put("com.github.alexthe666.iceandfire.entity.EntityDragonSkull", Double.valueOf(dfv));
         map.put("com.github.alexthe666.iceandfire.entity.EntityGorgon", Double.valueOf(dfv));
         map.put("com.github.alexthe666.iceandfire.entity.EntityHippocampus", Double.valueOf(dfv));
         map.put("com.github.alexthe666.iceandfire.entity.EntityHippogryphEgg", Double.valueOf(dfv));
         map.put("com.github.alexthe666.iceandfire.entity.EntityMultipartPart", Double.valueOf(dfv));
         map.put("com.github.alexthe666.iceandfire.entity.EntityMyrmexEgg", Double.valueOf(dfv));
         map.put("com.github.alexthe666.iceandfire.entity.EntityMyrmexQueen", Double.valueOf(dfv));
         map.put("com.github.alexthe666.iceandfire.entity.EntityMyrmexRoyal", Double.valueOf(dfv));
         map.put("com.github.alexthe666.iceandfire.entity.EntityMyrmexSentinel", Double.valueOf(dfv));
         map.put("com.github.alexthe666.iceandfire.entity.EntityMyrmexSoldier", Double.valueOf(dfv));
         map.put("com.github.alexthe666.iceandfire.entity.EntityMyrmexWorker", Double.valueOf(dfv));
         map.put("com.github.alexthe666.iceandfire.entity.EntityPixie", Double.valueOf(dfv));
         map.put("com.github.alexthe666.iceandfire.entity.EntitySeaSerpent", Double.valueOf(dfv));
         map.put("com.github.alexthe666.iceandfire.entity.EntitySeaSerpentBubbles", Double.valueOf(dfv));
         map.put("com.github.alexthe666.iceandfire.entity.EntitySiren", Double.valueOf(dfv));
         map.put("com.github.alexthe666.iceandfire.entity.EntitySnowVillager", Double.valueOf(dfv));
         map.put("com.github.alexthe666.iceandfire.entity.EntityStymphalianBird", Double.valueOf(dfv));
         map.put("com.github.alexthe666.iceandfire.entity.EntityTroll", Double.valueOf(dfv));
      }

      if (Loader.isModLoaded("lycanitesmobs")) {
         map.put("com.lycanitesmobs.core.entity.EntityItemCustom", Double.valueOf(dfv));
         map.put("com.lycanitesmobs.core.entity.creature.EntityArisaur", Double.valueOf(6.0D));
         map.put("com.lycanitesmobs.core.entity.creature.EntityAspid", Double.valueOf(4.0D));
         map.put("com.lycanitesmobs.core.entity.creature.EntityCephignis", Double.valueOf(2.0D));
         map.put("com.lycanitesmobs.core.entity.creature.EntityChupacabra", Double.valueOf(8.0D));
         map.put("com.lycanitesmobs.core.entity.creature.EntityCinder", Double.valueOf(4.0D));
         map.put("com.lycanitesmobs.core.entity.creature.EntityDjinn", Double.valueOf(4.0D));
         map.put("com.lycanitesmobs.core.entity.creature.EntityGrue", Double.valueOf(10.0D));
         map.put("com.lycanitesmobs.core.entity.creature.EntityIka", Double.valueOf(4.0D));
         map.put("com.lycanitesmobs.core.entity.creature.EntityJengu", Double.valueOf(4.0D));
         map.put("com.lycanitesmobs.core.entity.creature.EntityJoust", Double.valueOf(4.0D));
         map.put("com.lycanitesmobs.core.entity.creature.EntityJoustAlpha", Double.valueOf(4.0D));
         map.put("com.lycanitesmobs.core.entity.creature.EntityKobold", Double.valueOf(2.0D));
         map.put("com.lycanitesmobs.core.entity.creature.EntityKrake", Double.valueOf(4.0D));
         map.put("com.lycanitesmobs.core.entity.creature.EntityLacedon", Double.valueOf(4.0D));
         map.put("com.lycanitesmobs.core.entity.creature.EntityMaka", Double.valueOf(6.0D));
         map.put("com.lycanitesmobs.core.entity.creature.EntityMakaAlpha", Double.valueOf(6.0D));
         map.put("com.lycanitesmobs.core.entity.creature.EntityNymph", Double.valueOf(2.0D));
         map.put("com.lycanitesmobs.core.entity.creature.EntityPixen", Double.valueOf(4.0D));
         map.put("com.lycanitesmobs.core.entity.creature.EntityQuillbeast", Double.valueOf(4.0D));
         map.put("com.lycanitesmobs.core.entity.creature.EntityReaper", Double.valueOf(4.0D));
         map.put("com.lycanitesmobs.core.entity.creature.EntityReaver", Double.valueOf(4.0D));
         map.put("com.lycanitesmobs.core.entity.creature.EntitySilex", Double.valueOf(2.0D));
         map.put("com.lycanitesmobs.core.entity.creature.EntityWisp", Double.valueOf(2.0D));
         map.put("com.lycanitesmobs.core.entity.creature.EntityWraamon", Double.valueOf(4.0D));
         map.put("com.lycanitesmobs.core.entity.creature.EntityYale", Double.valueOf(4.0D));
         map.put("com.lycanitesmobs.core.entity.creature.EntityYeti", Double.valueOf(4.0D));
      }

      if (Loader.isModLoaded("trumpetskeleton")) {
         map.put("com.jamieswhiteshirt.trumpetskeleton.common.entity.EntityTrumpetSkeleton", Double.valueOf(dfv));
      }

      if (Loader.isModLoaded("tumbleweed")) {
         map.put("net.konwboy.tumbleweed.common.EntityTumbleweed", Double.valueOf(dfv));
      }

      map.put("net.minecraft.entity.item.EntityArmorStand", Double.valueOf(dfv));
      map.put("net.minecraft.entity.item.EntityItem", Double.valueOf(dfv));
      map.put("net.minecraft.entity.item.EntityItemFrame", Double.valueOf(dfv));
      map.put("net.minecraft.entity.item.EntityPainting", Double.valueOf(dfv));
      map.put("net.minecraft.entity.item.EntityXPOrb", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntityBlaze", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntityCaveSpider", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntityCreeper", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntityElderGuardian", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntityEnderman", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntityEndermite", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntityEvoker", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntityGhast", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntityGuardian", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntityHusk", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntityIllusionIllager", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntityMagmaCube", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntityPigZombie", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntityPolarBear", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntityShulker", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntitySilverfish", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntitySkeleton", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntitySlime", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntitySpider", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntityStray", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntityVex", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntityVindicator", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntityWitch", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntityWitherSkeleton", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntityZombie", Double.valueOf(dfv));
      map.put("net.minecraft.entity.monster.EntityZombieVillager", Double.valueOf(dfv));
      map.put("net.minecraft.entity.passive.EntityBat", Double.valueOf(dfv));
      map.put("net.minecraft.entity.passive.EntityChicken", Double.valueOf(dfv));
      map.put("net.minecraft.entity.passive.EntityCow", Double.valueOf(dfv));
      map.put("net.minecraft.entity.passive.EntityMooshroom", Double.valueOf(dfv));
      map.put("net.minecraft.entity.passive.EntityOcelot", Double.valueOf(dfv));
      map.put("net.minecraft.entity.passive.EntityParrot", Double.valueOf(dfv));
      map.put("net.minecraft.entity.passive.EntityPig", Double.valueOf(dfv));
      map.put("net.minecraft.entity.passive.EntityRabbit", Double.valueOf(dfv));
      map.put("net.minecraft.entity.passive.EntitySheep", Double.valueOf(dfv));
      map.put("net.minecraft.entity.passive.EntitySquid", Double.valueOf(dfv));
      map.put("net.minecraft.entity.passive.EntityVillager", Double.valueOf(dfv));
      return map;
   }
}
