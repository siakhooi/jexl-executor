package io.github.siakhooi.jexl.executor;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.jexl3.JexlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlInfo;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.jexl3.introspection.JexlPermissions;

public class JexlScriptExecutor {
    private static final Logger logger = LoggerFactory.getLogger(JexlScriptExecutor.class);

    private final JexlEngine jexl;

    public JexlScriptExecutor(ClassLoader classLoader) {
        this(classLoader, false);
    }

    /**
     * @param jexlDebug when true, enables {@link org.apache.commons.jexl3.JexlBuilder#debug(boolean)} so JEXL failures include more diagnostic detail
     */
    public JexlScriptExecutor(ClassLoader classLoader, boolean jexlDebug) {
        JexlPermissions permissions = JexlPermissions.UNRESTRICTED;
        jexl = new JexlBuilder()
                .loader(classLoader)
                .permissions(permissions)
                .debug(jexlDebug)
                .strict(true)
                .silent(false)
                .create();
        if (jexlDebug) {
            logger.debug("JEXL engine created with debug=true (richer errors on script failure)");
        }
    }

    public Object execute(Map<String, Object> contextMap, String jexlScript, String sourceLabel) {
        logger.debug("Evaluating JEXL from '{}' ({} chars)", sourceLabel, jexlScript.length());
        JexlContext context = new MapContext(new HashMap<>(contextMap));
        context.set("stdout", System.out); // NOSONAR intentional: bind real stdout for JEXL scripts
        context.set("stderr", System.err); // NOSONAR intentional: bind real stderr for JEXL scripts
        var info = new JexlInfo(sourceLabel, 1, 1);
        var script = jexl.createScript(info, jexlScript);
        return script.execute(context);
    }
}
