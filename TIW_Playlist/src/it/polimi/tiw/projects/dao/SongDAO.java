package it.polimi.tiw.projects.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.http.Part;

public class SongDAO {
	private Connection connection;

	public SongDAO(Connection connection) {
		this.connection = connection;
	}
	public void createNewSong(String title,int albumId,String genre, int userId, Part file) throws SQLException, IOException { // ..,file)
		String query = "INSERT into album (title, albumId, genre, userID, file) VALUES(?, ?, ?,? ,?)"; 
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, title);
			pstatement.setInt(2,albumId);
			pstatement.setString(3, genre);
			pstatement.setInt(4, userId);
			InputStream inputStream = file.getInputStream();
			if (inputStream != null) {
                
                pstatement.setBlob(5, inputStream);
            } else {
                pstatement.setNull(5, java.sql.Types.BLOB);
            }		
			pstatement.executeUpdate();
		}
	}
}
