import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;

class Game
{

	static void initWeights(Matrix m, Random r) {
		for(int i = 0; i < m.rows(); i++)
		{
			double[] chromosome = m.row(i);
			for(int j = 0; j < chromosome.length; j++)
				chromosome[j] = 0.03 * r.nextGaussian();
		}
	}

	static double[] evolveWeights() throws Exception
	{
		// Create a random initial population
		Random r = new Random(123456);
		Matrix population1 = new Matrix(100, 291);
		Matrix population2 = population1;
		initWeights(population1, r);
		initWeights(population2, r);

		// Evolve the population
		//       (For tournament selection, you will need to call Controller.doBattleNoGui(agent1, agent2).)

		int mostFit = 0;
		int generations = 250;
		for(int i = 0; i < generations; ++i) {
			// Output progress
			if(i % 10 == 0) System.out.println(Integer.toString(i));

			// Select random indices
			int randomChallenger1 = r.nextInt(population1.rows());
			int randomChallenger2 = r.nextInt(population2.rows());

			// Select challenger for battle
			double[] challenger1 = population1.row(randomChallenger1);
			double[] challenger2 = population2.row(randomChallenger2);
			float result = 1.0f / Controller.doBattleNoGui(new NeuralAgent(challenger1), new NeuralAgent(challenger2));

			if(result < 0) {
				// Left team wins
				crossover(population2, challenger1, r);
				--mostFit;
			} else if(result > 0) {
				// Right team wins
				crossover(population1, challenger2, r);
				++mostFit;
			} else {
				// Both teams tie
				mutate(population1, population2, r);
			}
		}

		// Return an arbitrary member from the population
		if(mostFit < 0)
			return population1.row(r.nextInt(population1.rows()));
		else if(mostFit > 0)
			return population2.row(r.nextInt(population1.rows()));
		else
			return (r.nextBoolean() ? population1.row(r.nextInt(population1.rows())) : population2.row(r.nextInt(population1.rows())));
	}

	static void mutate(Matrix population1, Matrix population2, Random r) {
		for(int j = 0; j < population1.rows(); ++j) {
			if(r.nextDouble() < 0.25) { // 25% chance of mutating each row in population (make this meta-param)
				double deviation = 10.0; // Some random deviation to help modify a chromosome

				int k = r.nextInt(291); // Pick a random element of the chromosome
				population1.row(j)[k] += r.nextGaussian() * deviation;
			}
		}

		for(int j = 0; j < population2.rows(); ++j) {
			if(r.nextDouble() < 0.80) { // 25% chance of mutating each row in population (make this meta-param)
				double deviation = 10.0; // Some random deviation to help modify a chromosome

				int k = r.nextInt(291); // Pick a random element of the chromosome
				population2.row(j)[k] += r.nextGaussian() * deviation;
			}
		}
	}

	static void crossover(Matrix population, double[] winner, Random r) throws Exception {
		// We need to piecewise combat, where each agent from the losing team has to
		// figt the selected agent from the winning team. The top two best performers
		// on the losing team get to mate together
		// The details of their gene pass-on have yet to be figured out
		// Perhaps we perform mutation during cross over as well
		// We need to produce a powerful metric for measuring success

		// find the most fit of the losing team
		float[] scores = new float[population.rows()];
		for(int i = 0; i < population.rows(); ++i) {
			double[] nextChallenger = population.row(i);
			float result = 1.0f / Controller.doBattleNoGui(new NeuralAgent(winner), new NeuralAgent(nextChallenger));
			scores[i] = result;
		}

		double[] father = population.row(0);
		double[] mother = population.row(0);
		float max = scores[0];
		float max2 = scores[1];
		for(int i = 0; i < scores.length; ++i) {
			if(scores[i] > max) {
				max = scores[i];
				father = population.row(i);
			}

			if(scores[i] > max2 && max2 < max) {
				max2 = scores[i];
				mother = population.row(i);
			}
		}

		double[] child = population.row(r.nextInt(population.rows()));
		for(int k = 0; k < 291; ++k) {
			child[k] = (r.nextBoolean() ? father[k] : mother[k]);
		}

		if(r.nextDouble() < 0.25) { // 25% chance of mutating each row in population (make this meta-param)
			double deviation = 10.0; // Some random deviation to help modify a chromosome

			int k = r.nextInt(291); // Pick a random element of the chromosome
			child[k] += r.nextGaussian() * deviation;
		}

	}


	public static void main(String[] args) throws Exception
	{
		double[] w = evolveWeights();
		Controller.doBattle(new ReflexAgent(), new NeuralAgent(w));
		//Controller.doBattle(new NeuralAgent(w), new NeuralAgent(w));

		//Controller.getIter();
	}

}
