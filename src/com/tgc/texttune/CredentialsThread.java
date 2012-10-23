package com.tgc.texttune;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class CredentialsThread extends Thread{
	AccountManager mgr = null;
	Context context = null;
	String token = null;
	String t = null;
	String t2 = null;
	int loopTries = 10;
	public CredentialsThread(Context c){
		context=c;
		mgr = AccountManager.get(c);
		Log.v("AccountThread", "About to run()");
	}
	public void run(){
	      try {
				Account[] accts = mgr.getAccountsByType("com.google");
				if(accts.length < 1){
					Toast.makeText(context, "No YouTube Account! Check Settings>Accounts", Toast.LENGTH_LONG).show();
				}
				Account acct = accts[0];
				Log.v("account in AccountThread", acct.name);
				for(int i = 1; i <= loopTries; i++){
					t = getAuthToken(acct, context);
					mgr.invalidateAuthToken("com.google", t);
					t = getAuthToken(acct, context);
					if(t != null && t.length() > 10)
						break;
	//				Log.v("Token t2:", t2);
				}
				if(t == null){
					//error getting token1!
					Toast.makeText(context, "Couldnt get token", Toast.LENGTH_LONG).show();
				}else{
					setToken(t);
				}
	         }catch (Exception e) {
	        	 e.printStackTrace();
	         }
	   }

public String getAuthToken(Account act, Context context) {
	String APP_NAME = "TT";
    String authTokenType = "youtube";
	Log.d("APP_NAME", "Getting " + authTokenType  + " authToken for " + act.name);
    if (act != null) {
      try {
        String tk = mgr.blockingGetAuthToken(act, authTokenType, true);
//        Log.v("Token tk:", tk);
        return tk;
      } catch (OperationCanceledException e) {
        Log.w(APP_NAME, e);
      } catch (IOException e) {
        Log.w(APP_NAME, e);
      } catch (AuthenticatorException e) {
        Log.w(APP_NAME, e);
      }
    }
    return null;
  }


	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
