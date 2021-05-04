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
import it.polimi.tiw.projects.dao.SongDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/CreateNewSong")
@MultipartConfig
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
		Map<String, String> fieldToValue = null;
		try {
			fieldToValue = handleRequest(request);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error in creating hashmap");
			return;
		}
		
		String title = null;
		String filePath = null;
		String genre = null;
		Integer albumId = null;

		try {
			title = StringEscapeUtils.escapeJava(fieldToValue.get("song_title"));
			genre = StringEscapeUtils.escapeJava(fieldToValue.get("genre"));
			albumId = Integer.parseInt(fieldToValue.get("album"));
			filePath = StringEscapeUtils.escapeJava(fieldToValue.get("songPath"));
		} catch (NumberFormatException| NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing parameters' values");
			return;
		}
		if (title.isEmpty() || genre.isEmpty() || albumId == null || filePath.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
					"BadRequest");
			return;
		}

		User user = (User) session.getAttribute("user");
		SongDAO songDAO = new SongDAO(connection);
		try {
			songDAO.createNewSong (title, albumId, genre , user.getId(), filePath);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossible to create song");
			return;
		} catch (IOException i){
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "IO exception");
			return;
		}

		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/Homepage";
		response.sendRedirect(path);
	}
	
	private Map<String, String> handleRequest(HttpServletRequest request) throws Exception{
		HashMap<String, String> fieldToValue = new HashMap<>();
		File file;
		String songPath = getServletContext().getRealPath("") + File.separator + "uploads" + File.separator;
		File uploadDir = new File(songPath);
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
                    String fileName = item.getName();
                    if (fileName.lastIndexOf('\\') >= 0)
                    	fileName = fileName.substring(fileName.lastIndexOf('\\'));    
                    file = new File(songPath + fileName);
                    item.write(file);
                    fieldToValue.put("songPath", "uploads" + File.separator + fileName);
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
