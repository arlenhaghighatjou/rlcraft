package org.orecruncher.dsurround.expression;

import java.util.function.Supplier;
import net.minecraft.world.World;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.lib.expression.DynamicVariantList;
import org.orecruncher.lib.expression.Dynamic.DynamicBoolean;
import org.orecruncher.lib.expression.Dynamic.DynamicNumber;
import org.orecruncher.lib.expression.Dynamic.DynamicString;

public class DimensionVariables extends DynamicVariantList {
   public DimensionVariables() {
      this.add(new DynamicNumber("dim.id", () -> {
         return (float)EnvironStateHandler.EnvironState.getDimensionId();
      }));
      this.add(new DynamicString("dim.name", () -> {
         return EnvironStateHandler.EnvironState.getDimensionName();
      }));
      this.add(new DynamicBoolean("dim.hasSky", () -> {
         World world = EnvironStateHandler.EnvironState.getWorld();
         return world != null && world.provider.hasSkyLight();
      }));
   }
}
