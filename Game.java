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
  static int numFights = 10;

  static void initWeights(Matrix m, Random r) {
    for(int i = 0; i < m.rows(); i++)
    {
      double[] chromosome = m.row(i);
      for(int j = 0; j < chromosome.length; j++)
        chromosome[j] = 0.03 * r.nextGaussian();
    }
  }

  static double[] evolveWeights() throws Exception {
    Random r = new Random(123);
		Matrix population = new Matrix(numSubjects, numChromosomes);
		for(int i = 0; i < numSubjects; i++)
		{
			double[] subject = population.row(i);
			for(int j = 0; j < numChromosomes - 4; j++)
				subject[j] = 0.03 * r.nextGaussian();

      // Set deviation
      subject[numChromosomes - 4] = 10.0;
      // Set winner survival rate
      subject[numChromosomes - 3] = 1.0;//Math.max(0.7, Math.min(r.nextDouble(), 0.9));
      // mutation chance
      subject[numChromosomes - 2] = Math.max(0.25, Math.min(r.nextDouble(), 0.4));
       // Every subject starts with 1 fight
		}

    // Evolve the population
		// todo: YOUR CODE WILL START HERE.
		//       Please write some code to evolve this population.
		//       (For tournament selection, you will need to call Controller.doBattleNoGui(agent1, agent2).)

    double[] chromosomes1 = new double[291];
    double[] chromosomes2 = new double[291];

		int generations = 200;
		for(int i = 0; i < generations; ++i) {

			// Output progress
			if(i % 100 == 0) {
				System.out.println(Integer.toString(i));
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
          if(r.nextDouble() < challenger1[numChromosomes - 3]) crossover(population, challenger2, r);
          else crossover(population, challenger1, r);
        } else if(result > 0){
          if(r.nextDouble() < challenger2[numChromosomes - 3]) crossover(population, challenger1, r);
          else crossover(population, challenger2, r);
        } else { // Kill a random one
          //if(r.nextBoolean()) crossover(population, challenger2, r);
          //else crossover(population, challenger1, r);
          int result1 = Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(chromosomes1));
          int result2 = Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(chromosomes2));
          if(result1 > result2) crossover(population, challenger1, r);
          else crossover(population, challenger2, r);
        }
      }

      // Mutate population
      for(int j = 0; j < population.rows(); ++j) {
        double[] subject = population.row(j);

        if(r.nextDouble() < subject[numChromosomes - 2]) { // 25% chance of mutating each row in population (make this meta-param)
          //double deviation = subject[numChromosomes - 4]; // Some random deviation to help modify a chromosome

          int k = r.nextInt(numChromosomes); // Pick a random element of the chromosome
          subject[k] += subject[numChromosomes - 4];
        }
      }


		}

		// Return an arbitrary member from the population
    System.out.println("Training finished");
		return population.row(r.nextInt(numSubjects));
  }

  static void crossover1(Matrix population, double[] victim, Random r) {
    double[] father = population.row(r.nextInt(numSubjects));
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

  static void crossover(Matrix population, double[] victim, Random r) {
    double[] father = population.row(r.nextInt(100));
    double[] mother = population.row(r.nextInt(100));
    for(int k = 0; k < numChromosomes; ++k) {
      victim[k] = (r.nextBoolean() ? father[k] : mother[k]);
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

		//Controller.getIter();
	}
}
