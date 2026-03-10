package io.github.siakhooi.jexl.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.siakhooi.jexl.executor.config.ExecutionStep;
import io.github.siakhooi.jexl.executor.config.ExecutionType;
import io.github.siakhooi.jexl.executor.config.FlowPath;

class FilesToFlowPathTest {
    @Test
    void testGenerateWithDotAtStart() {
        File dotStartFile = new File(".hiddenfile");
        FlowPath flowPath = FilesToFlowPath.generate(List.of(dotStartFile));
        List<ExecutionStep> steps = flowPath.getSteps();
        assertEquals(1, steps.size());
        assertEquals(".hiddenfile", steps.get(0).name());
        assertEquals(ExecutionType.UNKNOWN, steps.get(0).executionType());
    }

    @Test
    void testGenerateWithDotAtEnd() {
        File dotEndFile = new File("file.");
        FlowPath flowPath = FilesToFlowPath.generate(List.of(dotEndFile));
        List<ExecutionStep> steps = flowPath.getSteps();
        assertEquals(1, steps.size());
        assertEquals("file", steps.get(0).name());
        assertEquals(ExecutionType.UNKNOWN, steps.get(0).executionType());
    }
    @Test
    void testGenerateWithJexlAndJsonFiles() {
        File jexlFile = new File("test1.jexl");
        File jsonFile = new File("test2.json");
        FlowPath flowPath = FilesToFlowPath.generate(List.of(jexlFile, jsonFile));
        List<ExecutionStep> steps = flowPath.getSteps();
        assertEquals(2, steps.size());
        assertEquals("test1", steps.get(0).name());
        assertEquals(jexlFile, steps.get(0).scriptFile());
        assertEquals(ExecutionType.JEXL, steps.get(0).executionType());
        assertEquals("test2", steps.get(1).name());
        assertEquals(jsonFile, steps.get(1).scriptFile());
        assertEquals(ExecutionType.JSON, steps.get(1).executionType());
    }

    @Test
    void testGenerateWithUnknownExtension() {
        File unknownFile = new File("test3.unknown");
        FlowPath flowPath = FilesToFlowPath.generate(List.of(unknownFile));
        List<ExecutionStep> steps = flowPath.getSteps();
        assertEquals(1, steps.size());
        assertEquals("test3", steps.get(0).name());
        assertEquals(ExecutionType.UNKNOWN, steps.get(0).executionType());
    }

    @Test
    void testGenerateWithNoExtension() {
        File noExtFile = new File("test4");
        FlowPath flowPath = FilesToFlowPath.generate(List.of(noExtFile));
        List<ExecutionStep> steps = flowPath.getSteps();
        assertEquals(1, steps.size());
        assertEquals("test4", steps.get(0).name());
        assertEquals(ExecutionType.UNKNOWN, steps.get(0).executionType());
    }
}
