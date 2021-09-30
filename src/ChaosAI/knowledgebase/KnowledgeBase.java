package ChaosAI.knowledgebase;

import ChaosAI.knowledgebase.locationbased.GameBlock;
import ChaosAI.knowledgebase.locationbased.GameField;
import ChaosAI.utils.FieldTypes;
import ChaosAI.utils.GameState;
import ChaosAI.utils.Position;
import core.game.Event;
import core.game.Observation;
import ontology.Types;
import tools.Vector2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;


/** TODO Learn:
 Game: Win probability
 Block: (per coords) Win / loose / nothing / block / score+ / score-
 von Coords: + oder - healtpoints
 Distance to enemy
 Max/Durchscnittliche Schrittweite Enemy
 Aufhören zu lernen wenn genug wissen?
*/


public class KnowledgeBase {

    // learned at construction time:
    private int startingHealthPoints;
    private boolean isDeterministic = true;
    // learned purely after construction time
    private boolean npcsExist = false;
    private boolean instantKillNpcsExist = false;
    private boolean friendlyNpcsExist = false;
    private boolean neutralNpcsExist = false;
    private boolean hostileNpcsExist = false;
    // game field
    private GameField gameField;
    // other stuff
    private boolean learnedSomethingNewSinceLastCall = true;

    private HashMap<Integer, ITypeContainer> typeContainer = new HashMap<>();
    public ArrayList<Integer> wallTypes = new ArrayList<>();

    public ArrayList<Position> currentPath;
    public ArrayList<Position> visitedTargets = new ArrayList<>();
    public PriorityQueue<Observation> priorityTargets = new PriorityQueue<>();

    public int targetIndex = 0;
    private int avatarType = -1;

    // -------------------------------------------------------------------------------------------------------
    // *** Learn values which can be learned by processing a single GameState ***
    // -------------------------------------------------------------------------------------------------------
    public KnowledgeBase(GameState gameState) {
        // Build the inital GameField
        this.gameField = new GameField(gameState);

        this.avatarType = gameState.getAvatarType();
        updateRealState(gameState);

		// npcs at any time on the gamefield
		checkIfNpcsExist(gameState);

		// Deterministic or stochastic
		deterministicDeepDetection(gameState);

		// this is an rough simplification. we assume stochastics when we encounter npcs
		if(gameState.getNPCPositions() != null)
			if(gameState.getNPCPositions().length > 0)
				isDeterministic = false;

        // Get the starting health points
        startingHealthPoints = gameState.getAvatarHealthPoints();
    }

	// -------------------------------------------------------------------------------------------------------
	// *** Given a state and the IMMEDIATELY(!) advanced state ***
	// -------------------------------------------------------------------------------------------------------
	public void learn(KBFood food) {
		// inital stuff
		learnedSomethingNewSinceLastCall = true;
		GameState soOld = food.oldState;
		GameState soNew = food.newState;

		if (soNew.isGameOver())
			return;

		checkForSingleItypes(soNew);

		// detection of npcs and their state
		checkIfNpcsExist(soNew);

		// detection of walls
		if (KnowledgeBaseConfig.DETECT_WALLS)
			detectWalls(food);

		// check if npcs are friendly / hostile / neutral etc
        checkStateOfNpcs(food);

		// make sure the states are only one tick apart
		if (KnowledgeBaseConfig.DEBUG)
			assert(soOld.getGameTick() == soNew.getGameTick() - 1);

		// Get last event (this is the event with the highest gameTick)
		if (soNew.getEventsHistory().size() == 0)
			return;

		Event event = soNew.getEventsHistory().last();
		if (event.gameStep == soNew.getGameTick()) {
			// this is a recent evet
			if (event.position == soNew.getAvatarPosition()) {
				// collision, learn something :D
			}
		} else {
			// System.out.println("nix gelernt :(");
		}
	}

    // -------------------------------------------------------------------------------------------------------
    // *** checkStateOfNpcs ***
    // -------------------------------------------------------------------------------------------------------
    private void checkStateOfNpcs(KBFood food) {
        GameState soOld = food.oldState;
        GameState soNew = food.newState;

		if(soNew.getNPCPositions() != null) {
			if(soNew.getNPCPositions().length > 0) {
				for (int i = 0; i < soNew.getNPCPositions().length; i++) {
					for (Observation npc : soNew.getNPCPositions()[i]) {
						if (npc.position == soNew.getAvatarPosition()) {
							if (soNew.isGameOver() && soNew.getGameWinner() == Types.WINNER.PLAYER_LOSES && !soOld.isGameOver()
									&& soOld.getAvatarHealthPoints() == soOld.getAvatarMaxHealthPoints()) {
								instantKillNpcsExist = true;
							}
							if (soNew.getAvatarHealthPoints() < soOld.getAvatarHealthPoints()) {
								hostileNpcsExist = true;
							} else if (soNew.getAvatarHealthPoints() == soOld.getAvatarHealthPoints()) {
								neutralNpcsExist = true;
							} else if (soNew.getAvatarHealthPoints() > soOld.getAvatarHealthPoints()) {
								friendlyNpcsExist = true;
							}
						}
					}
				}
			}
        }
    }

    public void checkForSingleItypes(GameState gs) {
        for(Map.Entry<Integer, ITypeContainer> itc: this.typeContainer.entrySet()) {
            ITypeContainer con = itc.getValue();

            if(con.getObservationCount() == 1 && con.getCategory() != Types.TYPE_AVATAR && con.getCategory() != Types.TYPE_FROMAVATAR) {
                Observation obs = con.getObservation();
                if(!this.priorityTargets.contains(obs) && !this.visitedTargets.contains(new Position(obs.position, this.gameField.getBlockSize()))) {
                    obs.update(obs.itype, obs.obsID, obs.position, gs.getAvatarPosition(), obs.category);
                    this.priorityTargets.add(con.getObservation());
                }
            }
        }
    }

    public Observation getNextPriorityTarget() {
        return priorityTargets.poll();
    }

    // -------------------------------------------------------------------------------------------------------
    // *** This has to be the real state of the game, NOT a simulated state! ***
    // -------------------------------------------------------------------------------------------------------
    public void updateRealState(GameState gameState) {
        //this.gameField.update(gameState);
        if(gameState.getAvatarType() != this.avatarType || gameState.getGameTick() % 500 == 0) {
            this.visitedTargets.clear();
			this.avatarType = gameState.getAvatarType();
            targetIndex = 0;
        }

        for(ITypeContainer itc: this.typeContainer.values()) {
            itc.clearContainer();
        }

        ArrayList<Observation>[][] observationField = gameState.getObservationGrid();
        for(int x = 0; x < gameField.getWidth(); x++) {
            for (int y = 0; y < gameField.getHeight(); y++) {
                gameField.clearObserved(x, y);
                for (Observation obs : observationField[x][y]) {
                    if(typeContainer.containsKey(obs.itype)) {
                        this.typeContainer.get(obs.itype).addObservation(obs);
                    } else {
                        ITypeContainer tContainer = new ITypeContainer(obs.itype, obs.category);
                        tContainer.addObservation(obs);
                        this.typeContainer.put(obs.itype, tContainer);
                    }

                    switch (obs.category) {
                        // BEGIN types which are defined in the framework
                        case Types.TYPE_AVATAR:
                            // Not interesting right now
                            gameField.addTypeObserved(FieldTypes.TYPE_AVATAR, x, y);
                            break;
                        case Types.TYPE_RESOURCE:
                            gameField.addTypeObserved(FieldTypes.TYPE_RESOURCE, x, y);
                            break;
                        case Types.TYPE_PORTAL:
                            gameField.addTypeObserved(FieldTypes.TYPE_PORTAL, x, y);
                            break;
                        case Types.TYPE_NPC:
                            gameField.addTypeObserved(FieldTypes.TYPE_NPC, x, y);
                            break;
                        case Types.TYPE_STATIC:
                            gameField.addTypeObserved(FieldTypes.TYPE_STATIC, x, y);
                            break;
                        case Types.TYPE_FROMAVATAR:
                            // Not interesting right now
                            //gameField[x][y].addTypeObserved(FieldTypes.TYPE_FROMAVATAR);
                            break;
                        case Types.TYPE_MOVABLE:
                            gameField.addTypeObserved(FieldTypes.TYPE_MOVABLE, x, y);
                            break;
                        // BEGIN types which are self defined
                        //case FieldTypes.AVATAR:
                        default:
                            gameField.addTypeObserved(FieldTypes.TYPE_UNKNOWN, x, y);
                            break;
                    }
                }
            }
        }
    }

	// -------------------------------------------------------------------------------------------------------
	// *** This assumes that pressing down really moves the avatar down! ***
	// -------------------------------------------------------------------------------------------------------
	private Position updatePositionWithAction(Position pos, Types.ACTIONS action) {
		int x = pos.x;
		int y = pos.y;
		switch (action) {
			case ACTION_UP:
				y--;
				return new Position(x, y);
			case ACTION_LEFT:
				x--;
				return new Position(x, y);
			case ACTION_DOWN:
				y++;
				return new Position(x, y);
			case ACTION_RIGHT:
				x++;
				return new Position(x, y);
			default:
				return new Position(x, y);
		}
	}

	// -------------------------------------------------------------------------------------------------------
	// *** checkIfNpcsExist checks if there was an npc in the game at any time ***
	// -------------------------------------------------------------------------------------------------------
	private void checkIfNpcsExist(GameState so) {
        if(!npcsExist)
		    npcsExist = so.getNPCPositions() != null && so.getNPCPositions().length != 0;
	}

    // -------------------------------------------------------------------------------------------------------
    // *** checkIfGameIsDetOrStoch ***
    // -------------------------------------------------------------------------------------------------------
//    private void checkIfGameIsDetOrStoch(GameState so) {
//        if(!isDeterministic)
//            return;
//        int hash = 0;
//        for(int i = 0; i < KnowledgeBaseConfig.COUNT_ADVANCE_DETSTOCH; i++) {
//            final GameState state = so.copy();
//            state.advance(Types.ACTIONS.ACTION_NIL);
//            int x = state.hashCode();
//            if(hash == 0)
//                hash = x;
//            else if(hash != x) {
//                isDeterministic = false;
//                break;
//            }
//        }
//    }

	// -------------------------------------------------------------------------------------------------------
	// *** deterministicDeepDetection ***
	// -------------------------------------------------------------------------------------------------------
	private void deterministicDeepDetection(GameState gameState) {
		if(!npcsExist) {
            ArrayList<Types.ACTIONS> actions = gameState.getAvailableActions(true);
            final GameState firstState = gameState.copy();
            final GameState secondState = gameState.copy();
            // final GameState thirdState = gameState.copy();
            for (int i = 0; i < KnowledgeBaseConfig.COUNT_ADVANCE_DEEP_DETSTOCH; i++)
                for (Types.ACTIONS action : actions) {
                    firstState.advance(action);
                    secondState.advance(action);
                    // thirdState.advance(action);
                    boolean allEquals = (firstState.hashCode() == secondState.hashCode());// && (secondState.hashCode() ==
                    // thirdState.hashCode());
                    if (!allEquals) {
                        isDeterministic = false;
                        return;
                    }
                }
        } else {
            isDeterministic = false;
        }
	}

	// -------------------------------------------------------------------------------------------------------
	// *** detectWalls ***
	// -------------------------------------------------------------------------------------------------------
	private void detectWalls(KBFood food) {
		// get states and return if last action was not a move action
		GameState newState = food.newState;
		GameState oldState = food.oldState;
		Types.ACTIONS lastAction = newState.getAvatarLastAction();
		if (newState.isGameOver() || lastAction == Types.ACTIONS.ACTION_NIL || lastAction == Types.ACTIONS.ACTION_USE)
			return;

		// get position/orientation and return if
		Vector2d newPos = newState.getAvatarPosition();
		Vector2d oldPos = oldState.getAvatarPosition();
		Vector2d newOri = newState.getAvatarOrientation();
		Vector2d oldOri = oldState.getAvatarOrientation();
		// calculate gamefield coords
		int blockSize = newState.getBlockSize();
		int x = (int)(newPos.x / blockSize);
		int y = (int)(newPos.y / blockSize);
		Position gameFieldPosition = new Position(x, y);
		// it was just a change of orientation, no movement
		if(!newOri.equals(oldOri))
			return;
        // stop checking if position is not within the gameField
        if(!gameField.positionIsWithinGameField(gameFieldPosition))
            return;
		// we could move!
        Position position = updatePositionWithAction(gameFieldPosition, lastAction);
		if(!gameField.positionIsWithinGameField(position))
			return;
        ArrayList<Observation>[][] field = newState.getObservationGrid();
        ArrayList<Observation> wallPosition = field[position.x][position.y];
        ArrayList<Integer> staticTypes = new ArrayList<>();

        for(Observation obs: wallPosition) {
            if(obs.category == Types.TYPE_STATIC) {
                ITypeContainer itc = this.typeContainer.get(obs.itype);
                if(itc != null && itc.getObservationCount() > 4)
                    staticTypes.add(obs.itype);
            }
        }

		if(!newPos.equals(oldPos)) {
			// used to be a wall -> remove!
			if(gameField.getGameBlockAt(gameFieldPosition).containsTypeCombined(FieldTypes.TYPE_WALL)) {
				gameField.getGameBlockAt(gameFieldPosition).removeTypeLearned(FieldTypes.TYPE_WALL);
                this.wallTypes.removeAll(staticTypes);
				return;
			// we could move and this is not a wall in gamefield
			} else {
				return;
			}
		}

		// TODO: not necessary when getActionsNotBlockedByWall() is used!
		if(!gameField.positionIsWithinGameField(position))
			return;

		boolean isStatic = false;
		boolean isPortal = false;
		for(Observation obs : wallPosition) {
			if(obs.category == Types.TYPE_STATIC)
				isStatic = true;
			if(obs.category == Types.TYPE_PORTAL)
				isPortal = true;
		}

        if(this.currentPath != null && position.equals(this.currentPath.get(0))) {
            this.visitedTargets.add(this.currentPath.get(0));
            this.currentPath = null;
        }

		if(isStatic && !isPortal) {
            gameField.getGameBlockAt(position).addTypeLearned(FieldTypes.TYPE_WALL);
            this.wallTypes.addAll(staticTypes);
        }
	}

	// -------------------------------------------------------------------------------------------------------
	// *** Public getters ***
	// -------------------------------------------------------------------------------------------------------
	// Check if there was at ANY TIME an NPC in this game
//	public boolean getNpcsExist() {
//		return npcsExist;
//	}
	public boolean getInstantKillNpcsExist() {
		return instantKillNpcsExist;
	}
	public boolean getFriendlyNpcsExist() {
		return friendlyNpcsExist;
	}
	public boolean getNeutralNpcsExist() {
		return neutralNpcsExist;
	}
	public boolean getHostileNpcsExist() {
		return hostileNpcsExist;
	}
	public boolean getIsDeterministic() {
		return isDeterministic;
	} 									// DONE, optimize?
	public double getGameFieldCountOfBlocks() {
		return gameField.getBlockCount();
	}						// DONE
	public double getGameFieldHeightInBlocks() {
		return gameField.getHeight();
	} 						// DONE
	public double getGameFieldWidthInBlocks() {
		return gameField.getWidth();
	} 							// DONE
	public GameBlock getGameBlockAt(Position position) {
		return gameField.getGameBlockAt(position);
	}	// DONE, stays?
	public int getStartingHealthPoints() {
		return startingHealthPoints;
	} 								// DONE
	public boolean positionIsWithinGameField(Position position) {
		return gameField.positionIsWithinGameField(position);
	} 									// DONE
	public boolean getLearnedSomethingNewSinceLastCall() {
		if (learnedSomethingNewSinceLastCall) {
			learnedSomethingNewSinceLastCall = false;
			return true;
		} else {
			return false;
		}
	} 											// DONE, test!
    public ArrayList<Types.ACTIONS> getActionsNotBlockedByWall(Position avatarPosition){
        return getActionsNotBlockedByWall(avatarPosition, gameField.getAvailableActions());
    }
    public ArrayList<Types.ACTIONS> getActionsNotBlockedByWall(Position avatarPosition, ArrayList<Types.ACTIONS> actionsToCheck)   // DONE, test!
    {
        // TODO does pressing "down" always mean we want to go down?
        ArrayList<Types.ACTIONS> availableActions = actionsToCheck;
        ArrayList<Types.ACTIONS> unwalledActions = new ArrayList<>();
        for(Types.ACTIONS action : availableActions) {
            Position newPosition = updatePositionWithAction(avatarPosition, action);
            // changed from avatarPosition to newPosition \/
            if(!gameField.positionIsWithinGameField(newPosition))
                continue;
            if(!gameField.getGameBlockAt(newPosition).getTypesLearned().contains(FieldTypes.TYPE_WALL))
                unwalledActions.add(action);
        }
        return unwalledActions;
    }

	public void trackEnemy(ArrayList<Observation> newEnemies) {
	/*	HashMap<Observation, Double> oldEnemies = new HashMap<Observation, Double>(); // TODO replace with list of enemies
		if (oldEnemies != null) {
			for (Entry<Observation, Double> entry : oldEnemies.entrySet()) {
				Observation oldEnemy = entry.getKey();
				for (Observation newEnemy : newEnemies) {
					if (oldEnemy.obsID == newEnemy.obsID && oldEnemy.position != null && newEnemy.position != null) {
						double xDistance = Math.abs(oldEnemy.position.x - newEnemy.position.x);
						double yDistance = Math.abs(oldEnemy.position.y - newEnemy.position.y);
						double distance = xDistance + yDistance;
						if (distance < (Double) entry.getValue()) {
							entry.setValue(distance);
						}
						break;
					}
				}
			}
		}*/
	}

	public ArrayList<Object> getDestroyableObjects() {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

	public ArrayList<Observation> getEnemyList() {
		ArrayList<Observation> enemies = new ArrayList<>(); 
	/*	for (Entry<Observation, Double> enemyEntry : new HashMap<Observation, Double>().entrySet()) { // TODO replace with list of enemies
			enemies.add(enemyEntry.getKey());
		}*/
		return enemies;
	}

	/**
	 * @param enemy
	 * @return max speed of enemy
	 */
	public double getSpeed(Observation enemy) {
	/*	for (Entry<Observation, Double> enemyEntry : new HashMap<Observation, Double>().entrySet()) { // TODO replace with list of enemies
			if (enemyEntry.getKey() != null && enemy.obsID == enemyEntry.getKey().obsID) {
				if (enemyEntry.getValue() != null) {
					return enemyEntry.getValue();
				}
			}
		}*/
		return 0;
	}

	public ArrayList<Vector2d> getGoalPositions() {
		ArrayList<Vector2d> positions = new ArrayList<>();
		// TODO
		return positions;
	}
}
