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
<jsp:useBean id="pager" class="tud.ggpserver.formhandlers.ShowPlayers" scope="page">
	<c:catch> <% // this is for catching NumberFormatExceptions and the like %>
		<jsp:setProperty name="pager" property="page"/>
	</c:catch>
</jsp:useBean>

<c:set var="title">Players</c:set>
<jsp:directive.include file="/inc/pager_header.jsp" />
<jsp:directive.include file="/inc/pager.jsp" />

	<table>
		<thead>
			<tr>
				<th>player name</th>
				<th>owner</th>
				<th>status</th>
				<th>available for round robin</th>
				<th>available for manual play</th>
				<th>GDL</th>
				<c:if test='${navigationUserBean.user != null}'>
					<c:if test='<%= navigationUserBean.getUser().hasRole("admin") %>'>
						<th>host</th>
						<th>port</th>
					</c:if>
				</c:if>
			</tr>
		</thead>
		<tbody>
	      <c:forEach var="player" items="${pager.players}" varStatus="lineInfo">
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
					<c:url value="view_player.jsp" var="playerURL">
						<c:param name="name" value="${player.name}" />
					</c:url>
					<a href='<c:out value="${playerURL}" />'>${player.name}</a>
				</td>
				<td>
					<c:url value="view_user.jsp" var="userURL">
						<c:param name="userName" value="${player.owner.userName}" />
					</c:url>
					<a href='<c:out value="${userURL}" />'>${player.owner.userName}</a>
				</td>
				<td><div class="playerstatus-${player.status}"><span>${player.status}</span></div></td>
				<td>${player.availableForRoundRobinMatches}</td>
				<td>${player.availableForManualMatches}</td>
				<td>${player.gdlVersion}</td>
				<c:if test='${navigationUserBean.user != null}'>
					<c:if test='<%= navigationUserBean.getUser().hasRole("admin") %>'>
						<td>${player.host}</td>
						<td>${player.port}</td>
					</c:if>
				</c:if>
			</tr>
	      </c:forEach>
		</tbody>
	</table>
	
<jsp:directive.include file="/inc/pager.jsp" />
<jsp:directive.include file="/inc/footer.jsp" />