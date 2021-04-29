package it.polimi.tiw.projects.beans;
import javax.servlet.http.Part;

public class Album {
	private int id;
	private String title;
	private String interpreter;
	private int publicationYear;
	private Part cover;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getInterpreter() {
		return interpreter;
	}
	public void setInterpreter(String interpreter) {
		this.interpreter = interpreter;
	}
	public int getPublicationYear() {
		return publicationYear;
	}
	public void setPublicationYear(int publicationYear) {
		this.publicationYear = publicationYear;
	}
	public Part getCover() {
		return cover;
	}
	public void setCover(Part cover) {
		this.cover = cover;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
}
