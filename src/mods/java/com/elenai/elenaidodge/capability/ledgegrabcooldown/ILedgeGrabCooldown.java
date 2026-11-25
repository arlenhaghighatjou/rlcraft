package com.elenai.elenaidodge.capability.ledgegrabcooldown;

public interface ILedgeGrabCooldown {
   void increase(int var1);

   void decrease(int var1);

   void set(int var1);

   int getLedgeGrabs();
}
