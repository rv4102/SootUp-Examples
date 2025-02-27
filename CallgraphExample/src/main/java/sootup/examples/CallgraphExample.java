package sootup.examples;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import qilin.CoreConfig;
import qilin.core.PTA;
import qilin.driver.PTAFactory;
import qilin.driver.PTAPattern;
import sootup.callgraph.CallGraph;
import sootup.callgraph.CallGraphAlgorithm;
import sootup.callgraph.ClassHierarchyAnalysisAlgorithm;
import sootup.callgraph.RapidTypeAnalysisAlgorithm;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.signatures.MethodSignature;
import sootup.core.typehierarchy.ViewTypeHierarchy;
import sootup.core.types.ClassType;
import sootup.core.types.PrimitiveType;
//import sootup.core.types.VoidType;
import sootup.java.bytecode.frontend.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaIdentifierFactory;
import sootup.java.core.views.JavaView;

public class CallgraphExample {

  public static void main(String[] args) {
//    CoreConfig config = new CoreConfig();
//    System.out.println(config.getPtaConfig());
    // Create a AnalysisInputLocation, which points to a directory. All class files will be loaded
    // from the directory
    List<AnalysisInputLocation> inputLocations = new ArrayList<>();
    inputLocations.add(
            new JavaClassPathAnalysisInputLocation("src/test/resources/Callgraph/binary"));

    JavaView view = new JavaView(inputLocations);

    // Get a MethodSignature
    ClassType classTypeA = view.getIdentifierFactory().getClassType("A");
    ClassType classTypeC = view.getIdentifierFactory().getClassType("C");
    MethodSignature entryMethodSignature =
            JavaIdentifierFactory.getInstance()
                    .getMethodSignature(
                            classTypeC,
                            JavaIdentifierFactory.getInstance()
                                    .getMethodSubSignature(
                                        "calc",
                                        PrimitiveType.IntType.getInstance(),
                                        Collections.singletonList(PrimitiveType.IntType.getInstance())
                                    )
                    );

//    // Create type hierarchy and CHA
//    final ViewTypeHierarchy typeHierarchy = new ViewTypeHierarchy(view);
//    typeHierarchy.subclassesOf(classTypeA).forEach(System.out::println);


    // Create CG by initializing CHA with entry method(s)
    System.out.println("-------------CHA---------------");
    CallGraphAlgorithm cha = new ClassHierarchyAnalysisAlgorithm(view);
    CallGraph cg1 = cha.initialize(Collections.singletonList(entryMethodSignature));
    cg1.callsFrom(entryMethodSignature).forEach(System.out::println);

    // Create CG by using RTA with entry method(s)
    System.out.println("-------------RTA---------------");
    CallGraphAlgorithm rta = new RapidTypeAnalysisAlgorithm(view);
    CallGraph cg2 = rta.initialize(Collections.singletonList(entryMethodSignature));
    cg2.callsFrom(entryMethodSignature).forEach(System.out::println);

//    // Create CG by using QPA with entry method(s)
//    System.out.println("-------------PTA---------------");
//    PTAPattern pattern = new PTAPattern("insens");
//    PTA pta = PTAFactory.createPTA(pattern, view, "C");
//    pta.run();
//    CallGraph cg3 = pta.getCallGraph();
//    cg3.callsFrom(entryMethodSignature).forEach(System.out::println);
  }
}
