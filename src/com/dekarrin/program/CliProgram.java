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
	 * The shutdown message for this program.
	 */
	private String shutdownMessage = "Exiting";

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
				if(commandExists(input)) {
					if(input.syntaxIsGood()) {
						output = process(input);
					} else {
						output = input.getSyntaxMessage() + "\n";
						output += String.format("Type 'help %s' for more info.", input.getName());
					}
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
	protected void addCommandDefinition(String name, String description, ArgumentDescription[] args) {
		commands.put(name, new CommandDescription(name, description, args, flags));
	}
	
	/**
	 * Adds a new command description to the list.
	 *
	 * @param commandDescription
	 * The description of the command.
	 */
	protected void addCommandDefinition(CommandDescription commandDescription) {
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
	protected void addCommandDefinition(String name, String description) {
		commands.add(name, new CommandDescription(name, description));
	}
	
	/**
	 * Handles processing of any Command not directly listed
	 * by this CliProgram superclass.
	 *
	 * @param command
	 * The Command given to the program.
	 *
	 * @returns
	 * The output of the command.
	 */
	protected abstract String processCommand(Command command);
	
	/**
	 * Sets the shutdown message for when the program quits.
	 *
	 * @param message
	 * The new shutdown message.
	 */
	protected void setShutdownMessage(String message) {
		shutdownMessage = message;
	}
	
	/**
	 * Adds the basic commands to the program.
	 */
	private void addInitialCommands() {
		addCommandDefinitions();
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
	private boolean commandExists(Command command) {
		return commands.containsKey(command.getName());	
	}
	
	/**
	 * Processes a Command for the default listed commands.
	 * If the given Command does not match any
	 * CommandDescriptions that have been added, processing
	 * is passed to the subclass via the abstract method
	 * processCommand().
	 *
	 * @param command
	 * The Command given to the program.
	 *
	 * @returns
	 * The output of the processed command.
	 */
	private String process(Command command) {
		String name = command.getName();
		String output;
		if(name.equals("help")) {
			output = getCommandHelp(command.getArgument("command"));
		} else if(name.equals("list")) {
			output = getCommandList();
		} else if(name.equals("quit") || name.equals("exit")) {
			output = quit();
		} else {
			output = processCommand(command);
		}
		return output;
	}
	
	/**
	 * Gets the help and information page on a command.
	 *
	 * @param commandName
	 * The command that help is needed for.
	 *
	 * @returns
	 * The information on the specified command.
	 */
	private String getCommandHelp(String commandName) {
		CommandDescription desc = commands.get(commandName);
		String message = desc.getHelpMessage();
		return message;
	}
	
	/**
	 * Lists all commands in the program.
	 *
	 * @returns
	 * The list of commands.
	 */
	private String getCommandList() {
		String list = "";
		Iterator<CommandDescription> i = commands.values().iterator();
		while(i.hasNext()) {
			list += i.next().getListing() + "\n";
		}
		return list;
	}
	
	/**
	 * Prepares the system to quit. The program will exit
	 * on the next iteration of the execution loop, so as
	 * soon as output is given to the user, the program will
	 * quit.
	 *
	 * @returns
	 * The system shutdown message.
	 */
	private String quit() {
		running = false;
		return shutdownMessage;
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
		return String.format("Command '%s' not recognized. Type 'list' for command index.", command.getName());
	}
	
	/**
	 * Adds the default commands and their descriptions to this
	 * CliProgram.
	 */
	private void addCommandDefinitions() {
		ArgumentDescription[] args;
		args = new ArgumentDescription[]{new ArgumentDescription("command", "The command to get help on", true)};
		addCommandDefinition("help", "Gets help on a command; if no command is given, the commands are listed", args);
		addCommandDefinition("quit", "Quits the program");
		addCommandDefinition("exit", "Quits the program");
		addCommandDefinition("list", "Lists available commands");
	}
}