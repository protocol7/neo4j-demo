package com.protocol7.demo.neo4j;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class Neo {
    public static final RelationshipType FOLLOWS = DynamicRelationshipType.withName("FOLLOWS");

    public static void main(String[] args) {
        EmbeddedGraphDatabase db = new EmbeddedGraphDatabase("target/kvitter");
        Transaction tx = db.beginTx();
        try {
            /*
             * protocol7 - follows -> chids
             * chids - follows -> protocol7
             * protocol7 - follows -> evarab
             */

            Node protocol7 = db.createNode();
            protocol7.setProperty("nick", "protocol7");
            
            Node chids = db.createNode();
            chids.setProperty("nick", "chids");
            
            Node evarab = db.createNode();
            evarab.setProperty("nick", "evarab");

            protocol7.createRelationshipTo(chids, FOLLOWS);
            chids.createRelationshipTo(protocol7, FOLLOWS);
            protocol7.createRelationshipTo(evarab, FOLLOWS);
            
            Traverser traverser = protocol7.traverse(
                    Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, 
                    new ReturnableEvaluator() {
                        @Override
                        public boolean isReturnableNode(TraversalPosition currentPos) {
                            return currentPos.notStartNode();
                        }
                    }, FOLLOWS, Direction.OUTGOING);

            System.out.println(traverser.getAllNodes());

            tx.success();
        } catch (RuntimeException e) {
            tx.failure();
            throw e;
        } finally {
            tx.finish();

            db.shutdown();
        }

    }
}
