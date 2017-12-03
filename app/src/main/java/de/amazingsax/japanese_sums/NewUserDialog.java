package de.amazingsax.japanese_sums;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Zum Anmelden eines neuen Users auf dem Highscoreserver
 * bzw um sich als anderen user anzumelden
 */

public class NewUserDialog extends DialogFragment implements OnClickListener {

	Context context;
	String username;
	EditText usernameField;
	boolean sucess=false;
	boolean interneterror=false;
	int points;
	
	public NewUserDialog(Context context) {
		this.context=context;
	}

	/**
	 * Klasse zur Kommunikation mit dem Higgh Score Server
	 * wird als Async Task ausgefvüllt um UI Thread nicht zu blockieren
 	 */
	class AddNewUser extends AsyncTask<Void,Void,Boolean> {

		@Override
		protected Boolean doInBackground(Void... arg0) {
			try {
				AndroidHttpClient client = AndroidHttpClient.newInstance("japsum");
				HttpGet request = new HttpGet(Constants.highscoreserver+"?game="+Constants.game+"&newuser="+username);
				HttpResponse response = client.execute(request);
				HttpEntity entity = response.getEntity();
				InputStreamReader reader = new InputStreamReader(entity.getContent(),"utf-8");
				
				String answer = "";
				int c = reader.read();
				while(c != -1) {
					answer+= (char) c;
					c=reader.read();
				}
				client.close();
				interneterror=false;
				if(answer.equals("true\n")) {
					sucess=true;
				} else {
					sucess=false;
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				interneterror=true;
			}
			return null;
		}

		/**
		 * Zeigt dem user ob alles geklappt hat oder nicht.
		 * @param result
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			if(interneterror == true) {
				Toast toast= Toast.makeText(context,R.string.internetError,Toast.LENGTH_LONG);
				toast.show();
			} else {
				if(sucess) {
					 username=username.replace("+"," ");
			         showToast(getResources().getString(R.string.angemeldet)+" "+username);
			         points=0;
			         saveUsernameLokal();
			         NewUserDialog.this.dismiss();
				} else {
					showToast(getResources().getString(R.string.nameExisitiert));
				}				
			}
			super.onPostExecute(result);
		}
	}

	/**
	 * Am Highscoreserver als bestehender User anmelden
	 * Internetzugriff als async Task um UI Thread nicht zu blockieren.
	 */
	class LoginAsUser extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				AndroidHttpClient client = AndroidHttpClient.newInstance("japsum");
				HttpGet request = new HttpGet(Constants.highscoreserver+"?game="+Constants.game+"&getuserpoints="+username);
				HttpResponse response = client.execute(request);
				HttpEntity entity = response.getEntity();
				InputStreamReader reader = new InputStreamReader(entity.getContent(),"utf-8");
				
				String answer = "";
				int c = reader.read();
				while(c != -1) {
					answer+= (char) c;
					c=reader.read();
				}
				interneterror=false;
				if(answer.equals("no such user\n")) {
					sucess=false;
				} else {
					sucess=true;
					answer=answer.replace("\n","");
					points=Integer.parseInt(answer);
				}				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				interneterror=true;
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * Gibt Ergebnis aus.
		 * todo Schauen ob Code Replizierung vermieden werden kann
		 * @param result
		 */
		
		 @Override
		protected void onPostExecute(Void result) {

			if (interneterror == true) {
				Toast toast = Toast.makeText(context, R.string.internetError,
						Toast.LENGTH_LONG);
				toast.show();
			} else {
				if (sucess) {
					username = username.replace("+"," ");
					showToast(getResources().getString(R.string.angemeldet)
							+ " " + username + "\n"
							+ getResources().getString(R.string.points) + " "
							+ Integer.toString(points));
					saveUsernameLokal();
					NewUserDialog.this.dismiss();
				} else {
					showToast(getResources().getString(R.string.nosuchUser));
				}
			}
			super.onPostExecute(result);
		}
	}

	/**
	 * Gibt Medung aus
	 * @param s Die Meldung
	 */
	void showToast(String s) {
		Toast toast= Toast.makeText(context,s,Toast.LENGTH_LONG);
		toast.show();
	}

	/**
	 * Intialisiert die Vie
	 * @param inflater Android System parameter
	 * @param container Android System paramter
	 * @param savedInstanceState Android System Parameter
	 * @return
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle(R.string.userName);
		View view = inflater.inflate(R.layout.newuserdialog, container,
				false);
		Button newuserButton = (Button)view.findViewById(R.id.newuserButton);
		Button loginButton = (Button)view.findViewById(R.id.loginButton);
		Button offlineButton = (Button)view.findViewById(R.id.offlineButton);
		usernameField = (EditText)view.findViewById(R.id.usernameEdit);
		
		newuserButton.setOnClickListener(this);
		loginButton.setOnClickListener(this);
		offlineButton.setOnClickListener(this);
		
		
		
		return view;
	}


	/**
	 * Wertet klick aus und führt entsprechnende Aktion durch
	 * @param v View die Event auslöst (standard Android)
	 */
	@Override
	public void onClick(View v) {
		username=usernameField.getText().toString();
		username=username.replace(" ","+");
		switch(v.getId()) {
		case R.id.newuserButton:
			if(username.length()>0)
				addNewUser();
			break;
		case R.id.loginButton:
			if(username.length()>0)
				loginasuser();
			break;
		case R.id.offlineButton:
			setOfflineGame();
			username=null;
			this.dismiss();
		}

	}

	
	//startet async Task für Anmeldung als bestehender User users
	private void loginasuser() {
		new LoginAsUser().execute();
	}
    //Startet Async Task für Anmeldung als neuer User
	private void addNewUser() {
		new AddNewUser().execute();	
	}


	/**
	 * uPDATE Activity und beendet Dialog
	 * @param dialog
	 */
	@Override
	public void onDismiss(DialogInterface dialog) {
		StartActivity start = (StartActivity)context;
		start.updateUserdata();
		super.onDismiss(dialog);
	}

	/**
	 * Speichere aktuellen Usernamen auf Gerät
	 */
	private void saveUsernameLokal() {
		SharedPreferences pref=context.getSharedPreferences("GAME",0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean("ONLINE",true);
		editor.putString("USERNAME",username);
		editor.putInt("POINTS",points);
		editor.commit();
	}


	/**
	 * Offline Spiel ohne Highscoreliste starten
	 */
	private void setOfflineGame() {
		SharedPreferences pref=context.getSharedPreferences("GAME",0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean("ONLINE",false);
		editor.commit();
		
	}

}
