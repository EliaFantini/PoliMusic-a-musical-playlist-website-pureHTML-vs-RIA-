package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Playlist;

public class PlaylistDAO {
	private Connection connection;

	public PlaylistDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<Playlist> findPlaylistByUser(int userId) throws SQLException {
		List<Playlist> playlists = new ArrayList<Playlist>();

		String query = "SELECT * from playlist where creator = ? ORDER BY creation_date DESC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, userId);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Playlist playlist = new Playlist();
					playlist.setId(result.getInt("playlist_ID"));
					playlist.setPlaylistName(result.getString("playlist_name"));
					playlist.setCreationDate(result.getDate("creation_date"));
					playlist.setCreator(userId);
					playlists.add(playlist);
				}
			}
		}
		return playlists;
	}
	
	public void createNewPlaylist(String playlistName, Date creationDate, int userId) throws SQLException { // ..,cover)
		String query = "INSERT into playlist (playlist_name, creator, creation_date) VALUES(?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, playlistName);
			pstatement.setInt(2, userId);
			pstatement.setDate(3,creationDate);
			pstatement.executeUpdate();
		}
	}
	
}
