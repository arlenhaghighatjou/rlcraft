package com.elenai.elenaidodge.capability.walljumpcooldown;

public class WallJumpCooldown implements IWallJumpCooldown {
   private int wallJumps = 0;

   public WallJumpCooldown() {
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
