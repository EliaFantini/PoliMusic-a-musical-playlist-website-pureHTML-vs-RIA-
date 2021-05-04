package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.projects.dao.ContainmentDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/AddSongToPlaylist")
public class AddSongToPlaylist extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public AddSongToPlaylist() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// If the user is not logged in, redirects to login page
		String loginPath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginPath);
			return;
		}
		
		Integer songID = Integer.parseInt(request.getParameter("songToAdd"));
		Integer playlist_ID = Integer.parseInt(request.getParameter("playlist"));

		ServletContext servletContext = getServletContext();
		String ctxpath = servletContext.getContextPath();
		String path = ctxpath + "/WEB-INF/PlaylistPage.html";
		ContainmentDAO containmentDAO = new ContainmentDAO(connection);
		try {
			containmentDAO.createNewContainment(songID, playlist_ID);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to add song");
			return;
		}
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
