package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Part;

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

		String query = "SELECT * from song where song_ID in "
				+ "(SELECT song_ID from containment where playlist_ID = ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, playlist_ID);
			try (ResultSet result = pstatement.executeQuery();) {
				int i = 0;
				while (result.next()) {
					if (i == 4) {
						currentSongs = new ArrayList<Song>();
						allSongs.add(currentSongs);
						i = 0;
					}
					Song song = new Song();
					song.setSongID(result.getInt("song_ID"));
					song.setSongTitle(result.getString("song_title"));
					song.setAlbumID(result.getInt("album"));
					song.setGenre(result.getString("genre"));
					song.setFile((Part) result.getBlob("file"));
					currentSongs.add(song);
				}
			}
		}
		return allSongs;
	}
	
	public void createNewContainment(int playlist_ID, int song_ID) throws SQLException {
		String query = "INSERT into containment (song_ID, playlist_ID) VALUES(?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, song_ID);
			pstatement.setInt(2, playlist_ID);
			pstatement.executeUpdate();
		}
	}
}
