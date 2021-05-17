package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Song;

public class ContainmentDAO {
	private Connection connection;

	public ContainmentDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<List<Song>> findSongsByPlaylist(int playlist_ID) throws SQLException {
		List<List<Song>> allSongs = new ArrayList<List<Song>>();
		List<Song> currentSongs = new ArrayList<Song>();
		allSongs.add(currentSongs);

		String query = "SELECT * "
					+ "FROM song "
					+ "WHERE song_ID IN (SELECT song_ID "
									+ "FROM containment "
									+ "WHERE playlist_ID = ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, playlist_ID);
			try (ResultSet result = pstatement.executeQuery();) {
				int i = 0;
				while (result.next()) {
					if (i == 5) {
						currentSongs = new ArrayList<Song>();
						allSongs.add(currentSongs);
						i = 0;
					}
					Song song = new Song();
					song.setSongID(result.getInt("song_ID"));
					song.setSongTitle(result.getString("song_title"));
					song.setAlbumID(result.getInt("album"));
					song.setGenre(result.getString("genre"));
					song.setFilePath(result.getString("file"));
					currentSongs.add(song);
					i++;
				}
			}
		}
		return allSongs;
	}
	
	public void createNewContainment(int song_ID, int playlist_ID) throws SQLException {
		String query = "INSERT into containment (song_ID, playlist_ID) VALUES(?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, song_ID);
			pstatement.setInt(2, playlist_ID);
			pstatement.executeUpdate();
		}
	}
}
