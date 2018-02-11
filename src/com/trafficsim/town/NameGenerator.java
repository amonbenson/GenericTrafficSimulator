package com.trafficsim.town;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class NameGenerator {

	private ArrayList<String> vocals = new ArrayList<String>();
	private ArrayList<String> startConsonants = new ArrayList<String>();
	private ArrayList<String> endConsonants = new ArrayList<String>();
	private ArrayList<String> nameInstructions = new ArrayList<String>();
	
	private String[] busFirstChar = new String[] {"1", "2", "3", "M", "X"};
	
	private Random random = new Random();

	public NameGenerator() {
		String demoVocals[] = { "a", "e", "i", "o", "u", "ei", "ai", "y", "eu", "au", };

		String demoStartConsonants[] = { "b", "c", "d", "f", "g", "h", "k", "l", "m", "n", "r", "s", "t", "w", "z",
				"br", "fl", "gr", "kl", "st", "sch" };

		String demoEndConsonants[] = { "b", "d", "g", "k", "l", "m", "n", "p", "r", "s", "t", "w", "x", "z", "st", "sh",
				"tz", "rt" };

		String nameInstructions[] = { "cv", "cvd", "cvdv", "cvdvd", "cvdvdv" };

		this.vocals.addAll(Arrays.asList(demoVocals));
		this.startConsonants.addAll(Arrays.asList(demoStartConsonants));
		this.endConsonants.addAll(Arrays.asList(demoEndConsonants));
		this.nameInstructions.addAll(Arrays.asList(nameInstructions));
	}

	/**
	 *
	 * The names will look like this
	 * (v=vocal,c=startConsonsonant,d=endConsonants): vd, cvdvd, cvd, vdvd
	 *
	 * @param vocals
	 *            pass something like {"a","e","ou",..}
	 * @param startConsonants
	 *            pass something like {"s","f","kl",..}
	 * @param endConsonants
	 *            pass something like {"th","sh","f",..}
	 */
	public NameGenerator(String[] vocals, String[] startConsonants, String[] endConsonants) {
		this.vocals.addAll(Arrays.asList(vocals));
		this.startConsonants.addAll(Arrays.asList(startConsonants));
		this.endConsonants.addAll(Arrays.asList(endConsonants));
	}

	/**
	 * see {@link NameGenerator#NameGenerator(String[], String[], String[])}
	 *
	 * @param vocals
	 * @param startConsonants
	 * @param endConsonants
	 * @param nameInstructions
	 *            Use only the following letters:
	 *            (v=vocal,c=startConsonsonant,d=endConsonants)! Pass something
	 *            like {"vd", "cvdvd", "cvd", "vdvd"}
	 */
	public NameGenerator(String[] vocals, String[] startConsonants, String[] endConsonants,
			String[] nameInstructions) {
		this(vocals, startConsonants, endConsonants);
		this.nameInstructions.addAll(Arrays.asList(nameInstructions));
	}

	public String getName() {
		return firstCharUppercase(getNameByInstructions(getRandomElementFrom(nameInstructions)));
	}

	private int randomInt(int min, int max) {
		return min + random.nextInt(max - min + 1);
	}

	private String getNameByInstructions(String nameInstructions) {
		String name = "";
		int l = nameInstructions.length();

		for (int i = 0; i < l; i++) {
			char x = nameInstructions.charAt(0);
			switch (x) {
			case 'v':
				name += getRandomElementFrom(vocals);
				break;
			case 'c':
				name += getRandomElementFrom(startConsonants);
				break;
			case 'd':
				name += getRandomElementFrom(endConsonants);
				break;
			}
			nameInstructions = nameInstructions.substring(1);
		}
		return name;
	}

	private String firstCharUppercase(String name) {
		return Character.toString(name.charAt(0)).toUpperCase() + name.substring(1);
	}

	private String getRandomElementFrom(ArrayList<String> v) {
		return v.get(randomInt(0, v.size() - 1));
	}
	
	public String getBusName() {
		String char1 = busFirstChar[random.nextInt(busFirstChar.length)];
		String char2 = String.valueOf(1 + random.nextInt(9));
		String char3 = String.valueOf(random.nextInt(10));
		
		return char1 + char2 + char3;
	}
}