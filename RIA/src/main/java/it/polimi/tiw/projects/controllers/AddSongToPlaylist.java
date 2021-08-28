package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

import it.polimi.tiw.projects.beans.Song;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.ContainmentDAO;
import it.polimi.tiw.projects.dao.PlaylistDAO;
import it.polimi.tiw.projects.dao.SongDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/AddSongToPlaylist")
@MultipartConfig
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
		Map<String, String> fieldToValue = null;
		try {
			fieldToValue = handleRequest(request);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Error in creating hashmap");
			return;
		}
		
		Integer songID =null;
		Integer playlist_ID=null;
		boolean validPlaylist=false;
		User user = (User) session.getAttribute("user");
		try {
			songID = Integer.parseInt(fieldToValue.get("songToAdd"));
			playlist_ID = Integer.parseInt(fieldToValue.get("playlist"));	
		}catch (NumberFormatException| NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect or missing parameters' values");
			return;
		}
		if (songID==null || playlist_ID==null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing paramters' values");
			return;
		}
		
		
		if(playlist_ID!=null) {
			PlaylistDAO playlistDAO= new PlaylistDAO(connection);
			try {
				validPlaylist = playlistDAO.checkPlaylist(user.getId(), playlist_ID);
			} catch (SQLException e1) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Incorrect playlist ID value");
				return;
			}
		}
		
		if(!validPlaylist) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect playlist ID value");
			return;
		}
		
		SongDAO songDAO= new SongDAO(connection);
		
		if(songID!=null) {
			try {
				if(!songDAO.checkSong(user.getId(), songID)) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().println("Incorrect Song ID value");
					return;
				}
			} catch (SQLException e1) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Incorrect Song ID value");
				return;
			}
		}
		
		Song song = null;
		
		try {
			song = songDAO.findSongById(songID);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover album");
		}
		
		
		ContainmentDAO containmentDAO = new ContainmentDAO(connection);
		try {
			containmentDAO.createNewContainment(songID, playlist_ID, song.getAlbum().getPublicationYear());
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Not possible to add song");
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	private Map<String, String> handleRequest (HttpServletRequest request) throws Exception{
		HashMap<String, String> fieldToValue = new HashMap<>();
		try {
			ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
			List<FileItem> list = upload.parseRequest(new ServletRequestContext(request));
			for (FileItem item: list){
				fieldToValue.put(item.getFieldName(), item.getString());
			}
		} catch (Exception e) {
			throw e;
		}
		return fieldToValue;
	}
	
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
