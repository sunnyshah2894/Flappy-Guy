package com.sunny.flappyguy.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;

import org.andengine.AndEngine;
import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.StrokeFont;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.debug.Debug;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import android.R.integer;
import android.graphics.Color;
import android.graphics.Typeface;
import android.opengl.GLSurfaceView.Renderer;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import com.sunny.flappyguy.ScoreManager;

public class MainActivity extends SimpleBaseGameActivity {

	/**
	 * Set the camera height and width
	 */
	public static float CAMERA_WIDTH = 485;
	public static final float CAMERA_HEIGHT = 800;

	private static final float SCROLL_SPEED = 8.5f;

	// private static final float SCROLL_SPEED = 0.0f;
	// protected static final float GRAVITY = 0.00f;

	public static float FLOOR_BOUND = 771;

	protected static final int PIPE_SPAWN_INTERVAL = 100;

	protected static final int SCORE_PASS[] = { 0, 50, 100, 200, 250 , 300 , 400 , 600  , 900 , 1200 , 1500 , 1200 };
	protected static final int COIN_POINT[] = { 0, 10, 12, 15, 18 , 20 , 24 , 26 , 30 , 32 , 35 , 40 };
	protected static final float PIPE_SPEED[] = { 0, 0.12f, 0.15f, 0.15f, 0.17f , 0.20f , 0.23f , 0.25f , 0.28f , 0.30f , 0.30f , 0.30f  };
	protected static final int divisor[] = { 0 , 6 , 6 , 6 , 5 , 5 , 5 , 5 , 4 , 4 , 4 , 4 } ; 
	protected static final float PIPE_PASSING_SCORE[] = { 0,1, 1,2,3,4,4,4, 5, 5 , 5 ,6};
	protected static int CURRENT_LEVEL;
	protected static int MAX_LEVEL = 10;

	/**
	 * Constants for assets
	 */

	private Camera mCamera;

	/**
	 * Texture Region for the game graphics
	 */
	// background
	private BitmapTextureAtlas mBackgroundBitmapTextureAtlas;
	private ITextureRegion mBackgroundTextureRegion;

	

	/**
	 * Game States
	 */
	protected static final int STATE_START = 0;
	protected static final int STATE_READY = 1;
	protected static final int STATE_PLAYING = 2;
	protected static final int STATE_DYING = 3;
	protected static final int STATE_DEAD = 4;

	private int GAME_STATE = STATE_READY;

	/**
	 * Game Variables
	 */

	private int mScore = 0;

	// objects
	private static MainActivity instance;
	private Scene mScene;
	private VertexBufferObjectManager vbo;

	// sprites

	private ParallaxBackground mBackground;
	private ArrayList<PipePair> pipes = new ArrayList<PipePair>();
	private ArrayList<Float> pipeSpeedList = new ArrayList<Float>() ;
	private ArrayList<Boolean> pipeDirection = new ArrayList<Boolean>() ;
	private Bird mBird;
	private Sprite mCoin;
	private TextureRegion mCoinTexture;

	// physics variables
	protected float mCurrentWorldPosition;
	private float mBirdXOffset;

	// fonts
	private Font mScoreFont;
	private Font mGetReadyFont;
	private Font mCoinValueFont ; 
	//private Font mCopyFont;

	// text objects
	private Text mScoreText;
	private Text mGetReadyText;
	private Text mCoinValueText ; 
	
	private TimerHandler mTimer;
	//private Text mCopyText;
	private StrokeFont mYouSuckFont;
	private Text mYouSuckText;

	// sounds
	private Sound mDieSound;
	private Music mMusic;
	private Sound mScoreCoinSound;
	private Sound mScorePipeSound;

	private AdView adView;
	private InterstitialAd mInterstitialAd ; 
	//private RelativeLayout relativeLayout;
/*
	@Override
	protected void onSetContentView() {

		try {
			adView = new AdView(this);
			adView.setAdSize(AdSize.BANNER);
			adView.setAdUnitId("pub-4871292192482812");
			adView.setTag("adView");
			adView.refreshDrawableState();
			adView.setVisibility(AdView.VISIBLE);

			// Initiate a generic request to load it with an ad
			AdRequest adRequest = new AdRequest.Builder()
					
					
					.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)// This				
					.addTestDevice("7B20E70BC4FA07D6BDC5081AB94347D") // Nexus 5
					.build();
			adView.loadAd(adRequest);

			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

			adView.setLayoutParams(params);
			

		} catch (Exception e) {
			// ads aren't working. oh well
		}
		this.setContentView(adView);
		
	}

*/	
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                  .addTestDevice("1FE68AE5A6D9B3841F3220FEA51DDC37")
                  .build();

        mInterstitialAd.loadAd(adRequest);
    }
    
    private void startAd(){
    	
    	runOnUiThread(new Runnable() {
    	    public void run() {
    	        if (mInterstitialAd.isLoaded()) {
    	        	mInterstitialAd.show();
    	        } 
    	    }
    	}) ; 
    	
    }
	
	@Override
	
	protected void onSetContentView() {
	
	

    final FrameLayout frameLayout = new FrameLayout(this);
    final FrameLayout.LayoutParams frameLayoutLayoutParams =
            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
                                         FrameLayout.LayoutParams.FILL_PARENT);
    
    mInterstitialAd = new InterstitialAd(this);
    mInterstitialAd.setAdUnitId("ca-app-pub-4871292192482812/9682764085");

    mInterstitialAd.setAdListener(new AdListener() {
        public void onAdClosed() {
            requestNewInterstitial();
        }
    });

    requestNewInterstitial();

  

    AdView adView = new AdView(this);
    adView.setAdUnitId("ca-app-pub-4871292192482812/7230655289");
    adView.setAdSize(AdSize.BANNER);
    //final AdView adView = new AdView(this, AdSize.BANNER, "ca-app-pub-4871292192482812/7230655289");
    adView.refreshDrawableState();
    adView.setVisibility(AdView.VISIBLE);
    final FrameLayout.LayoutParams adViewLayoutParams =
            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                                         FrameLayout.LayoutParams.WRAP_CONTENT,
                                         Gravity.RIGHT | Gravity.BOTTOM);
/*    
    int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
            getResources ().getDisplayMetrics ());*/
    // top of AD is at middle of the screen
    adViewLayoutParams.topMargin = 0;

    AdRequest adRequest = new AdRequest.Builder()
    		.addTestDevice( AdRequest.DEVICE_ID_EMULATOR)
    		.addTestDevice("7B20E70BC4FA07D6BDC5081AB94347D")
    		.addTestDevice("1FE68AE5A6D9B3841F3220FEA51DDC37") 
    		.build();
    adView.loadAd(adRequest);

    this.mRenderSurfaceView = new RenderSurfaceView(this);
    mRenderSurfaceView.setRenderer( mEngine,this);

   // final android.widget.FrameLayout.LayoutParams surfaceViewLayoutParams =
       //     new FrameLayout.LayoutParams(super.createSurfaceViewLayoutParams());

    frameLayout.addView(this.mRenderSurfaceView, this.createSurfaceViewLayoutParams());
    frameLayout.addView(adView, adViewLayoutParams);

    this.setContentView(frameLayout, frameLayoutLayoutParams);

};


	public EngineOptions onCreateEngineOptions() {

		// figure out display size
		DisplayMetrics dm = new DisplayMetrics();
		CURRENT_LEVEL = ScoreManager.GetLevel(this);
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		final int height = dm.heightPixels;
		final int width = dm.widthPixels;
		float ratio = (float) width / (float) height;
		CAMERA_WIDTH = CAMERA_HEIGHT * ratio;
		FLOOR_BOUND = CAMERA_HEIGHT - 26;
		instance = this;
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT) {

			private int mPipeSpawnCounter;

			@Override
			public void onUpdate(float pSecondsElapsed) {

				switch (GAME_STATE) {

				case STATE_READY:
					ready();
					break;

				case STATE_PLAYING:
					play();
					break;

				case STATE_DYING:
					die();
					break;

				}

				super.onUpdate(pSecondsElapsed);
			}

			private void ready() {
				mCurrentWorldPosition -= SCROLL_SPEED;
				mBird.hover();
				// if(!mMusic.isPlaying()){
				// mMusic.play();
				// }
			}

			private void die() {
				float newY = mBird.move(); // get the bird to update itself
				if (newY >= FLOOR_BOUND)
					dead();
			}

			private void play() {

				mCurrentWorldPosition -= SCROLL_SPEED;
				float newY = mBird.move(); // get the bird to update itself
				if (newY >= FLOOR_BOUND)
					gameOver(); // check if it game over form twatting the floor

				// now create pipes
				mPipeSpawnCounter++;

				if (mPipeSpawnCounter > PIPE_SPAWN_INTERVAL) {
					mPipeSpawnCounter = 0;
					spawnNewPipe();
					// System.out.println("naya aaya ");
				}

				// now render the pipes
				for (int i = 0; i < pipes.size(); i++) {
					PipePair pipe = pipes.get(i);
					Float speed = pipeSpeedList.get(i) ;
					boolean direction = pipeDirection.get(i) ; 
					
					if (pipe.isOnScreen()) {
						pipe.move(SCROLL_SPEED,speed,direction);
						if (pipe.collidesWith(mBird.getSprite())) {
							gameOver();
						}
						if (pipe.collidesWithCoin(mBird.getSprite())) {
							scoreBaap();
							pipe.destroyCoin();

						}
						if (pipe.isCleared(mBirdXOffset)) {
							score();
						}
					} else {
						pipe.destroy();
						pipeSpeedList.remove(i);
						pipeDirection.remove(i) ; 
						pipes.remove(pipe);
					}
				}
			}
		};

		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);

		engineOptions.getAudioOptions().setNeedsSound(true);
		engineOptions.getAudioOptions().setNeedsMusic(true);

		return engineOptions;
	}

	protected void spawnNewPipe() {
		int Min = 150;
		int Max = 450;
		int spawn = Min + (int) (Math.random() * ((Max - Min) + 1));
		PipePair newPipes = new PipePair(spawn, this.getVertexBufferObjectManager(), mScene);
		pipes.add(newPipes);
		pipeSpeedList.add((float)(Math.random()/divisor[CURRENT_LEVEL])) ; 
		if( (int)(Math.random() * 40) < 5 ) pipeDirection.add(false) ; 
		else pipeDirection.add(true) ; 
	}

	@Override
	protected void onCreateResources() {

		SoundFactory.setAssetBasePath("sound/");
		MusicFactory.setAssetBasePath("sound/");
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("img/");

		// background
		mBackgroundBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 3000, 1184,
				TextureOptions.NEAREST_PREMULTIPLYALPHA);
		mBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBackgroundBitmapTextureAtlas,
				this.getAssets(), "back-new-3.png", 0, 0);

		mBackgroundBitmapTextureAtlas.load();

		BitmapTextureAtlas coinTextureAtlas =new BitmapTextureAtlas(this.getTextureManager(), 30, 30, TextureOptions.DEFAULT);
		mCoinTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(coinTextureAtlas, this, "coin.png", 0, 0);
		coinTextureAtlas.load();

		PipePair.onCreateResources(this); // let it sort its own resources out
		Bird.onCreateResources(this);

		// Typeface typeFace = Typeface.createFromAsset(getAssets(),
		// "flappy.ttf");
		Typeface typeFace = Typeface.DEFAULT;

		// score board
		final ITexture scoreFontTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256,
				TextureOptions.BILINEAR);
		
		mScoreFont = new StrokeFont(this.getFontManager(), scoreFontTexture, typeFace, 60, true, Color.WHITE, 7,
				Color.DKGRAY);
	
		mScoreFont.load();

		// get ready text
		final ITexture getReadyFontTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256,
				TextureOptions.BILINEAR);
		mGetReadyFont = new StrokeFont(this.getFontManager(), getReadyFontTexture, typeFace, 60, true, Color.WHITE, 5,
				Color.GRAY);
		mGetReadyFont.load();

//		// (c) text
//		final ITexture copyFontTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256,
//				TextureOptions.BILINEAR);
//		mCopyFont = new StrokeFont(this.getFontManager(), copyFontTexture, typeFace, 20, true, Color.WHITE, 2,
//				Color.BLACK);
//		mCopyFont.load();
		
		
		// coin Value
		final ITexture valueCoinTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256,
				TextureOptions.BILINEAR);
		mCoinValueFont = new StrokeFont(this.getFontManager(), valueCoinTexture , typeFace, 40, true, Color.WHITE, 3,
				Color.BLACK);
		mCoinValueFont.load();

		// (c) you suck text
		final ITexture youSuckTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256,
				TextureOptions.BILINEAR);
		mYouSuckFont = new StrokeFont(this.getFontManager(), youSuckTexture, typeFace, 80, true, Color.WHITE, 2,
				Color.BLACK);
		mYouSuckFont.load();

		// sounds
		try {
			mScoreCoinSound = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), this,
					"coin_taking_sound.wav");
			mScorePipeSound = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), this, "pipe.wav");
			mDieSound = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), this, "gameOver.wav");
		} catch (final IOException e) {
			Debug.e(e);
		}

		// music

		// try {
		// mMusic = MusicFactory.createMusicFromAsset(mEngine.getMusicManager(),
		// this, "song.ogg");
		// mMusic.setVolume(0.1f);
		// mMusic.setLooping(true);
		// } catch (final IOException e) {
		// Debug.e("Error", e);
		// }
	}

	@Override
	protected Scene onCreateScene() {

		this.mScene = new Scene();
		this.vbo = this.getVertexBufferObjectManager();

		Sprite backgroundSprite = new Sprite(0, 0, this.mBackgroundTextureRegion, this.vbo);
		mBackground = new ParallaxBackground(82 / 255f, 190 / 255f, 206 / 255f) {

			float prevX = 0;
			float parallaxValueOffset = 0;

			@Override
			public void onUpdate(float pSecondsElapsed) {

				switch (GAME_STATE) {

				case STATE_READY:
				case STATE_PLAYING:
					final float cameraCurrentX = mCurrentWorldPosition;// mCamera.getCenterX();

					if (prevX != cameraCurrentX) {

						parallaxValueOffset += cameraCurrentX - prevX;
						this.setParallaxValue(parallaxValueOffset);
						prevX = cameraCurrentX;
					}
					break;
				}

				super.onUpdate(pSecondsElapsed);
			}

		};

		mBackground.attachParallaxEntity(new ParallaxEntity(1, backgroundSprite));

		this.mScene.setBackground(mBackground);
		this.mScene.setBackgroundEnabled(true);

		mScene.setOnSceneTouchListener(new IOnSceneTouchListener() {

			@Override
			public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
				if (pSceneTouchEvent.isActionDown()) {

					switch (GAME_STATE) {

					case STATE_READY:
						startPlaying();
						break;

					case STATE_PLAYING:
						mBird.flap();
						break;

					case STATE_DEAD:
						// restartGame();
						break;
					}
				}
				return false;
			}
		});

		// bird
		mBirdXOffset = (CAMERA_WIDTH / 4) - (Bird.BIRD_WIDTH / 4);
		float birdYOffset = (CAMERA_HEIGHT / 2) - (Bird.BIRD_HEIGHT / 4);
		mBird = new Bird(mBirdXOffset, birdYOffset, getVertexBufferObjectManager(), mScene);

		// score
		mScoreText = new Text(0, 60, mScoreFont, "HighScore : 00000000", new TextOptions(HorizontalAlign.CENTER),
				getVertexBufferObjectManager());
		mScoreText.setZIndex(3);
		
		mScene.attachChild(mScoreText);
		updateScore();

		// get ready text
		String level_info ; 
		if(CURRENT_LEVEL > MAX_LEVEL)
			level_info = "Levels Completed!" ; 
		else 
			level_info = "Level " + CURRENT_LEVEL + " : score " + SCORE_PASS[CURRENT_LEVEL];
		mGetReadyText = new Text(0, 220, mGetReadyFont, level_info, new TextOptions(HorizontalAlign.CENTER),
				getVertexBufferObjectManager());
		mGetReadyText.setZIndex(3);
		mScene.attachChild(mGetReadyText);
		//centerText(mGetReadyText);
		rightAlignText(mGetReadyText) ; 
		
		mCoin = new Sprite( (CAMERA_WIDTH*3)/5 , 350 , 40 , 40 ,  mCoinTexture, getVertexBufferObjectManager() );
		mCoin.setZIndex(3);
		mScene.attachChild(mCoin);

//		// instructions image
//		mInstructionsSprite = new Sprite(0, 0, 200, 172, mInstructionsTexture, getVertexBufferObjectManager());
//		mInstructionsSprite.setZIndex(3);
//		mScene.attachChild(mInstructionsSprite);
//		//centerSprite(mInstructionsSprite);
//		rightAlignSprite(mInstructionsSprite);
//		mInstructionsSprite.setY(mInstructionsSprite.getY() + 20);

		// copy text
//		mCopyText = new Text(0, 750, mCopyFont, "Sunny Shah 2015", new TextOptions(HorizontalAlign.CENTER),
//				getVertexBufferObjectManager());
//		mCopyText.setZIndex(3);
//		mScene.attachChild(mCopyText);
//		centerText(mCopyText);
		//System.out.println( "Cam = " + (CAMERA_WIDTH*3)/5 + " -->  "+ mCoin.getX() );
		mCoinValueText = new Text( (CAMERA_WIDTH*3)/5 + 40 , 350, mCoinValueFont , " = "+ COIN_POINT[CURRENT_LEVEL], new TextOptions(HorizontalAlign.CENTER),
				getVertexBufferObjectManager());
		mCoinValueText.setZIndex(3);
		mScene.attachChild(mCoinValueText);
		//centerText(mCoinValueText);
		
		// you suck text
		mYouSuckText = new Text(0, CAMERA_HEIGHT / 2 - 100, mYouSuckFont, "Level Cleared !",
				new TextOptions(HorizontalAlign.CENTER), getVertexBufferObjectManager());
		mYouSuckText.setZIndex(3);
		centerText(mYouSuckText);

		return mScene;
	}

	private void score() {
		mScoreText.setScale(2f);
		mScore += PIPE_PASSING_SCORE[ CURRENT_LEVEL ] ; 
		mScorePipeSound.play();
		updateScore();
		mTimer = new TimerHandler(0.5f, false, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				
				mScoreText.reset();
				mScene.unregisterUpdateHandler(mTimer);
				
			}
		});
		mScene.registerUpdateHandler(mTimer);
	}

	private void scoreBaap() {
		mScoreText.setScale(2f);
		
		mScore += COIN_POINT[CURRENT_LEVEL];
		mScoreCoinSound.play();
		updateScore();
		
		mTimer = new TimerHandler(0.5f, false, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				
				mScoreText.reset();
				mScene.unregisterUpdateHandler(mTimer);
				
			}
		});
		mScene.registerUpdateHandler(mTimer);
	}

	private void updateScore() {

		if (GAME_STATE == STATE_READY) {
			mScoreText.setText("HighScore : " + ScoreManager.GetBestScore(this));
			//centerText(mScoreText);
			rightAlignText(mScoreText) ;
		} else {
			mScoreText.setText("" + mScore);
			centerText(mScoreText);
			//rightAlignText(mScoreText) ;
		}
	}

	private void centerText(Text text) {
		text.setX((CAMERA_WIDTH / 2) - (text.getWidth() / 2));
	}

	private void rightAlignText(Text text) {
		text.setX(((CAMERA_WIDTH)*3)/5 - (text.getWidth() /2 ));
	}
	
	public static void centerSprite(Sprite sprite) {
		sprite.setX((CAMERA_WIDTH / 2) - (sprite.getWidth() / 2));
		sprite.setY((CAMERA_HEIGHT / 2) - (sprite.getHeight() / 2));
	}
	public static void rightAlignSprite( Sprite sprite){
		
		sprite.setX(((CAMERA_WIDTH)*3)/5 - (sprite.getWidth() /2 ));
		sprite.setY((CAMERA_HEIGHT / 2) - (sprite.getHeight() / 2));
	}

	// STATE SWITCHES

	private void startPlaying() {
		GAME_STATE = STATE_PLAYING;
		// mMusic.pause();
		// mMusic.seekTo(0);
		mScene.detachChild(mGetReadyText);
		mScene.detachChild(mCoinValueText ) ;
		mScene.detachChild( mCoin ) ; 
		//mScene.detachChild(mInstructionsSprite);
		//mScene.detachChild(mCopyText);
		updateScore();
		mBird.flap();
	}

	private void gameOver() {
		 
		GAME_STATE = STATE_DYING;
		mDieSound.play();
		
		
		mBird.getSprite().stopAnimation();
		ScoreManager.SetBestScore(this, mScore);
		mYouSuckText.setText("Game Over!");
		if (SCORE_PASS[CURRENT_LEVEL] <= mScore && CURRENT_LEVEL <= MAX_LEVEL) {

			int level_new = -1;
			for (int i = CURRENT_LEVEL + 1; i <= MAX_LEVEL; i++) {

				if (SCORE_PASS[i] > mScore) {
					level_new = i;
					break;
				}

			}
			if (level_new != -1) {

				CURRENT_LEVEL = level_new;
				ScoreManager.setLevel(this, level_new);
				String level_info = "Level " + CURRENT_LEVEL + " : score " + SCORE_PASS[CURRENT_LEVEL];
				PipePair.pipespeed = PIPE_SPEED[CURRENT_LEVEL];
				mGetReadyText.setText(level_info);
				mYouSuckText.setText("Level Cleared!");

			}
			if( level_new == -1 && mScore > SCORE_PASS[ MAX_LEVEL ] ){
				
				CURRENT_LEVEL = MAX_LEVEL + 1;
				ScoreManager.setLevel(this, CURRENT_LEVEL);
				String level_info = "Levels Completed!" ; 
				mGetReadyText.setText(level_info);
				mYouSuckText.setText("Game Over!");
			}

		}
		mYouSuckText.setScale(2f);
		mScene.attachChild(mYouSuckText);
		if( (int)( Math.random() * 40 ) < 5 )
			startAd();
		
	}

	private void dead() {
		GAME_STATE = STATE_DEAD;

		mTimer = new TimerHandler(1.6f, false, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				mYouSuckText.reset();
				mScene.detachChild(mYouSuckText);
				restartGame();
				mScene.unregisterUpdateHandler(mTimer);

			}
		});

		mScene.registerUpdateHandler(mTimer);
	}

	private void restartGame() {
		GAME_STATE = STATE_READY;
		// mMusic.resume();
		mBird.restart();
		mScore = 0;
		updateScore();

		for (int i = 0; i < pipes.size(); i++) {
			PipePair pipe = pipes.get(i);
			pipe.destroy();
		}
		pipes.clear();

		mCoinValueText.setText( " = " + COIN_POINT[ CURRENT_LEVEL ] );
		mScene.attachChild(mGetReadyText);
		mScene.attachChild( mCoinValueText ) ; 
		mScene.attachChild( mCoin ) ; 
		//mScene.attachChild(mInstructionsSprite);
		//mScene.attachChild(mCopyText);
	}

	public static MainActivity getInstance() {
		return instance;
	}

	@Override
	public final void onPause() {
		super.onPause();
		/*
		 * if(mMusic!=null){ mMusic.pause(); }
		 */

	}

}