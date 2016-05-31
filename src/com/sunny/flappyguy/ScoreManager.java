package com.sunny.flappyguy;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ScoreManager {

	public static int GetBestScore(Context context){
		SharedPreferences prefs = context.getSharedPreferences(
				"com.sunny.flappyguy.score", Context.MODE_PRIVATE);

		return prefs.getInt("bestscore", 0);
	}

	public static void SetBestScore(Context context, int newScore){

		if(newScore > GetBestScore(context)){

			SharedPreferences prefs = context.getSharedPreferences(
					"com.sunny.flappyguy.score", Context.MODE_PRIVATE);

			Editor editor = prefs.edit();
			editor.putInt("bestscore", newScore);
			editor.commit();
		}
	}
	
	public static int GetLevel( Context context ){
		
		SharedPreferences prefs = context.getSharedPreferences(
				"com.sunny.flappyguy.level", Context.MODE_PRIVATE);

		return prefs.getInt("level", 1);
		
	}
	public static void setLevel(Context context , int level ){


			SharedPreferences prefs = context.getSharedPreferences(
					"com.sunny.flappyguy.level", Context.MODE_PRIVATE);

			Editor editor = prefs.edit();
			editor.putInt("level", level );
			editor.commit();

	}
	
}
