package team.creative.enhancedvisuals.common.handler;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.api.CreativeConfig.DecimalRange;
import com.creativemd.creativecore.common.config.premade.DecimalMinMax;
import com.creativemd.creativecore.common.config.premade.IntMinMax;
import com.creativemd.creativecore.common.config.premade.curve.DecimalCurve;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import team.creative.enhancedvisuals.api.VisualHandler;
import team.creative.enhancedvisuals.api.event.FireParticlesEvent;
import team.creative.enhancedvisuals.api.type.VisualType;
import team.creative.enhancedvisuals.api.type.VisualTypeOverlay;
import team.creative.enhancedvisuals.api.type.VisualTypeParticle;
import team.creative.enhancedvisuals.api.type.VisualTypeParticleColored;
import team.creative.enhancedvisuals.client.VisualManager;
import team.creative.enhancedvisuals.common.packet.DamagePacket;

public class DamageHandler extends VisualHandler {
   public static final ArrayList<Item> sharpList = new ArrayList();
   public static final ArrayList<Item> bluntList = new ArrayList();
   public static final ArrayList<Item> pierceList = new ArrayList();
   @CreativeConfig
   public VisualType damaged = new VisualTypeOverlay("damaged");
   @CreativeConfig
   @DecimalRange(
      min = 0.0D,
      max = 1.0D
   )
   public float hitEffectIntensity = 0.0F;
   @CreativeConfig
   public IntMinMax hitDuration = new IntMinMax(1, 10);
   @CreativeConfig
   public VisualType splatter = new VisualTypeParticle("splatter");
   @CreativeConfig
   public VisualType impact = new VisualTypeParticle("impact");
   @CreativeConfig
   public VisualType slash = new VisualTypeParticle("slash");
   @CreativeConfig
   public VisualType pierce = new VisualTypeParticle("pierce");
   @CreativeConfig
   public IntMinMax bloodDuration = new IntMinMax(500, 1500);
   @CreativeConfig
   public VisualType fire = new VisualTypeParticle("fire");
   @CreativeConfig
   public int fireSplashes = 1;
   @CreativeConfig
   public IntMinMax fireDuration = new IntMinMax(100, 1000);
   @CreativeConfig
   public int drownSplashes = 4;
   @CreativeConfig
   public IntMinMax drownDuration = new IntMinMax(10, 15);
   @CreativeConfig
   public VisualType waterDrown = (new VisualTypeParticleColored("blob", new Color(0, 0, 255))).setIgnoreWater();
   @CreativeConfig
   public int lightningSplashes = 10;
   @CreativeConfig
   public IntMinMax lightningDuration = new IntMinMax(10, 15);
   @CreativeConfig
   public VisualType lightning = (new VisualTypeParticleColored("shock", new Color(120, 120, 255))).setIgnoreWater();
   @CreativeConfig
   public int freezeSplashes = 4;
   @CreativeConfig
   public IntMinMax freezeDuration = new IntMinMax(10, 15);
   @CreativeConfig
   public VisualType freeze = new VisualTypeParticleColored("ice", new Color(200, 255, 255));
   @CreativeConfig
   public int flyIntoWallSplashes = 4;
   @CreativeConfig
   public IntMinMax flyIntoWallDuration = new IntMinMax(10, 15);
   @CreativeConfig
   public VisualType flyIntoWall = new VisualTypeParticleColored("break", new Color(255, 255, 255));
   @CreativeConfig
   public int heatSplashes = 4;
   @CreativeConfig
   public IntMinMax heatDuration = new IntMinMax(10, 15);
   @CreativeConfig
   public VisualType heat = new VisualTypeParticleColored("heat", new Color(255, 255, 255)) {
      public boolean canRotate() {
         return false;
      }
   };
   @CreativeConfig
   public int effectSplashes = 4;
   @CreativeConfig
   public IntMinMax effectDuration = new IntMinMax(10, 15);
   @CreativeConfig
   public VisualType parasites = (new VisualTypeParticleColored("parasite", new Color(0, 126, 0))).setIgnoreWater();
   @CreativeConfig
   public VisualType wither = (new VisualTypeParticleColored("wither", 0, new DecimalMinMax(1.5D, 2.5D), new Color(0, 0, 0))).setIgnoreWater();
   @CreativeConfig
   public IntMinMax tunnelDuration = new IntMinMax(10, 15);
   @CreativeConfig
   public VisualType tunnel = (new VisualTypeOverlay("tunnel", 0)).setIgnoreWater();
   @CreativeConfig
   public DecimalCurve healthScaler = new DecimalCurve(0.0D, 3.0D, 12.0D, 1.5D);
   @CreativeConfig
   public float damageScale = 1.0F;
   @CreativeConfig
   public List<String> damageBlackList = new ArrayList();
   public static Color bloodColor = new Color(0.3F, 0.01F, 0.01F, 0.7F);

   public DamageHandler() {
   }

   public void clientHurt() {
      if (this.hitEffectIntensity > 0.0F) {
         VisualManager.addVisualFadeOut(this.damaged, this, new DecimalCurve(VisualManager.rand, this.hitDuration, (double)this.hitEffectIntensity * 0.2D));
      }

   }

   public void playerDamaged(EntityPlayer player, DamagePacket packet) {
      if (packet.source.equalsIgnoreCase("attacker")) {
         if (packet.attackerClass.contains("arrow")) {
            this.createVisualFromDamageAndDistance(this.pierce, packet.damage, player, this.bloodDuration);
         }

         if (packet.stack != null) {
            if (isSharp(packet.stack)) {
               this.createVisualFromDamageAndDistance(this.slash, packet.damage, player, this.bloodDuration);
            } else if (isBlunt(packet.stack)) {
               this.createVisualFromDamageAndDistance(this.impact, packet.damage, player, this.bloodDuration);
            } else if (isPierce(packet.stack)) {
               this.createVisualFromDamageAndDistance(this.pierce, packet.damage, player, this.bloodDuration);
            } else {
               this.createVisualFromDamageAndDistance(this.splatter, packet.damage, player, this.bloodDuration);
            }
         } else if (!packet.attackerClass.contains("zombie") && !packet.attackerClass.contains("skeleton") && !packet.attackerClass.contains("ocelot")) {
            if (!packet.attackerClass.contains("golem") && !packet.attackerClass.contains("player")) {
               if (!packet.attackerClass.contains("wolf") && !packet.attackerClass.contains("spider")) {
                  this.createVisualFromDamageAndDistance(this.splatter, Math.min(20.0F, packet.damage), player, this.bloodDuration);
               } else {
                  this.createVisualFromDamageAndDistance(this.pierce, packet.damage, player, this.bloodDuration);
               }
            } else {
               this.createVisualFromDamageAndDistance(this.impact, packet.damage, player, this.bloodDuration);
            }
         } else {
            this.createVisualFromDamageAndDistance(this.slash, packet.damage, player, this.bloodDuration);
         }
      } else if (packet.source.equalsIgnoreCase("cactus")) {
         this.createVisualFromDamageAndDistance(this.pierce, packet.damage, player, this.bloodDuration);
      } else if (!packet.source.equalsIgnoreCase("fall") && !packet.source.equalsIgnoreCase("fallingBlock")) {
         if (packet.source.equalsIgnoreCase("drown")) {
            VisualManager.addParticlesFadeOut(this.waterDrown, this, this.drownSplashes, this.drownDuration, true);
         } else if (packet.source.equalsIgnoreCase("hypothermia")) {
            VisualManager.addParticlesFadeOut(this.freeze, this, this.freezeSplashes, this.freezeDuration, true);
         } else if (packet.source.equalsIgnoreCase("hyperthermia")) {
            VisualManager.addParticlesFadeOut(this.heat, this, this.heatSplashes, this.heatDuration, true);
         } else if (packet.source.equalsIgnoreCase("wither")) {
            VisualManager.addParticlesFadeOut(this.wither, this, this.effectSplashes, this.effectDuration, true);
         } else if (packet.source.equalsIgnoreCase("parasites")) {
            VisualManager.addParticlesFadeOut(this.parasites, this, this.effectSplashes, this.effectDuration, true);
         } else if (packet.source.equalsIgnoreCase("lightningBolt")) {
            VisualManager.addParticlesFadeOut(this.lightning, this, this.lightningSplashes, this.lightningDuration, true);
         } else if (packet.source.equalsIgnoreCase("flyIntoWall")) {
            VisualManager.addParticlesFadeOut(this.flyIntoWall, this, this.flyIntoWallSplashes, this.flyIntoWallDuration, true);
         } else if (!packet.source.equalsIgnoreCase("dehydration") && !packet.source.equalsIgnoreCase("starve")) {
            if (!packet.fire && !packet.source.equalsIgnoreCase("onFire")) {
               if (!this.damageBlackList.contains(packet.source)) {
                  this.createVisualFromDamageAndDistance(this.splatter, Math.min(20.0F, packet.damage), player, this.bloodDuration);
               }
            } else {
               FireParticlesEvent event = new FireParticlesEvent(this.fireSplashes, this.fireDuration.min, this.fireDuration.max);
               MinecraftForge.EVENT_BUS.post(event);
               VisualManager.addParticlesFadeOut(this.fire, this, event.getNewFireSplashes(), new IntMinMax(event.getNewFireDurationMin(), event.getNewFireDurationMax()), true, new Color(0, 0, 0));
            }
         } else {
            VisualManager.addVisualFadeOut(this.tunnel, this, this.tunnelDuration);
         }
      } else {
         this.createVisualFromDamageAndDistance(this.impact, packet.damage, player, this.bloodDuration);
      }

   }

   public void createVisualFromDamageAndDistance(VisualType type, float damage, EntityPlayer player, IntMinMax duration) {
      if (damage > 0.0F) {
         float health = player.getHealth() - damage;
         double rate = Math.max(0.0D, this.healthScaler.valueAt((double)health));
         VisualManager.addParticlesFadeOut(type, this, (int)((double)(this.damageScale * damage) * rate), new DecimalCurve(0.0D, 1.0D, (double)duration.next(VisualManager.rand), 0.0D), true, bloodColor);
      }
   }

   private static boolean isSharp(ItemStack item) {
      return sharpList.contains(item.getItem());
   }

   private static boolean isBlunt(ItemStack item) {
      return bluntList.contains(item.getItem());
   }

   private static boolean isPierce(ItemStack item) {
      return pierceList.contains(item.getItem());
   }

   static {
      sharpList.add(Items.IRON_SWORD);
      sharpList.add(Items.WOODEN_SWORD);
      sharpList.add(Items.STONE_SWORD);
      sharpList.add(Items.DIAMOND_SWORD);
      sharpList.add(Items.GOLDEN_SWORD);
      sharpList.add(Items.IRON_AXE);
      sharpList.add(Items.WOODEN_AXE);
      sharpList.add(Items.STONE_AXE);
      sharpList.add(Items.DIAMOND_AXE);
      sharpList.add(Items.GOLDEN_AXE);
      bluntList.add(Items.IRON_PICKAXE);
      bluntList.add(Items.WOODEN_PICKAXE);
      bluntList.add(Items.STONE_PICKAXE);
      bluntList.add(Items.DIAMOND_PICKAXE);
      bluntList.add(Items.GOLDEN_PICKAXE);
      bluntList.add(Items.IRON_SHOVEL);
      bluntList.add(Items.WOODEN_SHOVEL);
      bluntList.add(Items.STONE_SHOVEL);
      bluntList.add(Items.DIAMOND_SHOVEL);
      bluntList.add(Items.GOLDEN_SHOVEL);
      pierceList.add(Items.IRON_HOE);
      pierceList.add(Items.WOODEN_HOE);
      pierceList.add(Items.STONE_HOE);
      pierceList.add(Items.DIAMOND_HOE);
      pierceList.add(Items.GOLDEN_HOE);
      pierceList.add(Items.ARROW);
   }
}
