package mezz.jei.suffixtree;

class Edge {
   private String label;
   private final Node dest;

   public String getLabel() {
      return this.label;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   public Node getDest() {
      return this.dest;
   }

   public Edge(String label, Node dest) {
      this.label = label;
      this.dest = dest;
   }

   public String toString() {
      return "Edge: " + this.label;
   }
}
