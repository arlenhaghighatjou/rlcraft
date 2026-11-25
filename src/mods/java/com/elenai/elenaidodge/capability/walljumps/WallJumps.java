package com.elenai.elenaidodge.capability.walljumps;

public class WallJumps implements IWallJumps {
   private int wallJumps = 0;

   public WallJumps() {
   }

   public void increase(int wallJumps) {
      this.wallJumps += wallJumps;
   }

   public void decrease(int wallJumps) {
      this.wallJumps -= wallJumps;
   }

   public void set(int wallJumps) {
      this.wallJumps = wallJumps;
   }

   public int getWallJumps() {
      return this.wallJumps;
   }
}
