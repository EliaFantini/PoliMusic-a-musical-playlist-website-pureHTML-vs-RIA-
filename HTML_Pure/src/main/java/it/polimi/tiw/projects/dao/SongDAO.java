package it.polimi.tiw.projects.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Album;
import it.polimi.tiw.projects.beans.Song;

public class SongDAO {
	private Connection connection;

	public SongDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<Song> findSongsByUserNotInPLaylist(int userId, int playlistId) throws SQLException {
		List<Song> userSongs = new ArrayList<Song>();

		String query = "SELECT * "
					+ "FROM song AS s INNER JOIN album AS a on s.album_ID = a.album_ID "
					+ "WHERE s.owner_ID = ? AND s.song_ID NOT IN "
					+ "( SELECT song_ID FROM containment WHERE playlist_ID = ? )";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, userId);
			pstatement.setInt(2, playlistId);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Song song = new Song();
					song.setSongID(result.getInt("s.song_ID"));
					song.setSongTitle(result.getString("s.song_title"));
					song.setGenre(result.getString("s.genre"));
					song.setFilePath(result.getString("s.file_path"));
					song.setOwnerID(userId);
					Album album = new Album();
					album.setAlbumTitle(result.getString("a.album_title"));
					album.setInterpreter(result.getString("a.interpreter"));
					album.setImagePath(result.getString("a.image_path"));
					album.setPublicationYear(result.getInt("a.publication_year"));
					song.setAlbum(album);
					userSongs.add(song);
				}
			}
		}
		return userSongs;
	}
	
	public boolean checkSong(int userId, int songId) throws SQLException{
		String query = "SELECT * "
				+ "FROM song "
				+ "WHERE song_ID = ? AND owner_ID = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, songId);
			pstatement.setInt(2, userId);
			try (ResultSet result = pstatement.executeQuery();) {
				return result.next();
			}
		}
	}
	
	
	public void createNewSong(String title,int albumId,String genre, int userId, String filePath) throws SQLException, IOException { // ..,file)
		String query = "INSERT into song (song_title, album_ID, genre, file_path, owner_ID) VALUES(?, ?, ?, ?, ?)"; 
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
		String query = "SELECT * "
					+ "FROM song AS s INNER JOIN album AS a ON s.album_ID = a.album_ID "
					+ "WHERE s.song_ID = ?";
		Song song = null;
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, songID);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					song = new Song();
					song.setSongID(songID);
					song.setSongTitle(result.getString("s.song_title"));
					song.setGenre(result.getString("s.genre"));
					song.setFilePath(result.getString("s.file_path"));
					song.setOwnerID(result.getInt("s.owner_ID"));
					Album album = new Album();
					album.setAlbumTitle(result.getString("a.album_title"));
					album.setInterpreter(result.getString("a.interpreter"));
					album.setImagePath(result.getString("a.image_path"));
					album.setPublicationYear(result.getInt("a.publication_year"));
					song.setAlbum(album);
				}
			}
		}
		return song;
		
	}
	
	
}
