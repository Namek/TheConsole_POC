package net.namekdev.theconsole;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import net.namekdev.theconsole.commands.CommandLineService;
import net.namekdev.theconsole.scripts.ConsoleProxy;
import net.namekdev.theconsole.scripts.JsScriptManager;
import net.namekdev.theconsole.scripts.JsUtilsProvider;
import net.namekdev.theconsole.view.ConsoleView;
import net.namekdev.theconsole.view.INativeWindowController;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class TheConsole extends ApplicationAdapter {
	protected INativeWindowController windowController;
	protected JsUtilsProvider jsUtils;
	protected JsScriptManager scriptManager;
	protected PrintWriter errorStream;

	SpriteBatch batch;
	ShapeRenderer shapes;

	Texture consoleBackground, consoleBackground2;
	float backgroundWidth, backgroundHeight;
	float background2Width, background2Height;
	boolean touched;
	float progress = 0, progress2 = 0;

	final static float SPEED = 0.04f;
	final static float SPEED2 = 0.06f;

	Stage stage;
	ConsoleView consoleView;


	public TheConsole(INativeWindowController windowController) {
		this.windowController = windowController;
	}

	@Override
	public void resize(int width, int height) {
		consoleView.setPosition(0, 5);
		consoleView.setFillParent(true);
		consoleView.invalidateHierarchy();

	    stage.getViewport().update(width, height, true);
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		shapes = new ShapeRenderer();
		stage = new Stage(new ScreenViewport());

		consoleBackground = new Texture("console01.jpg");
		consoleBackground2 = new Texture("console02.jpg");
		backgroundWidth = consoleBackground.getWidth();
		backgroundHeight = consoleBackground.getHeight();
		background2Width = consoleBackground.getWidth();
		background2Height = consoleBackground.getHeight();

		Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

		consoleView = new ConsoleView(skin);
		stage.addActor(consoleView);
		Gdx.input.setInputProcessor(stage);

		TextField inputField = consoleView.getInputTextField();
		stage.setKeyboardFocus(inputField);

		errorStream = new PrintWriter(new OutputStream() {
			StringBuilder sb = new StringBuilder();
			@Override
			public void write(int c) throws IOException {
				if (c == '\n') {
					consoleView.addErrorEntry(sb.toString());
					sb = new StringBuilder();
				}
				else {
					sb.append((char)c);
				}
			}
		});

		jsUtils = new JsUtilsProvider(errorStream);
		scriptManager = new JsScriptManager(jsUtils, new ConsoleProxy(consoleView, windowController));
		new CommandLineService(consoleView, inputField, scriptManager);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		float delta = Gdx.graphics.getDeltaTime();
		progress += delta * SPEED;
		progress2 += delta * SPEED2;

		while (progress >= 1f) {
			progress -= 1f;
		}
		while (progress2 >= 1f) {
			progress2 -= 1f;
		}

		batch.begin();
		float width = Gdx.graphics.getWidth();
		float bwWidth = backgroundWidth * 2.5f;
		float bwHeight = backgroundHeight * 2.5f;
		float bw2Width = backgroundWidth * 5f;
		float bw2Height = backgroundHeight * 5f;

		for (float x = 0; x < width+bwWidth; x += bwWidth) {
			batch.setBlendFunction(GL20.GL_ZERO, GL20.GL_SRC_COLOR);
			batch.draw(consoleBackground, x - progress * bwWidth, 0, bwWidth, bwHeight);
			batch.draw(consoleBackground, x - progress * bwWidth, bwHeight, bwWidth, bwHeight);
		}

		for (float x = 0; x < width+bw2Width; x += bw2Width) {
			batch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_DST_COLOR);
			batch.setBlendFunction(GL20.GL_ONE_MINUS_SRC_COLOR, GL20.GL_ONE_MINUS_SRC_COLOR);
			batch.draw(consoleBackground2, x - progress2 * bw2Width, progress2 * bw2Height, bw2Width, bw2Height);
			batch.draw(consoleBackground2, x - progress2 * bw2Width, progress2 * bw2Height - bw2Height, bw2Width, bw2Height);
		}

		batch.end();

		stage.act();
		stage.draw();

		// red line on the bottom
		shapes.begin(ShapeType.Filled);
		shapes.setColor(1f, 0, 0, 0.9f);
		shapes.rect(0, 0, width, 1);
		shapes.setColor(0.9f, 0, 0, 0.9f);
		shapes.rect(0, 1, width, 1);
		shapes.setColor(0.75f, 0, 0, 0.9f);
		shapes.rect(0, 2, width, 1);
		shapes.end();
	}

	@Override
	public void dispose() {
		stage.dispose();
		batch.dispose();
		shapes.dispose();
		consoleBackground.dispose();
		consoleBackground2.dispose();
	}


}
