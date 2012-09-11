package com.thinkaurelius.faunus.mapreduce.transform;

import com.thinkaurelius.faunus.BaseTest;
import com.thinkaurelius.faunus.FaunusVertex;
import com.thinkaurelius.faunus.Holder;
import com.thinkaurelius.faunus.mapreduce.FaunusCompiler;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;

import java.io.IOException;
import java.util.Map;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class VerticesVerticesMapReduceTest extends BaseTest {

    MapReduceDriver<NullWritable, FaunusVertex, LongWritable, Holder, NullWritable, FaunusVertex> mapReduceDriver;

    public void setUp() {
        mapReduceDriver = new MapReduceDriver<NullWritable, FaunusVertex, LongWritable, Holder, NullWritable, FaunusVertex>();
        mapReduceDriver.setMapper(new VerticesVerticesMapReduce.Map());
        mapReduceDriver.setReducer(new VerticesVerticesMapReduce.Reduce());
    }

    public void testOutCreatedTraversal() throws IOException {
        Configuration config = new Configuration();
        config.set(VerticesVerticesMapReduce.DIRECTION, Direction.OUT.name());
        config.setStrings(VerticesVerticesMapReduce.LABELS, "created");

        mapReduceDriver.withConfiguration(config);

        Map<Long, FaunusVertex> results = runWithGraph(startPath(generateGraph(ExampleGraph.TINKERGRAPH, config), Vertex.class), mapReduceDriver);
        assertEquals(results.size(), 6);
        assertEquals(results.get(1l).pathCount(), 0);
        assertEquals(results.get(2l).pathCount(), 0);
        assertEquals(results.get(3l).pathCount(), 3);
        assertEquals(results.get(4l).pathCount(), 0);
        assertEquals(results.get(5l).pathCount(), 1);
        assertEquals(results.get(6l).pathCount(), 0);

        assertEquals(mapReduceDriver.getCounters().findCounter(VerticesVerticesMapReduce.Counters.EDGES_TRAVERSED).getValue(), 4);
        identicalStructure(results, ExampleGraph.TINKERGRAPH);
    }

    public void testOutAllTraversal() throws IOException {
        Configuration config = new Configuration();
        config.set(VerticesVerticesMapReduce.DIRECTION, Direction.OUT.name());
        config.setStrings(VerticesVerticesMapReduce.LABELS, "knows", "created");

        mapReduceDriver.withConfiguration(config);

        Map<Long, FaunusVertex> results = runWithGraph(startPath(generateGraph(ExampleGraph.TINKERGRAPH, config), Vertex.class), mapReduceDriver);
        assertEquals(results.size(), 6);
        assertEquals(results.get(1l).pathCount(), 0);
        assertEquals(results.get(2l).pathCount(), 1);
        assertEquals(results.get(3l).pathCount(), 3);
        assertEquals(results.get(4l).pathCount(), 1);
        assertEquals(results.get(5l).pathCount(), 1);
        assertEquals(results.get(6l).pathCount(), 0);

        assertEquals(mapReduceDriver.getCounters().findCounter(VerticesVerticesMapReduce.Counters.EDGES_TRAVERSED).getValue(), 6);
        identicalStructure(results, ExampleGraph.TINKERGRAPH);
    }

    public void testInAllTraversal() throws IOException {
        Configuration config = new Configuration();
        config.set(VerticesVerticesMapReduce.DIRECTION, Direction.IN.name());
        config.setStrings(VerticesVerticesMapReduce.LABELS);

        mapReduceDriver.withConfiguration(config);

        Map<Long, FaunusVertex> results = runWithGraph(startPath(generateGraph(ExampleGraph.TINKERGRAPH, config), Vertex.class), mapReduceDriver);
        assertEquals(results.size(), 6);
        assertEquals(results.get(1l).pathCount(), 3);
        assertEquals(results.get(2l).pathCount(), 0);
        assertEquals(results.get(3l).pathCount(), 0);
        assertEquals(results.get(4l).pathCount(), 2);
        assertEquals(results.get(5l).pathCount(), 0);
        assertEquals(results.get(6l).pathCount(), 1);

        assertEquals(mapReduceDriver.getCounters().findCounter(VerticesVerticesMapReduce.Counters.EDGES_TRAVERSED).getValue(), 6);
        identicalStructure(results, ExampleGraph.TINKERGRAPH);
    }

    public void testBothAllTraversal() throws IOException {
        Configuration config = new Configuration();
        config.set(VerticesVerticesMapReduce.DIRECTION, Direction.BOTH.name());
        config.setStrings(VerticesVerticesMapReduce.LABELS);

        mapReduceDriver.withConfiguration(config);

        Map<Long, FaunusVertex> results = runWithGraph(startPath(generateGraph(ExampleGraph.TINKERGRAPH, config), Vertex.class), mapReduceDriver);
        assertEquals(results.size(), 6);
        assertEquals(results.get(1l).pathCount(), 3);
        assertEquals(results.get(2l).pathCount(), 1);
        assertEquals(results.get(3l).pathCount(), 3);
        assertEquals(results.get(4l).pathCount(), 3);
        assertEquals(results.get(5l).pathCount(), 1);
        assertEquals(results.get(6l).pathCount(), 1);

        assertEquals(mapReduceDriver.getCounters().findCounter(VerticesVerticesMapReduce.Counters.EDGES_TRAVERSED).getValue(), 12);
        identicalStructure(results, ExampleGraph.TINKERGRAPH);
    }

    public void testBothCreatedTraversal() throws IOException {
        Configuration config = new Configuration();
        config.set(VerticesVerticesMapReduce.DIRECTION, Direction.BOTH.name());
        config.setStrings(VerticesVerticesMapReduce.LABELS, "created");

        mapReduceDriver.withConfiguration(config);

        Map<Long, FaunusVertex> results = runWithGraph(startPath(generateGraph(ExampleGraph.TINKERGRAPH, config), Vertex.class), mapReduceDriver);
        assertEquals(results.size(), 6);
        assertEquals(results.get(1l).pathCount(), 1);
        assertEquals(results.get(2l).pathCount(), 0);
        assertEquals(results.get(3l).pathCount(), 3);
        assertEquals(results.get(4l).pathCount(), 2);
        assertEquals(results.get(5l).pathCount(), 1);
        assertEquals(results.get(6l).pathCount(), 1);

        try {
            results.get(1l).getPaths();
            assertTrue(false);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }

        assertEquals(mapReduceDriver.getCounters().findCounter(VerticesVerticesMapReduce.Counters.EDGES_TRAVERSED).getValue(), 8);
        identicalStructure(results, ExampleGraph.TINKERGRAPH);
    }

    public void testOutKnowsWithPaths() throws IOException {
        Configuration config = new Configuration();
        config.set(VerticesVerticesMapReduce.DIRECTION, Direction.OUT.name());
        config.setStrings(VerticesVerticesMapReduce.LABELS, "knows");
        config.setBoolean(FaunusCompiler.PATH_ENABLED, true);

        mapReduceDriver.withConfiguration(config);

        Map<Long, FaunusVertex> results = runWithGraph(startPath(generateGraph(ExampleGraph.TINKERGRAPH, config), Vertex.class), mapReduceDriver);
        assertEquals(results.size(), 6);
        assertEquals(results.get(1l).pathCount(), 0);
        assertEquals(results.get(2l).pathCount(), 1);
        assertEquals(results.get(3l).pathCount(), 0);
        assertEquals(results.get(4l).pathCount(), 1);
        assertEquals(results.get(5l).pathCount(), 0);
        assertEquals(results.get(6l).pathCount(), 0);

        assertEquals(results.get(2l).getPaths().size(), 1);
        assertEquals(results.get(2l).getPaths().get(0).size(), 2);
        assertEquals(results.get(2l).getPaths().get(0).get(0).getId(), 1l);
        assertEquals(results.get(2l).getPaths().get(0).get(1).getId(), 2l);

        assertEquals(results.get(4l).getPaths().size(), 1);
        assertEquals(results.get(4l).getPaths().get(0).size(), 2);
        assertEquals(results.get(4l).getPaths().get(0).get(0).getId(), 1l);
        assertEquals(results.get(4l).getPaths().get(0).get(1).getId(), 4l);

        assertEquals(mapReduceDriver.getCounters().findCounter(VerticesVerticesMapReduce.Counters.EDGES_TRAVERSED).getValue(), 2);
        identicalStructure(results, ExampleGraph.TINKERGRAPH);
    }

    public void testOutKnowsWithPathsOnlyMarko() throws IOException {
        Configuration config = new Configuration();
        config.set(VerticesVerticesMapReduce.DIRECTION, Direction.OUT.name());
        config.setStrings(VerticesVerticesMapReduce.LABELS, "created");
        config.setBoolean(FaunusCompiler.PATH_ENABLED, true);

        mapReduceDriver.withConfiguration(config);

        Map<Long, FaunusVertex> results = generateIndexedGraph(ExampleGraph.TINKERGRAPH, config);
        results.get(1l).enablePath(true);
        results.get(1l).startPath();
        results = runWithGraph(results.values(), mapReduceDriver);

        assertEquals(results.size(), 6);
        assertEquals(results.get(1l).pathCount(), 0);
        assertEquals(results.get(2l).pathCount(), 0);
        assertEquals(results.get(3l).pathCount(), 1);
        assertEquals(results.get(4l).pathCount(), 0);
        assertEquals(results.get(5l).pathCount(), 0);
        assertEquals(results.get(6l).pathCount(), 0);

        assertEquals(results.get(3l).getPaths().size(), 1);
        assertEquals(results.get(3l).getPaths().get(0).size(), 2);
        assertEquals(results.get(3l).getPaths().get(0).get(0).getId(), 1l);
        assertEquals(results.get(3l).getPaths().get(0).get(1).getId(), 3l);

        assertEquals(mapReduceDriver.getCounters().findCounter(VerticesVerticesMapReduce.Counters.EDGES_TRAVERSED).getValue(), 1);
        identicalStructure(results, ExampleGraph.TINKERGRAPH);
    }
}
