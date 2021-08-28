package it.polimi.tiw.projects.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.projects.beans.Song;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.ContainmentDAO;
import it.polimi.tiw.projects.dao.PlaylistDAO;
import it.polimi.tiw.projects.dao.SongDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/PlaylistPage")
public class GoToPlaylistPage extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
	private String appPath;
	
	public GoToPlaylistPage() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		connection = ConnectionHandler.getConnection(getServletContext());
		appPath = getServletContext().getInitParameter("appPath");
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {


		HttpSession session = request.getSession();
		
		Integer playlist_ID = null;
		Integer index = null;
		try {
			playlist_ID = Integer.parseInt(request.getParameter("playlistID"));
			index = Integer.parseInt(request.getParameter("index"));
		} catch (NumberFormatException | NullPointerException e) {
			// only for debugging e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect playlist ID value");
			return;
		}
		User user = (User) session.getAttribute("user");
		PlaylistDAO playlistDAO= new PlaylistDAO(connection);
		try {
			if(!playlistDAO.checkPlaylist(user.getId(), playlist_ID)) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Incorrect playlist ID value");
				return;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Incorrect playlist ID value");
			return;
		}
		

		ContainmentDAO containmentDAO = new ContainmentDAO(connection);
		List<List<Song>> playlistSongs = new ArrayList<List<Song>>();
		try {
			playlistSongs = containmentDAO.getSongsByPlaylist(playlist_ID);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover playlist songs");
			return;
		}
		for(List<Song> list : playlistSongs) {
			for (Song s : list) {
				retrieveFile(s.getFilePath(), getServletContext().getRealPath(""), user.getId());
				retrieveFile(s.getAlbum().getImagePath(), getServletContext().getRealPath(""), user.getId());
			}
		}

		List<Song> allUserSongs = new ArrayList<Song>();
		SongDAO songDAO = new SongDAO(connection);
		try {
			allUserSongs = songDAO.findSongsByUserNotInPLaylist(user.getId(),playlist_ID);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover user songs");
			return;
		}
	
		// Redirect to the playlist page
		
		String path = "/PlaylistPage.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		if(playlistSongs.isEmpty()) {
			ctx.setVariable("errorMsgPlaylistSongs", "No songs added yet");
		}
		
		if(allUserSongs.isEmpty()) {
			ctx.setVariable("errorMsgNoMoreSongsToAdd", "No more songs to be added");
		}
		if(index < playlistSongs.size())
			ctx.setVariable("currentSongs", playlistSongs.get(index));
		else 
			ctx.setVariable("currentSongs", new ArrayList<Song>());
		
		ctx.setVariable("lastIndex", playlistSongs.size()-1);
		ctx.setVariable("pageIndex", index);
		ctx.setVariable("allUserSongs", allUserSongs);
		templateEngine.process(path, ctx, response.getWriter());
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
