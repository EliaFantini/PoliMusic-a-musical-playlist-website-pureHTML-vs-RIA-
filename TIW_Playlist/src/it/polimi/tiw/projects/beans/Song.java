package it.polimi.tiw.projects.beans;

import javax.servlet.http.Part;

public class Song {
	private int id;
	private String title;
	private String genre;
	private Part file;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public Part getFile() {
		return file;
	}
	public void setFile(Part file) {
		this.file = file;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
}
