package net.ilexiconn.llibrary.server.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.ilexiconn.llibrary.server.structure.rule.PlaceRule;
import net.ilexiconn.llibrary.server.structure.rule.RepeatRule;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class ComponentInfo {
   public HashMap<BlockPos, BlockList> blocks = new HashMap();
   public List<RepeatRule> repeats = new ArrayList();
   public EnumFacing front = EnumFacing.EAST;
   public EnumFacing top = EnumFacing.UP;

   public ComponentInfo() {
      this.repeats.add(new PlaceRule());
   }
}
