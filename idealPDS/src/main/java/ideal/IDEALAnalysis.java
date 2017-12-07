package ideal;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import boomerang.WeightedForwardQuery;
import com.google.common.collect.Maps;

import boomerang.Query;
import boomerang.jimple.AllocVal;
import boomerang.jimple.Statement;
import heros.InterproceduralCFG;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import soot.util.queue.QueueReader;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

public class IDEALAnalysis<W extends Weight> {

	public static boolean ENABLE_STRONG_UPDATES = true;
	public static boolean SEED_IN_APPLICATION_CLASS_METHOD = false;
	public static boolean PRINT_OPTIONS = false;

	private final InterproceduralCFG<Unit, SootMethod> icfg;
	protected final IDEALAnalysisDefinition<W> analysisDefinition;
	private final SeedFactory<W> seedFactory;
	private int timeoutCount;
	private int seedCount;

	public IDEALAnalysis(final IDEALAnalysisDefinition<W> analysisDefinition) {
		this.analysisDefinition = analysisDefinition;
		this.icfg = analysisDefinition.icfg();
		this.seedFactory = new SeedFactory<W>(analysisDefinition){

			@Override
			public BiDiInterproceduralCFG<Unit, SootMethod> icfg() {
				return analysisDefinition.icfg();
			}
		};
	}

	public Map<WeightedForwardQuery<W>, IDEALSeedSolver<W>> run() {
		printOptions();
		long before = System.currentTimeMillis();
		Set<WeightedForwardQuery<W>> initialSeeds = seedFactory.computeSeeds();
		long after = System.currentTimeMillis();
		System.out.println("Computed seeds in: "+ (after-before)  + " ms");
		if (initialSeeds.isEmpty())
			System.err.println("No seeds found!");
		else
			System.err.println("Analysing " + initialSeeds.size() + " seeds!");
		Map<WeightedForwardQuery<W>, IDEALSeedSolver<W>> seedToSolver = Maps.newHashMap();
		for (WeightedForwardQuery<W> seed : initialSeeds) {
			seedCount++;
			System.err.println("Analyzing "+ seed);
			try {
				IDEALSeedSolver<W> solver = run(seed);
				seedToSolver.put(seed, solver);
				System.err.println(String.format("Seed Analysis finished in ms (Solver1/Solver2):  %s/%s", solver.getPhase1Solver().getAnalysisStopwatch().elapsed(), solver.getPhase2Solver().getAnalysisStopwatch().elapsed()));
			} catch(IDEALSeedTimeout e){
				seedToSolver.put(seed, (IDEALSeedSolver<W>) e.getSolver());
				timeoutCount++;
			}
			System.err.println("Analyzed (finished,timedout): \t (" + (seedCount -timeoutCount)+ "," + timeoutCount + ") of "+ initialSeeds.size() + " seeds! ");
		}
		return seedToSolver;
	}
	public IDEALSeedSolver<W> run(Query seed) {
		IDEALSeedSolver<W> idealAnalysis = new IDEALSeedSolver<W>(analysisDefinition, seed);
		idealAnalysis.run();
		return idealAnalysis;
	}
	private void printOptions() {
		if(PRINT_OPTIONS)
			System.out.println(analysisDefinition);
	}


}
