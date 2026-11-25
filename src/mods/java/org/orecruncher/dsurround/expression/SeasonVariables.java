package org.orecruncher.dsurround.expression;

import java.util.function.Supplier;
import net.minecraft.world.World;
import org.orecruncher.dsurround.capabilities.CapabilitySeasonInfo;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.lib.expression.DynamicVariantList;
import org.orecruncher.lib.expression.Dynamic.DynamicString;

public class SeasonVariables extends DynamicVariantList {
   public SeasonVariables() {
      this.add(new DynamicString("season.season", () -> {
         World world = EnvironStateHandler.EnvironState.getWorld();
         return CapabilitySeasonInfo.getCapability(world).getSeasonString();
      }));
      this.add(new DynamicString("season.type", () -> {
         World world = EnvironStateHandler.EnvironState.getWorld();
         return CapabilitySeasonInfo.getCapability(world).getSeasonType().getValue();
      }));
      this.add(new DynamicString("season.subtype", () -> {
         World world = EnvironStateHandler.EnvironState.getWorld();
         return CapabilitySeasonInfo.getCapability(world).getSeasonSubType().getValue();
      }));
   }
}
