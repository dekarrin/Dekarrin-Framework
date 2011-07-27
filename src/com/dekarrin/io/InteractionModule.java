package com.dekarrin.io;

import java.io.*;

/**
 * Handles IO operation for stdin and stdout.
 */
public class InteractionModule {
	
	private BufferedReader stdin;
	
	public InteractionModule() {
		stdin = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public void print(CharSequence message) {
		System.out.print(message.toString());
	}
	
	public void println(CharSequence message) {
		System.out.println(message.toString());
	}
	
	public void println() {
		System.out.println();
	}
	
	public String read() {
		return read(":");
	}
	
	public String read(String prompt) {
		String read = null;
		
		System.out.print(prompt + " ");
		
		try {
			read = stdin.readLine();
		} catch(IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
		
		return read;
	}
	
	public void printError(String message) {
		System.err.println(message);
	}
}
