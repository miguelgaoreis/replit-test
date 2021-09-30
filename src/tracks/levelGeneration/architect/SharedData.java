package tracks.levelGeneration.architect;

import java.util.ArrayList;
import java.util.Random;

import core.game.GameDescription;
import core.generator.AbstractLevelGenerator;

public class SharedData {
	/**
	 * The size of the Genetic Algorithm Population
	 */
	public static final int POPULATION_SIZE = 50;
	/**
	 * The amount of times used to check the naive and do nothing algorithm
	 */
	public static final int REPETITION_AMOUNT = 25;
	/**
	 * the amount of time to evaluate a single level
	 */
	public static final long EVALUATION_TIME = 40000;
	/**
	 * The amount of time given for each time step
	 */
	public static final long EVALUATION_STEP_TIME = 40;
	/**
	 * Crossover probability
	 */
	public static final double CROSSOVER_PROB = 0.7;
	/**
	 * Mutation probability
	 */
	public static final double MUTATION_PROB = 0.1;
	/**
	 * number of the best chromosomes that are transfered from one generation to
	 * another
	 */
	public static final int ELITISM_NUMBER = 1;
	/**
	 * very small value
	 */
	public static final double EPSILON = 1e-6;
	/**
	 * the amount of mutations done on a chromosome to start as random
	 */
	public static final int RANDOM_INIT_AMOUNT = 50;
	/**
	 * the probability of inserting a new sprite
	 */
	public static final double INSERTION_PROB = 0.3;
	/**
	 * the probability of deleting an exisiting sprite
	 */
	public static final double DELETION_PROB = 0.3;
	/**
	 * the amount of times the mutation has to be done on a single chromosome
	 */
	public static final int MUTATION_AMOUNT = 1;
	/**
	 * used for calculating the minimum required score for the generated level
	 */
	public static final double MAX_SCORE_PERCENTAGE = 0.1;
	/**
	 * a fitness value given if the player ends in draw (not winning neither losing)
	 */
	public static final double DRAW_FITNESS = 0;
	/**
	 * minimum level size
	 */
	public static final double MIN_SIZE = 4;
	/**
	 * maximum level size
	 */
	public static final double MAX_SIZE = 18;
	/**
	 * minimum acceptable solution
	 */
	public static final double MIN_SOLUTION_LENGTH = 200;
	/**
	 * minimum acceptable do nothing steps before dying
	 */
	public static final double MIN_DOTHING_STEPS = 40;
	/**
	 * minimum acceptable cover percentage of sprites
	 */
	public static final double MIN_COVER_PERCENTAGE = 0.05;
	/**
	 * maximum acceptable cover percentage of sprites
	 */
	public static final double MAX_COVER_PERCENTAGE = 0.3;
	/**
	 * minimum amount of unique rules that should be applied
	 */
	public static final double MIN_UNIQUE_RULE_NUMBER = 3;
	/**
	 * starting the GA with seeds from the constructive algorithm
	 */
	public static final boolean CONSTRUCTIVE_INITIALIZATION = true;

	/**
	 * The name of a the best agent with some human error
	 */
	public static final String AGENT_NAME = "tracks.levelGeneration.architect.randomOLETS.Agent";
	/**
	 * The name of a naive agent
	 */
	public static final String NAIVE_AGENT_NAME = "tracks.singlePlayer.simple.sampleonesteplookahead.Agent";
	/**
	 * The name of the do nothing agent
	 */
	public static final String DO_NOTHING_AGENT_NAME = "tracks.singlePlayer.simple.doNothing.Agent";
	/**
	 * Turn multithreading on or off (must be disabled for competition)
	 */
	public static final boolean THREADING = false;
	/**
	 * The game description object
	 */
	public static GameDescription gameDescription;
	/**
	 * (old) game analyzer object to help in constructing the level Should be
	 * replaced (work-in-progress)
	 */
	public static tools.GameAnalyzer gameAnalyzer;
	/**
	 * (new) game analyzer object set to replace the above GameAnalyzer in the
	 * future
	 */
	public static Analyzer analyzer;
	/**
	 * random object to help in choosing random stuff
	 */
	public static Random random;
	/**
	 * constructive level generator to help in speeding up the level generation
	 * process
	 */
	public static AbstractLevelGenerator levelGen;
	/**
	 * option to change LevelGenerator Type 0 = constructive 1 = random
	 */
	public static int generatorType = 0;
	/**
	 * the floor sprite used in the current game
	 */
	public static String floor;
	/**
	 * all available sprites, excluding floor
	 */
	public static ArrayList<String> allSprites;
	/**
	 * boolean for saving chromosomes or not
	 */
	public static boolean saveMyChromosomes = false;
	/**
	 * to write every chromosome to a file
	 */
	public static String tempLevel;
	
	/**
	 * add some randomness to a number
	 */
	public static double noise(double input, double epsilon, double random) {
		return input * (1.0 + epsilon * (random - 0.5));
	}
}
