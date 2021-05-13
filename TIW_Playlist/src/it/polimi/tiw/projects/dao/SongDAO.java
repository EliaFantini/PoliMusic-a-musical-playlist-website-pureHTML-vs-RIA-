package it.polimi.tiw.projects.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Song;

public class SongDAO {
	private Connection connection;

	public SongDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<Song> findSongsByUserNotInPLaylist(int userId, int playlistID) throws SQLException {
		List<Song> userSongs = new ArrayList<Song>();

		String query = "SELECT * from song where owner_ID = ? AND song_ID NOT IN ( SELECT song_ID from containment where playlist_ID = ? )";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, userId);
			pstatement.setInt(2, playlistID);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Song song = new Song();
					song.setSongID(result.getInt("song_ID"));
					song.setSongTitle(result.getString("song_title"));
					song.setAlbumID(result.getInt("album"));
					song.setGenre(result.getString("genre"));
					song.setFilePath(result.getString("file"));
					song.setOwnerID(userId);
					userSongs.add(song);
				}
			}
		}
		return userSongs;
	}
	
	public List<Song> findSongsByUserID(int userId) throws SQLException {
		List<Song> userSongs = new ArrayList<Song>();

		String query = "SELECT * from song where owner_ID = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, userId);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Song song = new Song();
					song.setSongID(result.getInt("song_ID"));
					song.setSongTitle(result.getString("song_title"));
					song.setAlbumID(result.getInt("album"));
					song.setGenre(result.getString("genre"));
					song.setFilePath(result.getString("file"));
					song.setOwnerID(userId);
					userSongs.add(song);
				}
			}
		}
		return userSongs;
	}
	
	public void createNewSong(String title,int albumId,String genre, int userId, String filePath) throws SQLException, IOException { // ..,file)
		String query = "INSERT into song (song_title, album, genre, file, owner_ID) VALUES(?, ?, ?, ?, ?)"; 
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, title);
			pstatement.setInt(2, albumId);
			pstatement.setString(3, genre);
			pstatement.setString(4, filePath);
			pstatement.setInt(5, userId);
			pstatement.executeUpdate();
		}
	}
	
	public Song findSongById(int songID) throws SQLException{
		String query = "SELECT * from song where song_ID = ?";
		Song song = null;
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, songID);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					song = new Song();
					song.setSongID(songID);
					song.setSongTitle(result.getString("song_title"));
					song.setAlbumID(result.getInt("album"));
					song.setGenre(result.getString("genre"));
					song.setFilePath(result.getString("file"));
					song.setOwnerID(result.getInt("owner_ID"));
				}
			}
		}
		return song;
		
	}
	
	
}
