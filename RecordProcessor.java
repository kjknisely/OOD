package misc;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

 
public class RecordProcessor {
	private static String [] firstname;
	private static String [] lastName;
	private static int [] age;
	private static String [] payType;
	private static double [] payRate;
	private static int recordCount; 
	static final int INVALID_TYPE = -1;
	static final int INVALID_NUMBER = -2;
	
	public static String processFile(String inputFileName) {
		StringBuffer outputBuffer = new StringBuffer();
		Scanner fileInput = openInputFile(inputFileName);
		
		allocateRecordArrays(fileInput);

		fileInput.close();
		fileInput = openInputFile(inputFileName);

		recordCount = readRecords(fileInput);
		
		if(recordCount <= 0) {
			printErrorMessages(recordCount);
			fileInput.close();
			return null;
		}
		
		
		//print the rows
		outputBuffer.append(String.format("# of people imported: %d\n", firstname.length));
		
		outputBuffer.append(String.format("\n%-30s %s  %-12s %12s\n", "Person Name", "Age", "Emp. Type", "Pay"));
		for(int i = 0; i < 30; i++)
			outputBuffer.append(String.format("-"));
		outputBuffer.append(String.format(" ---  "));
		for(int i = 0; i < 12; i++)
			outputBuffer.append(String.format("-"));
		outputBuffer.append(String.format(" "));
		for(int i = 0; i < 12; i++)
			outputBuffer.append(String.format("-"));
		outputBuffer.append(String.format("\n"));
		
		for(int i = 0; i < firstname.length; i++) {
			outputBuffer.append(String.format("%-30s %-3d  %-12s $%12.2f\n", firstname[i] + " " + lastName[i], age[i]
				, payType[i], payRate[i]));
		}
		
		int sum1 = 0;
		float avg1 = 0f;
		int c2 = 0;
		double sum2 = 0;
		double avg2 = 0;
		int c3 = 0;
		double sum3 = 0;
		double avg3 = 0;
		int c4 = 0;
		double sum4 = 0;
		double avg4 = 0;
		for(int i = 0; i < firstname.length; i++) {
			sum1 += age[i];
			if(payType[i].equals("Commission")) {
				sum2 += payRate[i];
				c2++;
			} else if(payType[i].equals("Hourly")) {
				sum3 += payRate[i];
				c3++;
			} else if(payType[i].equals("Salary")) {
				sum4 += payRate[i];
				c4++;
			}
		}
		avg1 = (float) sum1 / firstname.length;
		outputBuffer.append(String.format("\nAverage age:         %12.1f\n", avg1));
		avg2 = sum2 / c2;
		outputBuffer.append(String.format("Average commission:  $%12.2f\n", avg2));
		avg3 = sum3 / c3;
		outputBuffer.append(String.format("Average hourly wage: $%12.2f\n", avg3));
		avg4 = sum4 / c4;
		outputBuffer.append(String.format("Average salary:      $%12.2f\n", avg4));
		
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		int c1 = 0;
		for(int i = 0; i < firstname.length; i++) {
			if(hm.containsKey(firstname[i])) {
				hm.put(firstname[i], hm.get(firstname[i]) + 1);
				c1++;
			} else {
				hm.put(firstname[i], 1);
			}
		}

		outputBuffer.append(String.format("\nFirst names with more than one person sharing it:\n"));
		if(c1 > 0) {
			Set<String> set = hm.keySet();
			for(String str : set) {
				if(hm.get(str) > 1) {
					outputBuffer.append(String.format("%s, # people with this name: %d\n", str, hm.get(str)));
				}
			}
		} else { 
			outputBuffer.append(String.format("All first names are unique"));
		}

		HashMap<String, Integer> hm2 = new HashMap<String, Integer>();
		int c21 = 0;
		for(int i = 0; i < lastName.length; i++) {
			if(hm2.containsKey(lastName[i])) {
				hm2.put(lastName[i], hm2.get(lastName[i]) + 1);
				c21++;
			} else {
				hm2.put(lastName[i], 1);
			}
		}

		outputBuffer.append(String.format("\nLast names with more than one person sharing it:\n"));
		if(c21 > 0) {
			Set<String> set = hm2.keySet();
			for(String str : set) {
				if(hm2.get(str) > 1) {
					outputBuffer.append(String.format("%s, # people with this name: %d\n", str, hm2.get(str)));
				}
			}
		} else { 
			outputBuffer.append(String.format("All last names are unique"));
		}
		
		//close the file
		fileInput.close();
		
		return outputBuffer.toString();
	}

	
	
	
	
	private static void printErrorMessages(int recordCount) {
		if(recordCount == 0){
			System.err.println("No records found in data file");
		}
		if(recordCount == INVALID_TYPE) {
			System.err.println("");
		}
	}





	private static int readRecords(Scanner fileInput) {
		int recordCount = 0;
		while(fileInput.hasNextLine()) {
			String l = fileInput.nextLine();
			if(l.length() > 0) {
				
				String [] words = l.split(",");

				int c2 = 0; 
				for(;c2 < lastName.length; c2++) {
					if(lastName[c2] == null)
						break;
					
					if(lastName[c2].compareTo(words[1]) > 0) {
						for(int i = recordCount; i > c2; i--) {
							firstname[i] = firstname[i - 1];
							lastName[i] = lastName[i - 1];
							age[i] = age[i - 1];
							payType[i] = payType[i - 1];
							payRate[i] = payRate[i - 1];
						}
						break;
					}
				}
				
				firstname[c2] = words[0];
				lastName[c2] = words[1];
				payType[c2] = words[3];

				try {
					age[c2] = Integer.parseInt(words[2]);
					payRate[c2] = Double.parseDouble(words[4]);
				} catch(Exception e) {
					System.err.println(e.getMessage());
					fileInput.close();
					return INVALID_NUMBER;
				}
				
				recordCount++;
			}
		}
		return recordCount;
	}


	private static Scanner openInputFile(String inputFileName) {
		Scanner fileInput; 
		try {
			fileInput = new Scanner(new File(inputFileName));
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			return null;
		}
		return fileInput;
	}

	private static void allocateRecordArrays(Scanner fileInput) {
		int c = 0;
		while(fileInput.hasNextLine()) {
			String l = fileInput.nextLine();
			if(l.length() > 0)
				c++;
		}

		firstname = new String[c];
		lastName = new String[c];
		age = new int[c];
		payType = new String[c];
		payRate = new double[c];
	}
	
}
