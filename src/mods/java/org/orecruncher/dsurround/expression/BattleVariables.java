package org.orecruncher.dsurround.expression;

import java.util.function.Supplier;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.lib.expression.DynamicVariantList;
import org.orecruncher.lib.expression.Dynamic.DynamicBoolean;
import org.orecruncher.lib.expression.Dynamic.DynamicNumber;

public class BattleVariables extends DynamicVariantList {
   public BattleVariables() {
      this.add(new DynamicBoolean("battle.inBattle", () -> {
         return EnvironStateHandler.EnvironState.getBattleScanner().inBattle();
      }));
      this.add(new DynamicBoolean("battle.isBoss", () -> {
         return EnvironStateHandler.EnvironState.getBattleScanner().isBoss();
      }));
      this.add(new DynamicBoolean("battle.isWither", () -> {
         return EnvironStateHandler.EnvironState.getBattleScanner().isWither();
      }));
      this.add(new DynamicBoolean("battle.isDragon", () -> {
         return EnvironStateHandler.EnvironState.getBattleScanner().isDragon();
      }));
      this.add(new DynamicNumber("battle.hostileCount", () -> {
         return (float)EnvironStateHandler.EnvironState.getBattleScanner().hostileCount();
      }));
   }
}
