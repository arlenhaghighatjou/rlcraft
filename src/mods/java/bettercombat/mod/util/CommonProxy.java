package bettercombat.mod.util;

import bettercombat.mod.capability.StorageOffHandAttack;
import bettercombat.mod.capability.StorageSecondHurtTimer;
import bettercombat.mod.combat.DefaultImplOffHandAttack;
import bettercombat.mod.combat.DefaultImplSecondHurtTimer;
import bettercombat.mod.combat.IOffHandAttack;
import bettercombat.mod.combat.ISecondHurtTimer;
import bettercombat.mod.network.PacketHandler;
import java.util.concurrent.Callable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
   public CommonProxy() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      PacketHandler.registerMessages("bettercombatmod");
      CapabilityManager.INSTANCE.register(IOffHandAttack.class, new StorageOffHandAttack(), DefaultImplOffHandAttack::new);
      CapabilityManager.INSTANCE.register(ISecondHurtTimer.class, new StorageSecondHurtTimer(), DefaultImplSecondHurtTimer::new);
   }

   public void spawnSweep(EntityPlayer player) {
   }
}
