<%--
    Copyright (C) 2009 Martin Günther (mintar@gmx.de)
                  2009 Stephan Schiffel (stephan.schiffel@gmx.de)

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
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<jsp:useBean id="pager" class="tud.ggpserver.formhandlers.ShowTournaments" scope="page">
	<c:catch>
		<jsp:setProperty name="pager" property="page"/>
	</c:catch>
</jsp:useBean>

<c:set var="title">Tournaments</c:set>
<jsp:directive.include file="/inc/pager_header.jsp" />
<jsp:directive.include file="/inc/pager.jsp" />

	<table>
		<thead>
			<tr>
				<th>tournament</th>
				<th>owner</th>
				<th colspan="3">actions</th>
			</tr>
		</thead>
		<tbody>
	      <c:forEach var="tournament" items="${pager.tournaments}" varStatus="lineInfo">
	      	 <c:choose>
			   <c:when test="${lineInfo.count % 2 == 0}">
			     <c:set var="rowClass" value="even" />
			   </c:when> 
			   <c:otherwise>
			     <c:set var="rowClass" value="odd" />
			   </c:otherwise>
			 </c:choose> 
		     <tr class="${rowClass}">
				<td>
				    <c:out value="${tournament.tournamentID}" />
				</td>
				<td>
					<c:url value="view_user.jsp" var="viewUserURL">
						<c:param name="userName" value="${tournament.owner.userName}" />
					</c:url>
					<a href='<c:out value="${viewUserURL}" />'><c:out value="${tournament.owner.userName}" /></a>
				</td>
				<td>
					<c:url value="view_tournament.jsp" var="viewTournamentURL">
						<c:param name="tournamentID" value="${tournament.tournamentID}" />
					</c:url>
					<a href='<c:out value="${viewTournamentURL}" />'>leader board</a>
				</td>
				<td>
					<c:url value="show_matches.jsp" var="showMatchesURL">
						<c:param name="tournamentID" value="${tournament.tournamentID}" />
					</c:url>
					<a href='<c:out value="${showMatchesURL}" />'>show matches</a>
				</td>
				<td>
					<c:if test="${tournament.numberOfMatches < 1000}">
						<c:url value="/servlet/ExportXML" var="exportXMLURL">
							<c:param name="tournamentID" value="${tournament.tournamentID}" />
						</c:url>
						<a href='<c:out value="${exportXMLURL}" />'>export xml</a>
					</c:if>
				</td>
			</tr>
	      </c:forEach>
		</tbody>
	</table>
	<jsp:directive.include file="/inc/download_stylesheets_text.jsp" />

<jsp:directive.include file="/inc/pager.jsp" />
<jsp:directive.include file="/inc/footer.jsp" />