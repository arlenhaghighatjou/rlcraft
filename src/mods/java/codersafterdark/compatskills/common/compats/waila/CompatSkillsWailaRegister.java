package codersafterdark.compatskills.common.compats.waila;

import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;

@WailaPlugin
public class CompatSkillsWailaRegister implements IWailaPlugin {
   public CompatSkillsWailaRegister() {
   }

   public void register(IWailaRegistrar registrar) {
      registrar.addConfig("compatskills", "compatskills.requirements", "Requirements", true);
      CompatSkillsWailaDataProvider provider = new CompatSkillsWailaDataProvider();
      registrar.registerBodyProvider(provider, Block.class);
      registrar.registerBodyProvider(provider, EntityLiving.class);
   }
}
