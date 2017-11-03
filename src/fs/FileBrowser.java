package fs;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class FileBrowser {
	
	
	
	private Path defaultDirectory = Paths.get("E:/Pictures/Toga Himiko");
	private Path currentDirectory = defaultDirectory;
	private boolean running;
	private ArrayList<String> whitelist, blacklist = new ArrayList<String>();

	public FileBrowser() {
		running = true;
	}

	public void changeDirectory(Path newDirectory) {
		if (Files.exists(newDirectory)) {
			if (Files.isDirectory(newDirectory)) {
				currentDirectory = newDirectory;
				System.out.println("Changed directory to: " + newDirectory.toString());
			} else if (Files.isRegularFile(newDirectory)) {
				System.out.println("Path specified is a file.");
			} else {
				System.out.println("Error");
			}
		} else {
			System.out.println("The specified path does not exist.");
		}
	}

	public void loadDirectory() {
		
		File[] listOfFiles = currentDirectory.toFile().listFiles();
		TaggedFile[] taggedFiles = new TaggedFile[listOfFiles.length];
		for (int i = 0; i < listOfFiles.length; i++) {
			taggedFiles[i] = new TaggedFile(listOfFiles[i].toPath());
			System.out.println("File name: " + listOfFiles[i].getName() + "  Tags: " + taggedFiles[i].getTags());
		}

		System.out.println(Integer.toString(listOfFiles.length) + " files found.");
	}

	public void getTags() {
		File[] listOfFiles = currentDirectory.toFile().listFiles();
		TaggedFile[] taggedFiles = new TaggedFile[listOfFiles.length];
		for (int i = 0; i < listOfFiles.length; i++) {
			taggedFiles[i] = new TaggedFile(listOfFiles[i].toPath());
		}
	}

	public void getTags(File[] listOfFiles) {
		TaggedFile[] taggedFiles = new TaggedFile[listOfFiles.length];
		for (int i = 0; i < listOfFiles.length; i++) {
			taggedFiles[i] = new TaggedFile(listOfFiles[i].toPath());
		}
	}

	public void printWelcome(){
		System.out.println("Welcome to TagFS");
		System.out.println("Current working directory: " + currentDirectory.toString());
	}
	
	public String addTag(String filename, String tag){
		Path fullPath = Paths.get(currentDirectory.toString()+filename);
		TaggedFile file;
		if(Files.exists(Paths.get(filename))){
			file = new TaggedFile(Paths.get(filename));
			file.addTag(tag);
			return "tag: " +tag+ " added successfully to file: " + filename;
		}
		else if(Files.exists(fullPath)){
			file = new TaggedFile(fullPath);
			file.addTag(tag);
			return "tag: " +tag+ " added successfully to file: " + filename;
		}
		return "File does not exist";
	}
	
	public void run() {
		printWelcome();
		String option = "";
		Scanner reader = new Scanner(System.in);
		String command = "";
		while (this.isRunning()) {
			getTags();
			option = reader.nextLine();
			command = option.substring(0, 2);
			switch (command) {
			case "cd":
				System.out.println(option);
				if (Paths.get(currentDirectory.toString() + "\\" + option.substring(3)).toFile().exists()) {
					changeDirectory(Paths.get(currentDirectory.toString() + "\\" + option.substring(3)));
				} else {
					changeDirectory(Paths.get(option.substring(3)));
				}
				break;
			case "up":
				changeDirectory(Paths.get(currentDirectory.toFile().getParent()));
			case "ls":
				loadDirectory();
			case "at":
				
			default:

			}
		}
	}

	public boolean isRunning() {
		return running;
	}
	
	public void addIncludedTag(String tag){
		whitelist.add(tag);
	}
	public void removeIncludedTag(String tag){
		whitelist.remove(tag);
	}
	
	public void addExcludedTag(String tag){
		blacklist.add(tag);
	}
	
	public void removeExcludedTag(String tag){
		blacklist.remove(tag);
	}
	
	public ArrayList<String> getWhitelist(){
		return whitelist;
	}
	
	public ArrayList<String> getBlacklist(){
		return blacklist;
	}
	
	public TaggedFile[] filterTags(File[] files){
		ArrayList<File> filtered = new ArrayList<File>();
		for(File file : files){
			filtered.add(file);
		}
		if(!whitelist.isEmpty()){
			for (String tag : getWhitelist()){
				for (File file : filtered){
					if (!new TaggedFile(file.toPath()).hasTag(tag)){
						filtered.remove(file);
					}
				}
			}
		}
		
		if(!blacklist.isEmpty()){
			for (String tag : getBlacklist()){
				for (File file : filtered){
					if (new TaggedFile(file.toPath()).hasTag(tag)){
						filtered.remove(file);
					}
				}
			}
		}
		
		TaggedFile[] filteredTagged = new TaggedFile[filtered.size()];
		for (int i = 0; i < filteredTagged.length; i++){
			filteredTagged[i] = new TaggedFile(filtered.get(i).toPath());
		}
		return filteredTagged;
	}
	
	public static void main(String[] args) {

		FileBrowser fb = new FileBrowser();
		fb.run();

	}
}
