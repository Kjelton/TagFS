package fs;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.List;

/**
 * This class extends the java.io.File class. Its purpose is to handle the meta
 * data associated with each file that is handled by the software.
 */

public class TaggedFile extends File {

	public String tagAttrib = "Tag";
	public UserDefinedFileAttributeView userView;

	/**
	 * A constructor. Adds the Tag attribute to the file's meta data if the file
	 * does not have it.
	 * 
	 * @param file
	 *            The string corresponding to the file path.
	 */
	public TaggedFile(String file) {
		super(file);
		if (this.isFile()) {
			userView = Files.getFileAttributeView(this.toPath(), UserDefinedFileAttributeView.class);

			if (!hasAttribute(tagAttrib)) {
				try {
					userView.write(tagAttrib, Charset.defaultCharset().encode(""));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Checks if the file has a given attribute in its meta data.
	 * 
	 * @param attribute
	 *            A string. The string corresponding to a user meta data
	 *            attribute
	 * @return A boolean value representing the existence of the given attribute
	 *         in this file's meta data
	 */
	public boolean hasAttribute(String attribute) {
		try {
			List<String> attribList = userView.list();
			for (String att : attribList) {
				if (att.equals(attribute)) {
					return true;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Checks if the file has a given tag in its tag meta data.
	 * 
	 * @param tagValue
	 *            A string corresponding to the tag value.
	 * @return A boolean value representing the existence of the given tag.
	 */
	public boolean hasTag(String tagValue) {
		String[] tagList = readTagList();

		for (String tag : tagList) {
			if (tag.equals(tagValue)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds the specified tag to the file's tag meta data
	 * 
	 * @param tagValue
	 *            The string containing the value of the tag
	 */
	public void addTag(String tagValue) {

		if (!hasTag(tagValue)) {
			String value = getTags() + tagValue + ";";
			try {
				userView.write(tagAttrib, Charset.defaultCharset().encode(value));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Removes the specified tag from the file's tag meta data
	 * 
	 * @param tagValue
	 *            The string containing the tag value to be removed.
	 */
	public void removeTag(String tagValue) {
		String value = "";
		if (hasTag(tagValue)) {
			for (String tag : readTagList()) {
				if (!tag.equals(tagValue)) {
					value += tag + ";";
				}
			}
			try {
				userView.write(tagAttrib, Charset.defaultCharset().encode(value));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Reads the tags from the file's tag meta data.
	 * 
	 * @return an array containing all the tags in the file's meta data.
	 */
	public String[] readTagList() {
		ByteBuffer bb;
		try {
			bb = ByteBuffer.allocate(userView.size(tagAttrib));
			userView.read(tagAttrib, bb);
			bb.flip();
			String value = Charset.defaultCharset().decode(bb).toString();

			String[] tagList = value.split(";");
			return tagList;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Reads the tag attribute field from the file.
	 * 
	 * @return a string containing the value of the tag attribute in meta data.
	 */
	public String getTags() {
		ByteBuffer bb;
		if (this.isFile()) {
			try {
				bb = ByteBuffer.allocate(userView.size(tagAttrib));
				userView.read(tagAttrib, bb);
				bb.flip();
				String value = Charset.defaultCharset().decode(bb).toString();
				if (value.equals("")) {
					return "";
				}
				return value;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "Folder";
	}

	/**
	 * Writes a null value to the tag attribute field in meta data
	 */
	public void clearTags() {
		try {
			userView.write(tagAttrib, Charset.defaultCharset().encode(""));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Creates a TaggedFile object for each file within this one, if this object
	 * is a folder.
	 * 
	 * @return an array containing all TaggedFiles corresponding to files
	 *         contained in this folder
	 */
	public TaggedFile[] listTaggedFiles() {
		File[] files = this.listFiles();
		TaggedFile[] taggedFiles = new TaggedFile[files.length];
		for (int i = 0; i < files.length; i++) {
			taggedFiles[i] = new TaggedFile(files[i].toString());
		}
		return taggedFiles;
	}
}
