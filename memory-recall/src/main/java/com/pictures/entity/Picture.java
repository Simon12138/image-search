package com.pictures.entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="Picture")
public class Picture {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 40, unique = true)
	private String name;
	
	@Column(nullable = false)
	private Date creationTime;
	
	@Column(nullable = false)
	private double creationHour;
	
	@Transient
	private String creationTimeString;
	
	@Column
	private double longitude;
	
	@Column
	private double latitude;
	
	@Column
	private String location;
	
	@OneToMany(cascade= {CascadeType.ALL},fetch=FetchType.LAZY ,mappedBy="picture")
	private List<PictureFace> faces = new ArrayList<>();
	
	@OneToMany(cascade= {CascadeType.ALL},fetch=FetchType.LAZY ,mappedBy="picture")
	private List<PictureObject> objects = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
	
	public double getCreationHour() {
		return creationHour;
	}

	public void setCreationHour(double creationHour) {
		this.creationHour = creationHour;
	}

	public String getCreationTimeString() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(creationTime);
	}

	public void setCreationTimeString(String creationTimeString) {
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<PictureFace> getFaces() {
		return faces;
	}

	public void setFaces(List<PictureFace> faces) {
		this.faces = faces;
	}
	
	public void addFace(PictureFace face) {
		this.faces.add(face);
		if(!this.equals(face.getPicture())) {
			face.setPicture(this);
		}
	}

	public List<PictureObject> getObjects() {
		return objects;
	}

	public void setObjects(List<PictureObject> objects) {
		this.objects = objects;
	}
	
	public void addObject(PictureObject object) {
		this.objects.add(object);
		if(!this.equals(object.getPicture())) {
			object.setPicture(this);
		}
	}
	
}
