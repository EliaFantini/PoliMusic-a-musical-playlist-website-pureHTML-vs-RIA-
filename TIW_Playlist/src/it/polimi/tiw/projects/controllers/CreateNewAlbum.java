package it.polimi.tiw.projects.controllers;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.http.Part;

import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.AlbumDAO;

import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/CreateNewAlbum")
public class CreateNewAlbum extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CreateNewAlbum() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		boolean isBadRequest = false;
		String title = null;
		String interpreter = null;
		int publicationYear = 0;
		Part cover = null;

		try {
			publicationYear = Integer.parseInt(request.getParameter("year"));
			title = StringEscapeUtils.escapeJava(request.getParameter("album_title"));
			interpreter = StringEscapeUtils.escapeJava(request.getParameter("interpreter"));
			cover= request.getPart("cover");
			isBadRequest =  interpreter.isEmpty() || title.isEmpty() || cover==null;
		} catch (NumberFormatException| NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		if (isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing parameters' values");
			return;
		}

		User user = (User) session.getAttribute("user");
		AlbumDAO albumDAO = new AlbumDAO(connection);
		try {
			albumDAO.createNewAlbum (title, interpreter, publicationYear, user.getId(), cover );
		} catch (SQLException| IOException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossible to create album");
			return;
		}

		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/Homepage";
		response.sendRedirect(path);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

}