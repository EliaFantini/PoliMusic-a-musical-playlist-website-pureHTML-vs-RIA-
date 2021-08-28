package it.polimi.tiw.projects.controllers;



import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.PlaylistDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/CreateNewPlaylist")
@MultipartConfig
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
		Map<String, String> fieldToValue = null;
		try {
			fieldToValue = handleRequest(request);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Error in creating hashmap");
			return;
		}
		
		boolean isBadRequest = false;
		String playlistName = null;
		try {
			playlistName = StringEscapeUtils.escapeJava(fieldToValue.get("playlist_name"));			
			isBadRequest =  (playlistName==null||playlistName.isEmpty());
		} catch ( NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		if (isBadRequest) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing or incorrect parameter values");
			return;
		}
		Date creationDate= java.sql.Date.valueOf( LocalDate.now());
		User user = (User) session.getAttribute("user");
		PlaylistDAO playlistDAO = new PlaylistDAO(connection);
		try {
			playlistDAO.createNewPlaylist (playlistName, creationDate, user.getId());
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Impossible to create playlist");
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