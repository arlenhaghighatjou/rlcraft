package sereneseasons.api.config;

public enum SeasonsOption implements ISyncedOption {
   DAY_DURATION("Day Duration"),
   SUB_SEASON_DURATION("Sub Season Duration"),
   STARTING_SUB_SEASON("Starting Sub Season"),
   PROGRESS_SEASON_WHILE_OFFLINE("Progress Season While Offline");

   private final String optionName;

   private SeasonsOption(String name) {
      this.optionName = name;
   }

   public String getOptionName() {
      return this.optionName;
   }
}
