package com.coding.challenges.adventofcode.year2020.Day20;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day20 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day20 day = new Day20();

    day.printPart1("sample-input", 20899048083289L);
    day.printPart1("input", 4006801655873L);

    day.printPart2("sample-input", 273L);
    day.printPart2("input", 1838L);
  }

  @Override
  public Long part1(List<String> lines) {
    return getCorners(extractImages(lines).values().stream().toList()).stream()
        .mapToLong(i -> i.id)
        .reduce(1, (a, b) -> a * b);
  }

  @Override
  public Long part2(List<String> lines) {
    Map<Long, Image> images = extractImages(lines);
    List<Image> corners = getCorners(images.values().stream().toList());

    // Find the damn top left corner
    Image topLeftCorner = corners.get(3);
    List<List<Character>> edges1 = topLeftCorner.getEdges();
    boolean[] matchingSides = new boolean[8];
    for (Image image2 : images.values()) {
      if (topLeftCorner.id == image2.id) {
        continue;
      }
      List<List<Character>> edges2 = image2.getEdges();
      for (int i = 0; i < edges1.size(); i++) {
        for (List<Character> e2 : edges2) {
          if (edges1.get(i).equals(e2)) {
            matchingSides[i] = true;
            break;
          }
        }
      }
    }
    if (matchingSides[0] && matchingSides[1]) {
      topLeftCorner = topLeftCorner.rotateRight();
    } else if (matchingSides[2] && matchingSides[3]) {
      topLeftCorner = topLeftCorner.rotateRight().rotateRight().rotateRight();
    } else if (matchingSides[3] && matchingSides[0]) {
      topLeftCorner = topLeftCorner.rotateRight().rotateRight();
    }

    // Start creating grid
    int gridSize = (int) Math.sqrt(images.size());
    Image[][] grid = new Image[gridSize][gridSize];
    images.put(topLeftCorner.id, topLeftCorner);
    grid[0][0] = topLeftCorner;
    // Add top row
    for (int j = 1; j < gridSize; j++) {
      Image previous = grid[0][j - 1];
      List<Character> previousEdge = previous.getEdges().get(1);
      Image next =
          images.values().stream()
              .filter(image -> image.id != previous.id)
              .filter(image -> image.getEdges().contains(previousEdge))
              .findFirst()
              .get();
      grid[0][j] = next.rotateTillMatchesEdge(previousEdge, 3);
      images.remove(next.id);
    }
    // Add all columns, one by one
    for (int j = 0; j < gridSize; j++) {
      for (int i = 1; i < gridSize; i++) {
        Image previous = grid[i - 1][j];
        List<Character> previousEdge = previous.getEdges().get(2);
        Image next =
            images.values().stream()
                .filter(image -> image.id != previous.id)
                .filter(image -> image.getEdges().contains(previousEdge))
                .findFirst()
                .get();
        grid[i][j] = next.rotateTillMatchesEdge(previousEdge, 0);
        images.remove(next.id);
      }
    }

    List<List<Character>> gridImage = new ArrayList<>();
    for (int i = 0; i < gridSize; i++) {
      for (int k = 1; k < 9; k++) {
        List<Character> row = new ArrayList<>();
        for (int j = 0; j < gridSize; j++) {
          row.addAll(grid[i][j].grid.get(k).subList(1, 9));
        }
        gridImage.add(row);
      }
    }
    Image finalImage = new Image(0, gridImage);

    List<List<Character>> imageToFind = new ArrayList<>();
    imageToFind.add("                  # ".chars().mapToObj(c -> (char) c).toList());
    imageToFind.add("#    ##    ##    ###".chars().mapToObj(c -> (char) c).toList());
    imageToFind.add(" #  #  #  #  #  #   ".chars().mapToObj(c -> (char) c).toList());

    for (int m = 0; m < 4; m++) {
      long monsters = finalImage.findMonsters(imageToFind);
      if (monsters > 0) {
        return finalImage.countHashes();
      }
      finalImage = finalImage.flipVertical();
      monsters = finalImage.findMonsters(imageToFind);
      if (monsters > 0) {
        return finalImage.countHashes();
      }
      finalImage = finalImage.flipVertical().rotateRight();
    }

    return 0L;
  }

  public List<Image> getCorners(List<Image> images) {
    List<Image> corners = new ArrayList<>();
    for (Image image1 : images) {
      boolean[] matchingSides = new boolean[8];
      List<List<Character>> edges1 = image1.getEdges();
      for (Image image2 : images) {
        if (image1.id == image2.id) {
          continue;
        }
        List<List<Character>> edges2 = image2.getEdges();
        for (int i = 0; i < edges1.size(); i++) {
          for (List<Character> e2 : edges2) {
            if (edges1.get(i).equals(e2)) {
              matchingSides[i] = true;
              break;
            }
          }
        }
      }
      long nbrSides =
          IntStream.range(0, matchingSides.length)
              .mapToObj(k -> matchingSides[k])
              .filter(k -> k)
              .count();
      if (nbrSides == 4) {
        corners.add(image1);
      }
    }
    return corners;
  }

  public Map<Long, Image> extractImages(List<String> lines) {
    Map<Long, Image> images = new HashMap<>();
    int k = 0;

    while (k < lines.size()) {
      long name =
          Pattern.compile("Tile (\\d+):")
              .matcher(lines.get(k))
              .results()
              .map(m -> m.group(1))
              .map(Long::parseLong)
              .findFirst()
              .get();
      List<List<Character>> grid = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
        grid.add(new ArrayList<>());
        for (int j = 0; j < 10; j++) {
          grid.get(i).add(lines.get(k + i + 1).charAt(j));
        }
      }
      k += 12;

      images.put(name, new Image(name, grid));
    }

    return images;
  }

  public record Image(long id, List<List<Character>> grid) {

    Image rotateTillMatchesEdge(List<Character> edge, int edgeIndex) {
      Image modified = this;
      for (int i = 0; i < 4; i++) {
        modified = modified.flipVertical();
        if (modified.getEdges().get(edgeIndex).equals(edge)) {
          return modified;
        }
        modified = modified.flipVertical();
        if (modified.getEdges().get(edgeIndex).equals(edge)) {
          return modified;
        }
        modified = modified.rotateRight();
      }
      throw new RuntimeException("No match found");
    }

    /**
     * Returns the 8 edges of the image: top, right, bottom, left, top flipped, right flipped,
     * bottom
     *
     * @return the 8 edges of the image
     */
    List<List<Character>> getEdges() {
      List<List<Character>> edges = new ArrayList<>();
      edges.add(grid.get(0));
      edges.add(grid.stream().map(row -> row.get(9)).toList());
      edges.add(grid.get(9));
      edges.add(grid.stream().map(row -> row.get(0)).toList());
      for (int i = 0; i < 4; i++) {
        edges.add(new ArrayList<>(edges.get(i).reversed()));
      }
      return edges;
    }

    Image rotateRight() {
      char[][] newGrid = new char[grid.size()][grid.size()];
      for (int i = 0; i < grid.size(); i++) {
        for (int j = 0; j < grid.get(0).size(); j++) {
          newGrid[j][grid.size() - 1 - i] = grid.get(i).get(j);
        }
      }
      List<List<Character>> newGridList =
          Arrays.stream(newGrid)
              .map(chars -> IntStream.range(0, newGrid[0].length).mapToObj(j -> chars[j]))
              .map(row -> row.collect(Collectors.toList()))
              .toList();
      return new Image(id, newGridList);
    }

    Image flipVertical() {
      char[][] newGrid = new char[grid.size()][grid.size()];
      for (int i = 0; i < grid.size(); i++) {
        for (int j = 0; j < grid.get(0).size(); j++) {
          newGrid[i][grid.size() - 1 - j] = grid.get(i).get(j);
        }
      }
      List<List<Character>> newGridList =
          Arrays.stream(newGrid)
              .map(chars -> IntStream.range(0, newGrid[0].length).mapToObj(j -> chars[j]))
              .map(row -> row.collect(Collectors.toList()))
              .toList();
      return new Image(id, newGridList);
    }

    long findMonsters(List<List<Character>> imageToFind) {
      int total = 0;
      for (int i = 0; i < grid.size() - imageToFind.size(); i++) {
        for (int j = 0; j < grid.get(0).size() - imageToFind.get(0).size(); j++) {
          boolean found = true;
          for (int k = 0; k < imageToFind.size(); k++) {
            for (int l = 0; l < imageToFind.get(0).size(); l++) {
              if (imageToFind.get(k).get(l) == '#' && grid.get(i + k).get(j + l) != '#') {
                found = false;
                break;
              }
            }
            if (!found) {
              break;
            }
          }
          if (found) {
            total++;
            for (int k = 0; k < imageToFind.size(); k++) {
              for (int l = 0; l < imageToFind.get(0).size(); l++) {
                if (imageToFind.get(k).get(l) == '#') {
                  grid.get(i + k).set(j + l, 'O');
                }
              }
            }
          }
        }
      }
      return total;
    }

    long countHashes() {
      return grid.stream()
          .map(row -> row.stream().filter(c -> c == '#').count())
          .reduce(0L, Long::sum);
    }
  }
}
