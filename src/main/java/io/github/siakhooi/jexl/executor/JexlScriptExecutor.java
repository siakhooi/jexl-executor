package io.github.siakhooi.jexl.executor;

import java.util.Map;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.jexl3.introspection.JexlPermissions;

public class JexlScriptExecutor {
    private final JexlEngine jexl;

    public JexlScriptExecutor(ClassLoader classLoader) {
        JexlPermissions permissions = JexlPermissions.UNRESTRICTED;
        jexl = new JexlBuilder().loader(classLoader).permissions(permissions).create();
    }

    public Object execute(Map<String, Object> contextMap, String jexlScript) {
        JexlContext context = new MapContext(contextMap);
        var script = jexl.createScript(jexlScript);
        return script.execute(context);
    }
}
