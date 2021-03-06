<%--
    Copyright (C) 2009 Martin Günther (mintar@gmx.de)

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

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<jsp:useBean id="createPlayer" class="tud.ggpserver.formhandlers.CreatePlayer" scope="request">
	<c:catch>
		<jsp:setProperty name="createPlayer" property="playerName"/>
		<jsp:setProperty name="createPlayer" property="userName" value="<%= request.getUserPrincipal().getName() %>"/>
	</c:catch>
</jsp:useBean>

<%
	response.setHeader("Cache-Control","private");
	response.setHeader("Pragma","no-cache");
%>

<c:set var="title">Create Player</c:set>
<jsp:directive.include file="/inc/header.jsp" />

	<form action="<%= request.getContextPath() + response.encodeURL("/members/process_player.jsp") %>" method="post">
	<table cellpadding="4" cellspacing="2" border="0">
		<tr>
			<td valign="top" align="right">
				Player Name
			</td>
			<td>
				<input type="text" name="playerName" size="20" value="${createPlayer.playerName}" maxlength="20"> <br>
				<c:if test="<%= createPlayer.getErrors().size() > 0 %>">
					<ul>
				    	<c:forEach var="errormessage" items="${createPlayer.errors}">
							<li class="validationerror">${errormessage}</li>
				    	</c:forEach>
		      		</ul>
	      		</c:if>
			</td>
		</tr>
		<tr>
			<td colspan="2" align="center">
				<input type="submit" value="Submit">
				<input type="reset" value="Reset"> 
			</td>
		</tr>
	</table>
	</form>

<jsp:directive.include file="/inc/footer.jsp" />