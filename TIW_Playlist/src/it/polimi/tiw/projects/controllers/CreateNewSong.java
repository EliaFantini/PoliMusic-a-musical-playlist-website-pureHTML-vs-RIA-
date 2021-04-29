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
import javax.servlet.http.Part;
import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.SongDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/CreateNewSong")
public class CreateNewSong extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CreateNewSong() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		boolean isBadRequest = false;
		String title = null;
		Part file = null;
		String genre = null;
		int albumId = 0;

		try {
			albumId = Integer.parseInt(request.getParameter("album"));
			title = StringEscapeUtils.escapeJava(request.getParameter("song_title"));
			genre = StringEscapeUtils.escapeJava(request.getParameter("genre"));
			file = request.getPart("file");
			isBadRequest = title.isEmpty() || genre.isEmpty() || albumId<0 ||file==null ;
		} catch (NumberFormatException| NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		if (isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing parameters' values");
			return;
		}

		User user = (User) session.getAttribute("user");
		SongDAO songDAO = new SongDAO(connection);
		try {
			songDAO.createNewSong (title, albumId, genre , user.getId(), file);
		} catch (SQLException | IOException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossible to create song");
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

