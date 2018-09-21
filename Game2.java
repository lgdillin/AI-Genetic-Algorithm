import java.util.ArrayList;
import java.util.Random;

class Game2
{

	static int numIndividuals = 30;

	static double[] evolveWeights() throws Exception
	{
		// Create a random initial population
		Random r = new Random(123456);
		Matrix population = new Matrix(numIndividuals, 291);
		for(int i = 0; i < numIndividuals; i++)
		{
			double[] chromosome = population.row(i);
			for(int j = 0; j < chromosome.length; j++)
				chromosome[j] = 0.03 * r.nextGaussian();
		}

		// Evolve the population
		// todo: YOUR CODE WILL START HERE.
		//       Please write some code to evolve this population.
		//       (For tournament selection, you will need to call Controller.doBattleNoGui(agent1, agent2).)

		int mostFit = 0;
		float bestFitnessScore = 0.0f;
		int generations = 250;
		for(int i = 0; i < generations; ++i) {

			// Output progress
			if(i % 100 == 0) {
				System.out.println(Integer.toString(i));
			}


			// Mutate population
			for(int j = 0; j < population.rows(); ++j) {
				if(r.nextDouble() < 0.4) { // 25% chance of mutating each row in population (make this meta-param)
					double deviation = 1.9; // Some random deviation to help modify a chromosome

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


			int result = 0;
			int fight = 0;
			int maxFights = 1000;
			while(result == 0) {
				result = Controller.doBattleNoGui(new NeuralAgent(challenger1), new NeuralAgent(challenger2));
				//System.out.println("fight #" + fight);

				// If the member wins, keep, otherwise, evolve
				if(result < 0) {
					double[] father = population.row(r.nextInt(numIndividuals));
					double[] mother = population.row(r.nextInt(numIndividuals));
					for(int k = 0; k < 291; ++k) {
						challenger2[k] = (r.nextBoolean() ? father[k] : mother[k]);
					}

				} else if(result > 0) {
					double[] father = population.row(r.nextInt(numIndividuals));
					double[] mother = population.row(r.nextInt(numIndividuals));
					for(int k = 0; k < 291; ++k)
						challenger1[k] = (r.nextBoolean() ? father[k] : mother[k]);
				} else {
					double deviation = 1.5; // Some random deviation to help modify a chromosome

					int k = r.nextInt(291); // Pick a random element of the chromosome
					if(r.nextBoolean()) {
						challenger1[k] += r.nextGaussian() * deviation;
					} else {
						challenger2[k] += r.nextGaussian() * deviation;
					}

					// if the fight # is too large, assume they are inept, and kill them both
					if(false) {
						int fatherIndex = r.nextInt(numIndividuals);
						int motherIndex = r.nextInt(numIndividuals);
						while(fatherIndex == randomChallenger1) { fatherIndex = r.nextInt(numIndividuals); }
						while(motherIndex == randomChallenger1) { motherIndex = r.nextInt(numIndividuals); }

						double[] father = population.row(fatherIndex);
						double[] mother = population.row(motherIndex);
						for(int j = 0; j < 291; ++j) {
							challenger1[j] = (r.nextBoolean() ? father[j] : mother[j]);
						}

						fatherIndex = r.nextInt(numIndividuals);
						motherIndex = r.nextInt(numIndividuals);
						while(fatherIndex == randomChallenger2) { fatherIndex = r.nextInt(numIndividuals); }
						while(motherIndex == randomChallenger2) { motherIndex = r.nextInt(numIndividuals); }

						father = population.row(fatherIndex);
						mother = population.row(motherIndex);
						for(int j = 0; j < 291; ++j) {
							challenger2[j] = (r.nextBoolean() ? father[j] : mother[j]);
						}
					}
				}

				++fight;
			}

			// create chart

			// float fitness = Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(population.row(0)));
			// if(fitness <= 0) fitness = 0;
			// else fitness = 1.0f / fitness;
			// bestFitnessScore = fitness;
			// for(int j = 1; j < population.rows(); ++j) {
			// 	fitness = Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(population.row(j)));
			//
			// 	if(fitness <= 0) fitness = 0;
			// 	else fitness = 1.0f / fitness;
			//
			// 	if(fitness > bestFitnessScore) {
			// 		bestFitnessScore = fitness;
			// 		//System.out.println("Most fit: " + bestFitnessScore);
			// 		mostFit = j;
			// 	}
			// }
			// System.out.println(bestFitnessScore);
			// System.out.println(bestFitnessScore);
			System.out.println(1.0f / result);
		}

		float fitness = Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(population.row(0)));
		if(fitness <= 0) fitness = 0;
		else fitness = 1.0f / fitness;
		bestFitnessScore = fitness;
		for(int j = 1; j < population.rows(); ++j) {
			fitness = Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(population.row(j)));

			if(fitness <= 0) fitness = 0;
			else fitness = 1.0f / fitness;


			if(fitness > bestFitnessScore) {
				bestFitnessScore = fitness;
				System.out.println("Most fit: " + bestFitnessScore);
				mostFit = j;
			}
		}

		System.out.println("Most fit: " + bestFitnessScore);
		// Return an arbitrary member from the population
		return population.row(r.nextInt(numIndividuals));
	}

	void crossover(Matrix population, int success, Random r) {

	}


	public static void main(String[] args) throws Exception
	{
		double[] w = evolveWeights();
		for(int i = 0; i < w.length; ++i) {
			System.out.print(w[i] + ", ");
		}
		//Controller.doBattle(new ReflexAgent(), new NeuralAgent(w));
		Controller.doBattle(new ReflexAgent(), new NeuralAgent(w));

		//Controller.getIter();
	}

}
