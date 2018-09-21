import java.util.Random;



class Game3 {
  static int numIndividuals = 30;
  static int generations = 3000;
  static int chromosomes = 291;
  static int metaParameters = 3;
  static int totalChromosomes = chromosomes + metaParameters;

  static double[] evolveWeights() throws Exception
  {
    // Create a random initial population
    Random r = new Random(123456);
    Matrix population = new Matrix(numIndividuals, chromosomes);
    for(int i = 0; i < numIndividuals; i++)
    {
      double[] chromosome = population.row(i);
      for(int j = 0; j < population.rows(); j++) {
        chromosome[j] = 0.03 * r.nextGaussian();
      }

      //chromosome[chromosomes] = 0.6; // Mutation Chance
      //chromosome[chromosomes + 1] = 1.7; // Mutation deviation
      //chromosome[chromosomes + 2] = 0.8; // Suvival chance on victory

      //
    }

    // Evolve the population
    // todo: YOUR CODE WILL START HERE.
    //       Please write some code to evolve this population.
    //       (For tournament selection, you will need to call Controller.doBattleNoGui(agent1, agent2).)
    for(int i = 0; i < generations; ++i) {

      // Mutation
      for(int j = 0; j < population.rows(); ++j) {
        if(r.nextDouble() < 0.25) {
          int k = r.nextInt(chromosomes);
          population.row(j)[k] += r.nextGaussian() * 10.0f;
        }
      }

      // Tournament
      int challengerIndex1 = r.nextInt(population.rows());
      double[] challenger1 = population.row(challengerIndex1);

      int challengerIndex2 = r.nextInt(population.rows());
      double[] challenger2 = population.row(challengerIndex2);

      int result = Controller.doBattleNoGui(new NeuralAgent(challenger1), new NeuralAgent(challenger2));
      if(result < 0) {
        crossover(population, challenger1, r);
      } else if(result > 0) {
        crossover(population, challenger2, r);
      } else {
        if(r.nextBoolean()) crossover(population, challenger2, r);
        else crossover(population, challenger1, r);
      }

    }

    //
    int maxFitness = 0;
    double fitness = Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(population.row(maxFitness)));
    if(fitness >= 0) fitness = 0;
    else fitness = Math.abs(1.0f / fitness);
    double maxFitnessScore = fitness;
    for(int j = 1; j < population.rows(); ++j) {
      fitness = Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(population.row(j)));

      if(fitness >= 0) continue;
      else fitness = Math.abs(1.0f / fitness);

      if(fitness > maxFitnessScore) {
        maxFitnessScore = fitness;
        maxFitness = j;
        System.out.println(maxFitness);
      }
    }

    return population.row(maxFitness);
  }

  static void crossover(Matrix population, double[] victim, Random r) {
    double[] father = population.row(r.nextInt(population.rows()));
    double[] mother = population.row(r.nextInt(population.rows()));

    int mode = r.nextInt(2);
    if(mode == 1) { // Randomly select a gene (Every point crossover)
      for(int i = 0; i < victim.length; ++i) {
        victim[i] = (r.nextBoolean() ? father[i] : mother[i]);
      }
    } else {
      int crossoverLevel = r.nextInt(victim.length);
      System.arraycopy(father, 0, victim, 0, crossoverLevel);
      System.arraycopy(mother, crossoverLevel + 1, victim, crossoverLevel + 1, victim.length - crossoverLevel - 1);
    }
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
