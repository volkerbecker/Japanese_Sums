package de.amazingsax.japanese_sums;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Dialog zu Eingabe der Summenblöckem wenn als Solver genutzt
 */
public class SummenDialog extends DialogFragment implements OnClickListener {
	PlayfieldActivity aufrufer;
	Button okayButton;
	EditText eingabe;
	int i;
	boolean horizontal;

	public SummenDialog() {
	};

	public void setContext(PlayfieldActivity context, int i, boolean horizontal) {
		aufrufer = context;
		this.i = i;
		this.horizontal = horizontal;
	}

	/**
	 * Initialisierung des Dialogs
 	 * @param inflater
	 * @param container
	 * @param bundle
	 * @return
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle bundle) {
		getDialog().setTitle(R.string.enterSum);
		View view = inflater.inflate(R.layout.summen_eingabedialog, container,
				false);
		okayButton = (Button) view.findViewById(R.id.dialogButtonOk);
		okayButton.setOnClickListener(this);
		eingabe = (EditText) view.findViewById(R.id.summenEingabe);
		Button b = (Button) view.findViewById(R.id.sumDialogCancel);
		b.setOnClickListener(this);
		b=(Button)view.findViewById(R.id.spaceButton);
		b.setOnClickListener(this);
		eingabe.requestFocus();
		getDialog().getWindow().setSoftInputMode(
				LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialogButtonOk:
			String werteasLine = eingabe.getText().toString();
			String[] werteasString = werteasLine.split(" ");
			int l = werteasString.length;
			if (l > 0) {
				int werte[] = new int[l];
				try {
					for (int i = 0; i < l; ++i) {
						werte[i] = Integer.valueOf(werteasString[i]);
						if (werte[i] > aufrufer.getmaxSum()) {
							Toast toast = Toast.makeText(aufrufer,
									R.string.numberToBig, Toast.LENGTH_SHORT);
							toast.show();
							eingabe.setText("");
							return;
						}
					}
				} catch (NumberFormatException e) {
					Toast toast = Toast.makeText(aufrufer,
							R.string.numberInvalid, Toast.LENGTH_SHORT);
					toast.show();
					eingabe.setText("");
					return;
				}
				aufrufer.setVorgaben(horizontal, werte, i);
				this.dismiss();
			} else {
				Toast toast = Toast.makeText(aufrufer, R.string.numberInvalid,
						Toast.LENGTH_SHORT);
				toast.show();
				eingabe.setText("");
			}
			break;
		case R.id.spaceButton:
			eingabe.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_SPACE));
			break;
		case R.id.sumDialogCancel:
			this.dismiss();
		}
	}

}
