package it.polimi.tiw.projects.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.projects.beans.Song;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.ContainmentDAO;
import it.polimi.tiw.projects.dao.PlaylistDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/GetPlaylistSongs")
public class GetPlaylistSongs extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private String appPath;

	public GetPlaylistSongs() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		appPath = getServletContext().getInitParameter("appPath");
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		Integer playlist_ID = null;
		try {
			playlist_ID = Integer.parseInt(request.getParameter("playlistID"));
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to read playlist_ID");
			return;	
		}
		
		PlaylistDAO playlistDAO= new PlaylistDAO(connection);
		boolean ok;
		try {
			ok = playlistDAO.checkPlaylist(user.getId(), playlist_ID);
		} catch (SQLException e1) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Impossible to recover playlist");
			return;
		}
		if(!ok) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Incorrect playlist ID value");
			return;
		}
		
		ContainmentDAO containmentDAO = new ContainmentDAO(connection);
		List<List<Song>> playlistSongs = new ArrayList<List<Song>>();
		try {
			playlistSongs = containmentDAO.findSongsByPlaylist(playlist_ID);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to recover playlist songs");
			return;
		}
		for(List<Song> list : playlistSongs) {
			for (Song s : list) {
				retrieveFile(s.getFilePath(), getServletContext().getRealPath(""), user.getId());
				retrieveFile(s.getAlbum().getImagePath(), getServletContext().getRealPath(""), user.getId());
			}
		}
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(playlistSongs);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		
	}
	
	private void retrieveFile(String relativePath, String serverPath, Integer userId){
		File file = new File(serverPath + relativePath);
		File uploadDir = new File(serverPath + "uploads" + File.separator + userId.toString() + File.separator);
		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}
		if(!file.exists()) {
			try {
				Files.copy(Paths.get(appPath + relativePath), file.toPath());
			} catch(Exception e) {
				System.out.println("Impossible to copy file");
			}
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
