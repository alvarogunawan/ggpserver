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

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="viewPlayer"
	class="tud.ggpserver.formhandlers.ViewPlayer" scope="page">
	<c:catch>
		<%
		// this is for catching NumberFormatExceptions and the like
		%>
		<jsp:setProperty name="viewPlayer" property="name" />
	</c:catch>
</jsp:useBean>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<jsp:directive.include file="/inc/headincludes.jsp" />
</head>
<body>
<div id="everything"><jsp:directive.include file="/inc/header.jsp" />
<jsp:directive.include file="/inc/navigation.jsp" /> <!-- Content -->
<div id="content">
<div id="ctitle">View player</div>
<%
if (viewPlayer.getPlayer() == null) {
	response.sendError(404, "That player doesn't exist.");
	return;
}
%>

<h1 class="notopborder">Information on player ${viewPlayer.name}</h1>
<table>
	<tbody>
		<tr>
			<td><b>player name</b></td>
			<td><c:out value="${viewPlayer.name}"></c:out></td>
		</tr>
		<tr>
			<td><b>owner</b></td>
			<td>
				<c:url value="view_user.jsp" var="userURL">
					<c:param name="userName" value="${viewPlayer.owner.userName}" />
				</c:url> <a href='<c:out value="${userURL}" />'>${viewPlayer.owner.userName}</a>
			</td>
		</tr>
		<tr>
			<td><b>status</b></td>
			<td><c:out value="${viewPlayer.status}"></c:out></td>
		</tr>
		<tr>
			<td><b>matches</b></td>
			<td>
				<c:url value="show_matches.jsp" var="playerURL">
					<c:param name="playerName" value="${viewPlayer.name}" />
				</c:url>
				<a href='<c:out value="${playerURL}" />'>show matches</a>
			</td>
		</tr>
	</tbody>
</table>

</div>
<!--end div "content"--> <jsp:directive.include file="/inc/footer.jsp" />
</div>
<!-- end div "everything" -->
</body>
</html>
