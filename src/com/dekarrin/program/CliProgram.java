package com.dekarrin.program;

/**
 * Program to be run from the command line interactively. This program
 * operates in a loop, continuously processing user input and giving
 * output for each command, until the user quits.
 */
public abstract class CliProgram extends ConsoleProgram {
	
	/**
	 * Whether or not the program is currently running.
	 */
	private boolean running;
	
	/**
	 * The list of commands that this program recognizes.
	 */
	private HashMap<String,CommandDescription> commands = new Vector<CommandDescription>();

	/**
	 * Creates a new CliProgram.
	 */
	public CliProgram(String[] args) {
		super(args);
		running = true;
		addInitialCommands();
	}
	
	/**
	 * Runs the main execution loop for the CLI program.
	 */
	public void startLoop() {
		while(running) {
			String i = ui.read(prompt);
			if(input != null) {
				Command input = new Command(i);
				String output;
				if(isValid(input)) {
					output = process(input);
				} else {
					output = getBadCommandMessage(input);
				}
				ui.println(output);
				ui.println();
			}
		}
	}
	
	/**
	 * Adds a new command description to the list.
	 *
	 * @param name
	 * The name of the command.
	 *
	 * @param description
	 * The description of the command.
	 *
	 * @param args
	 * The arguments that the command uses.
	 *
	 * @param flags
	 * The flags that the command accepts.
	 */
	protected void addCommandDescription(String name, String description, ArgumentDescription[] args, FlagDescription[] flags) {
		commands.put(name, new CommandDescription(name, description, args, flags));
	}
	
	/**
	 * Adds a new command description, that does not accept
	 * flags, to the list.
	 *
	 * @param name
	 * The name of the command.
	 *
	 * @param description
	 * The description of the command.
	 *
	 * @param args
	 * The arguments that the command uses.
	 */
	protected void addCommandDescription(String name, String description, ArgumentDescription[] args) {
		commands.put(name, new CommandDescription(name, description, args, flags));
	}
	
	/**
	 * Adds a new command description to the list.
	 *
	 * @param commandDescription
	 * The description of the command.
	 */
	protected void addCommandDescription(CommandDescription commandDescription) {
		commands.put(commandDescription.name, commandDescription);
	}
	
	/**
	 * Adds a new command description to the list that does
	 * not have any flags or arguments.
	 *
	 * @param name
	 * The name of the command.
	 *
	 * @param description
	 * The description of the command.
	 */
	protected void addCommandDescription(String name, String description) {
		commands.add(name, new CommandDescription(name, description));
	}
	
	/**
	 * Adds the basic commands to the program.
	 */
	private void addInitialCommands() {
		ArgumentDescription[] args;
		
		addCommandDescription("quit", "Quits the program");
		addCommandDescription("exit", "Quits the program");
		addCommandDescription("list", "Lists available commands");
		
		args = {new ArgumentDescription("command", "The command to get help on", true)};
		addCommandDescription("help", "Gets help on a command; if no command is given, the commands are listed", args);
	}
	
	/**
	 * Checks if the given cammand actually exists.
	 *
	 * @param command
	 * The command to check.
	 *
	 * @returns
	 * Whether the command exists.
	 */
	private boolean isValid(Command command) {
		return commands.containsKey(command.name);	
	}
	
	/**
	 * Gets the message for an invalid command.
	 *
	 * @param command
	 * The invalid entered command.
	 *
	 * @returns
	 * The message.
	 */
	private String getBadCommandMessage(Command command) {
		return String.format("Command '%s' not recognized. Type 'list' for command index.", command.name);
	}
}