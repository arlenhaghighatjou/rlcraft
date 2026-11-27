package meldexun.reachfix.config;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Ignore;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;

@Config(
   modid = "reachfix",
   category = ""
)
public class ReachFixConfig {
   @Name("general")
   public static final ReachFixConfig MASTER_CONFIG = new ReachFixConfig();
   @Ignore
   public static final ReachFixConfig SLAVE_CONFIG = new ReachFixConfig();
   @RangeDouble(
      min = 0.0D,
      max = 1024.0D
   )
   public double entityReach = 3.0D;
   @RangeDouble(
      min = 0.0D,
      max = 1024.0D
   )
   public double entityReachCreative = 3.5D;
   @RangeDouble(
      min = 0.0D,
      max = 1024.0D
   )
   public double reach = 4.5D;
   @RangeDouble(
      min = 0.0D,
      max = 1024.0D
   )
   public double reachCreative = 5.0D;
   @Comment({"When enabled 'canRiderInteract' is ignored when the players eyes are inside the entity that is riding or being ridden by the player."})
   public boolean forceInteractionInsideVehicles = true;

   public ReachFixConfig() {
   }

   public static ReachFixConfig getInstance() {
      return SLAVE_CONFIG;
   }

   public void copyAllSettings(ReachFixConfig dest) {
      dest.entityReach = this.entityReach;
      dest.entityReachCreative = this.entityReachCreative;
      dest.forceInteractionInsideVehicles = this.forceInteractionInsideVehicles;
      dest.reach = this.reach;
      dest.reachCreative = this.reachCreative;
   }

   public void copyClientSettings(ReachFixConfig dest) {
   }

   public void writeServerSettings(ByteBuf buffer) {
      buffer.writeBoolean(true);
      buffer.writeDouble(this.entityReach);
      buffer.writeDouble(this.entityReachCreative);
      buffer.writeBoolean(this.forceInteractionInsideVehicles);
      buffer.writeDouble(this.reach);
      buffer.writeDouble(this.reachCreative);
   }

   public void readServerSettings(ByteBuf buffer) {
      if (buffer.readBoolean()) {
         this.entityReach = buffer.readDouble();
         this.entityReachCreative = buffer.readDouble();
         this.forceInteractionInsideVehicles = buffer.readBoolean();
         this.reach = buffer.readDouble();
         this.reachCreative = buffer.readDouble();
      }

   }
}
