package it.polimi.tiw.projects.controllers;


import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;


import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.PlaylistDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/CreateNewPlaylist")
public class CreateNewPlaylist extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CreateNewPlaylist() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		boolean isBadRequest = false;
		String playlistName = null;
		try {
			playlistName = StringEscapeUtils.escapeJava(request.getParameter("playlist_name"));			
			isBadRequest =  playlistName.isEmpty();
		} catch ( NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		if (isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing parameters' values");
			return;
		}
		Date creationDate= java.sql.Date.valueOf( LocalDate.now());
		User user = (User) session.getAttribute("user");
		PlaylistDAO playlistDAO = new PlaylistDAO(connection);
		try {
			playlistDAO.createNewPlaylist (playlistName, creationDate, user.getId());
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossible to create playlist");
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
