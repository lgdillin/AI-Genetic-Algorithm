import java.util.ArrayList;
import java.util.Random;

class Game2
{

	static double[] evolveWeights() throws Exception
	{
		// Create a random initial population
		Random r = new Random(123456);
		Matrix population = new Matrix(100, 291);
		for(int i = 0; i < 100; i++)
		{
			double[] chromosome = population.row(i);
			for(int j = 0; j < chromosome.length; j++)
				chromosome[j] = 0.03 * r.nextGaussian();
		}

		// Evolve the population
		// todo: YOUR CODE WILL START HERE.
		//       Please write some code to evolve this population.
		//       (For tournament selection, you will need to call Controller.doBattleNoGui(agent1, agent2).)

		int generations = 250;
		for(int i = 0; i < generations; ++i) {

			// Output progress
			if(i % 100 == 0) {
				System.out.println(Integer.toString(i));
			}


			// Mutate population
			for(int j = 0; j < population.rows(); ++j) {
				if(r.nextDouble() < 0.25) { // 25% chance of mutating each row in population (make this meta-param)
					double deviation = 10.0; // Some random deviation to help modify a chromosome

					int k = r.nextInt(291); // Pick a random element of the chromosome
					population.row(j)[k] += r.nextGaussian() * deviation;
				}
			}

			// Do a tournament
			// Controller.doBattleNoGui(agent1, agent2);
			int randomChallenger1 = r.nextInt(population.rows());
			int randomChallenger2 = r.nextInt(population.rows());

			double[] challenger1 = population.row(randomChallenger1);
			double[] challenger2 = population.row(randomChallenger2);
			//int result = Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(challenger));
			int result = Controller.doBattleNoGui(new NeuralAgent(challenger1), new NeuralAgent(challenger2));
			System.out.println(result);

			// If the member wins, keep, otherwise, evolve
			if(result < 0) {
				//System.out.println("Challenger 1 wins, evolve Challenger 2");
				// Crossover
				double[] father = population.row(r.nextInt(100));
				double[] mother = population.row(r.nextInt(100));
				for(int k = 0; k < 291; ++k) {
					challenger2[k] = (r.nextBoolean() ? father[k] : mother[k]);
				}

			} else if(result > 0){
				//System.out.println("Challenger 2 wins, evolve Challenger 1");
				// Crossover
				double[] father = population.row(r.nextInt(100));
				double[] mother = population.row(r.nextInt(100));
				for(int k = 0; k < 291; ++k) {
					challenger1[k] = (r.nextBoolean() ? father[k] : mother[k]);
				}

				} else {
					//System.out.println("Tie!");
					double[] father = population.row(r.nextInt(100));
					double[] mother = population.row(r.nextInt(100));
					for(int k = 0; k < 291; ++k) {
						challenger1[k] = (r.nextBoolean() ? father[k] : mother[k]);
					}

					father = population.row(r.nextInt(100));
					mother = population.row(r.nextInt(100));
					for(int k = 0; k < 291; ++k) {
						challenger2[k] = (r.nextBoolean() ? father[k] : mother[k]);
					}

				}


		}

		// Return an arbitrary member from the population
		return population.row(0);
	}

	void crossover(Matrix population, int success, Random r) {

	}


	public static void main(String[] args) throws Exception
	{
		double[] w = evolveWeights();
		//Controller.doBattle(new ReflexAgent(), new NeuralAgent(w));
		Controller.doBattle(new ReflexAgent(), new NeuralAgent(w));

		//Controller.getIter();
	}

}
