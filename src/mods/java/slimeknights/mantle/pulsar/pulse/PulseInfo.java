package slimeknights.mantle.pulsar.pulse;

public final class PulseInfo {
   private final String id;
   private final String description;
   private final String modsRequired;
   private final String pulsesRequired;
   private final boolean forced;
   private final boolean defaultEnable;

   public PulseInfo(String id, String description, String modsRequired, String pulsesRequired, boolean forced, boolean defaultEnable) {
      this.id = id;
      this.description = description;
      this.modsRequired = modsRequired;
      this.pulsesRequired = pulsesRequired;
      this.forced = forced;
      this.defaultEnable = defaultEnable;
   }

   public String id() {
      return this.id;
   }

   public String description() {
      return this.description;
   }

   public String modsRequired() {
      return this.modsRequired;
   }

   public String pulsesRequired() {
      return this.pulsesRequired;
   }

   public boolean forced() {
      return this.forced;
   }

   public boolean defaultEnable() {
      return this.defaultEnable;
   }
}
