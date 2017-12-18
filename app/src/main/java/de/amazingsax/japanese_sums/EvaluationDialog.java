package de.amazingsax.japanese_sums;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * Dialogklasse zur Anzeige der erreichten Punktzahl
 * @author sax
 */

public class EvaluationDialog extends DialogFragment implements OnClickListener{
	Context context;
	
	int maxPoints=0;
	int malus =0;
	
	public int getMaxPoints() {
		return maxPoints;
	}

	public void setMaxPoints(int maxPoints) {
		this.maxPoints = maxPoints;
	}

	public int getMalus() {
		return malus;
	}

	public void setMalus(int malus) {
		this.malus = malus;
	}

	public static EvaluationDialog newInstance(Context context,int points,int malus)
	{
		EvaluationDialog f = new EvaluationDialog();
		f.context=context;
		f.setMaxPoints(points);
		f.setMalus(malus);
		return f;
	}
	//public EvaluationDialog(Context context) {
	//	this.context=context;
	//}

	/**
	 * Initialisert Dialog, zdigt Punkte an
	 *
	 * @param inflater standart Android parameter
	 * @param container standart Android parameter
	 * @param bundle  standart Android parameter
	 * @return
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle bundle) {
		
		View view = inflater.inflate(R.layout.gameevaluation, container,
				false);
		getDialog().setTitle(getResources().getString(R.string.erreichtePunkte));
		TextView maxpoints= (TextView)view.findViewById(R.id.punktemaxvalue);
		TextView checkpoints = (TextView)view.findViewById(R.id.punkteabzvalue);
		TextView sumvalue = (TextView)view.findViewById(R.id.punktesumvalue);
		maxpoints.setText(String.valueOf(maxPoints));
		checkpoints.setText(String.valueOf(malus));
		int points=maxPoints- malus;
		if(points < 0) points = 0;
		sumvalue.setText(String.valueOf(points));
		Button okButton = (Button) view.findViewById(R.id.pointsokay);
		okButton.setOnClickListener(this);
		
		return view;
	}

	/**
	 * Beendet Dialog bei klick auf okay Button
	 * @param v
	 */

	@Override
	public void onClick(View v) {
		this.dismiss();
		
	}

	/**
	 * Nach beendeten Dialog, wird die Activity beendet, es geht zurück zur Start Activity
	 * @param dialog
	 */
	@Override
	public void onDismiss(DialogInterface dialog) {
		PlayfieldActivity pfa = (PlayfieldActivity)context;
		pfa.finish();
		super.onDismiss(dialog);
	}
	
	
	

}
