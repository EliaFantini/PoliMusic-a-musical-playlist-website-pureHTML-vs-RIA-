package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

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
		boolean isBadRequest = false;
		String title = null;
		Part file = null;
		String genre = null;
		Integer albumId = 0;
		String idValue;
		Part idPart;

		idPart = request.getPart("album");
		try (Scanner scanner = new Scanner(idPart.getInputStream())) {
		    idValue = scanner.nextLine();
		    albumId = Integer.parseInt(idValue);
		}
		catch (IOException e) {
			isBadRequest = true;
		}
		
		idPart = request.getPart("song_title");
		try (Scanner scanner = new Scanner(idPart.getInputStream())) {
		    title = scanner.nextLine();
		}
		catch (IOException e) {
			isBadRequest = true;
		}
		
		idPart = request.getPart("genre");
		try (Scanner scanner = new Scanner(idPart.getInputStream())) {
		    genre = scanner.nextLine();
		}
		catch (IOException e) {
			isBadRequest = true;
		}
	
		file = request.getPart("file");
		
		isBadRequest = title.isEmpty() || genre.isEmpty() || albumId<0 ||file==null ;
		if (isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing parameters' values");
			return;
		}

		User user = (User) session.getAttribute("user");
		SongDAO songDAO = new SongDAO(connection);
		try {
			songDAO.createNewSong (title, albumId, genre , user.getId(), file);
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
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

}
