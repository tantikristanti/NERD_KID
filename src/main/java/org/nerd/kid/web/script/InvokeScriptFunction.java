package org.nerd.kid.web.script;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class InvokeScriptFunction {
    public static void main(String[] args) throws Exception{
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("nashorn");

        // to evaluate the Javascript code
        engine.eval("function hello(name){ print('Hello, ' + name); };");

        Invocable invocable = (Invocable) engine;
        invocable.invokeFunction("hello","world");
    }
}
