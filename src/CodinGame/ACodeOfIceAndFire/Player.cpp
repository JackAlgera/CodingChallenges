
#include <iostream>
#include <string>
#include <vector>
#include <list>
#include <algorithm>
#include <queue>
#include <set>

using namespace std;

/*
TODO :
Defensive : 
 - Make 3 level 3 units around castle to protect and block off enemies

Offensive :
 - If you have enough gold, make path from closest tile to enemy with level 1 units and win game 
 - Pick strategy depending on what opponent is doing -> if lots of level 2 units, hunt and kill wiht level 3 units 

 - Find closest tile to enemy to create unit path
*/

enum BuildingType {
	HQ,	MINE, TOWER
};

enum CommandType {
	WAIT,
	MOVE,
	TRAIN,
	BUILD
};

ostream &operator<<(ostream &os, CommandType cmdType) {
	switch (cmdType) {
	case WAIT:
		return os << "WAIT";
	case MOVE:
		return os << "MOVE";
	case TRAIN:
		return os << "TRAIN";
	case BUILD:
		return os << "BUILD";
	}
	return os;
}

ostream &operator<<(ostream &os, BuildingType buildType) {
	switch (buildType) {
	case MINE:
		return os << "MINE";
	case TOWER:
		return os << "TOWER";
	}
	return os;
}

class Position {
public:

	int x, y;

	Position(int x, int y) : x(x), y(y) {
	}
};

class Command {

public:

	CommandType t;
	Position p;
	int idOrLevel;

	Command(CommandType t, int idOrLevel, const Position &p) : t(t), idOrLevel(idOrLevel), p(p) {
	}

	void print() {
		if (t != BUILD)
		{
			cout << t << " " << idOrLevel << " " << p.x << " " << p.y << ";";
		}
		else
		{
			if (idOrLevel == 1)
			{
				cout << t << " " << "MINE" << " " << p.x << " " << p.y << ";";
			}
			else
			{
				cout << t << " " << "TOWER" << " " << p.x << " " << p.y << ";";
			}
		}
	}
};

class Unit {

public:

	int id;
	int owner;
	int level;
	bool isHunted = false;
	Position p;

	Unit(int x, int y, int id, int level, int owner) : p(x, y), id(id), level(level), owner(owner) {
	}

	void debug() {
		cerr << "unit of level " << level << " at " << p.x << " " << p.y << " owned by " << owner;
	}

	bool isOwned() {
		return owner == 0;
	}
};

class Building {
    public:

        Position p;
        BuildingType t;
        int distanceHQ;
        int owner;

        Building(int x, int y, int t, int owner) : p(x, y), t(static_cast<BuildingType>(t)), owner(owner) {
        }

        void debug() {
            cerr << t << " at " << p.x << " " << p.y << " owned by " << owner;
        }

        bool isHQ() {
            return t == HQ;
        }

        bool isOwned() {
            return owner == 0;
        }
};

class Cell
{

public:

	Position p;
	char type;
	bool isAccessible = false;
	bool isBeingVisited = false;
	Unit *unitOn = nullptr;
	Building *buildingOn = nullptr;

	Cell() : p(0, 0)
	{
	}

	~Cell()
	{

	}

	void SetPosition(Position p)
	{
		this->p = p;
	}

	void SetPosition(int x, int y)
	{
		p.x = x;
		p.y = y;
	}
};

// Inputs
int MAX_LVL1_UNITS = 25;
int MAX_LVL2_UNITS = 10;
int MAX_LVL3_UNITS = 5;

int xNeighbors[4] = { 0, 1, 0, -1 };
int yNeighbors[4] = { 1, 0, -1, 0 };

int nextX[49] = { 0, 1, 1, 1, 0, -1, -1, -1, -1, 0, 1, 2, 2, 2, 2, 2, 1, 0, -1, -2, -2, -2, -2, -2, -2, -1, 0, 1, 2, 3, 3, 3, 3, 3, 3, 3, 2, 1, 0, -1, -2, -3, -3, -3, -3, -3, -3, -3 };
int nextY[49] = { -1, -1, 0, 1, 1, 1, 0, -1, -2, -2, -2, -2, -1, 0, 1, 2, 2, 2, 2, 2, 1, 0, -1, -2, -3, -3, -3, -3, -3, -3, -2, -1, 0, 1, 2, 3, 3, 3, 3, 3, 3, 3, 2, 1, 0, -1, -2, -3 };

class Game {
	Position playerPos = Position(0,0), opponentPos = Position(11,11);

	vector<Unit> playerUnits;
	vector<Unit> opponentUnits;
	vector<int> nbOfUnits;
	vector<Cell *> trainPositions;

	vector<Building> minePositions;
	vector<Building> buildings;
	int gold, income, upkeep, nbMinesOwned;
	list<Command> commandes;
	Cell board[12][12];
	bool mineBuild = false, initSetupAfterLaunch = false, towerBuild = false;

public:

	void debug() {
		for_each(playerUnits.begin(), playerUnits.end(), [](Unit &u) { u.debug(); });
		for_each(opponentUnits.begin(), opponentUnits.end(), [](Unit &u) { u.debug(); });

		for_each(buildings.begin(), buildings.end(), [](Building &u) { u.debug(); });
	}

	// not useful in Wood 3
	void init() {
		int numberMineSpots;
		cin >> numberMineSpots; cin.ignore();
		for (int i = 0; i < numberMineSpots; i++) {
			int x;
			int y;
			cin >> x >> y; cin.ignore();
			minePositions.push_back(Building(x, y, 1, 0));
		}

		for (int x = 0; x < 12; x++)
		{
			for (int y = 0; y < 12; y++)
			{
				board[x][y].p = Position(x, y);
			}
		}
		nbOfUnits.resize(3);
		mineBuild = false;
		towerBuild = false;
		nbMinesOwned = 0;
	}

	void update() {

		playerUnits.clear();
		opponentUnits.clear();
		buildings.clear();
		commandes.clear();
		trainPositions.clear();
		upkeep = 0, nbMinesOwned = 0;

		for (int i = 0; i < nbOfUnits.size(); i++)
		{
			nbOfUnits[i] = 0;
		}

		// READ TURN INPUT
		cin >> gold; cin.ignore();
		cin >> income; cin.ignore();

		int opponentGold;
		cin >> opponentGold; cin.ignore();
		int opponentIncome;
		cin >> opponentIncome; cin.ignore();

		for (int y = 0; y < 12; y++) {
			string line;
			cin >> line; cin.ignore();
			for (int x = 0; x < 12; x++)
			{
				board[x][y].type = line[x];
				board[x][y].isBeingVisited = false;
				board[x][y].unitOn = nullptr;
				if (board[x][y].type != '#')
				{
					board[x][y].isAccessible = true;
				}
				else
				{
					board[x][y].isAccessible = false;
				}
			}
		}

		for (int y = 0; y < 12; y++)
		{
			for (int x = 0; x < 12; x++)
			{
				if (board[x][y].type == '.' || board[x][y].type == 'X' || board[x][y].type == 'x')
				{
					for (int k = 0; k < 4; k++)
					{
						int newPosX = board[x][y].p.x + xNeighbors[k];
						int newPosY = board[x][y].p.y + yNeighbors[k];
						if (IsOnBoard(newPosX, newPosY) && board[newPosX][newPosY].type == 'O')
						{
							trainPositions.push_back(&board[x][y]);
							break;
						}
					}
				}
			}
		}

		int buildingCount;
		cin >> buildingCount; cin.ignore();
		for (int i = 0; i < buildingCount; i++) {
			int owner;
			int buildingType;
			int x;
			int y;
			cin >> owner >> buildingType >> x >> y; cin.ignore();
			buildings.push_back(Building(x, y, buildingType, owner));
			board[x][y].buildingOn = &buildings.back();
			switch (buildingType)
			{
			case (int)HQ:
				if (owner == 0)
				{
					playerPos.x = x;
					playerPos.y = y;
				}
				else
				{
					opponentPos.x = x;
					opponentPos.y = y;
				}
				break;

			case (int)MINE:
				if (owner == 0)
				{
					nbMinesOwned++;
				}
				break;
			case (int)TOWER:
				break;
			default:
				break;
			}

			board[x][y].isAccessible = false;
		}

		int unitCount;
		cin >> unitCount; cin.ignore();
		for (int i = 0; i < unitCount; i++) {
			int owner;
			int unitId;
			int level;
			int x;
			int y;
			cin >> owner >> unitId >> level >> x >> y; cin.ignore();
			if (owner == 0)
			{
				playerUnits.push_back(Unit(x, y, unitId, level, owner));
				nbOfUnits[level - 1] += 1;
				board[x][y].unitOn = &playerUnits.back();
			}
			else
			{
				opponentUnits.push_back(Unit(x, y, unitId, level, owner));
				board[x][y].unitOn = &opponentUnits.back();
			}
			board[x][y].isAccessible = false;
		}

		if (!initSetupAfterLaunch)
		{
			for (int i = 0; i < minePositions.size(); i++)
			{
				Building * mine = &minePositions[i];
				mine->distanceHQ = abs(playerPos.x - mine->p.x) + abs(playerPos.y - mine->p.y);
			}
			initSetupAfterLaunch = true;
		}

		//PrintBoard();
	}

	void buildOutput() {
		// @TODO "core" of the AI
		buildBuildings();
		moveUnits();
		trainUnits();
	}

	void output() {
		for_each(commandes.begin(), commandes.end(), [](Command &c) {
			c.print();
		});
		cout << "WAIT" << endl;
	}


private:

	void PrintBoard()
	{
		for (int y = 0; y < 12; y++)
		{
			for (int x = 0; x < 12; x++)
			{
				if(board[x][y].unitOn != nullptr)
					cerr << board[x][y].unitOn->id << " ";
			}
			cerr << endl;
		}
	}

	bool IsOnBoard(int x, int y)
	{
		return (x < 11 && x > -1 && y < 11 && y > -1);
	}

	void MoveScout(Unit &unit)
	{
		int nbrMoves = 0;
		set<int, greater <int>> visited;
		queue<Cell *> q;
		q.push(&board[unit.p.x][unit.p.y]);

		while (!q.empty())
		{
			// Take top cell
			Cell *currentCell = q.front();
			q.pop();

			nbrMoves++;
			// Add it's neighbors to list
			for (int i = 0; i < 4; i++)
			{
				int x = currentCell->p.x + xNeighbors[i];
				int y = currentCell->p.y + yNeighbors[i];
				if (IsOnBoard(x, y) && visited.find(x + 12*y) == visited.end())
				{
					//cerr << board[x][y].isAccessible << "\n";
					if (board[x][y].isAccessible && !board[x][y].isBeingVisited && (board[x][y].type == '.' || board[x][y].type == 'X' || board[x][y].type == 'x'))
					{
						board[x][y].isBeingVisited = true;
						board[x][y].isAccessible = false;
						board[unit.p.x][unit.p.y].unitOn = nullptr;
						unit.p = Position(x, y);
						board[x][y].unitOn = &unit;

						commandes.push_back(Command(MOVE, unit.id, Position(x,y)));
						//cerr << "id=" << unit.id << " : moves=" << nbrMoves << "\n";
						return;
					}

					visited.insert(x + 12 * y);
					q.push(&board[x][y]);
				}
			}
		}
	}

	void MoveScountKiller(Unit &unit)
	{
		int closestDistance = 10000;
		Unit *theHunted = nullptr;

		for (auto &opponentUnit : opponentUnits)
		{
			if (opponentUnit.level == 1 && !opponentUnit.isHunted)
			{
				int d = abs(unit.p.x - opponentUnit.p.x) + abs(unit.p.y - opponentUnit.p.y);
				if (d < closestDistance)
				{
					closestDistance = d;
					theHunted = &opponentUnit;
				}
			}
		}

		if (theHunted != nullptr)
		{
			theHunted->isHunted = true;
			commandes.push_back(Command(MOVE, unit.id, theHunted->p));
		}
		else
		{
			commandes.push_back(Command(MOVE, unit.id, Position(opponentPos.x, opponentPos.y)));
		}
	}

	void MoveBrute(Unit &unit)
	{
		commandes.push_back(Command(MOVE, unit.id, Position(opponentPos.x, opponentPos.y)));
	}

	void PrintUnits()
	{
		cerr << "lvl 1=" << nbOfUnits[0] << " : lvl 2=" << nbOfUnits[1] << " : lvl 3=" << nbOfUnits[2] << "\n";
	}

	void buildBuildings()
	{
		if (!towerBuild && gold > 15 && playerUnits.size() > 4)
		{
			if (playerPos.x == 0 && board[1][1].unitOn == nullptr)
			{
				gold -= 15;
				towerBuild = true;
				commandes.push_back(Command(BUILD, 2, Position(1, 1)));
				cerr << "build tower\n";
			}
			else if(playerPos.x == 11 && board[10][10].unitOn == nullptr)
			{
				gold -= 15;
				towerBuild = true;
				commandes.push_back(Command(BUILD, 2, Position(10, 10)));
				cerr << "build tower\n";
			}
		}

		if ((!mineBuild && gold > 50) || (gold > (60 + nbMinesOwned*4) && playerUnits.size() > opponentUnits.size()))
		{
			Building *minePos = nullptr;
			int minDistance = 1000;
			for (int i = 0; i < minePositions.size(); i++)
			{
				int x = minePositions[i].p.x;
				int y = minePositions[i].p.y;
				Building *currentMine = &minePositions[i];

				if (board[x][y].type == 'O' && board[x][y].buildingOn == nullptr && board[x][y].unitOn == nullptr && (currentMine->distanceHQ < minDistance))
				{
					minDistance = currentMine->distanceHQ;
					minePos = &minePositions[i];
				}
			}
			if (minePos != nullptr)
			{
				commandes.push_back(Command(BUILD, 1, Position(minePos->p.x, minePos->p.y)));
				gold -= nbMinesOwned * 4 + 20;
				nbMinesOwned++;
				mineBuild = true;
			}
		}
	}

	void trainUnits() 
	{
		if (playerUnits.size() < 6 || mineBuild)
		{
			//cerr << "size =" << playerUnits.size() << "mine build=" << mineBuild << "\n";

			if (trainPositions.size() != 0)
			{
				/*
				// Build leve 3 units on their level 3 units
				// TODO (optimisation) : first check if((nbOfUnits[2] < MAX_LVL3_UNITS) && (gold > 30) && ((income - upkeep) > 20)) to see if you can build a level 3 bloke
				for (int i = 0; i < trainPositions.size(); i++)
				{
					Cell *cell = trainPositions[i];
					if (cell->unitOn != nullptr && cell->unitOn->level == 3)
					{
						if ((nbOfUnits[2] < MAX_LVL3_UNITS) && (gold > 30) && ((income - upkeep) > 20))
						{
							commandes.push_back(Command(TRAIN, 3, Position(cell->p.x, cell->p.y)));
							cell->type = 'O';
							cell->unitOn->level = 100;
							nbOfUnits[2] += 1;
							upkeep += 20;
							gold -= 30;
						}
						else
						{
							break;
						}
					}
				}
				*/

				// Check to build level 2 units on enemy level 1 units -> starting from end so that I can remove positions more easily
				for (int i = trainPositions.size() - 1; i > 0; i--)
				{
					Cell *cell = trainPositions[i];
					if (cell->unitOn != nullptr && cell->unitOn->level == 1 && cell->unitOn->owner == 1)
					{
						if ((nbOfUnits[1] < MAX_LVL2_UNITS) && (gold > 20) && ((income - upkeep) > 4))
						{
							commandes.push_back(Command(TRAIN, 2, Position(cell->p.x, cell->p.y)));
							cell->type = 'O';
							cell->unitOn->level = 100;
							trainPositions.erase(trainPositions.begin() + i);
							nbOfUnits[1] += 1;
							upkeep += 4;
							gold -= 20;
						}
						else
						{
							break;
						}
					}
				}

				// Build level 1 units
				for (int i = 0; i < trainPositions.size(); i++)
				{
					Cell *cell = trainPositions[i];
					if (cell->unitOn == nullptr)
					{
						if ((nbOfUnits[0] < MAX_LVL1_UNITS) && (gold > 10) && ((income - upkeep) > 0))
						{
							commandes.push_back(Command(TRAIN, 1, Position(cell->p.x, cell->p.y)));
							nbOfUnits[0] += 1;
							upkeep += 1;
							gold -= 10;
						}
						else
						{
							break;
						}
					}
				}

				// Build level 3 units
				for (int i = 0; i < trainPositions.size(); i++)
				{
					Cell *cell = trainPositions[i];
					if (cell->unitOn != nullptr && (cell->unitOn->level == 3 || cell->unitOn->level == 2))
					{
						if ((nbOfUnits[2] < MAX_LVL3_UNITS) && (gold > 30) && ((income - upkeep) > 20))
						{
							commandes.push_back(Command(TRAIN, 3, Position(cell->p.x, cell->p.y)));
							cell->type = 'O';
							cell->unitOn->level = 100;
							nbOfUnits[2] += 1;
							upkeep += 20;
							gold -= 30;
						}
						else
						{
							break;
						}
					}
				}
			}

			else // Build around HQ
			{
				for (int i = 0; i < 49; i++)
				{
					int x = playerPos.x + nextX[i];
					int y = playerPos.y + nextY[i];

					if (IsOnBoard(x, y) && board[x][y].isAccessible)
					{
						// Train level 1 scouts
						if ((nbOfUnits[0] < MAX_LVL1_UNITS) && (gold > 10) && ((income - upkeep) > 0))
						{
							commandes.push_back(Command(TRAIN, 1, Position(x, y)));
							nbOfUnits[0] += 1;
							upkeep += 1;
							gold -= 10;
						}

						// Train level 2 scout killers
						else if ((nbOfUnits[1] < MAX_LVL2_UNITS) && (gold > 20) && ((income - upkeep) > 4))
						{
							commandes.push_back(Command(TRAIN, 2, Position(x, y)));
							nbOfUnits[1] += 1;
							upkeep += 4;
							gold -= 20;
						}

						else if ((nbOfUnits[2] < MAX_LVL3_UNITS) && (gold > 30) && ((income - upkeep) > 20))
						{
							commandes.push_back(Command(TRAIN, 3, Position(x, y)));
							nbOfUnits[2] += 1;
							upkeep += 20;
							gold -= 30;
						}
						else
						{
							return;
						}
					}
				}
			}
		}
	}

	// move to the center
	void moveUnits() {
		Position center(getOpponentHQ().p.x, getOpponentHQ().p.y);
		for (auto &unit : playerUnits) {
			//cerr << unit.id << " ";
			switch (unit.level)
			{
			case 1:
				MoveScout(unit);
				break;
			case 2:
				MoveScountKiller(unit);
				break;
			case 3:
				MoveBrute(unit);
				break;
			default:
				break;
			}
		}
	}

	// train near the HQ for now
	Position findTrainingPosition() {
		const Building &HQ = getHQ();

		if (HQ.p.x == 0) {
			return Position(0, 1);
		}
		return Position(11, 10);
	}

	const Building &getHQ() {
		for (auto &b : buildings) {
			if (b.isHQ() && b.isOwned()) {
				return b;
			}
		}
	}

	const Building &getOpponentHQ() {
		for (auto &b : buildings) {
			if (b.isHQ() && !b.isOwned()) {
				return b;
			}
		}
	}
};

int main()
{
	Game g;
	g.init();

	// game loop
	while (true) {
		g.update();
		//g.debug();
		g.buildOutput();
		g.output();
	}
	return 0;
}
