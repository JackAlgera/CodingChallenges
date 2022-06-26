import java.util.*

fun main(args : Array<String>) {
    val input = Scanner(System.`in`)

    val grid = Grid(input)
    grid.populateGrid(input)

    val masterMind = MasterMind(grid)

    while (true) {
        masterMind.updateBoard(input)
        masterMind.changeGameStates()
        masterMind.playTurn()
    }
}

data class MasterMind (
        var grid: Grid,
        var day: Int = 0,
        var nutrient: Int = 0,
        val player: Player = Player(),
        val opponent: Player = Player(),
        var isStartOfDay: Boolean = true,
        var sunDirection: Direction = Direction.RIGHT
) {
    companion object {
        const val THROW_SHADOWS_DAYS_AHEAD = 5

        var isEndGame = false
        var prefGrowingInCenter = false
        const val MIN_TREES_BEFORE_COMPLETING = 5
        const val MIN_LARGE_TREES_BEFORE_COMPLETING = 2

        const val DO_ACTION_HEURISTIC = 10000
        const val AVOID_ACTION_HEURISTIC = -10000
    }

    private fun maxNumberOfSpecificTree(treeSize: TreeSize) : Int = when(treeSize) {
        TreeSize.SEED -> 1
        TreeSize.SMALL_TREE -> 2
        TreeSize.MEDIUM_TREE -> 3
        TreeSize.LARGE_TREE -> 5
    }

    private fun calculateActionHeuristic(action: Action): Int {
        return when (action) {
            is Action.Grow -> growHeuristic(action)
            is Action.Seed -> seedHeuristic(action)
            is Action.Complete -> completeHeuristic(action)
            is Action.Wait -> waitHeuristic(action)
        }
    }

    private fun growHeuristic(action: Action.Grow) : Int {
        return when {
            isEndGame -> {
                when {
                    (3 - action.tree.treeSize.value > 23 - day) -> AVOID_ACTION_HEURISTIC
                    (action.tree.treeSize != TreeSize.MEDIUM_TREE && player.numberOfSpecificTreesOwned(TreeSize.MEDIUM_TREE) > 0) -> AVOID_ACTION_HEURISTIC
                    action.tree.treeSize == TreeSize.MEDIUM_TREE && player.pointsForCompletingTree(nutrient, action.tree) < (3 + if (player.sunPoints % 3 != 0) 1 else 0) -> AVOID_ACTION_HEURISTIC
                    else -> 2 * action.tree.treeSize.value + 2 * action.tree.cell.richness
                }
            }
            player.numberOfSpecificTreesOwned(TreeSize.getNextTreeSize(action.tree.treeSize))
                    >= maxNumberOfSpecificTree(TreeSize.getNextTreeSize(action.tree.treeSize)) -> AVOID_ACTION_HEURISTIC
            action.tree.treeSize == TreeSize.SEED && player.numberOfSpecificTreesOwned(TreeSize.SMALL_TREE) == 0 -> 12 + action.tree.cell.richness
            action.tree.treeSize == TreeSize.SMALL_TREE && player.costToGrowTree(TreeSize.SMALL_TREE) <= (player.sunPoints + 1) -> 10 + action.tree.cell.richness
            player.costToGrowTree(action.tree.treeSize) == 0 -> 8 + action.tree.cell.richness + if (prefGrowingInCenter) 2 * action.tree.cell.richness else 0
            else -> {
//                val pointsLostWithoutGrow = numberOfSunPointsLost(action.tree.cell, action.tree.treeSize, Direction.of(sunDirection.directionIndex + 1))
                val pointsLostWithGrow = numberOfSunPointsLost(action.tree.cell, TreeSize.getNextTreeSize(action.tree.treeSize), Direction.of(sunDirection.directionIndex + 1))

                var heuristic = (TreeSize.LARGE_TREE.value - action.tree.treeSize.value) * 2

                if (pointsLostWithGrow.first <= 0 && pointsLostWithGrow.second > 0) {
                    heuristic += pointsLostWithGrow.second
                }

                if (action.tree.cell.isShaded(1, action.tree.treeSize.value) && !action.tree.cell.isShaded(1, action.tree.treeSize.value + 1)) {
                    heuristic += action.tree.treeSize.value + 1 - pointsLostWithGrow.first
                }

                heuristic
            }
        }
    }

    private fun seedHeuristic(action: Action.Seed) : Int {
        return when {
            isEndGame -> {
                if (day == 23 && player.numberOfSpecificTreesOwned(TreeSize.SEED) == 0)
                    DO_ACTION_HEURISTIC - 2 * action.fromTree.treeSize.value
                else
                    AVOID_ACTION_HEURISTIC
            }
            player.numberOfSpecificTreesOwned(TreeSize.SEED) >= maxNumberOfSpecificTree(TreeSize.SEED) -> AVOID_ACTION_HEURISTIC
            !action.toCell.potentialFutureShadow -> 10 + DO_ACTION_HEURISTIC + action.toCell.richness
            player.atleastOnePlantableLocationThatsNeverShaded && action.toCell.isNeverShaded() -> DO_ACTION_HEURISTIC + action.toCell.richness
            player.alreadyHasTreeAround(action.toCell.index, grid) -> AVOID_ACTION_HEURISTIC
            else -> {
                val pointsLostAtLargeTree = numberOfSunPointsLost(action.toCell, TreeSize.LARGE_TREE, Direction.of(sunDirection.directionIndex + 4))
                val pointsLostAtMediumTree = numberOfSunPointsLost(action.toCell, TreeSize.MEDIUM_TREE, Direction.of(sunDirection.directionIndex + 3))
                (action.toCell.richness + action.toCell.wontBeShadedForDays()
                        + pointsLostAtLargeTree.second - pointsLostAtLargeTree.first
                        + pointsLostAtMediumTree.second - pointsLostAtMediumTree.first)
            }
        }
    }

    private fun completeHeuristic(action: Action.Complete) : Int {
        return when {
            isEndGame ->
                if (player.pointsForCompletingTree(nutrient, action.tree) > 1) {
                    DO_ACTION_HEURISTIC + action.tree.cell.getRichnessCellPointValue()
                } else {
                    AVOID_ACTION_HEURISTIC
                }
            player.numberOfTreesOwned() <= MIN_TREES_BEFORE_COMPLETING -> AVOID_ACTION_HEURISTIC
            player.numberOfSpecificTreesOwned(TreeSize.LARGE_TREE) <= MIN_LARGE_TREES_BEFORE_COMPLETING -> AVOID_ACTION_HEURISTIC
            action.tree.cell.isShaded(1, action.tree.treeSize.value)
                    || player.numberOfSpecificTreesOwned(TreeSize.LARGE_TREE) >= maxNumberOfSpecificTree(TreeSize.LARGE_TREE) -> {
                val pointsLost = numberOfSunPointsLost(action.tree.cell, action.tree.treeSize, Direction.of(sunDirection.directionIndex + 1))
                DO_ACTION_HEURISTIC + pointsLost.first - pointsLost.second
            }
            else -> AVOID_ACTION_HEURISTIC
        }
    }

    private fun waitHeuristic(action: Action.Wait) : Int {
        return -10
    }

    fun numberOfSunPointsLost(cell: Cell, treeSize: TreeSize, sunDirection: Direction) : Pair<Int, Int> { // Pair of points lost for (player, opponent)
        var pointsLostPlayer = 0
        var pointsLostOpponent = 0

        cell.neighbors[sunDirection]!!.forEachIndexed { index, neighborCell ->
            if (index < treeSize.value) {
                val playerTree = player.trees[neighborCell.index]
                if (playerTree != null) {
                    pointsLostPlayer += if (playerTree.treeSize.value <= treeSize.value) playerTree.treeSize.value else 0
                }

                val opponentTree = opponent.trees[neighborCell.index]
                if (opponentTree != null) {
                    pointsLostOpponent += if (opponentTree.treeSize.value <= treeSize.value) opponentTree.treeSize.value else 0
                }
            }
        }

//        System.err.println("Points lost: player=$pointsLostPlayer - opponent=$pointsLostOpponent")

        return Pair(pointsLostPlayer, pointsLostOpponent)
    }

    fun updateBoard(input: Scanner) {
        val newDay = input.nextInt()
        if (newDay != day) {
            isStartOfDay = true
        }
        day = newDay

        sunDirection = Direction.of(day % 6)

        nutrient = input.nextInt()
        player.sunPoints = input.nextInt()
        player.score = input.nextInt()
        player.possibleActions = mutableListOf()
        player.trees = mutableMapOf()
        player.atleastOnePlantableLocationThatsNeverShaded = false

        opponent.sunPoints = input.nextInt()
        opponent.score = input.nextInt()
        opponent.isWaiting = input.nextInt() != 0 // whether your opponent is asleep until the next day
        opponent.trees = mutableMapOf()
        opponent.atleastOnePlantableLocationThatsNeverShaded = false

        grid.resetGrid()

        val numberOfTrees = input.nextInt() // the current amount of trees
        for (i in 0 until numberOfTrees) {
            val tree = Tree(
                    cell = grid.cells[input.nextInt()]!!,
                    treeSize = TreeSize.fromInt(input.nextInt()),
                    player = if (input.nextInt() != 0) player else opponent,
                    isDormant = input.nextInt() != 0
            )

            tree.player.trees[tree.cell.index] = tree
            tree.throwShadows(sunDirection)
        }

        val numberOfPossibleMoves = input.nextInt()
        if (input.hasNextLine()) {
            input.nextLine()
        }
        for (i in 0 until numberOfPossibleMoves) {
            player.addPossibleMove(input.nextLine(), this)
        }

        player.trees.forEach { (_, tree) ->
            tree.throwPotentialPersonalShadows()
        }

//        player.checkIfAtleastOnePlantableLocationThatsNeverShaded()
//        System.err.println("Today's shadows")
//        grid.printShadedArea(0)
//        System.err.println("Tomorrow's shadows")
//        grid.printShadedArea(1)
    }

    fun playTurn() {
        player.possibleActions
                .map { action ->
                    val heuristic = calculateActionHeuristic(action)
                    action.debugAction(heuristic.toString())
                    action to heuristic
                }.sortedByDescending { it.second }
                .first()
                .first
                .performAction()
    }

    fun changeGameStates() {
        if (day >= 13) {
            prefGrowingInCenter = true
        }

        if (day >= 18) {
            isEndGame = true
        }
    }
}

data class Player (
        var sunPoints: Int = 0,
        var score: Int = 0,
        var isWaiting: Boolean = false,
        var trees: MutableMap<Int, Tree> = mutableMapOf(),
        var atleastOnePlantableLocationThatsNeverShaded: Boolean = false,
        var possibleActions: MutableList<Action> = mutableListOf()
) {
    fun numberOfTreesOwned() : Int = trees.values.size

    fun numberOfSpecificTreesOwned(treeSize: TreeSize) : Int = trees.values.filter { it.treeSize == treeSize }.size

    fun costToGrowTree(treeSize: TreeSize) : Int =
            when (treeSize) {
                TreeSize.SEED -> 1 + numberOfSpecificTreesOwned(TreeSize.SMALL_TREE)
                TreeSize.SMALL_TREE -> 3 + numberOfSpecificTreesOwned(TreeSize.MEDIUM_TREE)
                TreeSize.MEDIUM_TREE -> 7 + numberOfSpecificTreesOwned(TreeSize.LARGE_TREE)
                else -> 10000
            }

    fun addPossibleMove(nextLine: String, masterMind: MasterMind) {
        possibleActions.add(Action.of(nextLine, masterMind))
    }

    fun pointsForCompletingTree(nutrient: Int, tree: Tree) : Int {
        return nutrient + tree.cell.getRichnessCellPointValue()
    }

    fun checkIfAtleastOnePlantableLocationThatsNeverShaded() {
        possibleActions.forEach { action ->
            if (action is Action.Seed && action.toCell.isNeverShaded()) {
                atleastOnePlantableLocationThatsNeverShaded = true
                return
            }
        }

        atleastOnePlantableLocationThatsNeverShaded = false
    }

    fun alreadyHasTreeAround(cellIndex: Int, grid: Grid) : Boolean {
        grid.cells[cellIndex]!!.circularNeighbors[0]!!.forEach { cell ->
            if (trees.containsKey(cell.index)) {
                return true
            }
        }

        return false
    }
}

data class Tree (
        val cell: Cell,
        var treeSize: TreeSize,
        val player: Player,
        val isDormant: Boolean
) {
    private fun throwShadowsForDay(sunDirection: Direction, daysAhead: Int) {
        if (treeSize == TreeSize.SEED) return

        cell.neighbors[sunDirection]!!.forEachIndexed { index, cell ->
            if (index < treeSize.value) {
                cell.addShade(daysAhead, treeSize)
//                System.err.println("Shadow at ${cell.index} - days ahead $daysAhead - level ${cell.shadesForDay[daysAhead]!!.shadowLevel}")
            }
        }
    }

    fun throwShadows(sunDirection: Direction) {
        (0..MasterMind.THROW_SHADOWS_DAYS_AHEAD).forEach {
            throwShadowsForDay(Direction.of(sunDirection.directionIndex + it), it)
        }
    }

    fun throwPotentialPersonalShadows() {
        cell.potentialFutureShadow = true

        Direction.values().forEach { direction ->
            cell.neighbors[direction]!!.forEach { neighborCell ->
                neighborCell.potentialFutureShadow = true
            }
        }
    }
}

enum class TreeSize(
        val value: Int
) {
    SEED(0),
    SMALL_TREE(1),
    MEDIUM_TREE(2),
    LARGE_TREE(3);

    companion object {
        fun fromInt(value: Int) = values().first { it.value == value }

        fun getNextTreeSize(treeSize: TreeSize) : TreeSize = when(treeSize) {
            SEED -> SMALL_TREE
            SMALL_TREE -> MEDIUM_TREE
            MEDIUM_TREE -> LARGE_TREE
            LARGE_TREE -> LARGE_TREE
        }
    }
}

data class Grid (
        private val input: Scanner
) {
    val cells: Map<Int, Cell>
    val cellsAxial: Map<Pair<Int, Int>, Cell>

    init {
        cells = (0..input.nextInt().minus(1)).map { it to Cell(it, 0) }.toMap()

        cellsAxial = mapOf(
                Pair(0, -3) to cells[25]!!, Pair(1, -3) to cells[24]!!, Pair(2, -3) to cells[23]!!, Pair(3, -3) to cells[22]!!,
                Pair(-1, -2) to cells[26]!!, Pair(0, -2) to cells[11]!!, Pair(1, -2) to cells[10]!!, Pair(2, -2) to cells[9]!!, Pair(3, -2) to cells[21]!!,
                Pair(-2, -1) to cells[27]!!, Pair(-1, -1) to cells[12]!!, Pair(0, -1) to cells[3]!!, Pair(1, -1) to cells[2]!!, Pair(2, -1) to cells[8]!!, Pair(3, -1) to cells[20]!!,
                Pair(-3, 0) to cells[28]!!, Pair(-2, 0) to cells[13]!!, Pair(-1, 0) to cells[4]!!, Pair(0, 0) to cells[0]!!, Pair(1, 0) to cells[1]!!, Pair(2, 0) to cells[7]!!, Pair(3, 0) to cells[19]!!,
                Pair(-3, 1) to cells[29]!!, Pair(-2, 1) to cells[14]!!, Pair(-1, 1) to cells[5]!!, Pair(0, 1) to cells[6]!!, Pair(1, 1) to cells[18]!!, Pair(2, 1) to cells[36]!!,
                Pair(-3, 2) to cells[30]!!, Pair(-2, 2) to cells[15]!!, Pair(-1, 2) to cells[16]!!, Pair(0, 2) to cells[17]!!, Pair(1, 2) to cells[35]!!,
                Pair(-3, 3) to cells[31]!!, Pair(-2, 3) to cells[32]!!, Pair(-1, 3) to cells[33]!!, Pair(0, 3) to cells[34]!!
        )
    }

    fun populateGrid(input: Scanner) {
        cells.forEach { (_, cell) ->
            cell.index = input.nextInt()
            cell.richness = input.nextInt()

            var neighborCellIndex = input.nextInt()
            if (neighborCellIndex != -1) {
                cell.putNeighbor(Direction.RIGHT, cells[neighborCellIndex]!!)
                cell.putCircularNeighbor(0, cells[neighborCellIndex]!!)
            }
            neighborCellIndex = input.nextInt()
            if (neighborCellIndex != -1) {
                cell.putNeighbor(Direction.TOP_RIGHT, cells[neighborCellIndex]!!)
                cell.putCircularNeighbor(0, cells[neighborCellIndex]!!)
            }
            neighborCellIndex = input.nextInt()
            if (neighborCellIndex != -1) {
                cell.putNeighbor(Direction.TOP_LEFT, cells[neighborCellIndex]!!)
                cell.putCircularNeighbor(0, cells[neighborCellIndex]!!)
            }
            neighborCellIndex = input.nextInt()
            if (neighborCellIndex != -1) {
                cell.putNeighbor(Direction.LEFT, cells[neighborCellIndex]!!)
                cell.putCircularNeighbor(0, cells[neighborCellIndex]!!)
            }
            neighborCellIndex = input.nextInt()
            if (neighborCellIndex != -1) {
                cell.putNeighbor(Direction.BOT_LEFT, cells[neighborCellIndex]!!)
                cell.putCircularNeighbor(0, cells[neighborCellIndex]!!)
            }
            neighborCellIndex = input.nextInt()
            if (neighborCellIndex != -1) {
                cell.putNeighbor(Direction.BOT_RIGHT, cells[neighborCellIndex]!!)
                cell.putCircularNeighbor(0, cells[neighborCellIndex]!!)
            }
        }

        cells.forEach { (_, cell) -> cell.fillRemainingNeighbors() }

        cellsAxial.forEach { (coordinates, cell) ->
            cell.fillRemainingCircularNeighbors(coordinates.first, coordinates.second, this)
        }
    }

    fun getCell(q: Int, r: Int) : Cell? = cellsAxial[Pair(q, r)]

    fun resetGrid() {
        cells.forEach { (_, cell) ->
            cell.resetShadows()
        }
    }

    fun printShadedArea(daysAhead: Int) {
        System.err.println("" +
                "   ${printShadedCell(25, daysAhead)} ${printShadedCell(24, daysAhead)} ${printShadedCell(23, daysAhead)} ${printShadedCell(22, daysAhead)}\n" +
                "  ${printShadedCell(26, daysAhead)} ${printShadedCell(11, daysAhead)} ${printShadedCell(10, daysAhead)} ${printShadedCell(9, daysAhead)} ${printShadedCell(21, daysAhead)}\n" +
                " ${printShadedCell(27, daysAhead)} ${printShadedCell(12, daysAhead)} ${printShadedCell(3, daysAhead)} ${printShadedCell(2, daysAhead)} ${printShadedCell(8, daysAhead)} ${printShadedCell(20, daysAhead)}\n" +
                "${printShadedCell(28, daysAhead)} ${printShadedCell(13, daysAhead)} ${printShadedCell(4, daysAhead)} ${printShadedCell(0, daysAhead)} ${printShadedCell(1, daysAhead)} ${printShadedCell(7, daysAhead)} ${printShadedCell(19, daysAhead)}\n" +
                " ${printShadedCell(29, daysAhead)} ${printShadedCell(14, daysAhead)} ${printShadedCell(5, daysAhead)} ${printShadedCell(6, daysAhead)} ${printShadedCell(18, daysAhead)} ${printShadedCell(36, daysAhead)}\n" +
                "  ${printShadedCell(30, daysAhead)} ${printShadedCell(15, daysAhead)} ${printShadedCell(16, daysAhead)} ${printShadedCell(17, daysAhead)} ${printShadedCell(35, daysAhead)}\n" +
                "   ${printShadedCell(31, daysAhead)} ${printShadedCell(32, daysAhead)} ${printShadedCell(33, daysAhead)} ${printShadedCell(34, daysAhead)}")
    }

    private fun printShadedCell(index: Int, daysAhead: Int) : String = cells[index]!!.shadowsForDay[daysAhead]!!.shadowLevel.toString() // if (cells[index]!!.potentialFutureShadow) "y" else "n"
}

data class Cell (
        var index: Int,     // 0 is the center cell, the next cells spiral outwards
        var richness: Int,  // 0 if the cell is unusable, 1-3 for usable cells,
        val shadowsForDay: MutableMap<Int, ShadeCellState> = (0..MasterMind.THROW_SHADOWS_DAYS_AHEAD).map { it to ShadeCellState() }.toMap().toMutableMap(),
        var potentialFutureShadow: Boolean = false,
        var neighbors: Map<Direction, MutableList<Cell>> = Direction.values().associateBy({it}, { mutableListOf<Cell>() }),
        var circularNeighbors: Map<Int, MutableList<Cell>> = (0..2).map { it to mutableListOf<Cell>() }.toMap()
) {
    companion object {
        val circular1NeighborCoordinates = listOf<Pair<Int, Int>>(
                Pair(-2, 0), Pair(-1, -1), Pair(0, -2), Pair(1, -2), Pair(2, -2), Pair(2, -1),
                Pair(2, 0), Pair(1, 1), Pair(0, 2), Pair(-1, 2), Pair(-2, 2), Pair(-2, 1)
        )
        val circular2NeighborCoordinates = listOf<Pair<Int, Int>>(
                Pair(-3, 0), Pair(-2, -1), Pair(-1, -2), Pair(0, -3), Pair(1, -3), Pair(2, -3),
                Pair(3, -3), Pair(3, -2), Pair(3, -1), Pair(3, 0), Pair(2, 1), Pair(1, 2),
                Pair(0, 3), Pair(-1, 3), Pair(-2, 3), Pair(-3, 3), Pair(-3, 2), Pair(-3, 1)
        )
    }

    fun putNeighbor(direction: Direction, neighbor: Cell) {
        neighbors[direction]!!.add(neighbor)
    }

    fun putCircularNeighbor(layer: Int, neighbor: Cell) {
        circularNeighbors[layer]!!.add(neighbor)
    }

    fun fillRemainingNeighbors() {
        Direction.values().forEach { direction ->
            for (i in 0..1) {
                val neighborCell = neighbors[direction]!!.getOrNull(i)

                if (neighborCell != null) {
                    val neighborOfNeighborCell = neighborCell.neighbors[direction]!!.firstOrNull()
                    if (neighborOfNeighborCell != null) {
                        neighbors[direction]!!.add(neighborOfNeighborCell)
                    } else {
                        break
                    }
                } else {
                    break
                }
            }
        }
    }

    fun fillRemainingCircularNeighbors(q: Int, r: Int, grid: Grid) {
        circular1NeighborCoordinates.forEach {
            val cell = grid.getCell(q + it.first, r + it.second)
            if (cell != null)
                circularNeighbors[1]!!.add(cell)
        }

        circular2NeighborCoordinates.forEach {
            val cell = grid.getCell(q + it.first, r + it.second)
            if (cell != null)
                circularNeighbors[2]!!.add(cell)
        }
    }

    fun getRichnessCellPointValue() : Int {
        return when (richness) {
            1 -> 0
            2 -> 2
            3 -> 4
            else -> 0
        }
    }

    fun addShade(daysAhead: Int, treeSize: TreeSize) {
        shadowsForDay[daysAhead]!!.addShadow(treeSize)
    }

    fun resetShadows() {
        shadowsForDay.forEach { (_, cellShade) ->
            cellShade.resetCell()
        }
        potentialFutureShadow = false
    }

    fun wontBeShadedForDays() : Int {
        shadowsForDay.forEach { (daysAhead, shadowState) ->
            if (shadowState.isShaded) {
                return daysAhead
            }
        }

        return MasterMind.THROW_SHADOWS_DAYS_AHEAD + 1
    }

    fun isShaded(daysAhead: Int, shadowLevel: Int) : Boolean {
        val shadowForDay = shadowsForDay[daysAhead]!!
        return shadowForDay.isShaded && shadowForDay.shadowLevel >= shadowLevel
    }

    fun isNeverShaded() : Boolean {
        shadowsForDay.forEach { (_, shadowState) ->
            if (shadowState.isShaded) {
                return false
            }
        }

        return true
    }
}

data class ShadeCellState(
        var isShaded: Boolean = false,
        var shadowLevel: Int = 0
) {
    fun addShadow(treeSize: TreeSize) {
        isShaded = true
        if (treeSize.value > shadowLevel)
            shadowLevel = treeSize.value
    }

    fun resetCell() {
        isShaded = false
        shadowLevel = 0
    }
}

enum class Direction(
        val directionIndex: Int
) {
    RIGHT(0),
    TOP_RIGHT(1),
    TOP_LEFT(2),
    LEFT(3),
    BOT_LEFT(4),
    BOT_RIGHT(5);

    companion object {
        fun of(value: Int) : Direction =
                when (value % 6) {
                    0 -> RIGHT
                    1 -> TOP_RIGHT
                    2 -> TOP_LEFT
                    3 -> LEFT
                    4 -> BOT_LEFT
                    5 -> BOT_RIGHT
                    else -> RIGHT
                }
    }
}

sealed class Action(
        private val say: String = ""
) {
    data class Complete(val tree: Tree) : Action()
    data class Grow(val tree: Tree) : Action()
    data class Seed(val fromTree: Tree, val toCell: Cell) : Action()
    class Wait : Action()

    companion object {
        fun of(st: String, masterMind: MasterMind): Action = st.split(" ").let {
            when(it[0]) {
                "COMPLETE" -> Complete(masterMind.player.trees[it[1].toInt()]!!)
                "GROW" -> Grow(masterMind.player.trees[it[1].toInt()]!!)
                "SEED" -> Seed(masterMind.player.trees[it[1].toInt()]!!, masterMind.grid.cells[it[2].toInt()]!!)
                "WAIT" -> Wait()
                else -> error("unknow Action $st")
            }
        }
    }

    // GROW cellIdx | SEED sourceIdx targetIdx | COMPLETE cellIdx | WAIT <message>
    fun performAction() = println(when (this) {
        is Action.Complete -> "COMPLETE ${tree.cell.index} $say"
        is Action.Grow -> "GROW ${tree.cell.index} $say"
        is Action.Seed -> "SEED ${fromTree.cell.index} ${toCell.index} $say"
        is Action.Wait -> "WAIT $say"
    })

    fun debugAction(heuristic: String = "") {
        when (this) {
            is Complete -> System.err.println("Complete ${tree.cell.index} $heuristic")
            is Seed -> System.err.println("Seed from ${fromTree.cell.index} to ${toCell.index} $heuristic")
            is Grow -> System.err.println("Grow ${tree.cell.index} $heuristic")
            is Wait -> System.err.println("Wait $heuristic")
        }
    }
}
