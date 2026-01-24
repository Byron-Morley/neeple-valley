
package com.liquidpixel.main.ai.formation.tests.utils.scene2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.CharArray;

/** A label continuously showing the most up-to-date integer value prefixed by a constant string.
 *
 * @author davebaol */
public abstract class IntValueLabel extends Label {

	int oldValue;
	int appendIndex;

	public IntValueLabel (CharSequence text, int initialValue, Skin skin) {
		this(text, initialValue, skin.get(LabelStyle.class));
	}

	public IntValueLabel (CharSequence text, int initialValue, Skin skin, String styleName) {
		this(text, initialValue, skin.get(styleName, LabelStyle.class));
	}

	public IntValueLabel (CharSequence text, int initialValue, Skin skin, String fontName, Color color) {
		this(text, initialValue, new LabelStyle(skin.getFont(fontName), color));
	}

	public IntValueLabel (CharSequence text, int initialValue, Skin skin, String fontName, String colorName) {
		this(text, initialValue, new LabelStyle(skin.getFont(fontName), skin.getColor(colorName)));
	}

	public IntValueLabel (CharSequence text, int initialValue, LabelStyle style) {
		super(text.toString() + initialValue, style);
		this.oldValue = initialValue;
		this.appendIndex = text.length();
	}

	public abstract int getValue();

	@Override
	public void act (float delta) {
		int newValue = getValue();
		if (oldValue != newValue) {
			oldValue = newValue;
            CharArray sb = getText();
			sb.setLength(appendIndex);
			sb.append(oldValue);
			invalidateHierarchy();
		}
		super.act(delta);
	}

}
