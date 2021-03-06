/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de> 
                  2010 Stephan Schiffel <stephan.schiffel@gmx.de>

    This file is part of GGP Server.

    GGP Server is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GGP Server is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GGP Server.  If not, see <http://www.gnu.org/licenses/>.
*/

package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import tud.gamecontroller.auxiliary.Pair;
import tud.ggpserver.datamodel.DBConnectorFactory;

public abstract class AbstractPager {
	private int startRow;
	private int numDisplayedRows = 30;
	private int maxNumDisplayedLinks = 21; // numbers with maxNumDisplayedLinks % 4 == 1 work best here
	
	public AbstractPager() {
		super();
	}

	protected int getNumDisplayedRows() {
		return numDisplayedRows;
	}

	protected void setNumDisplayedRows(int numDisplayedRows) {
		this.numDisplayedRows = numDisplayedRows;
	}

	protected int getMaxNumDisplayedLinks() {
		return maxNumDisplayedLinks;
	}

	protected void setMaxNumDisplayedLinks(int maxNumDisplayedLinks) {
		this.maxNumDisplayedLinks = maxNumDisplayedLinks;
	}

	/**
	 * Needs to be public (called from pager.jsp)
	 * @throws SQLException 
	 */
	public int getStartRow() {
		return startRow;
	}

	protected void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public int getEndRow() throws SQLException {
		int numReallyDisplayedRows;
		
		if (getPage() == getNumberOfPages()) {   // last page
			numReallyDisplayedRows = getRowCount() - (getNumberOfPages() - 1) * getNumDisplayedRows();
		} else {   // not last page
			numReallyDisplayedRows = getNumDisplayedRows();
		}
		return getStartRow() + numReallyDisplayedRows - 1;
		
	}

	public int getNumberOfPages() throws SQLException {
		return (getRowCount() - 1) / getNumDisplayedRows() + 1;
	}

	public List<LinkedPage> getLinkedPages() throws SQLException {
		final int numPages = getNumberOfPages();
		
		if(numPages <= getMaxNumDisplayedLinks()){
			// show links to all pages
			ArrayList<LinkedPage> result = new ArrayList<LinkedPage>(numPages);
			for(int i=0; i<numPages; i++){
				result.add(getLinkedPage(i+1));
			}
			return result;
		}
		
		// Show links to pages
		//		1 to n/4,
		//		getPage()-n/4 to getPage()+n/4,
		//		numPages-n/4 to numPages,
		// where n is the maximum number of displayed links.
		// That means we use 1/4th of the links for pages 1,2,...,
		// half of the links for pages around the current one and
		// 1/4th of the links for the last pages.
		// If the regions overlap we move the middle part in the other direction so we always get exactly maxNumDisplayedLinks. 
		LinkedList<LinkedPage> result=new LinkedList<LinkedPage>();
		
		int lastPageStart = (getMaxNumDisplayedLinks() - 1) / 4;
		int firstPageEnd = numPages + 1 - getMaxNumDisplayedLinks() / 4;
		int numMiddlePages = getMaxNumDisplayedLinks() - lastPageStart - (numPages + 1 - firstPageEnd); 
		int firstPageMiddle = getPage() - (numMiddlePages - 1) / 2;
		int lastPageMiddle = firstPageMiddle + numMiddlePages - 1;
		if(lastPageStart >= firstPageMiddle){
			// move the middle part to the right
			lastPageMiddle += lastPageStart - firstPageMiddle + 1;	
			firstPageMiddle = lastPageStart + 1;
		}
		if(firstPageEnd <= lastPageMiddle){
			// move the middle part to the left
			firstPageMiddle -= lastPageMiddle - firstPageEnd + 1;
			lastPageMiddle = firstPageEnd - 1;
			if(firstPageMiddle <= lastPageStart) {
				// actually this shouldn't happen because it means that numPages>maxNumDisplayedLinks
				firstPageMiddle = lastPageStart + 1;
			}
		}
		int i;
		for(i=1; i<=lastPageStart; i++){
			result.add(getLinkedPage(i));	
		}
		for(i=firstPageMiddle; i<=lastPageMiddle; i++){
			result.add(getLinkedPage(i));	
		}
		for(i=firstPageEnd; i<=numPages; i++){
			result.add(getLinkedPage(i));	
		}
		return result;
	}

	protected LinkedPage getLinkedPage(int i) throws SQLException {
		return new LinkedPage(i, getTitleOfPage(i));
	}

	public LinkedPage getNextPage() throws SQLException {
		int nextPage = getPage() + 1;
		if (nextPage <= getNumberOfPages())
			return getLinkedPage(nextPage);
		else
			return null;
	}

	public LinkedPage getPreviousPage() throws SQLException {
		int previousPage = getPage() - 1;
		if (previousPage > 0)
			return getLinkedPage(previousPage);
		else
			return null;
	}

	/**
	 * Override this method to return a title for each page (shown as a tool-tip on the link to the page).
	 * @param pageNumber
	 * @return the title
	 */
	protected String getTitleOfPage(int pageNumber) throws SQLException {
		return null;
	}

	protected int getRowCount() throws SQLException {
		return DBConnectorFactory.getDBConnector().getRowCount(getTableName());
	}

	public String getPageTitle() throws SQLException {
		return getTitleOfPage(getPage());
	}

	public int getPage() {
		return (getStartRow() / getNumDisplayedRows()) + 1;
	}

	/**
	 * starts with 1
	 */
	public void setPage(int page) throws SQLException {
		if (page < 1 || page > getNumberOfPages()) {
			page = getNumberOfPages();
			// throw new IllegalArgumentException("Page number < 1 or > number of pages.");
		}
		this.setStartRow(calcStartRowFromPage(page));
	}

	/**
	 * @return the correct start row such that the given page number is displayed
	 */
	protected int calcStartRowFromPage(int page) {
		return (page - 1) * getNumDisplayedRows();
	}
	
	/**
	 * This is the URL of the page that the result pages will link to (without the "page" parameter).
	 */
	public abstract String getTargetJsp();

	/**
	 * This String is used for two things:<br>
	 * 1. The name to be used in the pager title (see inc/pager_title.jsp)<br>
	 * 2. The database table to be used for calculating the row count. If a
	 *    subclass doesn't simply display all entries from one table, a more
	 *    complex query must be used for calculating the row count. This can be
	 *    achieved by overriding getRowCount().
	 */
	public abstract String getTableName();
	
	public static class LinkedPage extends Pair<Integer, String> {

		public LinkedPage(int pageNumber, String pageTitle) {
			super(pageNumber, pageTitle);
		}
		
		public String getTitle() {
			return getRight();
		}
		
		public int getNumber() {
			return getLeft();
		}
	}
}