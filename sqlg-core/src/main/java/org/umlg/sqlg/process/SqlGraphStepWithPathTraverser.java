package org.umlg.sqlg.process;

import com.google.common.collect.Multimap;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.Step;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.ImmutablePath;
import org.apache.tinkerpop.gremlin.process.traversal.traverser.B_O_P_S_SE_SL_Traverser;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by pieter on 2015/07/20.
 */
public class SqlGraphStepWithPathTraverser<T> extends B_O_P_S_SE_SL_Traverser<T> implements SqlgLabelledPathTraverser {

    private Multimap<String, Object> labeledObjects;

    public SqlGraphStepWithPathTraverser(final T t, Multimap<String, Object> labeledObjects, final Step<T, ?> step, final long initialBulk) {
        super(t, step, initialBulk);
        this.labeledObjects = labeledObjects;
        if (!this.labeledObjects.isEmpty()) {
            customSplit(t);
        }
    }

    /**
     * This odd logic is to ensure the path represents the path from left to right.
     * Calling this.path.extends(...) reverses the path. The test still pass but it seems wrong.
     */
    private void customSplit(final T t) {
        boolean addT = true;
        Path localPath = ImmutablePath.make();
        for (String label : labeledObjects.keySet()) {
            Collection<Object> labeledElements = labeledObjects.get(label);
            for (Object labeledElement : labeledElements) {
                if (!addT && labeledElement == t) {
                    addT = true;
                }
                localPath = localPath.extend(labeledElement, Collections.singleton(label));
            }
        }
        if (addT)
            //tp relies on all elements traversed being on the path.
            //if the element is not labelled put it on the path
            localPath = localPath.clone().extend(t);
        this.path = localPath;
    }

    @Override
    public void setPath(Path path) {
        this.path = path;
    }
}