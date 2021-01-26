package SourceCode.com.jda.qa.platform.framework.util;


/**
 * This interface needs to implemented by the classes which supports any web table in the application.
 * 
 * 
 * @author Santosh Kumar Medchalam and Vinay Kumar Tangella
 *
 */

public interface TableInterface {

	/**
	 * This method should be implemented such that it loads and stores all the table related details such as 
	 * row count, column count and column names etc.
	 * 
	 */
	public void loadTable();

	/**
	 * This method should be implemented such that it does an appropriate action on the web element 
	 * identified with the cross section of rowNum and column with column name identified using colHdrName
	 * 
	 * @param rowNum   The rownum using which the row number is identified
	 * @param colHdrName The colHdrName using which the column of the table is identified
	 */
	public void doTableAction(int rowNum, String colHdrName);
	
	/**
	 * This method should be implemented such that it does an appropriate action on the web element 
	 * identified with the cross section of primary key rowNum and column with column name identified using colHdrName
	 * 
	 * @param colHdrName   The colHdrName using which the column of the table is identified
	 */
	public void doTableAction(String colHdrName);

	/**
	 * This method should be implemented such this it will return table cell content 
	 * identified with the cross section of rowNum and column with column name identified using colHdrName
	 * 
	 * @param rowNum  The rownum using which the row number is identified
	 * @param colHdrName  The colHdrName using which the column of the table is identified
	 * @return The cell content of the table identified with the cross section of rowNum and column with column name identified using colHdrName
	 */
	
	public String getTableCellContent(int rowNum, String colHdrName);

	/**
	 * 
	 * This method should be implemented such this it will compare the table cell content  
	 * identified with the cross section of rowNum and column with column name identified using colHdrName against the provided colContent 
	 * and return the boolean status
	 * 
	 * @param rowNum The rownum using which the row number is identified
	 * @param colHdrName The colHdrName using which the column of the table is identified
	 * @param colContent The content against which the table cell content is validated with
	 * @return true if the cell content matches the colContent and false if vice-versa
	 * 		   
	 */
	
	public boolean validateTableCellContent(int rowNum, String colHdrName,
                                            String colContent);

	/**
	 * 
	 * This method should be implemented such that it will return the rownum of the table which has value for pkColValue for the primary key column
	 * 
	 * @param pkColValue The pkColValue using which the row in the table is identified using the primary key column 
	 * @return Will return the rownum of the table which has value for pkColValue for the primary key column
	 */
	public int getTableRowNumber(String pkColValue);
	
	/**
	 * This method should be implemented such that it will select the checkbox/radio button 
	 * besides the row with pkColValue for the primary key column
	 * 
	 */
	public void selectTableRow();

	/**
	 * 
	 * This method should be implemented in such a way that it will validate the table row content against the specified row content
	 * 
	 * @return
	 */
	boolean valiadateTableRowContent();

}
