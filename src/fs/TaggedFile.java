package fs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.ArrayList;
import java.util.List;

public class TaggedFile {
	
	public String tagAttrib = "Tag";
	public ArrayList<String> originList = new ArrayList<String>(); 
	public UserDefinedFileAttributeView userView;
	public Path file;
	
	public TaggedFile(Path file){
		userView = Files.getFileAttributeView(file, UserDefinedFileAttributeView.class);
		this.file = file;
		
		if(!hasAttribute(tagAttrib)){
			try {
				userView.write(tagAttrib, Charset.defaultCharset().encode(""));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public boolean hasAttribute(String attribute){
		try {
			List<String> attribList = userView.list();
			for(String att : attribList){
				if (att.equals(attribute)){
					return true;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean hasTag(String tagValue){
		String[] tagList = readTagList();
	
		for(String tag : tagList){
			if (tag.equals(tagValue)){
				return true;
			}
		}
		return false;
	}
	
	public void addTag(String tagValue){
		
		if (!hasTag(tagValue)){
			String value = readTags() + tagValue + ";";
			try {
				userView.write(tagAttrib, Charset.defaultCharset().encode(value));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String [] readTagList(){
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
	
	public String readTags(){
		ByteBuffer bb;
		try {
			bb = ByteBuffer.allocate(userView.size(tagAttrib));
			userView.read(tagAttrib, bb);
			bb.flip();
			String value = Charset.defaultCharset().decode(bb).toString();
			return value;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void clearTags(){
		
	}
	
	public static void main(String args[]){
		Path file = Paths.get("E:/Pictures/Toga Himiko/this.jpg");
		TaggedFile newFile = new TaggedFile(file);
		System.out.println(newFile.readTags());
		newFile.addTag("blonde");
		System.out.println(newFile.readTags());
	}
}
