package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlaylistDAO {
	private Connection connection;

	public PlaylistDAO(Connection connection) {
		this.connection = connection;
	}
	public void createNewPlaylist(String playlistName,Date creationDate, int userId) throws SQLException { // ..,cover)
		String query = "INSERT into playlist (name, creation_date, creator) VALUES(?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, playlistName);
			pstatement.setDate(2,creationDate);
			pstatement.setInt(3, userId);
			pstatement.executeUpdate();
		}
	}
	
}
