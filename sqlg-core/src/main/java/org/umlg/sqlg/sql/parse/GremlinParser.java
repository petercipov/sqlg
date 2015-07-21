package org.umlg.sqlg.sql.parse;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.umlg.sqlg.structure.SchemaTable;
import org.umlg.sqlg.structure.SqlgGraph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Date: 2015/01/03
 * Time: 1:06 PM
 */
public class GremlinParser<S extends Element, E extends Element> {

    private SqlgGraph sqlgGraph;

    public GremlinParser(SqlgGraph sqlgGraph) {
        this.sqlgGraph = sqlgGraph;
    }


    /**
     * This is for the GraphStep
     * The first replacedStep has the starting SchemaTable is the starting point
     * @param replacedSteps
     * @return
     */
    public Set<SchemaTableTree> parse(List<ReplacedStep<S, E>> replacedSteps) {
        ReplacedStep startReplacedStep = replacedSteps.remove(0);
        Set<SchemaTableTree> rootSchemaTableTrees = startReplacedStep.getRootSchemaTableTrees(this.sqlgGraph);
        for (SchemaTableTree rootSchemaTableTree : rootSchemaTableTrees) {
            Set<SchemaTableTree> schemaTableTrees = new HashSet<>();
            schemaTableTrees.add(rootSchemaTableTree);
            for (ReplacedStep<S, E> replacedStep : replacedSteps) {
                //This schemaTableTree represents the tree nodes as build up to this depth. Each replacedStep goes a level further
                schemaTableTrees = replacedStep.calculatePathForStep(schemaTableTrees);
            }
            rootSchemaTableTree.removeAllButDeepestLeafNodes(replacedSteps.size() + 1);
            rootSchemaTableTree.removeNodesInvalidatedByHas();
        }
        return rootSchemaTableTrees;

    }

    /**
     * Constructs the label paths from the given schemaTable to the leaf vertex labels for the gremlin query.
     * For each path Sqlg will execute a sql query. The union of the queries is the result the gremlin query.
     * The vertex labels can be calculated from the steps.
     *
     * @param schemaTable
     * @param replacedSteps The original VertexSteps and HasSteps that were replaced
     * @return a List of paths. Each path is itself a list of SchemaTables.
     */
    public SchemaTableTree parse(SchemaTable schemaTable, List<ReplacedStep<S, E>> replacedSteps) {
        Set<SchemaTableTree> schemaTableTrees = new HashSet<>();
        SchemaTableTree rootSchemaTableTree = new SchemaTableTree(this.sqlgGraph, schemaTable, 0);
        schemaTableTrees.add(rootSchemaTableTree);
        for (ReplacedStep<S, E> replacedStep : replacedSteps) {
            //This schemaTableTree represents the tree nodes as build up to this depth. Each replacedStep goes a level further
            schemaTableTrees = replacedStep.calculatePathForStep(schemaTableTrees);
        }
        rootSchemaTableTree.removeAllButDeepestLeafNodes(replacedSteps.size());
        rootSchemaTableTree.removeNodesInvalidatedByHas();
        return rootSchemaTableTree;
    }

}