package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

public class ConfigTAN {
   @Comment({"Master switch for this mod compatibility"})
   @Name("ENABLED")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean enabled = true;
   @Comment({"Fixes thirst drain on teleportation"})
   @Name("Teleport Thirst Fix")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean fixTeleportThirst = true;
   @Comment({"Exhaustion change in a tick for the teleport fix to kick in."})
   @Name("Teleport Thirst Threshold")
   @RangeDouble(
      min = 1.0D,
      max = 40.0D
   )
   @RLConfig.ImprovementsOnly("8.0")
   @RLConfig.RLCraftTwoEightTwo("8.0")
   @RLConfig.RLCraftTwoNine("8.0")
   public double teleportThirstThreshold = 8.0D;
   @Comment({"Regularly send players extra Thirst packets"})
   @Name("Send Extra Thirst Packets")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean sendExtraThirstPackets = true;
   @Comment({"How often extra Thirst packets get sent out, in ticks"})
   @Name("Thirst Packet Frequency")
   @RangeInt(
      min = 1
   )
   @RLConfig.ImprovementsOnly("20")
   @RLConfig.RLCraftTwoEightTwo("20")
   @RLConfig.RLCraftTwoNine("20")
   public int extraThirstPacketFrequency = 20;
   @Comment({"Prevents Tough As Nails from creating an extra attack entity event. Does nothing if ISeeDragons is in the pack."})
   @Name("Fix Extra Attack Bug")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("false")
   public boolean fixExtraAttackBug = false;
   @Comment({"Fixes TAN bug where holding spacebar after dismounting glitches your jump and drains your thirst"})
   @Name("Fix Dismount Thirst Drain Bug")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("false")
   public boolean fixDismountThirstDrainBug = true;

   public ConfigTAN() {
   }
}
