<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/chess_board.xsl"/>
	
	<xsl:template name="print_state">
		<xsl:call-template name="print_chess_state">
			<xsl:with-param name="DefaultCellContent">no</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="make_cell_content">
		<xsl:param name="content"/>
		<xsl:param name="piece"/>
		<xsl:param name="background"/>

		<xsl:variable name="piece2">
			<xsl:choose>
				<xsl:when test="$content='W'">xx</xsl:when>
				<xsl:when test="$content='A'">pl</xsl:when>
				<xsl:when test="$content='B'">pd</xsl:when>
				<xsl:when test="$content='O'"/>
			</xsl:choose>
		</xsl:variable>

		<xsl:call-template name="make_chess_img">
			<xsl:with-param name="piece" select="$piece2"/>
			<xsl:with-param name="background" select="$background"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
