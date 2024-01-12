package io.github.lianggty.pipeline.core.def;

import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizCmdLineEngine;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;
import io.github.lianggty.pipeline.core.Pipeline;
import io.github.lianggty.pipeline.core.Stage;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static guru.nidi.graphviz.attribute.Rank.RankType.SAME;
import static guru.nidi.graphviz.model.Factory.*;

public class Dependency {

    final Pipeline pipeline;

    Map<Object, Object> stageDepend = HashMap.empty();

    Map<String, io.vavr.collection.List<Object>> dataLoadDepend = HashMap.empty();

    public Dependency(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public synchronized void recordDependencies(Object source, Object target) {
        if (source instanceof Stage) {
            this.stageDepend = this.stageDepend.put(source, target);
        } else if (source instanceof String) {
            this.dataLoadDepend = this.dataLoadDepend.put((String) source, io.vavr.collection.List.of(target), io.vavr.collection.List::prependAll);

        } else {
            this.dataLoadDepend = this.dataLoadDepend.put(source.getClass().getSimpleName(), io.vavr.collection.List.of(target), io.vavr.collection.List::prependAll);
        }
    }


    public void draw() {
        Node per = null;
        List<Node> stageNodes = new ArrayList<>();

        for (Stage stage : pipeline.stages()) {
            Node nextNode = node(stage.name());
            if (per != null) {
                stageNodes.add(per.link(nextNode));
            }
            per = nextNode;
        }

        stageNodes.add(per);

        Node[] dataLoad = dataLoadDepend
                .flatMap(it -> {
                    return it._2.map(target -> node(target.getClass().getSimpleName()).link(node(it._1)));
                })
                .toJavaArray(Node[]::new);

        Node[] stage = stageDepend
                .map(it -> node(it._1.getClass().getSimpleName()).with(Shape.RECTANGLE).link(node(it._2().getClass().getSimpleName())))
                .toJavaArray(Node[]::new);

        Graph g = graph("example1").directed()
                .graphAttr().with(Rank.newRank())
                .with(
                        graph("stage").cluster().directed()
                                .nodeAttr().with(Style.FILLED, Color.WHITE)
                                .graphAttr().with(Color.LIGHTGREY, Label.of("Pipeline"))
                                .with(stageNodes),
                        graph("dataLoad").cluster().directed()
                                .nodeAttr().with(Style.FILLED, Font.size(8))
                                .graphAttr().with(Color.LIGHTGREY, Label.of("dataLoad"))
                                .with(dataLoad)
                )

                .with(stage);

        try {
            Graphviz.useEngine(new GraphvizCmdLineEngine());
            Graphviz.fromGraph(g)
//                    .processor(new Roughifyer()
//                            .bowing(2)
//                            .curveStepCount(6)
//                            .roughness(1)
//                            .fillStyle(FillStyle.hachure().width(2).gap(5).angle(0))
//                            .font("*serif", "Comic Sans MS"))
                    .render(Format.SVG)
                    .toFile(new File("example/ex1-rough.svg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
