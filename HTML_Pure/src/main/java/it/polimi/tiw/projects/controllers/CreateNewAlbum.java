package it.polimi.tiw.projects.controllers;


import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
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

import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/CreateNewAlbum")
public class CreateNewAlbum extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private String appPath;

	public CreateNewAlbum() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		appPath = getServletContext().getInitParameter("appPath");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		Map<String, String> fieldToValue = null;
		try {
			fieldToValue = handleRequest(request, user.getId());
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error in creating hashmap");
			return;
		}

		String title = null;
		String interpreter = null;
		Integer publicationYear = null;
		String coverPath = null;

		try {
			publicationYear = Integer.parseInt(fieldToValue.get("year"));
			title = StringEscapeUtils.escapeJava(fieldToValue.get("album_title"));
			interpreter = StringEscapeUtils.escapeJava(fieldToValue.get("interpreter"));
			coverPath= StringEscapeUtils.escapeJava(fieldToValue.get("filePath"));
		} catch (NumberFormatException| NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing parameters' values");
			return;
		}
		if (publicationYear == null || title==null|| interpreter==null|| coverPath==null|| title.isEmpty() || interpreter.isEmpty() || coverPath.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"BadRequest: " + publicationYear + " " + title + " " + interpreter + " " + coverPath);
			return;
		}


		AlbumDAO albumDAO = new AlbumDAO(connection);
		try {
			albumDAO.createNewAlbum (title, interpreter, publicationYear, user.getId(), coverPath );
		} catch (SQLException| IOException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossible to create album");
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