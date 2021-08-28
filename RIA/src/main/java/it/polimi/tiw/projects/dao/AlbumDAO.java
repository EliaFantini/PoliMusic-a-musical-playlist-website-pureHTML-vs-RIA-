package it.polimi.tiw.projects.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Album;


public class AlbumDAO {
	private Connection connection;

	public AlbumDAO(Connection connection) {
		this.connection = connection;
	}
	public void createNewAlbum(String title, String interpreter,int publicationYear,  int userId, String coverPath) throws SQLException, IOException { // ..,cover)
		String query = "INSERT into album (album_title, interpreter, publication_year, image_path, user_ID) VALUES(?, ?, ?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, title);
			pstatement.setString(2, interpreter);
			pstatement.setInt(3, publicationYear);
			pstatement.setString(4, coverPath);
			pstatement.setInt(5, userId);
			pstatement.executeUpdate();
		}
	}
	
	public List<Album> findAlbumByUser(int userID) throws SQLException, IOException{
		List<Album> albumList = new ArrayList<>();
		String query = "SELECT * FROM album where user_ID = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, userID);
			try (ResultSet result = pstatement.executeQuery();) {
				while(result.next()) {
					Album album = new Album();
					album.setAlbumId(result.getInt("album_ID"));
					album.setInterpreter(result.getString("interpreter"));
					album.setPublicationYear(result.getInt("publication_year"));
					album.setAlbumTitle(result.getString("album_title"));
					album.setImagePath("image_path");
					albumList.add(album);
				}
			}
		}
		return albumList;
	}
	
	
	public boolean checkAlbum(int userID, int albumID) throws SQLException{
		String query = "SELECT * "
				+ "FROM album "
				+ "WHERE album_ID = ? AND user_ID = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, albumID);
			pstatement.setInt(2, userID);
			try (ResultSet result = pstatement.executeQuery();) {
				return result.next();
			}
		}
	}


}
