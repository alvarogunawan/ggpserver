<%--
    Copyright (C) 2009 Martin Günther (mintar@gmx.de)
                  2010 Peter Steinke (peter.steinke@inf.tu-dresden.de)
                  2010 Stephan Schiffel (stephan.schiffel@gmx.de)

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
<jsp:useBean id="editMatchFilter" class="tud.ggpserver.formhandlers.EditMatchFilter" scope="page"/>
<jsp:useBean id="filterSet" class="tud.ggpserver.filter.FilterSet" scope="session"/>
<c:catch>
	<jsp:setProperty name="editMatchFilter" property="filterSet" value="${filterSet}"/> 
</c:catch>
<%
	// editMatchFilter.setApplyFilter(Boolean.valueOf((String)request.getParameter("applyFilter")));
	// parseParameterMap must be called after setProperty otherwise it won't be able to override the properties
	editMatchFilter.parseParameterMap(request.getParameterMap());
%>
