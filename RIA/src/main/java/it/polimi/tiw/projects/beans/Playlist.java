package it.polimi.tiw.projects.beans;

import java.sql.Date;

public class Playlist {
	private String playlistName;
	private Date creationDate;
	private int playlistId;
	private int creatorId;
	
	public int getPlaylistId() {
		return playlistId;
	}
	public void setPlaylistId(int id) {
		this.playlistId = id;
	}
	public int getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}
	public String getPlaylistName() {
		return playlistName;
	}
	public void setPlaylistName(String playlistName) {
		this.playlistName = playlistName;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
}
