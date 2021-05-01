package it.polimi.tiw.projects.beans;
import java.awt.Image;

public class Album {
	private Integer id;
	private String title;
	private String interpreter;
	private Integer publicationYear;
	private Image cover;
	
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
	public Integer getPublicationYear() {
		return publicationYear;
	}
	public void setPublicationYear(int publicationYear) {
		this.publicationYear = publicationYear;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Image getCover() {
		return cover;
	}
	public void setCover(Image cover) {
		this.cover = cover;
	}
	
}