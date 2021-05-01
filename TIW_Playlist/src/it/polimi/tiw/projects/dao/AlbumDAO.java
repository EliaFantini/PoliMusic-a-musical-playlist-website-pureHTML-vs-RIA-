package it.polimi.tiw.projects.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.Part;

import it.polimi.tiw.projects.beans.Album;


public class AlbumDAO {
	private Connection connection;

	public AlbumDAO(Connection connection) {
		this.connection = connection;
	}
	public void createNewAlbum(String title,String interpreter,int publicationYear,  int userId, Part cover) throws SQLException, IOException { // ..,cover)
		String query = "INSERT into album (album_title, interpreter, publication_year, image, user_ID) VALUES(?, ?, ,? ,? ,?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, title);
			pstatement.setString(2, interpreter);
			pstatement.setInt(3, publicationYear);
			pstatement.setInt(4, userId);
			InputStream inputStream = cover.getInputStream();
			if (inputStream != null) {
                pstatement.setBlob(5, inputStream);
            } else {
                pstatement.setNull(5, java.sql.Types.BLOB);
            }			
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
					album.setId(result.getInt("album_ID"));
					album.setInterpreter(result.getString("interpreter"));
					album.setPublicationYear(result.getInt("publication_year"));
					album.setTitle(result.getString("album_title"));
					InputStream cover = result.getBlob("image").getBinaryStream();
					album.setCover(ImageIO.read(cover));
					albumList.add(album);
				}
			}
		}
		return albumList;
	}
	
	public Album findAlbumBySongId(int songId) throws SQLException, IOException {
		Album album = null;
		String query = "SELECT * FROM album A LEFT JOIN song S ON A.id=S.album_id where S.id = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, songId);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					album = new Album();
					album.setId(result.getInt("album_ID"));
					album.setInterpreter(result.getString("interpreter"));
					album.setPublicationYear(result.getInt("publication_year"));
					album.setTitle(result.getString("album_title"));
					InputStream cover = result.getBlob("image").getBinaryStream();
					album.setCover(ImageIO.read(cover));
				}
			}
		}
		return album;
	}
}
