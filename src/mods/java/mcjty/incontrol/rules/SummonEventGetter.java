package mcjty.incontrol.rules;

import mcjty.tools.rules.RuleBase.EventGetter;
import net.minecraft.entity.monster.EntityZombie;

public interface SummonEventGetter extends EventGetter {
   EntityZombie getZombieHelper();
}
