import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;


class Game {

  // Deviation
	// Winner survival rate
	// mutation chance
	// fights per generation
  static int numChromosomes = 291 + 3;
  static int numSubjects = 50;
  static int numFights = 5;

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
			for(int j = 0; j < numChromosomes - 3; j++)
				subject[j] = 0.03 * r.nextGaussian();

      // Set deviation
      subject[numChromosomes - 3] = 1.3;
      // Set winner survival rate
      subject[numChromosomes - 2] = 0.8;//Math.max(0.7, Math.min(r.nextDouble(), 0.9));
      // mutation chance
      subject[numChromosomes - 1] = 0.75;//Math.max(0.4, Math.min(r.nextDouble(), 0.8));
       // Every subject starts with 1 fight
		}


    double[] chromosomes1 = new double[291];
    double[] chromosomes2 = new double[291];
    double[] fitnessTest = new double[291];

    int maxFitness = 0; // The most fit speciment after all training;
		int generations = 600;
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
        //System.out.println(result);

        // If the member wins, keep, otherwise, evolve
        if(result > 0) {
          if(r.nextDouble() < challenger1[numChromosomes - 2]) crossover(population, challenger2, maxFitness, r);
          else crossover(population, challenger1, maxFitness, r);
        } else if(result < 0){
          if(r.nextDouble() < challenger2[numChromosomes - 2]) crossover(population, challenger1, maxFitness, r);
          else crossover(population, challenger2, maxFitness, r);
        } else { // Kill a random one
          //if(r.nextBoolean()) crossover(population, challenger2, r);
          //else crossover(population, challenger1, r);

          crossover(population, challenger1, maxFitness, r);
          crossover(population, challenger2, maxFitness, r);

          // float result1 = 1.0f / Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(chromosomes1));
          // float result2 = 1.0f / Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(chromosomes2));
          // if(result1 <= 0) crossover(population, challenger1, maxFitness, r);
          // if(result2 <= 0) crossover(population, challenger2, maxFitness, r);
          // if(result1 > result2) crossover(population, challenger2, maxFitness, r);
          // else crossover(population, challenger1, maxFitness, r);

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
      if(fitness >= 0) fitness = 0;
      else fitness = Math.abs(1.0f / fitness);
      double maxFitnessScore = fitness;
      for(int j = 1; j < population.rows(); ++j) {
        System.arraycopy(population.row(j), 0, fitnessTest, 0, 291);
        fitness = Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(fitnessTest));
        //System.out.println(fitness);

        if(fitness >= 0) continue;
        else fitness = Math.abs(1.0f / fitness);

        if(fitness > maxFitnessScore) {
          maxFitnessScore = fitness;
          maxFitness = j;
        }
      }
      System.out.println(maxFitnessScore);

		}

    System.arraycopy(population.row(maxFitness), 0, fitnessTest, 0, 291);
    double fitness = Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(fitnessTest));
    if(fitness >= 0) fitness = 0;
    else fitness = Math.abs(1.0f / fitness);
    double maxFitnessScore = fitness;
    for(int j = 1; j < population.rows(); ++j) {
      System.arraycopy(population.row(j), 0, fitnessTest, 0, 291);
      fitness = Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(fitnessTest));
      //System.out.println(fitness);

      if(fitness >= 0) continue;
      else fitness = Math.abs(1.0f / fitness);

      if(fitness > maxFitnessScore) {
        maxFitnessScore = fitness;
        maxFitness = j;
      }
    }
    System.out.println(maxFitnessScore);

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
		//double[] w = evolveWeights();
		//Controller.doBattle(new ReflexAgent(), new NeuralAgent(w));
    double[] goodweights = {-0.5017843186565073, 0.5948151538982626, -0.1159978637041762, 0.1298827348434493, 0.1686686773419458, 0.07788105964198568, -0.4116013887711014, -0.4908854914969115, 0.014129654326048863, 0.4100369046268916, 0.25714134030426566, 0.5974523780029594, -0.010008073297119557, 0.23748911202686118, 0.0479880037520594, 0.4800111416424671, 0.37643767596449285, -0.4177918515765274, 0.13530266588178125, 0.08607666262148371, 0.5112740424831383, 0.03869746312403176, 0.044646490657499656, 0.15133156244770668, -0.5507381630186785, -0.06294529728119339, -0.28260607907721047, 0.1009481783068327, 0.03982918071221464, -0.5991695337315788, -0.041833827959516234, 0.09854657810637096, 1.1936077063493093, 0.08989203917876308, 0.009192211101993336, 0.1734796461622673, -0.18259886888068178, -0.15659961213420232, 0.0844553089186423, 0.35129317530370807, -0.18910895515615872, -1.4486439971150715, 0.1761295994931269, 0.11859633628855848, 0.241319291071689, -0.9579496311758883, -0.012899849736649194, -0.27814712601170644, -0.2230822463556406, 0.12694004358013844, -0.07461631628247299, 0.27353291648385053, 0.2345582188634577, -0.937001642729448, -0.1791941759905602, 0.22712384333738664, -0.027804165246478184, 0.06577234535739247, -0.061271201246680354, 0.2255262946344161, -0.09799224862068107, 0.05274617484267839, -0.09664634958967731, -0.18478819411050174, -0.04102855503642276, 0.09151186899063388, 0.006697925473559388, 0.9831459803798624, -0.039198908679135294, 0.00954150770253318, 2.6889777854505197, 0.060655276715490855, -0.29117900001899877, 0.019601500224393508, 0.020419631279658006, 0.20749286606198508, 0.048717545350994665, 0.9213184786250432, 0.020258670356445158, 0.1401148494921105, -0.8862540758089297, 0.08847027294087698, -0.4227045328944662, -0.1492590651137378, -0.016884061798651314, -0.5811167807434192, -0.14969260638422202, 0.12020310200906058, 0.13460854166929842, 0.2604443495729706, -0.5918431854215349, 0.015217070724311822, 0.14949034036546582, 0.32481274302411683, -0.011809031813293291, -0.7586853933436739, 0.24270360168475752, 0.11122325858945245, -0.11369172551455653, -0.12465653722002884, -0.027167707278061316, -0.10177445437838825, -0.1418332253713506, -1.2534895776829893, -0.7581579709571151, 0.06982887944671379, -0.4457075763788204, -0.21360179921082428, -0.22572748585793934, 0.23120945654117353, 0.17576877117271597, 0.052207454243089486, 1.203724522433427, 0.2262027273532803, -0.12470665767500316, 0.12564002113477807, -0.09123496677715967, 0.46079668103031773, 0.24638420770090425, -0.01772221376067629, -1.0168955611401937, -0.0041398845354585455, 0.15037394037509283, -0.32815819242235167, -0.04415920148548778, -0.2700401617834268, -0.057637886312170566, -1.1541442127498605, 0.10471223253365997, 0.053104171426705093, 0.09389885564819805, -0.292403749580467, 0.06354042169565961, 0.3650499410773993, -0.0029704856013891374, -0.027625300267244406, -0.07329183189424382, 0.15516699596360398, 0.26574125769286794, -1.2457388357634283, 0.026555852458978124, 0.028536507131096324, -0.05865786433954108, 0.1246595630966447, -0.14621948466576903, 0.52082490537448, -0.08950308016317023, 0.024501146452296205, -0.3618280105703323, -0.1823260247129776, 0.05883505434438992, -0.14303851803574155, -0.06832402113948031, -0.10783819188878854, -0.11215557884017935, -2.0628367546773703, 0.0036386257634239347, -1.3153012290384196, -0.11887219413838314, 0.4285611028015063, 0.202242896555692, -0.1567048626548165, -0.010765838761710117, 2.2920114034285977, 0.009561560486567368, -0.00348810272843135, 0.4446280937426936, 0.27307165032170105, 0.1706491275397471, -0.1390986772361239, -0.07377494468999171, -0.7927847762250531, -1.0430170242931918, 0.007586805433167039, 0.13620872584407137, -0.031739738422176714, -0.049586038318173056, 0.043813056001015906, 0.15362674563814327, 0.26379477197145207, 0.029444905236352667, -0.19922249389343932, -0.3581486717844481, 0.11754082492777562, 1.8648515467426043, -0.30103891974604957, 0.0664597853154799, 0.09375384931912847, -0.15995119007550532, 0.17056339762884287, -0.027059379628348167, 0.6477061295706535, -2.0870167609660584, 0.14333636569087277, 0.923837943638119, 0.04166837289077669, -0.021032522956675077, -1.4253795571775927, 0.24142021383602513, -0.29561204168094113, 0.09792685841321142, 0.006642451876405511, 0.4440175207855843, -0.13753952446798806, 0.019390663227287996, -0.26305225230403, 0.1726056688952833, -1.2258229072715665, 0.2508250209162901, 0.013845148516558364, -0.039167492713681584, 0.3297678385002164, -0.14658028341625756, -0.049303815074066906, 0.06987470869502087, -0.030844545752605524, 0.21235310655244835, 0.013342677444157604, 0.03764396742196499, 0.22157777052708022, 0.11455644794450626, 0.1495448910382002, -8.577854391822451E-4, 0.07956609424196884, 0.17830484131888574, 0.08416157886653096, 0.12391193193538222, 0.4169730890726808, -0.04567947782012507, 0.11747068663365962, 0.9910402075806413, -0.2491020385128947, 0.39653508256312875, 0.13080424034556143, -2.9787084589503374, -0.47417089303404636, 0.009984165200815517, -0.7963879605946185, 0.24641899933170192, -0.06377695855738529, 0.33441058302477933, 0.4630508142098077, -0.2649272895163606, -0.004262093515421633, -1.3498628887210566, -0.050775456187805726, -0.021817199048080466, 0.2577146649851938, -0.30921574760125203, -0.1851979587141922, 0.07968779271379851, -0.009649376459965433, 0.09049346231377665, -0.20442243756706302, -0.0041921499757895594, 0.03506067284320827, -0.05505035384183028, -0.011464036615901188, -0.027390529165507813, -0.01883238286919786, 0.36807543885395877, -0.004379305588703215, 0.23630742910317365, -1.215052395854296, 0.03914908503613661, -0.00926818960644439, 0.2710686926916225, 0.07492443692140084, 0.4210779185908423, -0.00687230880917529, -0.019344299955370715, 0.06800810719860222, -0.024498269754597027, -0.05571464272327227, -0.13486906503097928, 0.05055257331079656, 0.6321765973550659, 0.3503472280025365, 0.023466313664349522, -0.06045971166958182, 0.021827702014236793, 0.01756882073257981, 1.814102107605002, 0.4403995258601121, 1.5305071117923248, 0.1928176501737089, 0.04047417303425815, -0.21409109495626644, 0.012168469881158387, 0.006209187178555526, 0.07695300232289745, 0.23906029031931736, 0.8015233883071975, 0.6777022328234522};
    double[] trimmed = new double[291];
    System.arraycopy(goodweights, 0, trimmed, 0, 291);

    Controller.doBattle(new ReflexAgent(), new NeuralAgent(trimmed));
    // for(int i = 0; i < w.length; ++i) {
    //   System.out.print(w[i] + ", ");
    // }

		//Controller.getIter();
	}
}
