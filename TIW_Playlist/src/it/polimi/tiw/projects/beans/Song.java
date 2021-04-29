package it.polimi.tiw.projects.beans;

import javax.servlet.http.Part;

public class Song {
	private int songID;
	private String songTitle;
	private int albumID;
	private String genre;
	private Part file;
	private int owner_ID;
	
	public int getSongID() {
		return songID;
	}
	public void setSongID(int songID) {
		this.songID = songID;
	}
	public String getSongTitle() {
		return songTitle;
	}
	public void setSongTitle(String songTitle) {
		this.songTitle = songTitle;
	}
	public int getAlbumID() {
		return albumID;
	}
	public void setAlbumID(int albumID) {
		this.albumID = albumID;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	
	public int getOwner_ID() {
		return owner_ID;
	}
	public void setOwner_ID(int owner_ID) {
		this.owner_ID = owner_ID;
	}
	public Part getFile() {
		return file;
	}
	public void setFile(Part file) {
		this.file = file;
	}
	
	

}
