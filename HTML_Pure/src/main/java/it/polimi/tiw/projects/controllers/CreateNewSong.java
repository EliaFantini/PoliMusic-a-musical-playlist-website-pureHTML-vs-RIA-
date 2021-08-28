package it.polimi.tiw.projects.controllers;

import java.io.File;
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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;


import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.AlbumDAO;
import it.polimi.tiw.projects.dao.SongDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/CreateNewSong")
@MultipartConfig
public class CreateNewSong extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private String appPath;

	public CreateNewSong() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		appPath = getServletContext().getInitParameter("appPath");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		Map<String, String> fieldToValue = null;

		User user = (User) session.getAttribute("user");
		try {
			fieldToValue = handleRequest(request, user.getId());
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error in creating hashmap");
			return;
		}

		String title = null;
		String audioPath = null;
		String genre = null;
		Integer albumId = null;

		try {
			title = StringEscapeUtils.escapeJava(fieldToValue.get("song_title"));
			genre = StringEscapeUtils.escapeJava(fieldToValue.get("genre"));
			albumId = Integer.parseInt(fieldToValue.get("album"));
			audioPath = StringEscapeUtils.escapeJava(fieldToValue.get("filePath"));
		} catch (NumberFormatException| NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing parameters' values");
			return;
		}
		if (title.isEmpty() || genre.isEmpty() || albumId == null || audioPath.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
					"BadRequest");
			return;
		}

		AlbumDAO albumDAO=new AlbumDAO(connection);
		try {
			if(!albumDAO.checkAlbum(user.getId(), albumId)) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing parameters' values");
				return;
			}
				
		} catch (SQLException | IOException e1) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing parameters' values");
			return;
		}

		SongDAO songDAO = new SongDAO(connection);
		try {
			songDAO.createNewSong (title, albumId, genre , user.getId(), audioPath);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossible to create song");
			return;
		} catch (IOException i){
			i.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "IO exception");
			return;
		}

		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/Homepage";
		response.sendRedirect(path);

	}

	private Map<String, String> handleRequest (HttpServletRequest request, Integer userId) throws Exception{	
		HashMap<String, String> fieldToValue = new HashMap<>();
		File file;
		String filePath = appPath + "uploads" + File.separator + userId.toString() + File.separator;
		File uploadDir = new File(filePath);
		if(!uploadDir.exists()) {
			uploadDir.mkdirs();
		}
		try {
			ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
			List<FileItem> list = upload.parseRequest(new ServletRequestContext(request));
			for (FileItem item: list){
				if(item.isFormField()) {
					fieldToValue.put(item.getFieldName(), item.getString());
				}
				else {
					String fileName = item.getName().replaceAll("\\s+","");
					if (fileName.lastIndexOf('\\') >= 0)
						fileName = fileName.substring(fileName.lastIndexOf('\\'));    
					file = new File(filePath + fileName);
					item.write(file);
					fieldToValue.put("filePath", "uploads" + File.separator + userId.toString() + File.separator + fileName);
				}
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
