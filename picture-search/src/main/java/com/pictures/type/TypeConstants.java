package com.pictures.type;

public class TypeConstants {
	public final static String UPLOAD_PATH = TypeConstants.class.getResource("/").getPath()
			.substring(1, TypeConstants.class.getResource("/").getPath().indexOf("target")) + "uploads/";
	
	public final static String GOOGLE_MAP_SECRET_ID = "AIzaSyApwaYil8qGnKenNteh1RL5nc0MKfv2ONo";
	
	public final static String MICROSOFT_VISION_SECRET_ID = "59b68485d8244e51b2049ef2dac82652";
	
	public final static String MICROSOFT_FACE_SECRET_ID = "f1f402617d1040cbaf7081644d1b3d64";
	
	public final static String FACE_LIST_ID = "simon_love_echo";
	
	public final static String FACE_LIST_NAME = "FacePredefined";
	
	public final static String PICTURE_SAVED_LOCATION = "C:\\temp\\";
	
	public final static long AN_HOUR = 60 * 60 * 1000;
	
	public final static double CONFIDENCE = 0.85;
}
