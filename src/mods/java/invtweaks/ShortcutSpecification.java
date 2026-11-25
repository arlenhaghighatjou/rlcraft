package invtweaks;

public class ShortcutSpecification {
   private ShortcutSpecification.Action action;
   private ShortcutSpecification.Target target;
   private ShortcutSpecification.Scope scope;

   public ShortcutSpecification(ShortcutSpecification.Action a, ShortcutSpecification.Target t, ShortcutSpecification.Scope s) {
      this.action = a;
      this.target = t;
      this.scope = s;
   }

   public ShortcutSpecification.Action getAction() {
      return this.action;
   }

   public void setAction(ShortcutSpecification.Action action_) {
      this.action = action_;
   }

   public ShortcutSpecification.Target getTarget() {
      return this.target;
   }

   public void setTarget(ShortcutSpecification.Target target_) {
      this.target = target_;
   }

   public ShortcutSpecification.Scope getScope() {
      return this.scope;
   }

   public void setScope(ShortcutSpecification.Scope scope_) {
      this.scope = scope_;
   }

   public static enum Action {
      MOVE,
      DROP;

      private Action() {
      }
   }

   public static enum Scope {
      EVERYTHING,
      ALL_ITEMS,
      ONE_STACK,
      ONE_ITEM;

      private Scope() {
      }
   }

   public static enum Target {
      UP,
      DOWN,
      HOTBAR_SLOT,
      UNSPECIFIED;

      private Target() {
      }
   }
}
