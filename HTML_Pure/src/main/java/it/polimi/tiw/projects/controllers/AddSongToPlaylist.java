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

import it.polimi.tiw.projects.beans.Song;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.ContainmentDAO;
import it.polimi.tiw.projects.dao.PlaylistDAO;
import it.polimi.tiw.projects.dao.SongDAO;
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

		HttpSession session = request.getSession();
		
		Integer songID =null;
		Integer playlist_ID=null;
		boolean validPlaylist=false;
		User user = (User) session.getAttribute("user");
		try {
			songID = Integer.parseInt(request.getParameter("songToAdd"));
			playlist_ID = Integer.parseInt(request.getParameter("playlist"));
			
		}catch (NumberFormatException| NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing parameters' values");
			return;
		}
		if (songID==null || playlist_ID==null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
					"BadRequest");
			return;
		}
		
		
		if(playlist_ID!=null) {
			PlaylistDAO playlistDAO= new PlaylistDAO(connection);
			try {
				validPlaylist = playlistDAO.checkPlaylist(user.getId(),playlist_ID);
			} catch (SQLException e1) {
				e1.printStackTrace();
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Incorrect playlist ID value");
				return;
			}
		}
		
		if(!validPlaylist) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect playlist ID value");
			return;
		}
		
		
		if(songID!=null) {
			SongDAO songDAO= new SongDAO(connection);
			try {
				if(!songDAO.checkSong(user.getId(), songID)) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect Song ID value");
					return;
				}
			} catch (SQLException e1) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Incorrect Song ID value");
				return;
			}
		}
		
		Song song = null;
		SongDAO songDAO= new SongDAO(connection);
		try {
			song = songDAO.findSongById(songID);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover album");
		}
		
		ContainmentDAO containmentDAO = new ContainmentDAO(connection);
		try {
			containmentDAO.createNewContainment(songID, playlist_ID, song.getAlbum().getPublicationYear());
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to add song");
			return;
		}
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/PlaylistPage?playlistID=" + playlist_ID + "&index=0";
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
