package com.dekarrin.script;

import com.dekarrin.cli.Command;

/**
 * Interface for objects that wish to receive commands from
 * a ScriptReader.
 */
public interface ScriptListener {

	/**
	 * Executes a command from a script.
	 * 
	 * @param code
	 * The command being executed.
	 * 
	 * @throws CommandException
	 * Indicates that an error with a command was encountered.
	 * 
	 * @throws ScriptException
	 * Indicates that a critical error occurred in the class
	 * that implemented the command.
	 */
	public void executeScriptCommand(Command command) throws CommandException, ScriptException;
}
