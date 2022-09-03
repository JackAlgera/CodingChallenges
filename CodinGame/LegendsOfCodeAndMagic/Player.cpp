#include <iostream>
#include <string>
#include <vector>
#include <algorithm>
#include <ctime>

using namespace std;

// Card parameters
int maxGreenItems = 8;
int maxRedItems = 3;
int maxBlueItems = 1;
int max7Cards = 2;
int max6Cards = 3;
int max5Cards = 4;
int max2Cards = 7;

vector<int> reallyGoodCards = { 1,3,5,11,14,18,25,26,28,29,30,32,33,44,48,49,50,55,63,70,80,83,84,91,94,95,96,97,99,116,117,119,120,121,123,128,131,136,143,142,144,148,149,150,154 };
vector<int> goodCards = { 2,6,7,13,16,20,24,27,31,39,41,51,54,62,65,69,71,73,79,85,92,93,100,110,118,122,124,126,127,129,132,133,134,135,145,146,151,155,156,158,160 };
vector<int> normalCards = { 4,8,10,15,21,23,36,38,40,42,45,52,64,66,67,72,81,86,98,103,112,125,130,137,139,141,147,152,157,159 };
vector<int> badCards = { 9,12,17,34,37,43,46,47,53,56,58,68,74,75,77,78,82,88,89,101,104,109,111,113,114,138 };
vector<int> reallyBadCards = { 19,22,35,57,59,60,61,76,87,90,102,105,106,107,108,115,140,153 };

struct Player {
    int playerHealth, playerMana, playerDeck, playerRune;
} player, enemy;

struct Card {
    int cardNumber, instanceId, location, cardType, cost, attack, defense, myHealthChange, opponentHealthChange, cardDraw, value, position;
    string abilities;
	bool isDead;
	bool hasAttacked;
};

struct Deck {
	int numberOf2Cards = 0, numberOf5Cards = 0, numberOf6Cards = 0, numberOf7Cards = 0;
	int numberOfGreenItems = 0, numberOfRedItems = 0, numberOfBlueItems = 0;
} playerDeck;

// Functions
int abilitiesValue(string abilities);
bool sortingTest(Card card1, Card card2);
bool sortingTestAttack(Card card1, Card card2);
void optimalChoice(int offset, int k, int currentMoveCost);
void optimalCardCardCost(int currentMoveCost);
void clearVectors();
void optimalAttack(int myCreatureCurrentPosition, int currentValue, string currentAttack);
void optimalAttackIfDead(int myCreatureCurrentPosition, int currentValue, string currentAttack);
int getCardQuality(int cardNumber);

// Some variables
int totalValueCardUse = 0;
vector<Card> optimalCardsUse, combination, cards, enemyCreaturesWithGuard, enemyCreaturesWithoutGuard, myCreatures, myCreaturesJustSummoned, cardsInHand, cardsToSummon, cardsToPickFrom, greenCardsReserve;

string moves = "", attackSequence = "";
int bestAttackValue = 0, numberOfPaths = 0, sizeOfMyCreatures, sizeOfEnemyCreaturesWithGuard, sizeOfEnemyCreaturesWithoutGuard, currentMoveValue = 0;
int sizeEnemyCreaturesWithGuard = 0, sizeEnemyCreaturesWithoutGuard = 0;
double totalTime = 0;
clock_t clockStart;
const double maxStopTime = 60;

int main()
{
    // game loop
    while (1) {
		clockStart = clock();
        int opponentHand, cardCount;

        cin >> player.playerHealth >> player.playerMana >> player.playerDeck >> player.playerRune; cin.ignore();
        cin >> enemy.playerHealth >> enemy.playerMana >> enemy.playerDeck >> enemy.playerRune; cin.ignore();
        cin >> opponentHand; cin.ignore();
        cin >> cardCount; cin.ignore();

        //-------------------------------------------------------------------------------------------------------
        //----------------------------------- Draft phase -------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------

        if(cardCount == 3 && player.playerMana == 0)
        {
			cardsToPickFrom.clear();
            int cardNumber = 0;
			for (int i = 0; i < cardCount; i++)
			{
				Card newCard;
				cin >> newCard.cardNumber >> newCard.instanceId >> newCard.location >> newCard.cardType >> newCard.cost >> newCard.attack >> newCard.defense >> newCard.abilities >> newCard.myHealthChange >> newCard.opponentHealthChange >> newCard.cardDraw; cin.ignore();
				newCard.value = getCardQuality(newCard.cardNumber);
				newCard.position = i;
				cardsToPickFrom.push_back(newCard);
				cerr << "" << newCard.value << endl;
				//newCard.value = (newCard.attack + newCard.defense - newCard.cost)*2 + abilitiesValue(newCard.abilities) - newCard.cardType*2 - (newCard.cardType == 3) * 1000 - ((newCard.cardType == 2 || newCard.cardType == 1) && newCard.attack == 0 && newCard.defense == 0) * 1000;
			}
			sort(cardsToPickFrom.begin(), cardsToPickFrom.begin() + cardsToPickFrom.size(), sortingTest);
			bool cardChosen = false;
			cardNumber = cardsToPickFrom.back().position;

			while (!cardChosen && !cardsToPickFrom.empty())
			{
				Card newCard = cardsToPickFrom.back();
				cardsToPickFrom.pop_back();

				if (newCard.cost >= 7)
				{
					if (playerDeck.numberOf7Cards < max7Cards)
					{
						bool cardIsGood = false;
						switch (newCard.cardType)
						{
						case 0:
							cardIsGood = true;
							break;
						case 1: // Green item
							if (playerDeck.numberOfGreenItems < maxGreenItems)
							{
								playerDeck.numberOfGreenItems++;
								cardIsGood = true;
							}
							break;
						case 2: // Red item
							if (playerDeck.numberOfRedItems < maxRedItems)
							{
								playerDeck.numberOfRedItems++;
								cardIsGood = true;
							}
							break;
						case 3: // Blue item
							if (playerDeck.numberOfBlueItems < maxBlueItems)
							{
								playerDeck.numberOfBlueItems++;
								cardIsGood = true;
							}
							break;
						}
						if (cardIsGood)
						{
							playerDeck.numberOf7Cards++;
							cardChosen = true;
							cardNumber = newCard.position;
						}
					}
				}
				else if (newCard.cost == 6)
				{
					if (playerDeck.numberOf6Cards < max6Cards)
					{
						bool cardIsGood = false;
						switch (newCard.cardType)
						{
						case 0:
							cardIsGood = true;
							break;
						case 1: // Green item
							if (playerDeck.numberOfGreenItems < maxGreenItems)
							{
								playerDeck.numberOfGreenItems++;
								cardIsGood = true;
							}
							break;
						case 2: // Red item
							if (playerDeck.numberOfRedItems < maxRedItems)
							{
								playerDeck.numberOfRedItems++;
								cardIsGood = true;
							}
							break;
						case 3: // Blue item
							if (playerDeck.numberOfBlueItems < maxBlueItems)
							{
								playerDeck.numberOfBlueItems++;
								cardIsGood = true;
							}
							break;
						}
						if (cardIsGood)
						{
							playerDeck.numberOf6Cards++;
							cardChosen = true;
							cardNumber = newCard.position;
						}
					}
				}
				else if (newCard.cost == 5)
				{
					if (playerDeck.numberOf5Cards < max5Cards)
					{
						bool cardIsGood = false;
						switch (newCard.cardType)
						{
						case 0:
							cardIsGood = true;
							break;
						case 1: // Green item
							if (playerDeck.numberOfGreenItems < maxGreenItems)
							{
								playerDeck.numberOfGreenItems++;
								cardIsGood = true;
							}
							break;
						case 2: // Red item
							if (playerDeck.numberOfRedItems < maxRedItems)
							{
								playerDeck.numberOfRedItems++;
								cardIsGood = true;
							}
							break;
						case 3: // Blue item
							if (playerDeck.numberOfBlueItems < maxBlueItems)
							{
								playerDeck.numberOfBlueItems++;
								cardIsGood = true;
							}
							break;
						}
						if (cardIsGood)
						{
							playerDeck.numberOf5Cards++;
							cardChosen = true;
							cardNumber = newCard.position;
						}
					}
				}
				else if (newCard.cost == 2)
				{
					if (playerDeck.numberOf2Cards < max2Cards)
					{
						bool cardIsGood = false;
						switch (newCard.cardType)
						{
						case 0:
							cardIsGood = true;
							break;
						case 1: // Green item
							if (playerDeck.numberOfGreenItems < maxGreenItems)
							{
								playerDeck.numberOfGreenItems++;
								cardIsGood = true;
							}
							break;
						case 2: // Red item
							if (playerDeck.numberOfRedItems < maxRedItems)
							{
								playerDeck.numberOfRedItems++;
								cardIsGood = true;
							}
							break;
						case 3: // Blue item
							if (playerDeck.numberOfBlueItems < maxBlueItems)
							{
								playerDeck.numberOfBlueItems++;
								cardIsGood = true;
							}
							break;
						}
						if (cardIsGood)
						{
							playerDeck.numberOf2Cards++;
							cardChosen = true;
							cardNumber = newCard.position;
						}
					}
				}
				else
				{
					bool cardIsGood = false;
					switch (newCard.cardType)
					{
					case 0:
						cardIsGood = true;
						break;
					case 1: // Green item
						if (playerDeck.numberOfGreenItems < maxGreenItems)
						{
							playerDeck.numberOfGreenItems++;
							cardIsGood = true;
						}
						break;
					case 2: // Red item
						if (playerDeck.numberOfRedItems < maxRedItems)
						{
							playerDeck.numberOfRedItems++;
							cardIsGood = true;
						}
						break;
					case 3: // Blue item
						if (playerDeck.numberOfBlueItems< maxBlueItems)
						{
							playerDeck.numberOfBlueItems++;
							cardIsGood = true;
						}
						break;
					}
					if (cardIsGood)
					{
						cardChosen = true;
						cardNumber = newCard.position;
					}
				}
			}
            cout << "PICK " << cardNumber << endl;
        }
        //-------------------------------------------------------------------------------------------------------
        //----------------------------------- Battle phase -------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------

        else
        {
            clearVectors();
            moves = "";

            for (int i = 0; i < cardCount; i++) {
                Card newCard;
                cin >> newCard.cardNumber >> newCard.instanceId >> newCard.location >> newCard.cardType >> newCard.cost >> newCard.attack >> newCard.defense >> newCard.abilities >> newCard.myHealthChange >> newCard.opponentHealthChange >> newCard.cardDraw; cin.ignore();

                switch(newCard.location)
                {
                    case 0: // Player's hand
                        cardsInHand.push_back(newCard);
						break;
                    case 1: // Player's side of the board
						newCard.isDead = false;
						newCard.hasAttacked = false;
						myCreatures.push_back(newCard);
						break;
                    case -1: // Enemy's side of the board
						newCard.isDead = false;
						newCard.hasAttacked = false;
                        if(newCard.abilities[3] == 'G')
                        {
                            enemyCreaturesWithGuard.push_back(newCard);
                        }
                        else
                        {
							enemyCreaturesWithoutGuard.push_back(newCard);
                        }
						break;
                }
            }

            //----------------------------------- Cards to use phase -------------------------------------------------------

            for (Card & card : cardsInHand)
            {
				if (card.cost <= player.playerMana)
				{
					switch (card.cardType)
					{
					case 0: //Creature
						if (myCreatures.size() < 6)
						{
							cards.push_back(card);
						}
						break;
					case 1: //Green item
						cards.push_back(card);
						break;
					case 2: //Red Item
						if (enemyCreaturesWithoutGuard.size() + enemyCreaturesWithGuard.size() > 0)
						{
							if (card.abilities[3] != 'G')
							{
								cards.push_back(card);
							}
							else
							{
								if (!enemyCreaturesWithGuard.empty())
								{
									cards.push_back(card);
								}
							}
						}
						break;
					case 3: //Blue item
						cards.push_back(card);
						break;
					}
				}
            }

			currentMoveValue = -1000;
            for (int i = 1; i <= cards.size(); i++)
            {
				optimalChoice(0, i, 0);
            }

            for(Card & card : optimalCardsUse)
            {
				bool creatureHasGuard = false;
				int bestCardPosition = 0;
				int bestCardValue = 0;
                switch(card.cardType)
                {
                    case 0: // Creature
                        moves += "SUMMON " + to_string(card.instanceId) + ";";
						myCreaturesJustSummoned.push_back(card);
                        if(card.abilities[1] == 'C')
                            myCreatures.push_back(card);
                        break;
                    case 1: // Green card
						if (myCreatures.empty())
						{
							greenCardsReserve.push_back(card);
						}
						else
						{
							int creaturePosition = 0;
							moves += "USE " + to_string(card.instanceId) + " " + to_string(myCreatures[creaturePosition].instanceId) + ";";
							myCreatures[creaturePosition].attack += card.attack;
							myCreatures[creaturePosition].defense += card.defense;
							if (card.abilities[3] == 'G')
							{
								myCreatures[creaturePosition].abilities[3] = 'G';
							}
							if (card.abilities[4] == 'L')
							{
								myCreatures[creaturePosition].abilities[4] = 'L';
							}
							if (card.abilities[5] == 'W')
							{
								myCreatures[creaturePosition].abilities[5] = 'W';
							}
						}
                        break;
                    case 2: // Red card
						for (int i = 0; i < enemyCreaturesWithGuard.size(); i++)
						{
							int currentCardValue = 0;
							Card enemyCreature = enemyCreaturesWithGuard[i];

							if (enemyCreature.abilities[5] != 'W')
							{
								if (enemyCreature.defense + card.defense <= 0)
								{
									currentCardValue += 10;
									currentCardValue += enemyCreature.defense;
								}
								else if (card.defense == 0)
								{
									currentCardValue += 100 + enemyCreature.defense;
								}
								else
								{
									currentCardValue += (-1)*card.defense;
								}
							}
							else
							{
								currentCardValue += 6;
							}

							if (currentCardValue > bestCardValue)
							{
								creatureHasGuard = true;
								bestCardValue = currentCardValue;
								bestCardPosition = i;
							}
						}

						for (int i = 0; i < enemyCreaturesWithoutGuard.size(); i++)
						{
							int currentCardValue = 0;
							Card enemyCreature = enemyCreaturesWithoutGuard[i];

							if (enemyCreature.abilities[5] != 'W')
							{
								if (enemyCreature.defense + card.defense <= 0)
								{
									currentCardValue += enemyCreature.attack;
								}
								else
								{
									currentCardValue = -50;
								}
							}
							else
							{
								currentCardValue = -100;
							}

							if (currentCardValue > bestCardValue)
							{
								creatureHasGuard = false;
								bestCardValue = currentCardValue;
								bestCardPosition = i;
							}
						}
						if (creatureHasGuard)
						{
							moves += "USE " + to_string(card.instanceId) + " " + to_string(enemyCreaturesWithGuard[bestCardPosition].instanceId) + ";";

							enemyCreaturesWithGuard[bestCardPosition].attack -= card.attack;
							enemyCreaturesWithGuard[bestCardPosition].defense -= card.defense;
							if (card.abilities[3] == 'G')
							{
								enemyCreaturesWithGuard[bestCardPosition].abilities[3] = '-';
							}
							if (card.abilities[5] == 'W')
							{
								enemyCreaturesWithGuard[bestCardPosition].abilities[5] = '-';
							}
							if (card.defense <= 0)
							{
								if (!enemyCreaturesWithGuard.empty())
								{
									enemyCreaturesWithGuard.erase(enemyCreaturesWithGuard.begin() + bestCardPosition);
								}
							}
						}
						else
						{
							moves += "USE " + to_string(card.instanceId) + " " + to_string(enemyCreaturesWithoutGuard[bestCardPosition].instanceId) + ";";

							enemyCreaturesWithoutGuard[bestCardPosition].attack -= card.attack;
							enemyCreaturesWithoutGuard[bestCardPosition].defense -= card.defense;
							if (card.abilities[3] == 'G')
							{
								enemyCreaturesWithoutGuard[bestCardPosition].abilities[3] = '-';
							}
							if (card.abilities[5] == 'W')
							{
								enemyCreaturesWithoutGuard[bestCardPosition].abilities[5] = '-';
							}
							if (card.defense <= 0)
							{
								if (!enemyCreaturesWithoutGuard.empty())
								{
									enemyCreaturesWithoutGuard.erase(enemyCreaturesWithoutGuard.begin() + bestCardPosition);
								}
							}
						}
                        break;
                    case 3:
						if (card.defense != 0)
						{
							bestCardPosition = -1;
							if (enemy.playerHealth == card.defense)
							{
								int bestCardValue = 1000;
							}

							for (int i = 0; i < enemyCreaturesWithGuard.size(); i++)
							{
								int currentCardValue = 0;
								Card enemyCreature = enemyCreaturesWithGuard[i];

								if (enemyCreature.abilities[5] != 'W')
								{
									if (enemyCreature.defense <= card.defense)
									{
										currentCardValue += 10;
										currentCardValue += enemyCreature.defense;
									}
									else
									{
										currentCardValue += card.defense;
									}
								}
								else
								{
									currentCardValue += 4;
								}

								if (currentCardValue > bestCardValue)
								{
									creatureHasGuard = true;
									bestCardValue = currentCardValue;
									bestCardPosition = i;
								}
							}

							for (int i = 0; i < enemyCreaturesWithoutGuard.size(); i++)
							{
								int currentCardValue = 0;
								Card enemyCreature = enemyCreaturesWithoutGuard[i];

								if (enemyCreature.abilities[5] != 'W')
								{
									if (enemyCreature.defense <= card.defense)
									{
										currentCardValue += enemyCreature.attack;
									}
									else
									{
										currentCardValue = -50;
									}
								}
								else
								{
									currentCardValue = -100;
								}

								if (currentCardValue > bestCardValue)
								{
									creatureHasGuard = false;
									bestCardValue = currentCardValue;
									bestCardPosition = i;
								}
							}

							if (bestCardPosition == -1)
							{
								moves += "USE " + to_string(card.instanceId) + " -1;";
							}
							else
							{
								if(creatureHasGuard)
									moves += "USE " + to_string(card.instanceId) + " " + to_string(enemyCreaturesWithGuard[bestCardPosition].instanceId) + ";";
								else
									moves += "USE " + to_string(card.instanceId) + " " + to_string(enemyCreaturesWithoutGuard[bestCardPosition].instanceId) + ";";
							}
						}
						else
						{
							moves += "USE " + to_string(card.instanceId) + " -1;";
						}
						break;
                }
            }

			while (!greenCardsReserve.empty())
			{
				Card greenCard = greenCardsReserve.back();
				greenCardsReserve.pop_back();
				moves += "USE " + to_string(greenCard.instanceId) + " " + to_string(myCreaturesJustSummoned[0].instanceId) + ";";
			}
            //----------------------------------- Attack phase -------------------------------------------------------


			// USE RED CARDS DURING ATTACK PHASE

			int totalEnemyAttack = 0;
			int totalMyDefence = player.playerHealth;
			for (Card & creature : enemyCreaturesWithGuard)
			{
				totalEnemyAttack += creature.attack;
			}
			for (Card & creature : enemyCreaturesWithoutGuard)
			{
				totalEnemyAttack += creature.attack;
			}
			for (Card & creature : myCreatures)
			{
				if (creature.abilities[3] == 'G')
				{
					totalMyDefence += creature.attack;
				}
			}

			sort(myCreatures.begin(), myCreatures.begin() + myCreatures.size(), sortingTestAttack);

			bestAttackValue = -1000;
			numberOfPaths = 0;
			attackSequence = "";
			Card enemyPlayer;
			enemyPlayer.instanceId = -1;
			enemyPlayer.isDead = false;
			enemyCreaturesWithoutGuard.push_back(enemyPlayer);
			sizeEnemyCreaturesWithGuard = enemyCreaturesWithGuard.size();
			sizeEnemyCreaturesWithoutGuard = enemyCreaturesWithoutGuard.size();

			//if (totalMyDefence > totalEnemyAttack)
				int n = -1;
				do
				{
					n++;
					optimalAttack(0, 0, "");
				} while (totalTime < 95 && next_permutation(myCreatures.begin(), myCreatures.end(), sortingTestAttack));

				clock_t end = clock();

			moves += attackSequence;
			cerr << "" << "totalTime=" << totalTime << "ms -- nb of paths=" << (numberOfPaths) << endl;
            cout << moves << endl;
        }
    }
}

//-----------------------------------------------------------------------------------------------------
//----------------------------------- Functions -------------------------------------------------------
//-----------------------------------------------------------------------------------------------------

int abilitiesValue(string abilities)
{
    int value = 0;
    if(abilities[0] == 'B') value += 2;
    if(abilities[1] == 'C') value += 0;
    if(abilities[2] == 'G') value += 6;
    if(abilities[3] == 'D') value += 2;
    if(abilities[4] == 'L') value += 200;
    if(abilities[5] == 'W') value += 1;
    return value;
}

bool sortingTest(Card card1, Card card2)
{
    return (card1.value < card2.value);
}

bool sortingTestAttack(Card card1, Card card2)
{
	return (card1.attack < card2.attack);
}

void optimalChoice(int offset, int k, int currentMoveCost) {
	if (k == 0) {
		if (currentMoveCost <= player.playerMana)
		{
			optimalCardCardCost(currentMoveCost);
		}
		return;
	}
	for (int i = offset; i <= cards.size() - k; ++i) {
		combination.push_back(cards[i]);
		currentMoveCost += cards[i].cost;
		optimalChoice(i + 1, k - 1, currentMoveCost);
		combination.pop_back();
		currentMoveCost -= cards[i].cost;
	}
}

void optimalCardCardCost(int currentMoveCost)
{
	bool needsCreature = false;
	bool hasCreature = false;
	currentMoveValue = currentMoveCost;
	for (Card & card : combination)
	{
		switch (card.cardType)
		{
		case 0: // Creature
			hasCreature = true;
			if (myCreatures.empty())
			{
				currentMoveValue += 20;
			}
			currentMoveValue += card.attack;
			break;
		case 1: // Green card
			needsCreature = true;
			currentMoveValue += card.attack + 1;
			break;
		case 2: // Red card
			if (card.abilities[3] == 'G')
			{
				if (card.cost == 0)
				{
					currentMoveValue += 1000;
				}
				currentMoveValue += 7;
			}
			if (card.defense < 0)
			{
				int i = 0;
				bool enemyFound = false;
				while(i < enemyCreaturesWithGuard.size() && !enemyFound)
				{
					Card enemyCreature = enemyCreaturesWithGuard[i];
					if ((enemyCreature.defense + card.defense) <= 0)
					{
						currentMoveValue += enemyCreature.defense + enemyCreature.attack;
						enemyFound = true;
					}
					i++;
				}
			}
			break;
		case 3: // Blue card
			break;
		}
	}
	if (currentMoveValue > totalValueCardUse)
	{
		if (needsCreature)
		{
			if (hasCreature || !myCreatures.empty())
			{
				totalValueCardUse = currentMoveValue;
				optimalCardsUse = combination;
			}
		}
		else
		{
			totalValueCardUse = currentMoveValue;
			optimalCardsUse = combination;
		}
	}
}

void optimalAttack(int myCreatureCurrentPosition, int currentValue, string currentAttack)
{
	if (totalTime < maxStopTime)
	{
		if (myCreatureCurrentPosition == myCreatures.size())
		{
			numberOfPaths++;
			clock_t endTurn = clock();
			totalTime = 1000.0f * double(endTurn - clockStart) / CLOCKS_PER_SEC;
			if (currentValue > bestAttackValue)
			{
				bestAttackValue = currentValue;
				attackSequence = currentAttack;
			}
		}
		else
		{
			for (int i = myCreatureCurrentPosition; i < myCreatures.size(); i++)
			{
				if (!myCreatures[i].hasAttacked)
				{
					myCreatures[i].hasAttacked = true;
					Card myCreature = myCreatures[i];
					if (!enemyCreaturesWithGuard.empty())
					{
						for (int j = 0; j < enemyCreaturesWithGuard.size(); j++)
						{
							if (!enemyCreaturesWithGuard[j].isDead)
							{
								bool hadWard = false;
								Card enemyCreature = enemyCreaturesWithGuard[j];

								int newCurrentValue = currentValue;
								string newCurrentAttack = currentAttack;

								if ((myCreature.defense <= enemyCreature.attack || enemyCreature.abilities[4] == 'L') && (myCreature.abilities[5] != 'W'))
								{
									newCurrentValue -= myCreature.attack;
									if (myCreature.abilities[3] == 'G')
									{
										newCurrentValue -= 1000;
									}
								}
								newCurrentAttack += "ATTACK " + to_string(myCreature.instanceId) + " " + to_string(enemyCreature.instanceId) + ";";

								if (myCreature.attack == 0)
								{
									newCurrentValue -= 1000;
								}

								// Test to see if we kill the target with guard
								if ((myCreature.attack >= enemyCreature.defense || myCreature.abilities[4] == 'L') && !(enemyCreature.abilities[5] == 'W'))
								{
									if (myCreature.abilities[4] == 'L')
									{
										newCurrentValue += 1 + (enemyCreature.attack + enemyCreature.defense) / 2;
									}
									newCurrentValue += enemyCreature.attack;
									enemyCreaturesWithGuard[j].isDead = true;
								}
								// We didnt kill the target
								else
								{
									if (enemyCreature.abilities[5] == 'W')
									{
										enemyCreaturesWithGuard[j].abilities[5] = '-';
										hadWard = true;
									}
								}
								myCreatures[i].hasAttacked = true;
								optimalAttack(i + 1, newCurrentValue + 50, newCurrentAttack);
								myCreatures[i].hasAttacked = false;
								enemyCreaturesWithGuard[j].isDead = false;
								if (hadWard)
								{
									enemyCreaturesWithGuard[j].abilities[5] = 'W';
								}
							}
						}
					}
					else
					{
						for (int j = 0; j < enemyCreaturesWithoutGuard.size(); j++)
						{
							if (!enemyCreaturesWithoutGuard[j].isDead)
							{
								bool hadWard = false;
								Card enemyCreature = enemyCreaturesWithoutGuard[j];

								int newCurrentValue = currentValue;
								string newCurrentAttack = currentAttack;

								if (enemyCreature.instanceId == -1)
								{
									newCurrentValue += (int)((double)myCreature.attack * 0.75);
									newCurrentAttack += "ATTACK " + to_string(myCreature.instanceId) + " -1;";
									myCreatures[i].hasAttacked = true;
									optimalAttack(i + 1, newCurrentValue + 50, newCurrentAttack);
									myCreatures[i].hasAttacked = false;
								}
								else
								{
									if ((myCreature.defense <= enemyCreature.attack || enemyCreature.abilities[4] == 'L') && (myCreature.abilities[5] != 'W'))
									{
										newCurrentValue -= myCreature.attack;
										if (myCreature.abilities[3] == 'G')
										{
											newCurrentValue -= myCreature.defense * 2;
										}
									}
									newCurrentAttack += "ATTACK " + to_string(myCreature.instanceId) + " " + to_string(enemyCreature.instanceId) + ";";
									if (myCreature.attack == 0)
									{
										newCurrentValue -= 1000;
									}
									// Test to see if we kill the target with guard
									if ((myCreature.attack >= enemyCreature.defense || myCreature.abilities[4] == 'L') && (enemyCreature.abilities[5] != 'W'))
									{
										if (myCreature.abilities[4] == 'L')
										{
											newCurrentValue += 1 + (enemyCreature.attack + enemyCreature.defense)/2;
										}
										newCurrentValue += enemyCreature.attack;
										enemyCreaturesWithoutGuard[j].isDead = true;
									}
									// We didnt kill the target
									else
									{
										if (enemyCreature.abilities[5] == 'W')
										{
											enemyCreaturesWithoutGuard[j].abilities[5] = '-';
										}
									}

									myCreatures[i].hasAttacked = true;
									optimalAttack(i + 1, newCurrentValue + 50, newCurrentAttack);
									myCreatures[i].hasAttacked = false;
									enemyCreaturesWithoutGuard[j].isDead = false;
									if (hadWard)
									{
										enemyCreaturesWithoutGuard[j].abilities[5] = 'W';
									}
								}
							}
						}
					}
				}
			}
		}
	}
	else
	{
		return;
	}
}
/*
void optimalAttackIfDead(int myCreatureCurrentPosition, int currentValue, string currentAttack)
{
	if (myCreatureCurrentPosition == myCreatures.size())
	{
		numberOfPaths++;
		clock_t clockEnd = clock();
		totalTime = 1000.0 * (double(clockStart - clockEnd) / CLOCKS_PER_SEC);
		if (currentValue > bestAttackValue)
		{
			bestAttackValue = currentValue;
			attackSequence = currentAttack;
		}
	}
	else
	{
		Card myCreature = myCreatures[myCreatureCurrentPosition];
		if (sizeEnemyCreaturesWithGuard + sizeEnemyCreaturesWithoutGuard - 1 != 0)
		{
			for (int j = 0; j < enemyCreaturesWithGuard.size(); j++)
			{
				if (!enemyCreaturesWithGuard[j].isDead)
				{
					bool hadWard = false;
					Card enemyCreature = enemyCreaturesWithGuard[j];

					int newCurrentValue = currentValue;
					string newCurrentAttack = currentAttack;

					if (myCreature.defense <= enemyCreature.attack)
					{
						newCurrentValue -= myCreature.attack;
						if (myCreature.abilities[3] == 'G')
						{
							newCurrentValue -= myCreature.defense;
						}
					}
					newCurrentAttack += "ATTACK " + to_string(myCreature.instanceId) + " " + to_string(enemyCreature.instanceId) + ";";

					// Test to see if we kill the target with guard
					if ((myCreature.attack >= enemyCreature.defense || myCreature.abilities[4] == 'L') && !(enemyCreature.abilities[5] == 'W'))
					{
						newCurrentValue += enemyCreature.defense;
						enemyCreaturesWithGuard[j].isDead = true;
					}
					// We didnt kill the target
					else
					{
						if (enemyCreature.abilities[5] == 'W' && myCreature.attack != 0)
						{
							enemyCreaturesWithGuard[j].abilities[5] = '-';
							hadWard = true;
						}
					}
					//cerr << "" << newCurrentAttack << " -- Value :" << newCurrentValue << endl;
					myCreatures[myCreatureCurrentPosition].hasAttacked = true;
					optimalAttackIfDead(myCreatureCurrentPosition + 1, newCurrentValue, newCurrentAttack);
					if (totalTime >= 95)
						return;
					enemyCreaturesWithGuard[j].isDead = false;
					if (hadWard)
					{
						enemyCreaturesWithGuard[j].abilities[5] = 'W';
					}
				}
			}
			if (!myCreatures[myCreatureCurrentPosition].hasAttacked)
			{
				for (int j = 0; j < enemyCreaturesWithoutGuard.size(); j++)
				{
					if (enemyCreaturesWithoutGuard[j].instanceId != -1)
					{
						if (!enemyCreaturesWithoutGuard[j].isDead)
						{
							bool hadWard = false;
							Card enemyCreature = enemyCreaturesWithoutGuard[j];

							int newCurrentValue = currentValue;
							string newCurrentAttack = currentAttack;

							if (myCreature.defense <= enemyCreature.attack)
							{
								newCurrentValue -= myCreature.attack;
								if (myCreature.abilities[3] == 'G')
								{
									newCurrentValue -= myCreature.defense - 10;
								}
							}
							newCurrentAttack += "ATTACK " + to_string(myCreature.instanceId) + " " + to_string(enemyCreature.instanceId) + ";";

							// Test to see if we kill the target with guard
							if ((myCreature.attack >= enemyCreature.defense || myCreature.abilities[4] == 'L') && !(enemyCreature.abilities[5] == 'W'))
							{
								newCurrentValue += enemyCreature.defense;
								enemyCreaturesWithoutGuard[j].isDead = true;
							}
							// We didnt kill the target
							else
							{
								if (enemyCreature.abilities[5] == 'W' && myCreature.attack != 0)
								{
									enemyCreaturesWithoutGuard[j].abilities[5] = '-';
									hadWard = true;
								}
							}
							//cerr << "" << newCurrentAttack << " -- Value :" << newCurrentValue << endl;
							myCreatures[myCreatureCurrentPosition].hasAttacked = true;
							optimalAttackIfDead(myCreatureCurrentPosition + 1, newCurrentValue, newCurrentAttack);
							if (totalTime >= 95)
								return;
							enemyCreaturesWithoutGuard[j].isDead = false;
							if (hadWard)
							{
								enemyCreaturesWithoutGuard[j].abilities[5] = 'W';
							}
						}
					}
					else
					{

						int newCurrentValue = currentValue;
						newCurrentValue += myCreature.attack;
						string newCurrentAttack = currentAttack;
						newCurrentAttack += "ATTACK " + to_string(myCreature.instanceId) + " -1;";
						enemy.playerHealth -= myCreature.attack;
						if (enemy.playerHealth <= 0)
							newCurrentValue += 10000;
						myCreatures[myCreatureCurrentPosition].hasAttacked = true;
						optimalAttackIfDead(myCreatureCurrentPosition + 1, newCurrentValue, newCurrentAttack);
						if (totalTime >= 95)
							return;
						enemy.playerHealth += myCreature.attack;
					}
				}
			}
			if (!myCreatures[myCreatureCurrentPosition].hasAttacked)
			{
				int newCurrentValue = currentValue;
				string newCurrentAttack = currentAttack;
				newCurrentAttack += "ATTACK " + to_string(myCreature.instanceId) + " -1;";
				//cerr << "" << newCurrentAttack << " -- Value :" << newCurrentValue << endl;
				enemy.playerHealth -= myCreature.attack;
				if (enemy.playerHealth <= 0)
					newCurrentValue += 10000;
				newCurrentValue += myCreature.attack + 4;
				optimalAttackIfDead(myCreatureCurrentPosition + 1, newCurrentValue, newCurrentAttack);
				if (totalTime >= 95)
					return;
				enemy.playerHealth += myCreature.attack;
			}
		}
		else
		{
			int newCurrentValue = currentValue;
			string newCurrentAttack = currentAttack;
			newCurrentAttack += "ATTACK " + to_string(myCreature.instanceId) + " -1;";
			newCurrentValue += myCreature.attack + 4;
			if (enemy.playerHealth <= 0)
				newCurrentValue += 10000;
			newCurrentValue += myCreature.attack + 4;
			optimalAttackIfDead(myCreatureCurrentPosition + 1, newCurrentValue, newCurrentAttack);
			if (totalTime >= 95)
				return;
			enemy.playerHealth += myCreature.attack;
		}
	}
}
*/

int getCardQuality(int cardNumber)
{
	bool cardFound = false;
	int cardRank = 0;
	int i = 0;
	while (!cardFound && i < 5)
	{
		switch (i)
		{
		case 0:
			while (!cardFound && find(reallyBadCards.begin(), reallyBadCards.end(),cardNumber) != reallyBadCards.end())
			{
				cardRank = 0;
				cardFound = true;
			}
			break;
		case 1:
			while (!cardFound && find(badCards.begin(), badCards.end(), cardNumber) != badCards.end())
			{
				cardRank = 1;
				cardFound = true;
			}
			break;
		case 2:
			while (!cardFound && find(normalCards.begin(), normalCards.end(), cardNumber) != normalCards.end())
			{
				cardRank = 2;
				cardFound = true;
			}
			break;
		case 3:
			while (!cardFound && find(goodCards.begin(), goodCards.end(), cardNumber) != goodCards.end())
			{
				cardRank = 3;
				cardFound = true;
			}
			break;
		case 4:
			while (!cardFound && find(reallyGoodCards.begin(), reallyGoodCards.end(), cardNumber) != reallyGoodCards.end())
			{
				cardRank = 4;
				cardFound = true;
			}
		}
		i++;
	}
	return cardRank;
}

void clearVectors()
{
    totalValueCardUse = 0;
    optimalCardsUse.clear();
    combination.clear();
    cards.clear();
    enemyCreaturesWithGuard.clear();
	myCreatures.clear();
    cardsInHand.clear();
    cardsToSummon.clear();
	enemyCreaturesWithoutGuard.clear();
	greenCardsReserve.clear();
	myCreaturesJustSummoned.clear();
}
