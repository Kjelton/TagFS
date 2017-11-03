package fs;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class FileBrowser {
	
	
	
	private TaggedFile defaultDirectory = new TaggedFile("C:/Users/Elton/Pictures/test");
	private TaggedFile currentDirectory = defaultDirectory;
	private TaggedFile [] currentDirectoryFiles = currentDirectory.listTaggedFiles();
	private boolean running;
	private ArrayList<String> whitelist, blacklist = new ArrayList<String>();

	public FileBrowser() {
		running = true;
	}

	public void changeDirectory(Path newDirectory) {
		if (Files.exists(newDirectory)) {
			if (Files.isDirectory(newDirectory)) {
				currentDirectory = new TaggedFile(newDirectory.toString());
				currentDirectoryFiles = currentDirectory.listTaggedFiles();
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
	
		for (TaggedFile file : currentDirectoryFiles){
			System.out.println("File name: " + file.getName() + "  Tags: " + file.getTags());
		}

		System.out.println(Integer.toString(currentDirectoryFiles.length) + " files found.");
	}

	public void printWelcome(){
		System.out.println("Welcome to TagFS");
		System.out.println("Current working directory: " + currentDirectory.toString());
	}
	
	public void run() {
		printWelcome();
		String input = "";
		String [] option;
		Scanner reader = new Scanner(System.in);
		while (this.isRunning()) {
			//getTags();
			input = reader.nextLine();
			option = input.split(" ");
			
			switch (option[0]) {
			case "cd":
				if (option.length >= 2){
					if (Paths.get(currentDirectory.toString() + "\\" + option[1]).toFile().exists()) {
						changeDirectory(Paths.get(currentDirectory.toString() + "\\" + option[1]));
					} else {
						changeDirectory(Paths.get(option[1]));
					}
				}
				break;
			case "up":
				changeDirectory(Paths.get(currentDirectory.getParent()));
				break;
			case "ls":
				loadDirectory();
				break;
			case "at":
				if (option.length >= 3){
					for (TaggedFile file : currentDirectoryFiles){
						if(file.getName().equals(option[1])){
							file.addTag(option[2]);
						}
					}
				}
				System.out.println("Successfully added tag: "+option[2]+ " to file: "+option[1]);
				break;
			case "rt":
				if (option.length >= 3){
					for (TaggedFile file : currentDirectoryFiles){
						if(file.getName().equals(option[1])){
							file.removeTag(option[2]);
						}
					}
				}
				System.out.println("Successfully remove tag: "+option[2]+ " to file: "+option[1]);
				break;
			case "cl":
				if (option.length >= 2){
					for (TaggedFile file : currentDirectoryFiles){
						if(file.getName().equals(option[1])){
							file.clearTags();
						}
					}
				}
				System.out.println("Successfully removed all tags from file: "+option[1]);
				break;
			case "ex":
				System.out.println("Goodbye!");
				running = false;
				break;
			default:

			}
		}
		reader.close();
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
					if (!new TaggedFile(file.toString()).hasTag(tag)){
						filtered.remove(file);
					}
				}
			}
		}
		
		if(!blacklist.isEmpty()){
			for (String tag : getBlacklist()){
				for (File file : filtered){
					if (new TaggedFile(file.toString()).hasTag(tag)){
						filtered.remove(file);
					}
				}
			}
		}
		
		TaggedFile[] filteredTagged = new TaggedFile[filtered.size()];
		for (int i = 0; i < filteredTagged.length; i++){
			filteredTagged[i] = new TaggedFile(filtered.get(i).toString());
		}
		return filteredTagged;
	}
	
	public static void main(String[] args) {

		FileBrowser fb = new FileBrowser();
		fb.run();

	}
}
