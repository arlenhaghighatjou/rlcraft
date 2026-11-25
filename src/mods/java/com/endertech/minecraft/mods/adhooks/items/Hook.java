package com.endertech.minecraft.mods.adhooks.items;

import com.endertech.common.FloatBounds;
import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.ForgeConfig;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.forge.items.ForgeItem;
import com.endertech.minecraft.mods.adhooks.init.HookType;
import net.minecraft.creativetab.CreativeTabs;

public class Hook extends ForgeItem {
   public static final FloatBounds STRENGTH_BOUNDS = new FloatBounds(0.0F, 20.0F);
   public static final FloatBounds DAMAGE_BOUNDS = new FloatBounds(0.0F, 100.0F);
   public static final FloatBounds RESISTANCE_BOUNDS = new FloatBounds(0.0F, 60.0F);
   public static final FloatBounds WEIGHT_BOUNDS = new FloatBounds(0.0F, 65536.0F);
   public final HookType type;
   public final float resistance;
   public final boolean affectsNPCs;
   public final boolean affectsPlayers;
   public final boolean canHookAnyBlock;
   public final float maxEntityWeight;
   protected float strength;
   protected float damage;

   public Hook(ForgeMod mod, UnitConfig config, HookType type, float strength, float damage, float resistance) {
      super(mod, config, type.name(), CreativeTabs.TOOLS);
      this.type = type;
      this.strength = ForgeConfig.getFloat(config, this.getUnitCategory(), "strength", strength, STRENGTH_BOUNDS, "Defines how heavy duty it can withstand.");
      this.damage = ForgeConfig.getFloat(config, this.getUnitCategory(), "damage", damage, DAMAGE_BOUNDS, "Defines how much damage it will deal to entity on hit.");
      this.resistance = ForgeConfig.getFloat(config, this.getUnitCategory(), "resistance", resistance, RESISTANCE_BOUNDS, "Defines how long it can be under fire influence (in seconds).");
      this.affectsNPCs = ForgeConfig.getBool(config, this.getUnitCategory(), "affectsNPCs", true, "Defines interation with non-playable characters.");
      this.maxEntityWeight = ForgeConfig.getFloat(config, this.getUnitCategory(), "maxEntityWeight", WEIGHT_BOUNDS.max.floatValue(), WEIGHT_BOUNDS, "Defines the maximum weight of an entity that can be grappled.\nWhere weight = entity.width * entity.height (ex: villagerWeight = 0.6 * 1.95 = 1.17, pigWeight = 0.9 * 0.9 = 0.81)");
      this.affectsPlayers = ForgeConfig.getBool(config, this.getUnitCategory(), "affectsPlayers", true, "Defines interation with players.");
      this.canHookAnyBlock = ForgeConfig.getBool(config, this.getUnitCategory(), "canHookAnyBlock", false, "If true, you can hook any block regardless of its material and hardness.");
   }

   public float getStrength() {
      return this.strength;
   }

   public float getDamage() {
      return this.damage;
   }
}
