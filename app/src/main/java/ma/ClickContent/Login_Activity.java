package ma.ClickContent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.HashMap;
import java.util.List;

import ma.ClickContent.check_registeration.wp_register;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login_Activity extends AppCompatActivity implements Validator.ValidationListener {
    AppCompatButton btn;
    TextInputLayout username,password;
    @NotEmpty
    EditText mUsername;
      @Password @NotEmpty
      EditText mPassword;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        btn = (AppCompatButton) findViewById(R.id.btn_login);
        mUsername=(EditText) findViewById(R.id.input_email);
        mPassword=(EditText) findViewById(R.id.input_password);
        pd=new ProgressDialog(Login_Activity.this);
        pd.setMessage("Please Wait...");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        Drawable drawable=new ProgressBar(this).getIndeterminateDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(this, R.color.primary),
                PorterDuff.Mode.SRC_IN);
        pd.setIndeterminateDrawable(drawable);
      final Validator  validator = new Validator(Login_Activity.this);
        validator.setValidationListener(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  if(!mUsername.getText().toString().isEmpty()&&!mPassword.getText().toString().isEmpty()) {
                   //login(mUsername.getText().toString(), mPassword.getText().toString());
                }else{
                    if(pd.isShowing()){
                        pd.dismiss();
                    }
                    Toast.makeText(Login_Activity.this,"Please Provide all required Information",Toast.LENGTH_LONG).show();
                }*/
              validator.validate();
            }
        });
    }

    private void login(final String username, String password) {
     if(!pd.isShowing()){
            pd.show();
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.palmsaresweaty.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FeedAPI feedAPI = retrofit.create(FeedAPI.class);
        Call<wp_register> call=feedAPI.get_Login_status(username,password);
        call.enqueue(new Callback<wp_register>() {
            @Override
            public void onResponse(Call<wp_register> call, Response<wp_register> response) {
                if(response.body()!=null){
                    if(pd.isShowing()){
                        pd.dismiss();
                    }
                    Toast.makeText(Login_Activity.this,"Login Success",Toast.LENGTH_LONG).show();
                    setSessionParams(username);
                    Toast.makeText(Login_Activity.this,"Welcome "+username,Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Login_Activity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }else{
                    if(pd.isShowing()){
                        pd.dismiss();
                    }
                    Toast.makeText(Login_Activity.this,"invalid Username or Password",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<wp_register> call, Throwable throwable) {
                if(pd.isShowing()){
                    pd.dismiss();
                }
             Log.e("login",throwable.getMessage());
            }
        });
    }
    private void setSessionParams(String username){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Login_Activity.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("@string/SessionUsername", username);
        editor.commit();
    }
    public void open_signup_activity(View v){
        Intent i=new Intent(Login_Activity.this,Register.class);
        startActivity(i);
    }

    @Override
    public void onValidationSucceeded() {
        login(mUsername.getText().toString(), mPassword.getText().toString());
    }

    @Override
    public void onValidationFailed(List<ValidationError> list) {
        for(int i=0;i<list.size();i++){
            View v=list.get(i).getView();
            if(v.getId()==R.id.input_email){
                mUsername.setError(list.get(i).getCollatedErrorMessage(Login_Activity.this));

            }else if(v.getId()==R.id.input_password){
                mPassword.setError(list.get(i).getCollatedErrorMessage(Login_Activity.this));
            }

        }

    }
}
