package it.polimi.tiw.projects.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Part;

import it.polimi.tiw.projects.beans.Song;

public class SongDAO {
	private Connection connection;

	public SongDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<Song> findSongsByUser(int userId) throws SQLException {
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
					Part file = (Part) result.getBlob("file");
					song.setFile(file);
					song.setOwner_ID(userId);
					userSongs.add(song);
				}
			}
		}
		return userSongs;
	}
	
	public void createNewSong(String title,int albumId,String genre, int userId, Part file) throws SQLException, IOException { // ..,file)
		String query = "INSERT into song (song_title, album, genre, file, owner_ID) VALUES(?, ?, ?, ?, ?)"; 
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, title);
			pstatement.setInt(2, albumId);
			pstatement.setString(3, genre);
			InputStream inputStream = file.getInputStream();
			if (inputStream != null) {
                pstatement.setBlob(4, inputStream);
            } else {
                pstatement.setNull(4, java.sql.Types.BLOB);
            }
			pstatement.setInt(5, userId);
			pstatement.executeUpdate();
		}
	}
	
}
