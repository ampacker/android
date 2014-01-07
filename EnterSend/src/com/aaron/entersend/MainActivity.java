/*
 * Copyright (C) 2014 Aaron Packer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aaron.entersend;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends Activity implements OnEditorActionListener {
	
	/**
	 * the input text box
	 */
	EditText mTextInput;
	/**
	 * the large TextView which simply shows the previously inputed text post
	 * action execution
	 */
	TextView mTextResult;
	/**
	 * The button to the right of mTextInput
	 */
	View mBtnAction;
	/**
	 * The CompoundButton which toggles the imeOptions. ToggleButton for API<14;
	 * Switch for API 14+
	 */
	CompoundButton mBtnToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// get ref's to the View's.
		mTextInput = (EditText) findViewById(R.id.txt_input);
		mTextResult = (TextView) findViewById(R.id.txtResult);
		// NB: we don't need a ref to the buttons outside of onCreate(). 
		mBtnAction = findViewById(R.id.btn_send);
		mBtnToggle = (CompoundButton) findViewById(R.id.btn_toggle);
		
		// when mBtnAction is pressed, execute the appropriate action
		mBtnAction.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onSendAction();
			}
		});
		
		// init the toggle button to false and add listener for changes to
		// checked status. Note we don't need to know whether the CompoundButton
		// is of type Switch or ToggleButton
		mBtnToggle.setChecked(false);
		mBtnToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						setupInput(isChecked);	// re-setup the input's EditText
					}
				});
		// init the input EditText appropriately
		setupInput(mBtnToggle.isChecked());
	}

	/**
	 * Setup the input EditText based on whether we want to use
	 * EditorInfo.IME_ACTION_SEND or EditorInfo.IME_ACTION_UNSPECIFIED
	 * 
	 * @param actionSend
	 *            whether to use EditorInfo.IME_ACTION_SEND
	 */
	void setupInput(boolean actionSend) {
		// these depend on whether we want the enter key to do specified action
		OnEditorActionListener actionListener;
		int imeOptions;
		int inputType = InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
				| InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;

		if (actionSend) {
			actionListener = this;
			imeOptions = EditorInfo.IME_ACTION_SEND;
			
			/*
			 * TODO: Exercise #1. Uncomment the following line and observe what
			 * happens when using EditorInfo.IME_ACTION_SEND. Hint: everything
			 * may appear to work, but pay close attention to the keyboard's UI.
			 */
			// inputType |= InputType.TYPE_TEXT_FLAG_MULTI_LINE;
			
		} else {
			actionListener = null;
			imeOptions = EditorInfo.IME_ACTION_UNSPECIFIED;
			
			/*
			 * TODO: Exercise #2. Not the following line. Based on what happens
			 * with EditorInfo.IME_ACTION_SEND when the InputType flag
			 * TYPE_TEXT_FLAG_MULTI_LINE is not set, do you think it is
			 * necessary to set here for IME_ACTION_UNSPECIFIED? Comment out the
			 * line and confirm.
			 */
			 inputType |= InputType.TYPE_TEXT_FLAG_MULTI_LINE;
		}
		
		/*
		 * set the EditText's properties. Note that unless we call
		 * setRawInputType(), the enter button's graphic will not properly
		 * change. E.g. SwiftKey will still show the "NewLine" symbol on the
		 * enter key, even though it is using EditorInfo.IME_ACTION_SEND.
		 */
		mTextInput.setOnEditorActionListener(actionListener);
		mTextInput.setRawInputType(inputType);
		mTextInput.setImeOptions(imeOptions);
		
		/*
		 * restart the input method since we've changed everything. if
		 * restartInput(mTextInput) is not called then the change to the enter
		 * key's action options will not take affect until mTxtInput has lost
		 * then regained focus. E.g. pressing home then re-entering app.
		 * restartInput() is nice because if the soft KB is opened when the
		 * method is called then it will remain so.
		 */
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.restartInput(mTextInput);
	}
	
	/**
	 * Execute send action, which may be due to enter key or the action Button mBtnAction
	 */
	void onSendAction() {
		String input = mTextInput.getText().toString();
		mTextResult.setText(input);
		mTextInput.setText("");
	}
	
	/**
	 * Called when the enter key is pressed on soft or hardware KB. Needs to
	 * determine whether or not to consume event; if so, call onButtonPressed()
	 */
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// soft KB's actionSend button or hardware KB's enter key being pressed
		// down for the first time
		if (actionId == EditorInfo.IME_ACTION_SEND
				|| (actionId == EditorInfo.IME_NULL
						&& event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getRepeatCount() == 0)) {
			onSendAction();
		} else if (actionId != EditorInfo.IME_NULL) {
			// neither soft nor hardware KB enter key was pressed down for first
			// time. Don't consume event.
			return false;
		}
		return true; // consume event.
	}
}
