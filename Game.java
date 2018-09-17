import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;


class Game {

  // Deviation
	// Winner survival rate
	// mutation chance
	// fights per generation
  static int numChromosomes = 291 + 4;

  static void initWeights(Matrix m, Random r) {
    for(int i = 0; i < m.rows(); i++)
    {
      double[] chromosome = m.row(i);
      for(int j = 0; j < chromosome.length; j++)
        chromosome[j] = 0.03 * r.nextGaussian();
    }
  }

  static double[] evolveWeights() throws Exception {
    Random r = new Random(123456);
		Matrix population = new Matrix(100, numChromosomes);
		for(int i = 0; i < 100; i++)
		{
			double[] subject = population.row(i);
			for(int j = 0; j < numChromosomes - 4; j++)
				subject[j] = 0.03 * r.nextGaussian();

      // Set deviation
      subject[numChromosomes - 4] = 10.0;
      // Set winner survival rate
      subject[numChromosomes - 3] = 0.9;
      // mutation chance
      subject[numChromosomes - 2] = 0.25f;
      // fights per generation
      subject[numChromosomes - 1] = 10.0f; // Every subject starts with 1 fight
		}

    // Evolve the population
		// todo: YOUR CODE WILL START HERE.
		//       Please write some code to evolve this population.
		//       (For tournament selection, you will need to call Controller.doBattleNoGui(agent1, agent2).)

    double[] chromosomes1 = new double[291];
    double[] chromosomes2 = new double[291];

    double mutationChance = 0.25f;
    double deviation = 10.0f;

		int generations = 1000;
		for(int i = 0; i < generations; ++i) {

			// Output progress
			// if(i % 100 == 0) {
			// 	System.out.println(Integer.toString(i));
			// }


			// Mutate population
			for(int j = 0; j < population.rows(); ++j) {
        double[] subject = population.row(j);

				if(r.nextDouble() < mutationChance) { // 25% chance of mutating each row in population (make this meta-param)
					//double deviation = subject[numChromosomes - 4]; // Some random deviation to help modify a chromosome

					int k = r.nextInt(numChromosomes); // Pick a random element of the chromosome
					subject[k] += r.nextGaussian() * deviation;
				}
			}
      deviation = (deviation * 0.99);
      mutationChance = (mutationChance * 0.99);

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
			System.out.println(1.0f / result);

			// If the member wins, keep, otherwise, evolve
			if(result < 0) {
				//System.out.println("Challenger 1 wins, evolve Challenger 2");
				// Crossover
				double[] father = population.row(r.nextInt(100));
				double[] mother = population.row(r.nextInt(100));
				for(int k = 0; k < numChromosomes; ++k) {
					challenger2[k] = (r.nextBoolean() ? father[k] : mother[k]);
				}

			} else if(result > 0){
				//System.out.println("Challenger 2 wins, evolve Challenger 1");
				// Crossover
				double[] father = population.row(r.nextInt(100));
				double[] mother = population.row(r.nextInt(100));
				for(int k = 0; k < numChromosomes; ++k) {
					challenger1[k] = (r.nextBoolean() ? father[k] : mother[k]);
				}

				} else {
					//System.out.println("Tie!");
          if(r.nextBoolean()) {
            double[] father = population.row(r.nextInt(100));
            double[] mother = population.row(r.nextInt(100));
            for(int k = 0; k < numChromosomes; ++k) {
              challenger1[k] = (r.nextBoolean() ? father[k] : mother[k]);
            }
          } else {
            double[] father = population.row(r.nextInt(100));
            double[] mother = population.row(r.nextInt(100));
            for(int k = 0; k < numChromosomes; ++k) {
              challenger2[k] = (r.nextBoolean() ? father[k] : mother[k]);
            }
          }

				}


		}

		// Return an arbitrary member from the population
    System.out.println("Training finished");
		return population.row(0);
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
