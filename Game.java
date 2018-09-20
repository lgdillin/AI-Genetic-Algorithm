import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;


class Game {

  // Deviation
	// Winner survival rate
	// mutation chance
	// fights per generation
  static int numChromosomes = 291 + 3;
  static int numSubjects = 100;
  static int numFights = 3;

  static void initWeights(Matrix m, Random r) {
    for(int i = 0; i < m.rows(); i++)
    {
      double[] chromosome = m.row(i);
      for(int j = 0; j < chromosome.length; j++)
        chromosome[j] = 0.03 * r.nextGaussian();
    }
  }

  static double[] evolveWeights() throws Exception {
    Random r = new Random();
		Matrix population = new Matrix(numSubjects, numChromosomes);
		for(int i = 0; i < numSubjects; i++)
		{
			double[] subject = population.row(i);
			for(int j = 0; j < numChromosomes - 3; j++)
				subject[j] = 0.03 * r.nextGaussian();

      // Set deviation
      subject[numChromosomes - 3] = 1.3;
      // Set winner survival rate
      subject[numChromosomes - 2] = 0.99;//Math.max(0.7, Math.min(r.nextDouble(), 0.9));
      // mutation chance
      subject[numChromosomes - 1] = 0.8;//Math.max(0.4, Math.min(r.nextDouble(), 0.8));
       // Every subject starts with 1 fight
		}

    double[] subject1 = population.row(0);
    for(int i = 0; i < numChromosomes; ++i) {
      System.out.print(subject1[i]);
    }System.out.println();

    double[] chromosomes1 = new double[291];
    double[] chromosomes2 = new double[291];
    double[] fitnessTest = new double[291];

    int maxFitness = 0; // The most fit speciment after all training;
		int generations = 500;
		for(int i = 0; i < generations; ++i) {

			// Output progress
			if(i % 100 == 0) {
				//System.out.println(Integer.toString(i));
			}

      // Do 10 fights per generation
      for(int j = 0; j < numFights; ++j) {
        // Do a tournament
        // Controller.doBattleNoGui(agent1, agent2);
        int randomChallenger1 = r.nextInt(population.rows());
        int randomChallenger2 = r.nextInt(population.rows());

        double[] challenger1 = population.row(randomChallenger1);
        double[] challenger2 = population.row(randomChallenger2);

        System.arraycopy(challenger1, 0, chromosomes1, 0, 291);
        System.arraycopy(challenger2, 0, chromosomes2, 0, 291);
        //int result = Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(challenger));

        //for(number of fights)
        int result = Controller.doBattleNoGui(new NeuralAgent(chromosomes1), new NeuralAgent(chromosomes2));
           //kill loser
        //System.out.println(1.0f / result);

        // If the member wins, keep, otherwise, evolve
        if(result < 0) {
          if(r.nextDouble() < challenger1[numChromosomes - 2]) crossover(population, challenger2, maxFitness, r);
          else crossover(population, challenger1, maxFitness, r);
        } else if(result > 0){
          if(r.nextDouble() < challenger2[numChromosomes - 2]) crossover(population, challenger1, maxFitness, r);
          else crossover(population, challenger2, maxFitness, r);
        } else { // Kill a random one
          //if(r.nextBoolean()) crossover(population, challenger2, r);
          //else crossover(population, challenger1, r);
          float result1 = 1.0f / Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(chromosomes1));
          float result2 = 1.0f / Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(chromosomes2));
          if(result1 <= 0) crossover(population, challenger1, maxFitness, r);
          if(result2 <= 0) crossover(population, challenger2, maxFitness, r);
          if(result1 > result2) crossover(population, challenger2, maxFitness, r);
          else crossover(population, challenger1, maxFitness, r);
        }
      }

      // Mutate population
      for(int j = 0; j < population.rows(); ++j) {
        double[] subject = population.row(j);

        if(r.nextDouble() < subject[numChromosomes - 1]) { // 25% chance of mutating each row in population (make this meta-param)
          //double deviation = subject[numChromosomes - 4]; // Some random deviation to help modify a chromosome

          int k = r.nextInt(numChromosomes); // Pick a random element of the chromosome
          subject[k] += r.nextGaussian() * subject[numChromosomes -3];
        }
      }

      // Pick the most fit
      System.arraycopy(population.row(maxFitness), 0, fitnessTest, 0, 291);
      double fitness = Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(fitnessTest));
      if(fitness <= 0) fitness = 0;
      else fitness = 1.0f / fitness;
      double maxFitnessScore = fitness;
      for(int j = 1; j < population.rows(); ++j) {
        System.arraycopy(population.row(j), 0, fitnessTest, 0, 291);
        fitness = Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(fitnessTest));
        if(fitness <= 0) continue;
        else fitness = 1.0f / fitness;

        // System.out.println("Current: " + fitness);

        if(fitness > maxFitnessScore) {
          maxFitnessScore = fitness;
          maxFitness = j;
          System.out.println(maxFitness);
          System.out.println("new: " + (maxFitnessScore));
        }
      }
      System.out.println(maxFitnessScore);
		}

		// Return an arbitrary member from the population
    System.out.println("Training finished");
		return population.row(maxFitness);
  }

  static void crossover(Matrix population, double[] victim, int mostFit, Random r) {
    double prob = r.nextDouble();
    if(prob < 0.25) {
      crossoverEPC(population, victim, mostFit, r);
    } else if(prob < 0.5) {
      crossoverSPC(population, victim, mostFit, r);
    } else if (prob < 0.75) {
      interpolation(population, victim, mostFit, r);
    } else {
      crossoverAPC(population, victim, mostFit, r);
    }
  }

  static void crossoverSPC(Matrix population, double[] victim, int mostFit,Random r) {
    double[] father = population.row(mostFit);
    double[] mother = population.row(r.nextInt(numSubjects));
    int crossoverLevel = r.nextInt(numChromosomes);
    if(r.nextBoolean()) {
      System.arraycopy(father, 0, victim, 0, crossoverLevel);
      System.arraycopy(mother, crossoverLevel + 1, victim, crossoverLevel + 1, numChromosomes - crossoverLevel - 1);
    } else {
      System.arraycopy(mother, 0, victim, 0, crossoverLevel);
      System.arraycopy(father, crossoverLevel + 1, victim, crossoverLevel + 1, numChromosomes - crossoverLevel - 1);
    }
  }

  static void crossoverEPC(Matrix population, double[] victim, int mostFit, Random r) {
    double[] father = population.row(mostFit);
    double[] mother = population.row(r.nextInt(numSubjects));
    for(int k = 0; k < numChromosomes; ++k) {
      victim[k] = (r.nextBoolean() ? father[k] : mother[k]);
    }
  }

  static void crossoverAPC(Matrix population, double[] victim, int mostFit, Random r) {
    double[] father = population.row(mostFit);
    double[] mother = population.row(r.nextInt(numSubjects));
    for(int k = 0; k < numChromosomes; ++k) {
      if(k % 2 == 0)
        victim[k] = (father[k]);
      else
        victim[k] = mother[k];
    }
  }

  static void interpolation(Matrix population, double[] victim, int mostFit, Random r) {
    double[] father = population.row(mostFit);
    double[] mother = population.row(r.nextInt(numSubjects));
    double d = r.nextDouble();

    for(int i = 0; i < victim.length; ++i) {
      victim[i] = d * mother[i] + (1.0 - d) * father[i];
    }
  }

  static void mutate() {

  }

  public static void main(String[] args) throws Exception
	{
		double[] w = evolveWeights();
    double[] trimmed = new double[291];
    System.arraycopy(w, 0, trimmed, 0, 291);
		//Controller.doBattle(new ReflexAgent(), new NeuralAgent(w));
		Controller.doBattle(new ReflexAgent(), new NeuralAgent(trimmed));
    for(int i = 0; i < w.length; ++i) {
      System.out.print(w[i] + ", ");
    }

		//Controller.getIter();
	}
}
