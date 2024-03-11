package DataIO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.tinylog.Logger;

public class DataIO {

	public static boolean WriteToFile(String path, ArrayList<Float> data, int id, String savedData) {
		File file = new File(path);
		if(!file.mkdirs() && !file.exists()) {
			Logger.error("File or file path was unable to be created!");
			return false;
		}else if(file.isDirectory()) {
			Logger.error("Path is either a directory or does not contain the file extension!");
			return false;
		}else if(!file.canWrite()) {
			Logger.error("File does not have write permission, cannot write to file!");
			return false;
		}
		
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(path), 32767)) {
			Logger.info("Writing " + savedData + " data to " + path + "!");
			long millis = System.currentTimeMillis();
			writer.append(String.valueOf(ReadDataFromBoard.sampleTime) + "," + String.valueOf(id));
			String tempData = data.toString();
			writer.append(tempData.substring(1, tempData.length() - 1));
			Logger.info("Writing " + savedData + " data to " + path + " took " + (System.currentTimeMillis() - millis) + " ms!");
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.error(e);
		}
		
		return true;
	}
	
	private static HashMap<String, ArrayList<ArrayList<Float>>> readInLists = new HashMap<String, ArrayList<ArrayList<Float>>>();
	
	private static ArrayList<ArrayList<Float>> ReadFromFile(String path) {
		ArrayList<ArrayList<Float>> arr = new ArrayList<ArrayList<Float>>();
		
		try(Scanner scan = new Scanner(new BufferedReader(new FileReader(path)))) {
			while(scan.hasNextLine()) {
				int sampleRate = scan.nextInt();
				int id = scan.nextInt();
				while(arr.size() < id) {
					arr.add(new ArrayList<Float>());
				}
				ArrayList<Float> idList = arr.get(id);
				String next = scan.nextLine();
				String[] split = next.split(",");
				for(String f : split) {
					idList.add(Float.parseFloat(f));
				}
				idList.add(Float.valueOf(sampleRate));
			}
			scan.close();
		} catch (FileNotFoundException e) {
			Logger.error(e);
			return null;
		}
		
		return arr;
	}
	
	public static ArrayList<Float> ReadFromFile(String path, int id) {
		if(readInLists == null || !readInLists.containsKey(path)) {
			readInLists.put(path, ReadFromFile(path));
		}
		
		return readInLists.get(path).get(id);
	}
	
	public static int GetSampleRate(String path, int id) {
		if(readInLists == null || !readInLists.containsKey(path)) {
			readInLists.put(path, ReadFromFile(path));
		}
		
		return Integer.parseInt(String.valueOf(readInLists.get(path).get(id).get(readInLists.get(path).get(id).size() - 1)));
	}
}
