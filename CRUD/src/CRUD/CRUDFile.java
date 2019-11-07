package CRUD;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author avishai
 *
 */
public class CRUDFile implements CRUD<String, Integer> {
	private File copyFile;
	private int line;

	/**
	 * constructor to create the CRUDFile
	 * @param name - the name of the copy file to create.
	 * @throws IOException 
	 */
	public CRUDFile(String name) throws IOException {
//		System.getProperty("user.dir"),
		this.copyFile = new File(name);
		// to create if not exist and open if exist
		this.copyFile.createNewFile();
	}

	@Override
	public Integer create(String entry) throws Exception {
		// try with resources
		try (FileWriter fw = new FileWriter(this.copyFile, true)) {
			fw.write(this.line + " " + entry + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return this.line++;
	}

	@Override
	public String read(Integer entryID) throws Exception {
		String retLine = null;

		try (FileInputStream fis = new FileInputStream(this.copyFile);
			 BufferedReader br = new BufferedReader(new InputStreamReader(fis))) {
			
			while ((retLine = br.readLine()) != null) {
				if (retLine.startsWith(entryID.toString() + " ")) {
					retLine = retLine.substring(retLine.indexOf(" ") + 1);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return retLine;
	}

	@Override
	public boolean update(Integer entryID, String newEntry) throws Exception {
		return updateAndDelete(entryID, entryID + " " + newEntry);
	}

	@Override
	public boolean delete(Integer entryID) throws Exception {
		return updateAndDelete(entryID, "");
	}
	
	/**
	 * help method to update and delete lines in the file.
	 * @param entryID - the line to update or delete.
	 * @param newEntry - the new line to replace in the given place.
	 * if needs to delete the line - the newEntry is "".
	 * @return <code>true</code> for success, and <code>false</code> if there wasn't the given line.
	 */
	private boolean updateAndDelete(Integer entryID, String newEntry) throws Exception {
		boolean isFound = false;
		File temp = new File("temp");
		temp.createNewFile();

		try (FileWriter fw = new FileWriter("temp", true);
			 FileInputStream fis = new FileInputStream(this.copyFile);
			 BufferedReader br = new BufferedReader(new InputStreamReader(fis))) {
			
			String oldEntry = null;

			while ((oldEntry = br.readLine()) != null) {
				if (oldEntry.startsWith(entryID.toString() + " ")) {
					isFound = true;
					fw.write(newEntry + "\n");
				}
				else {
					fw.write(oldEntry + "\n");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// to save the temp file as the copyFile
		temp.renameTo(this.copyFile);

		return isFound;
	}
	
	/**
	 * a wrapper method for the create method
	 * @param entry - the string to insert
	 * @return - null
	 * @throws Exception
	 */
	public Void notifyOnMonitor(String entry) throws Exception {
		create(entry);
		
		return null;
	}
	
	public void startOverFile() throws IOException {
		byte[] empty = new byte[0];
		com.google.common.io.Files.write(empty, this.copyFile);
	}
}
