package ma.ClickContent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import ma.ClickContent.Upvote_model.check_upvote;
import ma.ClickContent.check_registeration.wp_register;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Url;

public class Register extends AppCompatActivity implements Validator.ValidationListener {
    @NotEmpty
EditText username;
        @Password(min = 6)@NotEmpty
        EditText password;
    @Email @NotEmpty
    EditText email;
    TextView link_login;
    AppCompatButton btn_signup;
    ProgressDialog pd;
     Validator validator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        username =(EditText) findViewById(R.id.input_name);
        password=(EditText) findViewById(R.id.input_password);
        email=(EditText) findViewById(R.id.input_email);
        link_login=(TextView) findViewById(R.id.link_login);
        btn_signup=(AppCompatButton) findViewById(R.id.btn_signup);
        pd=new ProgressDialog(Register.this);
        pd.setMessage("Please Wait...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        Drawable  drawable=new ProgressBar(this).getIndeterminateDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(this, R.color.primary),
                PorterDuff.Mode.SRC_IN);
        pd.setIndeterminateDrawable(drawable);
          validator = new Validator(Register.this);
        validator.setValidationListener(this);
    }
    public void signup(View view){
      validator.validate();
        }

public void open_login_activity(View v){
    startActivity(new Intent(Register.this,Login_Activity.class));
}

    @Override
     public void onValidationSucceeded() {
        if(!pd.isShowing()){
            pd.show();

        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.palmsaresweaty.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FeedAPI feedAPI = retrofit.create(FeedAPI.class);
        Call<wp_register> call=feedAPI.get_registration_status(username.getText().toString(),email.getText().toString(),password.getText().toString());
        call.enqueue(new Callback<wp_register>() {
            @Override
            public void onResponse(Call<wp_register> call, Response<wp_register> response) {
                if(pd.isShowing()){
                    pd.dismiss();
                }
                if(response.body()!=null) {
                    Log.e("register", response.body().toString());
                    Toast.makeText(Register.this,response.body().getMessage(),Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Register.this,Login_Activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }else{
                    if(pd.isShowing()){
                        pd.dismiss();
                    }
                    Toast.makeText(Register.this,"Your Email or Username already Exist",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<wp_register> call, Throwable throwable) {
                if(pd.isShowing()){
                    pd.dismiss();
                }
                Log.e("register_error",throwable.getMessage());
            }
        });
    }

    @Override
    public void onValidationFailed(List<ValidationError> list) {
        for(int i=0;i<list.size();i++){
            View v=list.get(i).getView();
            if(v.getId()==R.id.input_name){
                username.setError(list.get(i).getCollatedErrorMessage(Register.this));

            }else if(v.getId()==R.id.input_email){
                email.setError(list.get(i).getCollatedErrorMessage(Register.this));
            }else if(v.getId()==R.id.input_password){
                password.setError(list.get(i).getCollatedErrorMessage(Register.this));
            }

        }

    }
}
