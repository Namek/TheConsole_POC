package net.namekdev.theconsole.view;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.SnapshotArray;

public class ConsoleView extends Table {
	private Skin skin;
	private ScrollPane scrollPane;
	private Table entriesStack;
	private TextField inputField;


	public ConsoleView(Skin skin) {
		this.skin = skin;
		entriesStack = new Table(skin);
		entriesStack.setFillParent(true);
		scrollPane = new ScrollPane(entriesStack, skin);
		scrollPane.setFadeScrollBars(false);
		scrollPane.setScrollbarsOnTop(false);
		scrollPane.setOverscroll(false, false);

		inputField = new TextField("", skin);

		this.add(scrollPane).expand().fill().pad(2).row();
		this.add(inputField).expandX().fillX().pad(4);

		setTouchable(Touchable.enabled);

		clearEntries();
	}

	public TextField getInputTextField() {
		return inputField;
	}

	public void clearEntries() {
		entriesStack.clear();

		// expand first so labels start at the bottom
		entriesStack.add().expand().fill().row();
	}

	public Actor addInputEntry(String text) {
		return addTextEntry("> " + text);
	}

	public Actor addTextEntry(String text) {
		return addTextEntry(text, Color.WHITE);
	}

	public Actor addTextEntry(String text, Color color) {
		Label label = new Label(text, skin, "default-font", color);
		label.setWrap(true);
		addFullRowActor(label);

		return label;
	}

	/**
	 * Adds red text.
	 */
	public Actor addErrorEntry(String text) {
		return addTextEntry(text, Color.RED);
	}

	public void addFullRowActor(Actor actor) {
		entriesStack.add(actor).expandX().fillX().top().left().padLeft(4).row();
		scrollPane.layout();
		scrollPane.validate();
		scrollPane.setScrollPercentY(1);
	}

	public Actor getLatestAddedChild() {
		SnapshotArray<Actor> children = entriesStack.getChildren();
		return children.get(children.size - 1);
	}
}
