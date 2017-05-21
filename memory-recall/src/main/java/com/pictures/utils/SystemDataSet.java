package com.pictures.utils;

import java.util.Arrays;
import java.util.List;

public class SystemDataSet {
	// MAC OS
	// public static String PICTURE_SAVED_LOCATION = "/memory/";
	// public static String AVATAR_SAVED_LOCATION = "/memory/avatar/";
	// Windows OS
	public static String PICTURE_SAVED_LOCATION = "C:\\temp\\";
	public static String AVATAR_SAVED_LOCATION = "C:\\temp\\avatar\\";
	
	public final static String GOOGLE_MAP_SECRET_ID = "AIzaSyApwaYil8qGnKenNteh1RL5nc0MKfv2ONo";
	
	public final static String MICROSOFT_VISION_SECRET_ID = "eed61a82c13847f8b81c129f632fbf08";
	
	public final static String MICROSOFT_FACE_SECRET_ID = "bab529ddc62e49208e7766ef41a9b5c5";
	
	public final static String FACE_LIST_ID = "20131203_3";
	
	public final static String FACE_LIST_NAME = "MEMORYRECALL";
	
	public final static double SIMILAR_FACE_CONFIDENCE_IMPORT_PROCESS = 0.6;
	
	public final static double SIMILAR_FACE_CONFIDENCE_QUERY_PROCESS = 0.5;
	
	public final static double CONFIDENCE = 0.8;
	
	public final static double ZERO = 0.0;
	
	public final static List<String> EXCLUDE_TAGS = Arrays.asList();
	
	public final static List<String> SCREEN_TAGS = Arrays.asList(
			"ELECTRONICS");
}
