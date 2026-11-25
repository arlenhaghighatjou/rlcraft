package com.github.alexthe666.iceandfire.client.model.util;

public final class LegSolverBiped extends LegSolver {
   public final LegSolver.Leg left;
   public final LegSolver.Leg right;

   public LegSolverBiped(float forward, float side) {
      super(new LegSolver.Leg(forward, side), new LegSolver.Leg(forward, -side));
      this.left = this.legs[0];
      this.right = this.legs[1];
   }
}
