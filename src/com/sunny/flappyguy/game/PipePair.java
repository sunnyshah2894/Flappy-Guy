package com.sunny.flappyguy.game;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;


public class PipePair {


	float PIPE_WIDTH = MainActivity.CAMERA_WIDTH * 0.18f;
	float PIPE_HEIGHT = PIPE_WIDTH * 0.46f;

	// upper pipe
	private static TextureRegion mUpperPipeTexture;
	private static TextureRegion mUpperPipeSectionTexture;

	//lower pipe
	private static TextureRegion mLowerPipeTexture;
	private static TextureRegion mLowerPipeSectionTexture;
	private static TextureRegion mLowerPipeSectionTexture2 ;
	private static TextureRegion coinTexture; 

	public static void onCreateResources(SimpleBaseGameActivity activity){

		// upper pipe		
		BitmapTextureAtlas upperPipeTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 130, 80, TextureOptions.DEFAULT);
		mUpperPipeTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(upperPipeTextureAtlas, activity, "pipetop.png", 0, 0);
		upperPipeTextureAtlas.load();

		// upper pipe section	
		BitmapTextureAtlas upperPipeSectionTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 120, 80, TextureOptions.DEFAULT);
		mUpperPipeSectionTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(upperPipeSectionTextureAtlas, activity, "pipesection.png", 0, 0);
		upperPipeSectionTextureAtlas.load();


		// lower pipe		
		BitmapTextureAtlas lowerPipeTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 130, 80, TextureOptions.DEFAULT);
		mLowerPipeTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(lowerPipeTextureAtlas, activity, "pipetop.png", 0, 0);
		lowerPipeTextureAtlas.load();

		// lower pipe section	
		//BitmapTextureAtlas lowerPipeSectionTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 120, 120, TextureOptions.BILINEAR);
		BitmapTextureAtlas lowerPipeSectionTextureAtlas =new BitmapTextureAtlas(activity.getTextureManager(), 120, 80, TextureOptions.DEFAULT);
		mLowerPipeSectionTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(lowerPipeSectionTextureAtlas, activity, "pipesection.png", 0, 0);
		lowerPipeSectionTextureAtlas.load();
		
		BitmapTextureAtlas coinTextureAtlas =new BitmapTextureAtlas(activity.getTextureManager(), 30, 30, TextureOptions.DEFAULT);
		coinTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(coinTextureAtlas, activity, "coin.png", 0, 0);
		coinTextureAtlas.load();
		
//		BitmapTextureAtlas lowerPipeSectionTextureAtlas2 = new BitmapTextureAtlas(activity.getTextureManager(), 120, 1, TextureOptions.BILINEAR);
//		mLowerPipeSectionTexture2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(lowerPipeSectionTextureAtlas2, activity, "pipesectionlower.png", 0, 0);
//		lowerPipeSectionTextureAtlas2.load();
		
	}

	private float mOpeningHeight;
	private float mCurrentPosition;

	private VertexBufferObjectManager mVertexBufferObjectManager;
	private Scene mScene;

	private Sprite mUpperPipe;
	private Sprite mUpperPipeSection;
	private Sprite mLowerPipe;
	private Sprite mLowerPipeSection;
	private Sprite coin; 
	
	private int pipeSectionWidths[] = { 30 , 40 , 30 , 60 , 60 , 90 , 90 , 50  } ;
	private int pipeWidths[] = { 90 , 300 , 120 ,  120 , 180 ,  180 , 210 , 150 } ;
	private int pipediffs[] = { 30 , 130 , 45 , 30 , 60 , 45 , 60 , 50 } ;
	
	
	
	
	
	private static float PIPE_Y_OFFSET = MainActivity.CAMERA_WIDTH + 200; // make sure they always spawn way off screen
	
	public PipePair(int mOpeningHeight,
			VertexBufferObjectManager mVertexBufferObjectManager, Scene mScene) {
		super();
		
		this.mOpeningHeight = mOpeningHeight;
		this.mVertexBufferObjectManager = mVertexBufferObjectManager;
		this.mScene = mScene;
		PIPE_Y_OFFSET = MainActivity.CAMERA_WIDTH - 50 + (int)(Math.random() * ((200 - 50 ) + 1));	
		
		int rand_number = (int)( Math.random() * 7 ) ;
		int min_offset = 100 + (int)(Math.random() * ((200 - 100 ) + 1));
		int coin_height = 30 + (int)( Math.random() * 800 ) ;
		
		// upper pipe
		
		mUpperPipeSection = new Sprite(PIPE_Y_OFFSET + pipediffs[rand_number], 0, pipeSectionWidths[rand_number], mOpeningHeight-145, mUpperPipeSectionTexture, mVertexBufferObjectManager);
		mUpperPipeSection.setZIndex(1);
		mScene.attachChild(mUpperPipeSection);
		
		
		mUpperPipe = new Sprite(PIPE_Y_OFFSET, mOpeningHeight-145, pipeWidths[rand_number], 20, mUpperPipeTexture, mVertexBufferObjectManager);
		mUpperPipe.setZIndex(1);
		mScene.attachChild(mUpperPipe);
		

		//lower pipe		
		mLowerPipe = new Sprite(PIPE_Y_OFFSET, mOpeningHeight+125 , pipeWidths[rand_number], 20, mLowerPipeTexture, mVertexBufferObjectManager);
		mLowerPipe.setZIndex(1);
		mScene.attachChild(mLowerPipe);

	
		mLowerPipeSection = new Sprite(PIPE_Y_OFFSET + pipediffs[rand_number] , mOpeningHeight+145, pipeSectionWidths[rand_number], MainActivity.FLOOR_BOUND - mOpeningHeight - 145  , mLowerPipeSectionTexture, mVertexBufferObjectManager);
		mLowerPipeSection.setZIndex(1);
		mScene.attachChild(mLowerPipeSection);
		
		
		coin = new Sprite( PIPE_Y_OFFSET - min_offset , coin_height , 40 , 40 ,  coinTexture, mVertexBufferObjectManager);
		coin.setZIndex(1);
		mScene.attachChild(coin);
		coindestroyed = false;
		
		mScene.sortChildren();
		mUpperPipeSection.setRotationCenterY(0) ;
		

	}


	
	//static float pipespeed = MainActivity.PIPE_SPEED[MainActivity.CURRENT_LEVEL] ;
	static float pipespeed = 0.12f ; 
	
	
	public void move(float offset , float rand_number,  boolean direction){

		
		coin.setPosition(coin.getX() - offset, coin.getY());
		
		//System.out.println(rand_number);
		
		
		
		float deltaSpeed = (pipespeed + rand_number) *((!direction)?-1:1) ; 
		
		mUpperPipeSection.setHeight(mUpperPipeSection.getHeight() + deltaSpeed);
		
		mLowerPipeSection.setHeight(mLowerPipeSection.getHeight() + deltaSpeed ) ;
		
		
		mUpperPipe.setPosition(mUpperPipe.getX() - offset, mUpperPipe.getY() + deltaSpeed);
		mUpperPipeSection.setPosition(mUpperPipeSection.getX() - offset, mUpperPipeSection.getY());

		mLowerPipe.setPosition(mLowerPipe.getX() - offset, mLowerPipe.getY() - deltaSpeed );
		mLowerPipeSection.setPosition(mLowerPipeSection.getX() - offset, mLowerPipeSection.getY() - deltaSpeed) ;	

	}

	public boolean isOnScreen(){

		if(mUpperPipe.getX() < -200){
			return false;
		}

		return true;		
	}
	
	boolean counted = false;
	
	public boolean isCleared(float birdXOffset){
		
		if(!counted){
			if(mUpperPipe.getX()<(birdXOffset - (Bird.BIRD_WIDTH/2))){
				counted = true; // make sure we don't count this again
				return true;
			}
		}		
		
		return false;
	
	}
	
	
	boolean coindestroyed = false ; 

	public void destroy(){
		mScene.detachChild(mUpperPipe);
		mScene.detachChild(mUpperPipeSection);
		mScene.detachChild(mLowerPipe);
		mScene.detachChild(mLowerPipeSection);
		if( !coindestroyed )destroyCoin();
		
	}
	
	public void destroyCoin(){
		
		mScene.detachChild( coin ) ; 
		coindestroyed = true ; 
		
	}

	public boolean collidesWith(Sprite bird){

		if(mUpperPipe.collidesWith(bird)) return true;
		if(mUpperPipeSection.collidesWith(bird)) return true;
		if(mLowerPipe.collidesWith(bird)) return true;
		if(mLowerPipeSection.collidesWith(bird)) return true;
		return false;

	}
	
	public boolean collidesWithCoin( Sprite bird ){
		
		if( coindestroyed )return false;
		if(coin.collidesWith(bird)) return true;
		return false ; 
		
	}

	private boolean collidesWithCircle(float centerX, float centerY,
			float centerX1, float centerY1, float radius) {

		// pythagorus
		double a = centerX - centerX1;
		double b = centerY - centerY1;
		double c = (a * a) + (b * b);
		double distance = Math.sqrt(c);

		//radius*2 - because its the center of both circles
		if (distance <= radius*2){  
			return true;
		}
		else {
			return false;
		}
	}





}
