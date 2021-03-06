package fs;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class FileBrowser {

	private TaggedFile defaultDirectory = new TaggedFile("C:/Users/Elton/Pictures/test");
	private TaggedFile currentDirectory = defaultDirectory;
	private TaggedFile[] currentDirectoryFiles = currentDirectory.listTaggedFiles();
	private boolean running;
	private ArrayList<String> whitelist = new ArrayList<String>(), blacklist = new ArrayList<String>();

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
		
		TaggedFile [] files = filterTags();
		
		for (TaggedFile file : files ) {
			System.out.println(file.getName() + "  Tags: " + file.getTags());
		}

		System.out.println(Integer.toString(files.length) + " files found.");
	}

	public void printWelcome() {
		System.out.println("Welcome to TagFS");
		System.out.println("Current working directory: " + currentDirectory.toString());
	}

	public void run() {
		printWelcome();
		String input = "";
		String[] option;
		Scanner reader = new Scanner(System.in);
		while (this.isRunning()) {
			// getTags();
			input = reader.nextLine();
			option = input.split(" ");

			switch (option[0]) {
			case "cd":
				if (option.length >= 2) {
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
				if (option.length >= 3) {
					for (TaggedFile file : currentDirectoryFiles) {
						if (file.getName().equals(option[1])) {
							file.addTag(option[2]);
						}
					}
				}
				System.out.println("Successfully added tag: " + option[2] + " to file: " + option[1]);
				break;
			case "rt":
				if (option.length >= 3) {
					for (TaggedFile file : currentDirectoryFiles) {
						if (file.getName().equals(option[1])) {
							file.removeTag(option[2]);
						}
					}
				}
				System.out.println("Successfully removed tag: " + option[2] + " from file: " + option[1]);
				break;
			case "cl":
				if (option.length >= 2) {
					for (TaggedFile file : currentDirectoryFiles) {
						if (file.getName().equals(option[1])) {
							file.clearTags();
						}
					}
				}
				System.out.println("Successfully removed all tags from file: " + option[1]);
				break;
			case "aw":
				if (option.length >= 2) {
					addIncludedTag(option[1]);
				}
				break;
			case "rw":
				if (option.length >= 2) {
					removeIncludedTag(option[1]);
				}
				break;
			case "ab":
				if (option.length >= 2) {
					addExcludedTag(option[1]);
				}
				break;
			case "rb":
				if (option.length >= 2) {
					removeExcludedTag(option[1]);
				}
				break;
			case "ex":
				System.out.println("Goodbye!");
				running = false;
				break;
			default:
				System.out.println("Command not recognized");
			}
		}
		reader.close();
	}

	public boolean isRunning() {
		return running;
	}

	public void addIncludedTag(String tag) {
		whitelist.add(tag);
	}

	public void removeIncludedTag(String tag) {
		whitelist.remove(tag);
	}

	public void addExcludedTag(String tag) {
		blacklist.add(tag);
	}

	public void removeExcludedTag(String tag) {
		blacklist.remove(tag);
	}

	public ArrayList<String> getWhitelist() {
		return whitelist;
	}

	public ArrayList<String> getBlacklist() {
		return blacklist;
	}

	public TaggedFile[] filterTags() {
		ArrayList<TaggedFile> filtered = new ArrayList<TaggedFile>();
		for (TaggedFile file : currentDirectoryFiles) {
			if(file.isFile()){
				if (!whitelist.isEmpty()) {
					for (String tag : getWhitelist()) {
						if (file.hasTag(tag)) {
							filtered.add(file);
							break;
						}
					}
				} else {
					filtered.add(file);
				}
			}
		}

		if (!blacklist.isEmpty()) {
			for (String tag : getBlacklist()) {
				for (TaggedFile file : filtered) {
					if (file.isFile()){
						if (file.hasTag(tag)) {
							filtered.remove(file);
						}
					}
				}
			}
		}

		TaggedFile[] filteredTagged = new TaggedFile[filtered.size()];
		for (int i = 0; i < filteredTagged.length; i++) {
			filteredTagged[i] = new TaggedFile(filtered.get(i).toString());
		}
		return filteredTagged;
	}
	
	public static void main(String[] args) {

		FileBrowser fb = new FileBrowser();
		fb.run();

	}
}
