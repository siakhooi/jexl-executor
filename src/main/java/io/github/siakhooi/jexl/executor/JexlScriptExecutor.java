package io.github.siakhooi.jexl.executor;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlInfo;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.jexl3.introspection.JexlPermissions;

public class JexlScriptExecutor {
    private final JexlEngine jexl;

    public JexlScriptExecutor(ClassLoader classLoader) {
        JexlPermissions permissions = JexlPermissions.UNRESTRICTED;
        jexl = new JexlBuilder().loader(classLoader).permissions(permissions).create();
    }

    public Object execute(Map<String, Object> contextMap, String jexlScript, String sourceLabel) {
        JexlContext context = new MapContext(new HashMap<>(contextMap));
        context.set("stdout", System.out);
        context.set("stderr", System.err);
        var info = new JexlInfo(sourceLabel, 1, 1);
        var script = jexl.createScript(info, jexlScript);
        return script.execute(context);
    }
}
