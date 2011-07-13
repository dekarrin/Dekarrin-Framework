package com.dekarrin.program;

/**
 * A quiz to test skills on Japanese kana. Both katakana and hiragana can
 * be tested.
 *
 * This program makes use of the ACM's ConsoleProgram to deliver graphics.
 * It would be cool if one day I could get this to work without ACM.
 */
public class JapaneseQuiz extends acm.program.ConsoleProgram {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1966725904114805303L;

	/**
	 * The lowest row of kana that can be selected for a question.
	 */
	private int lowLevel = 0;
	
	/**
	 * The highest row of kana that can be selected for a question.
	 */
	private int highLevel = 0;

	/**
	 * A list of all katakana.
	 */
	private String[][] katakana;

	/**
	 * A list of all hiragana.
	 */
	private String[][] hiragana;
	
	/**
	 * A list of all romaji.
	 */
	private String[][] romaji;
	
	/**
	 * The game mode.
	 */
	private int mode;
	
	/**
	 * The game mode for hiragana only.
	 */
	public static final int MODE_HIRAGANA = 1;
	
	/**
	 * The game mode for katakana only.
	 */
	public static final int MODE_KATAKANA = 2;
	
	/**
	 * The game mode for both character sets.
	 */
	public static final int MODE_BOTH = 3;

	/**
	 * Whether or not the game is currently running.
	 */
	private boolean running = true;

	/**
	 * Execution hook for ConsoleProgram superclass.
	 */
	public void run() {
		initialize();
		while(running) {		
			setFont(new java.awt.Font("Helvetica", java.awt.Font.PLAIN, 14));
			lowLevel = 0;
			highLevel = 0;
			getMode();
			getLevel();
			askQuestions();
		}
		exit();
	}

	/**
	 * Gets the game mode from the user.
	 */
	private void getMode() {
		println("Select Mode");
		mode = 0;
		while(mode != MODE_HIRAGANA && mode != MODE_KATAKANA && mode != MODE_BOTH) {
			mode = readInt(MODE_HIRAGANA+" - Hiragana; "+MODE_KATAKANA+" - Katakana; "+MODE_BOTH+" - Both: ");
		}
	}

	/**
	 * Creates the initial arrays of letters and populates them.
	 */
	private void initialize() {
		romaji = new String[10][5];
		hiragana = new String[10][5];
		katakana = new String[10][5];

		String[] katakana = {
			"\u30a2","\u30a4","\u30a6","\u30a8","\u30aa",
			"\u30ab","\u30ad","\u30af","\u30b1","\u30b3",
			"\u30b5","\u30b7","\u30b9","\u30bb","\u30be",
			"\u30bf","\u30c1","\u30c4","\u30c6","\u30c8",
			"\u30ca","\u30cb","\u30cc","\u30cd","\u30ce",
			"\u30cf","\u30d2","\u30d5","\u30d8","\u30db",
			"\u30de","\u30df","\u30e0","\u30e1","\u30e2",
			"\u30e4",null,"\u30e6",null,"\u30e8",
			"\u30e9","\u30ea","\u30eb","\u30ec","\u30ed",
			"\u30ef",null,"\u30f2",null,"\u30f3"
		};

		String[] hiragana = {
			"\u3042","\u3044","\u3046","\u3048","\u304a",
			"\u304b","\u304d","\u304f","\u3051","\u3053",
			"\u3055","\u3057","\u3059","\u305b","\u305d",
			"\u305f","\u3061","\u3064","\u3066","\u3068",
			"\u306a","\u306b","\u306c","\u306d","\u306e",
			"\u306f","\u3072","\u3075","\u3078","\u307b",
			"\u307e","\u307f","\u3080","\u3081","\u3082",
			"\u3084",null,"\u3086",null,"\u3088",
			"\u3089","\u308a","\u308b","\u308c","\u308d",
			"\u308f",null,"\u3092",null,"\u3093"
		};

		String[] answers = {"a","i","u","e","o",
				"ka","ki","ku","ke","ko",
				"sa","shi","su","se","so",
				"ta","chi","tsu","te","to",
				"na","ni","nu","ne","no",
				"ha","hi","fu","he","ho",
				"ma","mi","mu","me","mo",
				"ya",null,"yu",null,"yo",
				"ra","ri","ru","re","ro",
				"wa",null,"wo",null,"n"
			};
		int k = 0;
		int j = 0;
		for(int i = 0; i < answers.length; i++) {
			this.hiragana[j][k] = hiragana[i];
			this.romaji[j][k] = answers[i];
			this.katakana[j][k] = katakana[i];
			
			k++;
			if(k > 4) {
				k = 0;
				j++;
			}
		}
	}

	/**
	 * Gets the letter levels from the user.
	 */
	private void getLevel() {
		int maxLevel = romaji.length;
		while(lowLevel < 1 || lowLevel > maxLevel) {
			lowLevel = readInt("Enter the lowest level to test, from 1-"+maxLevel+": ");
		}
		while(highLevel < lowLevel || highLevel > maxLevel) {
			highLevel = readInt("Enter the highest level to test, from 1-"+maxLevel+": ");
		}
	}
	
	/**
	 * Gives the user the quiz.
	 */
	private void askQuestions() {
		int count = 0;
		while(count == 0) {
			count = readInt("How many questions? ");
		}
		clear();
		
		
		setFont(new java.awt.Font("Helvetica", java.awt.Font.PLAIN, 30));
		int score = 0;
		for(int i = 0; i < count; i++) {
			String correct = askQuestion();
			if(correct == null) {
				println("Correct!");
				score++;
			} else {
				println("Incorrect. " + correct);
			}
		}

		println("Game over. Final score: "+score+"/"+count);
		println("(Press enter to play again, or type quit to stop)");
		String lastOne = readLine();
		if(lastOne.equalsIgnoreCase("quit")) {
			running = false;
		}
	}

	/**
	 * Asks a single quize question.
	 */
	private String askQuestion() {
		java.util.Random r = new java.util.Random();
		String answer = null;
		String letter = null;
		while(answer == null) {
			int level = highLevel - lowLevel + 1;
			int num = r.nextInt(level);
			num += lowLevel - 1;
			int col = r.nextInt(5);
			answer = this.romaji[num][col];

			int switchMode = (mode != 3) ? mode : r.nextInt(2)+1;
			letter = (switchMode == 2) ? katakana[num][col] : hiragana[num][col];
		}

		String a = readLine(letter + " = ");
		if(a.equalsIgnoreCase(answer)) {
			return null;
		} else {
			return letter + " = " + answer;
		}
	}
	
	/**
	 * Clears the screen by forcing everything up.
	 */
	private void clear() {
		for(int i = 0; i < 50; i++) {
			println();
		}
	}
}
