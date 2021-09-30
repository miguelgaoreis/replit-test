package tracks.levelGeneration.architect;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import core.game.GameDescription;
import core.game.GameDescription.SpriteData;
import core.generator.AbstractLevelGenerator;
import tools.ElapsedCpuTimer;
import tools.GameAnalyzer;
import tools.LevelMapping;

public class LevelGenerator extends AbstractLevelGenerator{

	/**
	 * Level mapping of the best chromosome
	 */
	private LevelMapping bestChromosomeLevelMapping;
	/**
	 * The best chromosome fitness across generations
	 */
	private ArrayList<Double> bestFitness;
	/**
	 * number of feasible chromosomes across generations
	 */
	private ArrayList<Integer> numOfFeasible;
	/**
	 * number of infeasible chromosomes across generations
	 */
	private ArrayList<Integer> numOfInFeasible;
	
	/**
	 * Initializing the level generator
	 * @param game			game description object
	 * @param elapsedTimer	amount of time for initialization
	 */
	public LevelGenerator(GameDescription game, ElapsedCpuTimer elapsedTimer){
		SharedData.random = new Random();
		SharedData.gameDescription = game;
		SharedData.gameAnalyzer = new GameAnalyzer(game);
		SharedData.analyzer = new Analyzer(game);

		if(SharedData.generatorType == 0)
		{
			SharedData.levelGen = new Constructive(game, null);
		}
		else if(SharedData.generatorType == 1)
		{
			SharedData.levelGen = new ModifiedRandomGen(game,null);
		}
		
		char floorChar = SharedData.analyzer.getFloor();
		if (floorChar != ' ') {
			SharedData.floor = SharedData.analyzer.levelMapping.get(floorChar).get(0);
		}
		
		SharedData.allSprites = new ArrayList<String>();
		ArrayList<SpriteData> sprites = SharedData.gameDescription.getAllSpriteData();
		for (int j=0; j<sprites.size(); j++) {
			if (!sprites.get(j).name.equals(SharedData.floor)) {
				SharedData.allSprites.add(sprites.get(j).name);
			}
		}


		bestChromosomeLevelMapping = null;
		bestFitness = null;
		numOfFeasible = null;
		numOfInFeasible = null;
	}
	
	/**
	 * Get the next population based on the current feasible infeasible population
	 * @param fPopulation	array of the current feasible chromosomes
	 * @param iPopulation	array of the current infeasible chromosomes
	 * @return				array of the new chromosomes at the new population
	 */
	private ArrayList<Chromosome> getNextPopulation(ArrayList<Chromosome> fPopulation, ArrayList<Chromosome> iPopulation){
		ArrayList<Chromosome> newPopulation = new ArrayList<Chromosome>();
		
		//collect some statistics about the current generation
		ArrayList<Double> fitnessArray = new ArrayList<Double>();
		for(int i=0;i<fPopulation.size();i++){
			fitnessArray.add(fPopulation.get(i).getFitness().get(0));
		}

		Collections.sort(fitnessArray);
		if(fitnessArray.size() > 0){
			bestFitness.add(fitnessArray.get(fitnessArray.size() - 1));
		}
		else{
			bestFitness.add((double) 0);
		}
		numOfFeasible.add(fPopulation.size());
		numOfInFeasible.add(iPopulation.size());
		

		
		while(newPopulation.size() < SharedData.POPULATION_SIZE){
			//choosing which population to work on with 50/50 probability 
			//of selecting either any of them
			ArrayList<Chromosome> population = fPopulation;
			if(fPopulation.size() <= 0){
				population = iPopulation;
			}
			if(SharedData.random.nextDouble() < 0.5){
				population = iPopulation;
				if(iPopulation.size() <= 0){
					population = fPopulation;
				}
			}

			//select the parents using roulettewheel selection
			Chromosome parent1 = rouletteWheelSelection(population);
			Chromosome parent2 = rouletteWheelSelection(population);
			Chromosome child1 = parent1.clone();
			Chromosome child2 = parent2.clone();
			//do cross over
			if(SharedData.random.nextDouble() < SharedData.CROSSOVER_PROB){
				ArrayList<Chromosome> children = parent1.crossOver(parent2);
				child1 = children.get(0);
				child2 = children.get(1);
				

				//do muation to the children
				if(SharedData.random.nextDouble() < SharedData.MUTATION_PROB){
					child1.mutate();
				}
				if(SharedData.random.nextDouble() < SharedData.MUTATION_PROB){
					child2.mutate();
				}
			}

			//mutate the copies of the parents
			else if(SharedData.random.nextDouble() < SharedData.MUTATION_PROB){
				child1.mutate();
			}
			else if(SharedData.random.nextDouble() < SharedData.MUTATION_PROB){
				child2.mutate();
			}
			

			//add the new children to the new population
			newPopulation.add(child1);
			newPopulation.add(child2);
		}
		
		if (SharedData.THREADING) {
			// +++ THREADING +++
			// calculate the fitness for all the chromosomes
			ExecutorService pool = Executors.newFixedThreadPool(4);
			int temp = 0;
			for(Chromosome c : newPopulation) {
				int numb = temp++;
				pool.execute(new Runnable() {
					@Override
					public void run() {
						c.calculateFitness(SharedData.EVALUATION_TIME, numb);
					}
				});
			}
			pool.shutdown();
			// wait for all threads to finish
			try {
				while(!pool.awaitTermination(1, TimeUnit.SECONDS)) {}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// +++ END THREADING +++
		}
		else {
			int temp = 0;
			for(Chromosome c : newPopulation) {
				int numb = temp++;
				c.calculateFitness(SharedData.EVALUATION_TIME, numb);
			}
		}
		

		//add the best chromosome(s) from old population to the new population
		Collections.sort(newPopulation);
		for(int i=SharedData.POPULATION_SIZE - SharedData.ELITISM_NUMBER;i<newPopulation.size();i++){
			newPopulation.remove(i);
		}

		if(fPopulation.isEmpty()){
			Collections.sort(iPopulation);
			for(int i=0;i<SharedData.ELITISM_NUMBER;i++){
				newPopulation.add(iPopulation.get(i));
			}
		}
		else{
			Collections.sort(fPopulation);
			for(int i=0;i<SharedData.ELITISM_NUMBER;i++){
				newPopulation.add(fPopulation.get(i));
			}
		}
		
		return newPopulation;
	}

	/**
	 * Roullete wheel selection for the infeasible population
	 * @param population	array of chromosomes surviving in this population
	 * @return				the picked chromosome based on its constraint fitness
	 */
	private Chromosome constraintRouletteWheelSelection(ArrayList<Chromosome> population){
		//calculate the probabilities of the chromosomes based on their fitness
		double[] probabilities = new double[population.size()];
		probabilities[0] = population.get(0).getConstrainFitness();
		for(int i=1; i<population.size(); i++){
			probabilities[i] = probabilities[i-1] + population.get(i).getConstrainFitness() + SharedData.EPSILON;
		}
		
		for(int i=0; i<probabilities.length; i++){
			probabilities[i] = probabilities[i] / probabilities[probabilities.length - 1];
		}
		

		//choose a chromosome based on its probability
		double prob = SharedData.random.nextDouble();
		for(int i=0; i<probabilities.length; i++){
			if(prob < probabilities[i]){
				return population.get(i);
			}
		}
		
		return population.get(0);
	}
	

	/**
	 * Get the fitness for any population
	 * @param population	array of chromosomes surviving in this population
	 * @return				the picked chromosome based on its fitness
	 */
	private Chromosome rouletteWheelSelection(ArrayList<Chromosome> population){
		//if the population is infeasible use the constraintRoulletWheel function
		if(population.get(0).getConstrainFitness() <= 0.99999){
			return constraintRouletteWheelSelection(population);
		}
		

		//calculate the probabilities for the current population
		double[] probabilities = new double[population.size()];
		probabilities[0] = population.get(0).getCombinedFitness();
		for(int i=1; i<population.size(); i++){
			probabilities[i] = probabilities[i-1] + population.get(i).getCombinedFitness() + SharedData.EPSILON;
		}
		
		for(int i=0; i<probabilities.length; i++){
			probabilities[i] = probabilities[i] / probabilities[probabilities.length - 1];
		}

		//choose random chromosome based on its fitness
		double prob = SharedData.random.nextDouble();
		for(int i=0; i<probabilities.length; i++){
			if(prob < probabilities[i]){
				return population.get(i);
			}
		}
		
		return population.get(0);
	}


	/**
	 * generates the random level width and height from hardcoded average values
	 * @return width and height in an array; 0=width and 1=height
	 */
	private int[] generateLevelSize()
	{
		// average size from example levels
		double avgWidth = 21.74;
		double avgHeight = 12.44;
		double blocks = SharedData.analyzer.getAllBlocks().size();
		if (blocks == 0) {
			--avgWidth;
			--avgHeight;
		}
		// apply resizing factor based on # different blocks
		avgWidth = avgWidth * (1 + (blocks-9)/100);
		avgHeight = avgHeight * (1 + (blocks-9)/100);
		// add randomness
		int[] result = new int[2];
		result[0] = (int)SharedData.noise(avgWidth, 0.5, SharedData.random.nextDouble());
		result[1] = (int)SharedData.noise(avgHeight, 0.5, SharedData.random.nextDouble());

		return result;
	}


	/**
	 * Generate a level using GA in a fixed amount of time and 
	 * return the level in form of a string
	 * @param game			the current game description object
	 * @param elapsedTimer	the amount of time allowed for generation
	 * @return				string for the generated level
	 */
	@Override
	public String generateLevel(GameDescription game, ElapsedCpuTimer elapsedTimer) {
		//initialize the statistics objects
		bestFitness = new ArrayList<Double>();
		numOfFeasible = new ArrayList<Integer>();
		numOfInFeasible = new ArrayList<Integer>();
		
		SharedData.gameDescription = game;
				
		System.out.println("Generation #1: ");
		
		//initialize first half of the population with random level size
		int[] levelSize = generateLevelSize();
		System.out.println("// Level Size " + levelSize[0] + "x" + levelSize[1]);
		ArrayList<Chromosome> chromosomes = initializeChromosomes(SharedData.POPULATION_SIZE/2, levelSize);

		// Try different level size if fitness is low
		if (avgConstrainFitness(chromosomes) < 0.85) {
			int[] levelSizeA = generateLevelSize();
			while (levelSizeA[0]*levelSizeA[1] > 0.9*levelSize[0]*levelSize[1]
					&& levelSizeA[0]*levelSizeA[1] < 1.1*levelSize[0]*levelSize[1]) {
				levelSizeA = generateLevelSize();
			}
			System.out.println("// Level Size " + levelSizeA[0] + "x" + levelSizeA[1]);
			ArrayList<Chromosome> chromosomesA = initializeChromosomes(SharedData.POPULATION_SIZE/2, levelSizeA);
			if (avgConstrainFitness(chromosomesA) > avgConstrainFitness(chromosomes)) {
				levelSize = levelSizeA;
				chromosomes = chromosomesA;
			}
			System.out.println("// Level Size " + levelSize[0] + "x" + levelSize[1]);
		}
		
		// initialize remaining half of chromosomes
		chromosomes.addAll(initializeChromosomes(SharedData.POPULATION_SIZE-SharedData.POPULATION_SIZE/2, levelSize));
		
		//Saves the chromosomes
		if(SharedData.saveMyChromosomes) {
			for (int i=0; i<chromosomes.size(); i++) {
				Chromosome chromosome = chromosomes.get(i);
				saveTempLevel(chromosome.getLevelString(chromosome.getLevelMapping()),"examples/gridphysics/temp_gen1lvl" + i + ".txt", chromosome.getLevelMapping().getCharMapping());	
			}
		}							
		
		// add chromosomes to feasible or infeasible population
		ArrayList<Chromosome> fChromosomes = new ArrayList<Chromosome>();
		ArrayList<Chromosome> iChromosomes = new ArrayList<Chromosome>();
		for(Chromosome c : chromosomes) {
			if(c.getConstrainFitness() <= 0.99999){
				iChromosomes.add(c);
			}
			else{
				fChromosomes.add(c);
			}
		}

		//some variables to make sure not getting out of time
		long worstTime = SharedData.EVALUATION_TIME * SharedData.POPULATION_SIZE;
		long avgTime = elapsedTimer.elapsedMillis();
		long totalTime = elapsedTimer.elapsedMillis();
		int numberOfIterations = 1;
		
		while(elapsedTimer.remainingTimeMillis() > 2 * avgTime &&
				elapsedTimer.remainingTimeMillis() > worstTime){
			System.out.println(totalTime/1000 + "s elapsed | "
					+ elapsedTimer.remainingTimeMillis()/1000 + "s remaining\n");
			System.out.println("Generation #" + (numberOfIterations + 1) + ": ");
			
			//get the new population and split it to a the feasible and infeasible populations
			chromosomes = getNextPopulation(fChromosomes, iChromosomes);
			fChromosomes.clear();
			iChromosomes.clear();
			int j=0;
			for(Chromosome c:chromosomes){
				if(c.getConstrainFitness() <= 0.99999){
					iChromosomes.add(c);
				}
				else{
					fChromosomes.add(c);
				}
				//saves the chromosomes
				if (SharedData.saveMyChromosomes) {
					saveTempLevel(c.getLevelString(c.getLevelMapping()),"examples/gridphysics/" + numberOfIterations+1 + "lvl" + j++ + ".txt", c.getLevelMapping().getCharMapping());	
				}
			}
			
			numberOfIterations += 1;
			totalTime = elapsedTimer.elapsedMillis();
			avgTime = totalTime / numberOfIterations;
		}
		

		//return the best infeasible chromosome
		if(fChromosomes.isEmpty()){
			System.out.println("fChromosomes is empty -> returning best iChromosome");
			for(int i=0;i<iChromosomes.size();i++){
				iChromosomes.get(i).calculateFitness(SharedData.EVALUATION_TIME, i);
			}

			Collections.sort(iChromosomes);
			bestChromosomeLevelMapping = iChromosomes.get(0).getLevelMapping();
			System.out.println("Best Fitness: " + iChromosomes.get(0).getConstrainFitness());
			return iChromosomes.get(0).getLevelString(bestChromosomeLevelMapping);
		}
		
		//return the best feasible chromosome otherwise and print some statistics
		for(int i=0;i<fChromosomes.size();i++){
			fChromosomes.get(i).calculateFitness(SharedData.EVALUATION_TIME, i);
		}
		Collections.sort(fChromosomes);
		bestChromosomeLevelMapping = fChromosomes.get(0).getLevelMapping();
		System.out.println("Best Chromosome Fitness: " + fChromosomes.get(0).getFitness());
		System.out.println(bestFitness);
		System.out.println(numOfFeasible);
		System.out.println(numOfInFeasible);
		return fChromosomes.get(0).getLevelString(bestChromosomeLevelMapping);
	}

	/**
	 * Calculate average constraint fitness of the supplied chromosomes
	 * @param chromosomes
	 */
	private double avgConstrainFitness(ArrayList<Chromosome> chromosomes) {
		double avg = 0;
		for (Chromosome c : chromosomes) {
			avg += c.getConstrainFitness();
		}
		return avg / chromosomes.size();
	}

	/**
	 * Initialize chromosomes and calculate their fitness
	 * @param noOfChromosomes
	 * @param levelSize
	 * @return
	 */
	private ArrayList<Chromosome> initializeChromosomes(int noOfChromosomes, int[] levelSize) {
		ArrayList<Chromosome> chromosomes = new ArrayList<Chromosome>();
		for(int i=0; i < noOfChromosomes; i++){
			Chromosome chromosome = new Chromosome(levelSize[0], levelSize[1]);
			if(SharedData.CONSTRUCTIVE_INITIALIZATION){
				chromosome.InitializeConstructive();
			}
			else{
				chromosome.InitializeRandom();
			}
			chromosomes.add(chromosome);
		}

		if (SharedData.THREADING) {
			// +++ THREADING +++
			ExecutorService pool = Executors.newFixedThreadPool(4);
			int temp = 0;
			for(Chromosome c : chromosomes) {
				int numb = temp++;
				pool.execute(new Runnable() {
					@Override
					public void run() {
						c.calculateFitness(SharedData.EVALUATION_TIME, numb);
					}
				});
			}
			pool.shutdown();
			// wait for all threads to finish
			try {
				while(!pool.awaitTermination(1, TimeUnit.SECONDS)) {}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// +++ END THREADING +++
		}
		else {
			int temp = 0;
			for(Chromosome c : chromosomes) {
				int numb = temp++;
				c.calculateFitness(SharedData.EVALUATION_TIME, numb);
			}
		}

		return chromosomes;
	}

	/**
	 * get the current used level mapping to create the level string
	 * @return	the level mapping used to create the level string
	 */
	@Override
	public HashMap<Character, ArrayList<String>> getLevelMapping(){
		return bestChromosomeLevelMapping.getCharMapping();
	}
	
	/**
	 * Saves a level string to a file
	 * 
	 * @param level
	 *            current level to save
	 * @param levelFile
	 *            saved file
	 */
	private static void saveTempLevel(String level, String levelFile,
			HashMap<Character, ArrayList<String>> charMapping) {
		try {
			if (levelFile != null) {
				BufferedWriter writer = new BufferedWriter(new FileWriter(levelFile));
				writer.write("LevelMapping");
				writer.newLine();
				for (Map.Entry<Character, ArrayList<String>> e : charMapping.entrySet()) {
					writer.write("    " + e.getKey() + " > ");
					for (String s : e.getValue()) {
						writer.write(s + " ");
					}
					writer.newLine();
				}
				writer.newLine();
				writer.write("LevelDescription");
				writer.newLine();
				writer.write(level);
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
