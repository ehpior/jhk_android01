package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import android.content.SharedPreferences;

public class SignActivity extends Activity implements View.OnClickListener {

    private static String IP_ADDRESS = "ehpior.dothome.co.kr";
    private static String TAG = "phpexample";

    Button sign_login;
    Button sign_cancel;
    Button sign_join;
    EditText sign_id;
    EditText sign_pw;
    EditText sign_pwchk;
    LinearLayout sign_layout_pwchk;

    int sign_flag = 0; // 0은 로그인 1은 회원가입

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        sign_id = (EditText)findViewById(R.id.sign_id);
        sign_pw = (EditText)findViewById(R.id.sign_pw);
        sign_pwchk = (EditText)findViewById(R.id.sign_pw_chk);
        sign_login = (Button)findViewById(R.id.sign_login);
        sign_cancel = (Button)findViewById(R.id.sign_cancel);
        sign_join = (Button)findViewById(R.id.sign_join);
        sign_layout_pwchk = (LinearLayout)findViewById(R.id.sign_layout_pwchk);

        sign_login.setOnClickListener(this);
        sign_cancel.setOnClickListener(this);
        sign_join.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch(id){
            case R.id.sign_login:
                if(sign_flag==0){ // 로그인 모드
                    String Id = sign_id.getText().toString();
                    String Pw = sign_pw.getText().toString();
                    loginData task = new loginData();
                    task.execute("http://" + IP_ADDRESS + "/login.php", Id, Pw);
                }
                else{ // 회원가입 모드
                    String Id = sign_id.getText().toString();
                    String Pw = sign_pw.getText().toString();
                    String Pwchk = sign_pwchk.getText().toString();
                    if(!(Pw.equals(Pwchk))){
                        Toast.makeText(getApplicationContext(), "Check the password", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        InsertData task = new InsertData();
                        //task.execute("http://" + IP_ADDRESS + "/insert.php", Id, Pw);
                        try {
                            String result = task.execute("http://" + IP_ADDRESS + "/insert.php", Id, Pw).get();
                            if(result.equals("0")) {
                                sign_layout_pwchk.setVisibility(GONE);
                                sign_login.setText("Login");
                                sign_join.setText("Sign up");
                                sign_flag = 0;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case R.id.sign_cancel:
                finish();
                break;
            case R.id.sign_join:
                if(sign_layout_pwchk.getVisibility() == GONE) {
                    sign_layout_pwchk.setVisibility(VISIBLE);
                    sign_login.setText("Create");
                    sign_join.setText("Sign In");
                    sign_flag = 1;
                }
                else{
                    sign_layout_pwchk.setVisibility(GONE);
                    sign_login.setText("Login");
                    sign_join.setText("Sign up");
                    sign_flag = 0;
                }
                break;
        }
    }

    class InsertData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String msg = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(SignActivity.this, "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e(TAG, "POST response  - " + result);


            try {
                switch (Integer.valueOf(result)) {
                    case 0:
                        msg += "이메일을 확인하세요";
                        break;
                    case 1:
                        msg = "ID를 입력하세요";
                        break;
                    case 3:
                        msg = "PW를 입력하세요";
                        break;
                    case 4:
                        msg = "ID또는 PW를 확인하세요";
                        break;
                    case 5:
                        msg = "중복된 ID가 있습니다.";
                        break;
                }
            }
            catch(Exception e){
                msg = "알수없는 오류";
            }
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        }


        @Override
        protected String doInBackground(String... params) {

            String u_id = (String)params[1];
            String u_pw = (String)params[2];

            String serverURL = (String)params[0];
            String postParameters = "u_id=" + u_id + "&u_pw=" + u_pw;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.e(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();



            } catch (Exception e) {

                Log.e(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }


    class loginData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String msg = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(SignActivity.this, "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.e(TAG, "Login response  - " + result);


            try{
                switch(Integer.valueOf(result)){
                    case 0:
                        msg = "로그인 되었습니다.";
                        finish();
                        break;
                    case 1:
                        msg = "ID를 입력하세요";
                        break;
                    case 3:
                        msg = "PW를 입력하세요";
                        break;
                    case 4:
                        msg = "ID또는 PW를 확인하세요";
                        break;
                    case 5:
                        msg = "알수없는 오류";
                        break;
                }
            }
            catch(Exception e){
                msg = "알수없는 오류";
            }
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();


        }


        @Override
        protected String doInBackground(String... params) {

            String u_id = (String)params[1];
            String u_pw = (String)params[2];

            String serverURL = (String)params[0];
            String postParameters = "u_id=" + u_id + "&u_pw=" + u_pw;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.e(TAG, "LOGIN response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();



            } catch (Exception e) {

                Log.e(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

}
