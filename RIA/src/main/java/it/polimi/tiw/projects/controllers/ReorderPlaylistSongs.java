package it.polimi.tiw.projects.controllers;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.ContainmentDAO;
import it.polimi.tiw.projects.dao.PlaylistDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/ReorderPlaylistSongs")
@MultipartConfig
public class ReorderPlaylistSongs extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public ReorderPlaylistSongs() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		StringBuilder stringBuilder = new StringBuilder(1000);
	    Scanner scanner = new Scanner(request.getInputStream());
	    while (scanner.hasNextLine()) {
	        stringBuilder.append(scanner.nextLine());
	    }

	    String body = stringBuilder.toString();
	    String[] IDsString = body.split(",");
	    List<Integer> songIDs = new ArrayList<Integer>();

	    for (int i = 0; i < IDsString.length; i++) {
	        try {
	        	songIDs.add(Integer.parseInt(IDsString[i]));
	        } catch (NumberFormatException nfe) {
	            //NOTE: write something here if you need to recover from formatting errors
	        };
	    }  
	    if(songIDs.isEmpty()) {
	    	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect or missing parameters' values");
			return;
	    }
		Integer playlist_ID=songIDs.get(0);
		songIDs.remove(0);
		User user = (User) session.getAttribute("user");		
		if (songIDs.isEmpty() || playlist_ID==null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing paramters' values");
			return;
		}
		
		
		if(playlist_ID!=null) {
			PlaylistDAO playlistDAO= new PlaylistDAO(connection);
			try {
				if(!playlistDAO.checkPlaylist(user.getId(), playlist_ID)) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().println("Incorrect playlist ID value");
					return;
				}
			} catch (SQLException e1) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Incorrect playlist ID value");
				return;
			}
		}
		
		if(!songIDs.isEmpty()) {
			ContainmentDAO containmentDAO= new ContainmentDAO(connection);
			try {
				for(int i=0; i<songIDs.size(); i++ ) {
					containmentDAO.changeSongOrdinal(songIDs.size()-i, songIDs.get(i) , playlist_ID);
				}
			} catch (SQLException e1) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Incorrect Song ID value, unable to reorder");
				return;
			}
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
