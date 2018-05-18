package boomerang.callgraph;

import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import soot.toolkits.graph.DirectedGraph;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ObservableStaticICFG implements ObservableICFG<Unit, SootMethod>{
    /**
     * Wrapped static ICFG. If available, this is used to handle all queries.
     */
    private BiDiInterproceduralCFG<Unit, SootMethod> precomputedGraph;

    public ObservableStaticICFG(BiDiInterproceduralCFG<Unit, SootMethod> icfg) {
        this.precomputedGraph = icfg;
    }

    @Override
    public SootMethod getMethodOf(Unit unit) {
        return precomputedGraph.getMethodOf(unit);
    }

    @Override
    public List<Unit> getPredsOf(Unit unit) {
        return precomputedGraph.getPredsOf(unit);
    }

    @Override
    public List<Unit> getSuccsOf(Unit unit) {
        return precomputedGraph.getSuccsOf(unit);
    }

    @Override
    public void addCalleeListener(CalleeListener<Unit, SootMethod> listener) {
        for (SootMethod method : precomputedGraph.getCalleesOfCallAt(listener.getObservedCaller())){
            listener.onCalleeAdded(listener.getObservedCaller(), method);
        }
        //TODO deal with ALL_UNITS callee
    }

    @Override
    public void addCall(Unit caller, SootMethod callee) {
        throw new UnsupportedOperationException("Static ICFG should not get new calls");
    }

    @Override
    public void addCallerListener(CallerListener<Unit, SootMethod> listener) {
        for (Unit unit : precomputedGraph.getCallersOf(listener.getObservedCallee())){
            listener.onCallerAdded(unit, listener.getObservedCallee());
        }
        //TODO deal with ALL_METHODS callee
    }

    @Override
    public Set<Unit> getCallsFromWithin(SootMethod sootMethod) {
        return precomputedGraph.getCallsFromWithin(sootMethod);
    }

    @Override
    public Collection<Unit> getStartPointsOf(SootMethod sootMethod) {
        return precomputedGraph.getStartPointsOf(sootMethod);
    }

    @Override
    public boolean isCallStmt(Unit stmt) {
        return precomputedGraph.isCallStmt(stmt);
    }

    @Override
    public boolean isExitStmt(Unit stmt) {
        return precomputedGraph.isExitStmt(stmt);
    }

    @Override
    public boolean isStartPoint(Unit stmt) {
        return precomputedGraph.isStartPoint(stmt);
    }

    @Override
    public Set<Unit> allNonCallStartNodes() {
        return precomputedGraph.allNonCallStartNodes();
    }

    @Override
    public Collection<Unit> getEndPointsOf(SootMethod sootMethod) {
        return precomputedGraph.getEndPointsOf(sootMethod);
    }

    @Override
    public Set<Unit> allNonCallEndNodes() {
        return precomputedGraph.allNonCallEndNodes();
    }

    @Override
    public List<Value> getParameterRefs(SootMethod sootMethod) {
        return precomputedGraph.getParameterRefs(sootMethod);
    }

    @Override
    public boolean isReachable(Unit u) {
        return precomputedGraph.isReachable(u);
    }
}