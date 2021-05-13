package it.polimi.tiw.projects.controllers;

import java.io.IOException;
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

import it.polimi.tiw.projects.beans.Playlist;
import it.polimi.tiw.projects.beans.Song;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.ContainmentDAO;
import it.polimi.tiw.projects.dao.PlaylistDAO;
import it.polimi.tiw.projects.dao.SongDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/GetPreviousSongs")
public class GetPreviousSongs extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
	
	public GetPreviousSongs() {
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
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	
		HttpSession session = request.getSession();
		
		// Redirect to the playlistPage
		Integer pageIndex=null;
		Integer playlistID=null;
		try {
			pageIndex = Integer.parseInt(request.getParameter("pageIndex")) - 1;
			playlistID = Integer.parseInt(request.getParameter("playlistID"));
		} catch (NumberFormatException | NullPointerException e) {
			// only for debugging e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect parameters values");
			return;
		}
		
		boolean validPlaylist=false;	
		User user = (User) session.getAttribute("user");
		PlaylistDAO playlistDAO= new PlaylistDAO(connection);
		List<Playlist> playlists;
		try {
			playlists = playlistDAO.findPlaylistByUser(user.getId());
		} catch (SQLException e1) {
			e1.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Incorrect playlist ID value");
			return;
		}
		if(playlists!=null) {
			for(Playlist p: playlists) {
				if(p.getId()==playlistID) {
					validPlaylist=true;
				}
			}
		}
		
		if(!validPlaylist) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect playlist ID value");
			return;
		}
		
		
		ContainmentDAO containmentDAO = new ContainmentDAO(connection);
		List<List<Song>> playlistSongs = new ArrayList<List<Song>>();
		try {
			playlistSongs = containmentDAO.findSongsByPlaylist(playlistID);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover playlist songs");
			return;
		}
		List<Song> allUserSongs = new ArrayList<Song>();
		SongDAO songDAO = new SongDAO(connection);
		try {
			allUserSongs = songDAO.findSongsByUserNotInPLaylist(user.getId(),playlistID);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover user songs");
			return;
		}
		String path = "/WEB-INF/PlaylistPage.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		if(playlistSongs.get(0).isEmpty()) {
			ctx.setVariable("errorMsgPlaylistSongs", "No songs added yet");
		}
		if(allUserSongs.isEmpty()) {
			ctx.setVariable("errorMsgNoMoreSongsToAdd", "No more songs to be added");
		}
		ctx.setVariable("currentSongs", playlistSongs.get(pageIndex));
		ctx.setVariable("pageIndex", pageIndex);
		ctx.setVariable("lastIndex", playlistSongs.size()-1);
		ctx.setVariable("allUserSongs", allUserSongs);
		templateEngine.process(path, ctx, response.getWriter());
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
