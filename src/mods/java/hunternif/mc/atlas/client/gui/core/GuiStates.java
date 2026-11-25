package hunternif.mc.atlas.client.gui.core;

public class GuiStates {
   private volatile GuiStates.IState currentState;

   public GuiStates() {
   }

   public GuiStates.IState current() {
      return this.currentState;
   }

   public boolean is(GuiStates.IState state) {
      return this.currentState == state;
   }

   public void switchTo(GuiStates.IState state) {
      if (this.currentState != null) {
         this.currentState.onExitState();
      }

      this.currentState = state;
      if (state != null) {
         state.onEnterState();
      }

   }

   public interface IState {
      void onEnterState();

      void onExitState();
   }

   public static class SimpleState implements GuiStates.IState {
      public SimpleState() {
      }

      public void onEnterState() {
      }

      public void onExitState() {
      }
   }
}
