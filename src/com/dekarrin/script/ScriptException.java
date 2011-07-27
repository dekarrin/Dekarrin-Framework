package com.dekarrin.script;

/**
 * Indicates an unrecoverable exception with a script.
 */
public class ScriptException extends Exception {
	
	private static final long serialVersionUID = 2689863355333678678L;

	public ScriptException(String message) {
		super(message);
	}
}
