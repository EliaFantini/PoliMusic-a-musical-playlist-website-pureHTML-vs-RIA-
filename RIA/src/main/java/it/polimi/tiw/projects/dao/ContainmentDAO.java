package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Album;
import it.polimi.tiw.projects.beans.Song;

public class ContainmentDAO {
	private Connection connection;

	public ContainmentDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<List<Song>> findSongsByPlaylist(int playlist_ID) throws SQLException {
		List<List<Song>> allSongs = new ArrayList<List<Song>>();
		List<Song> currentSongs = new ArrayList<Song>();
		String query = "SELECT * "
					+ "FROM song AS s "
							+ "INNER JOIN album AS a ON a.album_ID = s.album_ID "
							+ "INNER JOIN containment AS c ON c.song_ID = s.song_ID "
					+ "WHERE c.playlist_ID = ? "
					+ "ORDER BY c.ordinal DESC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, playlist_ID);
			try (ResultSet result = pstatement.executeQuery();) {
				int i = 5;
				while (result.next()) {
					if (i == 5) {
						currentSongs = new ArrayList<Song>();
						allSongs.add(currentSongs);
						i = 0;
					}
					Song song = new Song();
					song.setSongID(result.getInt("s.song_ID"));
					song.setSongTitle(result.getString("s.song_title"));
					song.setGenre(result.getString("s.genre"));
					song.setFilePath(result.getString("s.file_path"));
					Album album = new Album();
					album.setAlbumTitle(result.getString("a.album_title"));
					album.setInterpreter(result.getString("a.interpreter"));
					album.setImagePath(result.getString("a.image_path"));
					album.setPublicationYear(result.getInt("a.publication_year"));
					song.setAlbum(album);
					currentSongs.add(song);
					i++;
				}
			}
		}
		return allSongs;
	}
	
	public void createNewContainment(int song_ID, int playlist_ID, int ordinal) throws SQLException {
		String query = "INSERT into containment (song_ID, playlist_ID, ordinal) VALUES(?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, song_ID);
			pstatement.setInt(2, playlist_ID);
			pstatement.setInt(3, ordinal);
			pstatement.executeUpdate();
		}
	}
	
	public void changeSongOrdinal(int ordinal, int songID, int playlistID) throws SQLException {
		String query = "UPDATE containment SET ordinal = ? WHERE playlist_ID = ? AND song_ID = ? ";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, ordinal);
			pstatement.setInt(2, playlistID);
			pstatement.setInt(3, songID);
			pstatement.executeUpdate();
		}
	}
}
