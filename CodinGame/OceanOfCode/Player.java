package CodinGame.OceanOfCode;

import java.util.*;
import java.io.*;
import java.math.*;

enum ESonarResult {
    Y, N, NOT_USED
}

abstract class Command {
    public abstract String getString();
}

class Sonar extends Command {
    public int sector;
    public ESonarResult result;

    public Sonar(int sector) {
        this.sector = sector;
        this.result = ESonarResult.NOT_USED;
    }

    @Override
    public String getString() {
        return "SONAR " + sector;
    }
}

class Torpedo extends Command {
    public int row, column;

    public Torpedo(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public String getString() {
        return "TORPEDO " + column + " " + row;
    }
}

class Surface extends Command {

    public Surface() {
    }

    @Override
    public String getString() {
        return "SURFACE";
    }
}

class Movement extends Command {
    public EDirection direction;
    public EPower power;

    public Movement(EDirection direction, EPower power) {
        this.direction = direction;
        this.power = power;
    }

    @Override
    public String getString() {
        return "MOVE " + direction.getDirectionStr() + " " + power.getPowerStr();
    }
}

enum EPower {
    TORPEDO("TORPEDO"), SONAR("SONAR"), SILENCE("SILENCE");

    private String powerStr;

    private EPower(String powerStr) {
        this.powerStr = powerStr;
    }

    public String getPowerStr() {
        return this.powerStr;
    }
}

enum ETileType {
    WATER("."), ISLAND("x");

    private String tileStr;

    private ETileType(String tileStr) {
        this.tileStr = tileStr;
    }

    public String getTileStr() {
        return tileStr;
    }

    public static ETileType getEnum(String value) {
        for (ETileType type : values()) {
            if(type.getTileStr().equals(value)) {
                return type;
            }
        }

        return WATER;
    }
}

enum EDirection {
    NORTH("N", 0), EAST("E", 1), SOUTH("S", 2), WEST("W", 3), NOTHING("NOTHING", 4);

    private String directionStr;
    private int directionInt;

    private EDirection(String directionStr, int directionInt) {
        this.directionStr = directionStr;
        this.directionInt = directionInt;
    }

    public String getDirectionStr() {
        return directionStr;
    }

    public int getDirectionInt() {
        return directionInt;
    }

    public static EDirection getEnum(int value) {
        for (EDirection direction : values()) {
            if(direction.getDirectionInt() == value) {
                return direction;
            }
        }

        return NORTH;
    }

    public static EDirection getEnum(String value) {
        for (EDirection direction : values()) {
            if(direction.getDirectionStr().equals(value)) {
                return direction;
            }
        }

        return NORTH;
    }
}

enum EGameState {
    CHARGE_TORPEDO, CHARGE_SONAR;
}

class Tile {
    public int row, column;
    public ETileType type;
    public boolean visited;
    public ArrayList<Tile> neighbors;

    public Tile(int row, int column, ETileType type) {
        this.row = row;
        this.column = column;
        this.type = type;
        visited = false;
        neighbors = new ArrayList<Tile>();
    }

    public void addNeighbor(Tile neighbor) {
        this.neighbors.add(neighbor);
    }

    public void setTileType(ETileType type) {
        this.type = type;
    }

    public EDirection getNeighborDirection(Tile neighbor) {
        if(row == neighbor.row) {
            if(neighbor.column > column) {
                return EDirection.EAST;
            } else {
                return EDirection.WEST;
            }
        } else {
            if(neighbor.row > row) {
                return EDirection.SOUTH;
            } else {
                return EDirection.NORTH;
            }
        }
    }
}

class Board {
    public int width, height, myId;
    public Tile[][] tiles;
    public OpponentFinder oppFinder;

    public Board(Scanner in) {
        width = in.nextInt();
        height = in.nextInt();
        myId = in.nextInt();

        oppFinder = new OpponentFinder(width, height, this);
        tiles = new Tile[height][width];

        if (in.hasNextLine()) {
            in.nextLine();
        }

        for (int i = 0; i < height; i++) {
            String line = in.nextLine();
            for (int j = 0; j < line.length(); j++) {
                ETileType tileType = ETileType.getEnum(String.valueOf(line.charAt(j)));
                tiles[i][j] = new Tile(i, j, tileType);
                switch (tileType) {
                    case ISLAND:
                        oppFinder.setCellState(i, j, false);
                        break;
                    case WATER:
                        oppFinder.setCellState(i, j, true);
                        break;
                }
            }
        }
    }

    public void setNeighbors() {
        int[] neightRows = { -1, 0, 1, 0 };
        int[] neightColumns = { 0, 1, 0, -1};

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                Tile currentTile = tiles[i][j];

                if(currentTile.type != ETileType.ISLAND) {
                    for (int k = 0; k < 4; k++) {
                        int neighborRow = currentTile.row + neightRows[k];
                        int neighborColumn = currentTile.column + neightColumns[k];

                        if(isOnBoard(neighborRow, neighborColumn)) {
                            Tile neighbor = tiles[neighborRow][neighborColumn];
                            if(neighbor.type != ETileType.ISLAND) {
                                currentTile.addNeighbor(neighbor);
                            }
                        }
                    }
                } else {
                    currentTile.visited = true;
                }
            }
        }
    }

    public void clearVisitedTiles() {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                Tile currentTile = tiles[i][j];

                if(currentTile.type == ETileType.ISLAND) {
                    currentTile.visited = true;
                } else {
                    tiles[i][j].visited = false;
                }
            }
        }
    }

    public void print() {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if(tiles[i][j].visited) {
                    System.err.print("x ");
                } else {
                    System.err.print("o ");
                }
//				System.err.print(tiles[i][j].type.getTileStr() + " ");
            }
            System.err.println("");
        }
    }

    public boolean isOnBoard(int row, int column) {
        return (row >= 0 && row < height && column >= 0 && column < width);
    }
}

class OpponentFinder {
    public boolean[][] mightHaveOppBoard, tempBoard;
    public Board theBoard;
    public MasterMind masterMind;
    public boolean[] isInSection;
    public int[] nbrTilesPerSection;

    public static int[] neighborsRow = { -1, -1, -1, 0, 0, 0, 1, 1, 1 };
    public static int[] neighborsColumn = { -1, 0, 1, -1, 0, 1, -1, 0, 1 };

    public OpponentFinder(int width, int height, Board theBoard) {
        this.theBoard = theBoard;

        mightHaveOppBoard = new boolean[height][width];
        tempBoard = new boolean[height][width];
        isInSection = new boolean[9];
        nbrTilesPerSection = new int[9];
        for (int i = 0; i < isInSection.length; i++) {
            isInSection[i] = false;
        }
    }

    public void setInSector(boolean[][] board, int sectorNum, boolean inSector) {
        int minRow, maxRow, minColumn, maxColumn;
        switch (sectorNum) {
            case 1:
                minRow = 0; maxRow = 5;
                minColumn = 0; maxColumn = 5;
                break;
            case 2:
                minRow = 0; maxRow = 5;
                minColumn = 5; maxColumn = 10;
                break;
            case 3:
                minRow = 0; maxRow = 5;
                minColumn = 10; maxColumn = 15;
                break;
            case 4:
                minRow = 5; maxRow = 10;
                minColumn = 0; maxColumn = 5;
                break;
            case 5:
                minRow = 5; maxRow = 10;
                minColumn = 5; maxColumn = 10;
                break;
            case 6:
                minRow = 5; maxRow = 10;
                minColumn = 10; maxColumn = 15;
                break;
            case 7:
                minRow = 10; maxRow = 15;
                minColumn = 0; maxColumn = 5;
                break;
            case 8:
                minRow = 10; maxRow = 15;
                minColumn = 5; maxColumn = 10;
                break;
            case 9:
                minRow = 10; maxRow = 15;
                minColumn = 10; maxColumn = 15;
                break;
            default:
                minRow = 0; maxRow = 0;
                minColumn = 0; maxColumn = 0;
                break;
        }

        for (int i = minRow; i < maxRow; i++) {
            for (int j = minColumn; j < maxColumn; j++) {
                board[i][j] = inSector;
            }
        }
    }

    public void findOpponent(Opponent opp) {
        copyToTemp();

        for (int i = 0; i < isInSection.length; i++) {
            isInSection[i] = false;
            nbrTilesPerSection[i] = 0;
        }

        if(masterMind.sonarUsed.result != ESonarResult.NOT_USED) {
            switch (masterMind.sonarUsed.result) {
                case Y:
                    for (int i = 0; i < isInSection.length; i++) {
                        if(masterMind.sonarUsed.sector != (i + 1)) {
                            setInSector(tempBoard, i + 1, false);
                            isInSection[i] = false;
                        } else {
                            isInSection[i] = true;
                        }
                    }
                    break;
                case N:
                    setInSector(tempBoard, masterMind.sonarUsed.sector, false);
                    isInSection[masterMind.sonarUsed.sector - 1] = false;
                    break;
            }
        }

        if(masterMind.shotTorpedo) {
            switch (masterMind.enemyLostNLife) {
                case 2:	// Found him !
                    for (int i = 0; i < isInSection.length; i++) {
                        setInSector(tempBoard, i + 1, false);
                        isInSection[i] = false;
                    }
                    tempBoard[masterMind.torpedoShot.row][masterMind.torpedoShot.column] = true;
                    break;
                case 1:
                    for (int i = 0; i < isInSection.length; i++) {
                        setInSector(tempBoard, i + 1, false);
                        isInSection[i] = false;
                    }
                    for (int i = 0; i < neighborsRow.length; i++) {
                        int neighborRow = masterMind.torpedoShot.row + neighborsRow[i];
                        int neighborColumn = masterMind.torpedoShot.column + neighborsColumn[i];
                        if(theBoard.isOnBoard(neighborRow, neighborColumn)) {
                            tempBoard[neighborRow][neighborColumn] = mightHaveOppBoard[neighborRow][neighborColumn];
                        }
                    }
                    tempBoard[masterMind.torpedoShot.row][masterMind.torpedoShot.column] = false;
                    break;
                case 0:
                    for (int i = 0; i < neighborsRow.length; i++) {
                        int neighborRow = masterMind.torpedoShot.row + neighborsRow[i];
                        int neighborColumn = masterMind.torpedoShot.column + neighborsColumn[i];
                        if(theBoard.isOnBoard(neighborRow, neighborColumn)) {
                            tempBoard[neighborRow][neighborColumn] = false;
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        if(opp.surfaceSector > 0) {
            for (int i = 0; i < isInSection.length; i++) {
                if((i + 1) != opp.surfaceSector) {
                    setInSector(mightHaveOppBoard, i + 1, false);
                    isInSection[i] = false;
                } else {
                    isInSection[i] = true;
                }
            }

            return;
        }

        for (int i = 0; i < mightHaveOppBoard.length; i++) {
            for (int j = 0; j < mightHaveOppBoard[0].length; j++) {
                if(theBoard.tiles[i][j].type == ETileType.ISLAND) {
                    mightHaveOppBoard[i][j] = false;
                    continue;
                }

                // If silence was used
                if(opp.usedSilence && tempBoard[i][j]) {
                    for (Tile tile : theBoard.tiles[i][j].neighbors) {
                        mightHaveOppBoard[tile.row][tile.column] = true;
                        int neighborSection = getSection(tile.row, tile.column);
                        isInSection[neighborSection] = true;
                        nbrTilesPerSection[neighborSection] = nbrTilesPerSection[neighborSection] + 1;
                    }
                    continue;
                }

                // Silence wasn't used
                switch (opp.oppLastDirection) {
                    case NORTH:
                        if(theBoard.isOnBoard(i + 1, j) && tempBoard[i + 1][j]) {
                            mightHaveOppBoard[i][j] = true;
                        } else {
                            mightHaveOppBoard[i][j] = false;
                        }
                        break;
                    case EAST:
                        if(theBoard.isOnBoard(i, j - 1) && tempBoard[i][j - 1]) {
                            mightHaveOppBoard[i][j] = true;
                        } else {
                            mightHaveOppBoard[i][j] = false;
                        }
                        break;
                    case SOUTH:
                        if(theBoard.isOnBoard(i - 1, j) && tempBoard[i - 1][j]) {
                            mightHaveOppBoard[i][j] = true;
                        } else {
                            mightHaveOppBoard[i][j] = false;
                        }
                        break;
                    case WEST:
                        if(theBoard.isOnBoard(i, j + 1) && tempBoard[i][j + 1]) {
                            mightHaveOppBoard[i][j] = true;
                        } else {
                            mightHaveOppBoard[i][j] = false;
                        }
                        break;
                }

                if(mightHaveOppBoard[i][j]) {
                    int neighborSection = getSection(i, j);
                    isInSection[neighborSection] = true;
                    nbrTilesPerSection[neighborSection] = nbrTilesPerSection[neighborSection] + 1;
                }
            }
        }
//		printBoard();
//		
//		for (int nbr : nbrTilesPerSection) {
//			System.err.print(nbr + " ");
//		}
//		for (boolean b : isInSection) {
//			if(b) {
//				System.err.print("1 ");
//			} else {
//				System.err.print("0 ");
//			}
//		}
//		System.err.println();
    }

    public int getSection(int row, int column) {
        if(column > 9) {
            if(row > 9) {
                return 8;
            } else if(row > 4) {
                return 5;
            } else {
                return 2;
            }
        } else if(column > 4) {
            if(row > 9) {
                return 7;
            } else if(row > 4) {
                return 4;
            } else {
                return 1;
            }
        } else {
            if(row > 9) {
                return 6;
            } else if(row > 4) {
                return 3;
            } else {
                return 0;
            }
        }
    }

    public void setCellState(int row, int column, boolean possibility) {
        mightHaveOppBoard[row][column] = possibility;
    }

    public void copyToTemp() {
        for (int i = 0; i < mightHaveOppBoard.length; i++) {
            for (int j = 0; j < mightHaveOppBoard[0].length; j++) {
                tempBoard[i][j] = (mightHaveOppBoard[i][j]) ? true : false;
            }
        }
    }

    public void copyFromTemp() {
        for (int i = 0; i < mightHaveOppBoard.length; i++) {
            for (int j = 0; j < mightHaveOppBoard[0].length; j++) {
                mightHaveOppBoard[i][j] = (tempBoard[i][j]) ? true : false;
            }
        }
    }

    public void printBoard() {
        for (int i = 0; i < mightHaveOppBoard.length; i++) {
            for (int j = 0; j < mightHaveOppBoard[0].length; j++) {
                if(mightHaveOppBoard[i][j]) {
                    System.err.print("1 ");
                } else {
                    System.err.print("0 ");
                }
            }
            System.err.println("");
        }
    }

}

class Opponent {
    public EDirection oppLastDirection;
    public boolean usedSilence;
    public int surfaceSector;
    public int life;

    public Opponent() {
        oppLastDirection = null;
        surfaceSector = -1;
        life = -1;
    }

}

class MasterMind {
    public ArrayList<Command> commands;
    public int playerRow, playerColumn;
    public EDirection lastDirection;

    public Torpedo torpedoShot;
    public boolean shotTorpedo;
    public int enemyLostNLife;

    public Sonar sonarUsed;

    public int myLife;
    public int torpedoCD, sonarCD, silenceCD, mineCD;
    public Movement movement;
    public Board gameBoard;

    public EGameState state;

    public Opponent opp;

    public MasterMind(Board gameBoard) {
        this.torpedoShot = new Torpedo(-1, -1);
        this.shotTorpedo = false;
        this.enemyLostNLife = -1;
        this.sonarUsed = new Sonar(-1);

        this.commands = new ArrayList<Command>();
        this.gameBoard = gameBoard;
        lastDirection = EDirection.WEST;
        state = EGameState.CHARGE_TORPEDO;
        opp = new Opponent();
    }

    public void update(Scanner in) {
        movement = new Movement(null, null);
        playerColumn = in.nextInt();
        playerRow = in.nextInt();
        myLife = in.nextInt();

        int tempOppLife = in.nextInt();
        enemyLostNLife = opp.life - tempOppLife;
        opp.life = tempOppLife;
        torpedoCD = in.nextInt();
        sonarCD = in.nextInt();
        silenceCD = in.nextInt();
        mineCD = in.nextInt();

        switch (in.next()) {
            case "Y":
                sonarUsed.result = ESonarResult.Y;
                break;
            case "N":
                sonarUsed.result = ESonarResult.N;
                break;
            case "NA":
                sonarUsed.result = ESonarResult.NOT_USED;
                break;
            default:
                break;
        }

        if (in.hasNextLine()) {
            in.nextLine();
        }

        commands.clear();
        handleOpponentOrders(in.nextLine());
    }

    public void findOpponent() {
        gameBoard.oppFinder.findOpponent(opp);
    }

    public void handleOpponentOrders(String opponentOrders) {
        if(opponentOrders == null || opponentOrders.equals("")) {
            return;
        }

        String[] orders = opponentOrders.split("\\|");

        opp.oppLastDirection = EDirection.NOTHING;
        opp.usedSilence = false;
        opp.surfaceSector = -1;

        for (String order : orders) {
            String[] splitOrder = order.split(" ");
            switch (splitOrder[0]) {
                case "MOVE":
                    opp.oppLastDirection = EDirection.getEnum(splitOrder[1]);
//				System.err.println(splitOrder[0] + " " + splitOrder[1]);
//				System.err.println(oppLastDirection.getDirectionStr());
                    break;
                case "SILENCE":
                    opp.usedSilence = true;
                    break;
                case "SURFACE":
                    opp.surfaceSector = Integer.parseInt(splitOrder[1]);
                    System.err.println(opp.surfaceSector);
                    break;
                default:
                    break;
            }
        }
    }

    public void chooseStartPosition() {
        // Start in middle sector
        for (int i = 5; i < 10; i++) {
            for (int j = 5; j < 10; j++) {
                Tile currentTile = gameBoard.tiles[i][j];
                if(currentTile.type == ETileType.WATER) {
                    currentTile.visited = true;
                    System.out.println(currentTile.column + " " + currentTile.row);
                    return;
                }
            }
        }

        System.out.println("14 14");
    }

    public void moveShip() {
//		gameBoard.print();
        Tile currentTile = gameBoard.tiles[playerRow][playerColumn];

//		for (Tile neighbor : currentTile.neighbors) {
//			if(!neighbor.visited) {
//				movement.direction = currentTile.getNeighborDirection(neighbor);
//				lastDirection = movement.direction;
//				neighbor.visited = true;
//				commands.add(movement);
//				return;
//			}
//		}
//		
        for (int i = 0; i < EDirection.values().length; i++) {
            EDirection desiredDirection = EDirection.getEnum((5 - i + lastDirection.getDirectionInt()) % 4);

            int desiredRow = currentTile.row;
            int desiredColumn = currentTile.column;

            switch (desiredDirection) {
                case NORTH:
                    desiredRow--;
                    break;
                case EAST:
                    desiredColumn++;
                    break;
                case SOUTH:
                    desiredRow++;
                    break;
                case WEST:
                    desiredColumn--;
                    break;
            }

            if(gameBoard.isOnBoard(desiredRow, desiredColumn) && !gameBoard.tiles[desiredRow][desiredColumn].visited) {
                gameBoard.tiles[desiredRow][desiredColumn].visited = true;
                lastDirection = desiredDirection;
                movement.direction = desiredDirection;
                commands.add(movement);
                return;
            }
        }

        // Stuck !
        surfaceShip();
    }

    public void surfaceShip() {
        commands.add(new Surface());
        gameBoard.clearVisitedTiles();
        gameBoard.tiles[playerRow][playerColumn].visited = true;
    }

    public void useSonar() {
//		
//		if(opp.surfaceSector > 0) {zz
//			// DO NOTHING
//		}

        int targetSection = -1;
        int nbrTilesTargetSection = -1;

        for (int i = 0; i < gameBoard.oppFinder.isInSection.length; i++) {
            if(gameBoard.oppFinder.isInSection[i] && gameBoard.oppFinder.nbrTilesPerSection[i] > nbrTilesTargetSection) {
                targetSection = i + 1;
                nbrTilesTargetSection = gameBoard.oppFinder.nbrTilesPerSection[i];
            }
        }

        if(targetSection > 0) {
            sonarUsed = new Sonar(targetSection);
            commands.add(sonarUsed);
            return;
        }

        sonarUsed = new Sonar(3);
        commands.add(sonarUsed);
    }

    public boolean fireTorpedo() {
        // Cycle through firable radius
        boolean[][] visited = new boolean[gameBoard.height][gameBoard.width];
        Tile target = shouldFireHere(playerRow, playerColumn, 0, visited);
        if(target != null) {
            torpedoShot.row = target.row;
            torpedoShot.column = target.column;
            shotTorpedo = true;
            commands.add(torpedoShot);
            return true;
        }
        return false;
    }

    public Tile shouldFireHere(int row, int column, int distFromShip, boolean[][] visited) {

        if(!gameBoard.isOnBoard(row, column) || visited[row][column] || distFromShip > 4) {
            return null;
        }

        if(Math.abs(playerRow - row) > 1 || Math.abs(playerColumn - column) > 1) {
            if(gameBoard.oppFinder.mightHaveOppBoard[row][column]) {
                return gameBoard.tiles[row][column];
            }
        }

        visited[row][column] = true;

        for (Tile neighbor : gameBoard.tiles[row][column].neighbors) {
            if(!visited[neighbor.row][neighbor.column]) {
                Tile tile = shouldFireHere(neighbor.row, neighbor.column, distFromShip + 1, visited);
                if(tile != null) {
                    return tile;
                }
            }
        }

        return null;
    }

    public void handleCommands() {
        for (Command command : commands) {
            System.out.print(command.getString() + " | ");
        }
    }

    public void handlePowerCharge() {
        if(torpedoCD == 0) {
            if(fireTorpedo()) {
                movement = new Movement(null, EPower.TORPEDO);
                return;
            } else {
                if(sonarCD == 0) {
                    useSonar();
                }

                movement = new Movement(null, EPower.SONAR);
            }
        } else {
            movement = new Movement(null, EPower.TORPEDO);
            shotTorpedo = false;
        }
    }
}

class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        Board gameBoard = new Board(in);
        gameBoard.setNeighbors();
        gameBoard.clearVisitedTiles();
        gameBoard.print();

        MasterMind masterMind = new MasterMind(gameBoard);
        gameBoard.oppFinder.masterMind = masterMind;
        masterMind.chooseStartPosition();

        // game loop
        while (true) {
            masterMind.update(in);
            masterMind.findOpponent();
            masterMind.handlePowerCharge();
            masterMind.moveShip();
            masterMind.handleCommands();
            System.out.println("");
        }
    }
}
