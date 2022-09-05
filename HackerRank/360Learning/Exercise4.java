import java.util.List;
import java.util.Objects;

public class Exercise4 {

  public static void main(String[] args) {
    System.out.println(countSheep(  // 2
        List.of(
            List.of("Y", "Y", "N"),
            List.of("Y", "N", "N"),
            List.of("N", "N", "Y")
        )
    ));
    System.out.println(countSheep(  // 1
        List.of(
            List.of("Y", "Y", "Y", "Y", "Y"),
            List.of("Y", "N", "N", "N", "Y"),
            List.of("Y", "Y", "Y", "Y", "Y")
        )
    ));
  }

  // Top, Right, Bot, Left
  static int[] neighborsX = { 0, 1, 0, -1 };
  static int[] neighborsY = { 1, 0, -1, 0 };

  public static int countSheep(List<List<String>> farmGrid)  {
    int height = farmGrid.size();
    int width = farmGrid.get(0).size();

    boolean[][] visited = new boolean[width][height];
    int totalSheep = 0;

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (!visited[x][y] && Objects.equals(farmGrid.get(y).get(x), "Y")) {
          System.err.println(x + " - " + y);
          totalSheep++;
          bfs(x, y, farmGrid, visited, width, height);
        }
      }
    }

    return totalSheep;
  }

  static void bfs(int startX, int startY, List<List<String>> farmGrid, boolean[][] visited, int width, int height) {
    visited[startX][startY] = true;

    // Visit all it's neighbors to check if there's grass
    for (int k = 0; k < neighborsX.length; k++) {
      int x = startX + neighborsX[k];
      int y = startY + neighborsY[k];

      if (isInBounds(x, y, width, height) && !visited[x][y] && farmGrid.get(y).get(x) == "Y") {
        bfs(x, y, farmGrid, visited, width, height);
      }
    }
  }

  static boolean isInBounds(int x, int y, int width, int height) {
    return x >= 0 && x < width && y >= 0 && y < height;
  }
}
