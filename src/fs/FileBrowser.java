package fs;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class FileBrowser {

	private Path defaultDirectory = Paths.get("E:/Pictures/Toga Himiko");
	private Path currentDirectory = defaultDirectory;
	private boolean running;

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

		for (File file : listOfFiles) {
			System.out.println(file.getName());
		}
		System.out.println(Integer.toString(listOfFiles.length) + " files found.");
	}

	public void run() {
		String option = "";
		Scanner reader = new Scanner(System.in);
		String command = "";
		while (this.isRunning()) {
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
			default:

			}
		}
	}

	public boolean isRunning() {
		return running;
	}

	public static void main(String[] args) {

		FileBrowser fb = new FileBrowser();
		fb.run();

	}
}
