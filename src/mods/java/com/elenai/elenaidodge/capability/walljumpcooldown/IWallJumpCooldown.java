package com.elenai.elenaidodge.capability.walljumpcooldown;

public interface IWallJumpCooldown {
   void increase(int var1);

   void decrease(int var1);

   void set(int var1);

   int getWallJumps();
}
