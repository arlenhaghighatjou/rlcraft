package crafttweaker.mc1120.game;

import crafttweaker.api.game.ITeam;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.scoreboard.Team;

public class MCTeam implements ITeam {
   private final Team team;

   public MCTeam(Team team) {
      this.team = team;
   }

   public String getName() {
      return this.team.getName();
   }

   public String formatString(String input) {
      return this.team.formatString(input);
   }

   public boolean getAllowFriendlyFire() {
      return this.team.getAllowFriendlyFire();
   }

   public String getColorPrefix() {
      return this.team.getColor().toString();
   }

   public List<String> getMembershipCollection() {
      return new LinkedList(this.team.getMembershipCollection());
   }

   public String getDeathMessageVisibility() {
      return this.team.getDeathMessageVisibility().internalName;
   }

   public String getCollisionRule() {
      return this.team.getCollisionRule().name;
   }

   public Team getInternal() {
      return this.team;
   }
}
