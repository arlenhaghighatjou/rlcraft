package rtg.api.util.noise;

@FunctionalInterface
public interface CellularNoise {
   VoronoiResult eval2D(double var1, double var3);
}
