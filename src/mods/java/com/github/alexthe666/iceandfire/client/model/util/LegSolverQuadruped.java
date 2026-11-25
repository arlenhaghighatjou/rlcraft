package com.github.alexthe666.iceandfire.client.model.util;

public final class LegSolverQuadruped extends LegSolver {
   public final LegSolver.Leg backLeft;
   public final LegSolver.Leg backRight;
   public final LegSolver.Leg frontLeft;
   public final LegSolver.Leg frontRight;

   public LegSolverQuadruped(float forward, float side) {
      this(0.0F, forward, side);
   }

   public LegSolverQuadruped(float forwardCenter, float forward, float side) {
      super(new LegSolver.Leg(forwardCenter - forward, side), new LegSolver.Leg(forwardCenter - forward, -side), new LegSolver.Leg(forwardCenter + forward, side), new LegSolver.Leg(forwardCenter + forward, -side));
      this.backLeft = this.legs[0];
      this.backRight = this.legs[1];
      this.frontLeft = this.legs[2];
      this.frontRight = this.legs[3];
   }
}
