package CRUD;

import java.io.Serializable;

/**
 * @author Avishai
 *
 * @param <T> - the type of the lines in the CRUDFile.
 * @param <ID> - serial numbers of the lines.
 */
public interface CRUD<T extends Serializable, ID extends Serializable> {
	/**
	 * to append a new line to the copy file
	 * @param entry - the line to append
	 * @return the next line in the copy file
	 * @throws Exception if there was an error with the file 
	 */
	public ID create (T entry) throws Exception;
	
	/**
	 * to read a specific line in the copy file
	 * @param entryID - the line to read in the file
	 * @return the string in the given line.
	 * if the line doesn't exist - returns null.
	 * @throws Exception if there was an error with the file 
	 */
	public T read (ID entryID) throws Exception;
	
	/**
	 * to update a specific line in the copy file
	 * @param entryID - the line to update in the file
	 * @param newEntry - the new line to replace with
	 * @return <code>true</code> if there was a line in the given number,
	 * and <code>false</code> if there wasn't a line.
	 * @throws Exception if there was an error with the file 
	 */
	public boolean update(Integer entryID, T newEntry) throws Exception;
	
	/**
	 * to delete a specific line in the copy file
	 * @param entryID - the line to update in the file
	 * @return<code>true</code> if there was a line to delete in the given number,
	 * and <code>false</code> if there wasn't a line.
	 * @throws Exception if there was an error with the file 
	 */
	public boolean delete (ID entryID) throws Exception;
}
