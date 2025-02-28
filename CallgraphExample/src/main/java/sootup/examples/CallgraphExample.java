package sootup.examples;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;

import sootup.callgraph.CallGraph;
import sootup.callgraph.CallGraphAlgorithm;
import sootup.callgraph.ClassHierarchyAnalysisAlgorithm;
import sootup.callgraph.RapidTypeAnalysisAlgorithm;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.model.SourceType;
import sootup.core.signatures.MethodSignature;
import sootup.core.typehierarchy.ViewTypeHierarchy;
import sootup.core.types.ClassType;
import sootup.core.types.PrimitiveType;
import sootup.core.types.VoidType;
import sootup.core.types.ArrayType;
import sootup.java.bytecode.frontend.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.bytecode.frontend.inputlocation.JrtFileSystemAnalysisInputLocation;
import sootup.java.core.JavaIdentifierFactory;
import sootup.java.core.views.JavaView;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.Level;

public class CallgraphExample {
  static {
    // Set logger level programmatically
    Logger rootLogger = (Logger) LoggerFactory.getLogger("sootup");
    rootLogger.setLevel(Level.ERROR);

    Logger callgraphLogger = (Logger) LoggerFactory.getLogger("sootup.callgraph");
    callgraphLogger.setLevel(Level.ERROR);

    Logger typeHierarchyLogger = (Logger) LoggerFactory.getLogger("sootup.core.typehierarchy");
    typeHierarchyLogger.setLevel(Level.ERROR);
  }

  public static void main(String[] args) {
    List<AnalysisInputLocation> inputLocations = new ArrayList<>();
    inputLocations.add(
        new JavaClassPathAnalysisInputLocation("src/test/resources/teammates_package/teammates.jar")
    );
//    inputLocations.add(
//        new JrtFileSystemAnalysisInputLocation()
//    );

    JavaView view = new JavaView(inputLocations);
//    view.getClasses().forEach(System.out::println);

//    ClassType mainType = view.getIdentifierFactory().getClassType("teammates.main.Application");
//    ClassType StringType = view.getIdentifierFactory().getClassType("java.lang.String");
//    MethodSignature entryMethodSignature = JavaIdentifierFactory.getInstance().getMethodSignature(
//        mainType,
//        JavaIdentifierFactory.getInstance().getMethodSubSignature(
//            "main",
//            VoidType.getInstance(),
//            Collections.singletonList(ArrayType.createArrayType(StringType, 1))
//        )
//    );

    ClassType createAccountRequestActionType = view.getIdentifierFactory().getClassType("teammates.ui.webapi.CreateAccountRequestAction");
    MethodSignature entryMethodSignature = JavaIdentifierFactory.getInstance().getMethodSignature(
        createAccountRequestActionType,
        JavaIdentifierFactory.getInstance().getMethodSubSignature(
            "execute",
            view.getIdentifierFactory().getClassType("teammates.ui.webapi.JsonResult"),
            Collections.emptyList()
        )
    );

//    // Create type hierarchy and CHA
//    final ViewTypeHierarchy typeHierarchy = new ViewTypeHierarchy(view);
//    typeHierarchy.subclassesOf(testClassType).forEach(System.out::println);


    // Create CG by initializing CHA with entry method(s)
    System.out.println("-------------CHA---------------");
    CallGraphAlgorithm cha = new ClassHierarchyAnalysisAlgorithm(view);
    CallGraph cg1 = cha.initialize(Collections.singletonList(entryMethodSignature));
//    cg1.callsFrom(entryMethodSignature).forEach(System.out::println);
    recursivePrint(entryMethodSignature, cg1, new HashSet<>(), "");

//    // Create CG by using RTA with entry method(s)
//    System.out.println("-------------RTA---------------");
//    CallGraphAlgorithm rta = new RapidTypeAnalysisAlgorithm(view);
//    CallGraph cg2 = rta.initialize(Collections.singletonList(entryMethodSignature));
////    cg2.callsFrom(entryMethodSignature).forEach(System.out::println);
//    recursivePrint(entryMethodSignature, cg2, new HashSet<>(), "");

//    // Create CG by using QPA with entry method(s)
//    System.out.println("-------------PTA---------------");
//    PTAPattern pattern = new PTAPattern("insens");
//    PTA pta = PTAFactory.createPTA(pattern, view, "C");
//    pta.run();
//    CallGraph cg3 = pta.getCallGraph();
//    cg3.callsFrom(entryMethodSignature).forEach(System.out::println);
  }

  public static void recursivePrint(MethodSignature m, CallGraph cg, HashSet<Integer> visited, String indent) {
      // Base case: if the method has already been visited, return to avoid infinite recursion
      if (visited.contains(m.hashCode())) {
        return;
      }
      // Pretty print the node
      System.out.println(indent + " -> " + m);

      // Mark the method as visited
      visited.add(m.hashCode());

      // Pretty print the children with indentation
      cg.callTargetsFrom(m).forEach(child -> recursivePrint(child, cg, visited, indent + "    "));
  }
}
