package ps.global.zajil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// Base Stitch Packages
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;

// Packages needed to interact with MongoDB and Stitch
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;

// Necessary component for working with MongoDB Mobile
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;
import com.mongodb.stitch.core.services.mongodb.remote.sync.DefaultSyncConflictResolvers;
import com.mongodb.stitch.core.services.mongodb.remote.sync.SyncInsertOneResult;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class RegisterAcativity extends AppCompatActivity {

    private Button mRegstier;
    private EditText mEmailreg, mPasswordreg;
    private TextView mHaveAlReadyAccount;
    //private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference RootRef;
    private RemoteMongoClient _mongoClient;
    private RemoteMongoCollection _remoteCollection;
    private StitchAppClient _client;
    private RemoteMongoCollection<Document> _usersColl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_acativity);
        //mAuth = FirebaseAuth.getInstance();
        this._client =
                Stitch.initializeDefaultAppClient("zajil-nttyw");

        this._mongoClient =
                this._client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");


        //RootRef = FirebaseDatabase.getInstance().getReference();
        InitializeFields();


        mHaveAlReadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendtoLoginPage();
            }
        });

        mRegstier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatNewAccount();
            }
        });

    }

    private void InitializeFields() {

        mRegstier = (Button) findViewById(R.id.butt_regsiter);
        mEmailreg = (EditText) findViewById(R.id.regsiter_email);
        mPasswordreg = (EditText) findViewById(R.id.regsiter_passowrd);
        mHaveAlReadyAccount = (TextView) findViewById(R.id.alreadyhave_account);
        loadingBar = new ProgressDialog(this);

    }


    private void SendtoLoginPage() {

        Intent loginpage = new Intent(RegisterAcativity.this, LoginActivity.class);
        startActivity(loginpage);

    }


    private void CreatNewAccount() {
        // Log-in using an Anonymous authentication provider from Stitch

        String regMail = mEmailreg.getText().toString();
        String regPassword = mPasswordreg.getText().toString();
        if (TextUtils.isEmpty(regMail)) {


            Toast.makeText(this, "Please Enter your Email", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(regPassword)) {


            Toast.makeText(this, "Please Enter your Password", Toast.LENGTH_LONG).show();
        } else {
            loadingBar.setTitle("Creating new Account...");
            loadingBar.setMessage("Please wait");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            final RemoteMongoCollection<Document>  usersColl =
                    this._mongoClient.getDatabase("shop").getCollection("users");

            this._client.getAuth().loginWithCredential(new AnonymousCredential()).continueWithTask(
                    new Continuation<StitchUser, Task<RemoteInsertOneResult>>() {
                        @Override
                        public Task<RemoteInsertOneResult> then(@NonNull Task<StitchUser> task) throws Exception {
                            if (!task.isSuccessful()) {
                                Log.e("STITCH", "Login failed!");
                                throw task.getException();
                            }

                            final Document newUser = new Document("email", regMail)
                                    .append("password", regPassword);

                            return usersColl.insertOne(newUser);
                        }
                    }
            ).continueWithTask(new Continuation<RemoteInsertOneResult, Task<List<Document>>>() {
                @Override
                public Task<List<Document>> then(@NonNull Task<RemoteInsertOneResult> task) throws Exception {
                    if (!task.isSuccessful()) {
                        Log.e("STITCH", "Insert failed!");
                        throw task.getException();
                    } else {
                        Log.e("STITCH", "Insert succeeded!" + task.getResult().toString());
                    }
                    List<Document> docs = new ArrayList<>();
                    return usersColl
                            .find(new Document("email", regMail))
                            .limit(100)
                            .into(docs);
                }
            }).addOnCompleteListener(new OnCompleteListener<List<Document>>() {
                @Override
                public void onComplete(@NonNull Task<List<Document>> task) {
                    if (task.isSuccessful()) {
                        Log.d("STITCH", "Found docs: " + task.getResult().toString());
                        return;
                    }
                    Log.e("STITCH", "Error: " + task.getException().toString());
                    task.getException().printStackTrace();
                }
            });
            Toast.makeText(RegisterAcativity.this, "Account Created Successfully", Toast.LENGTH_LONG).show();
            loadingBar.dismiss();
        }


    }
    private void CreatNewAccount2() {

        String regMail = mEmailreg.getText().toString();
        String regPassword = mPasswordreg.getText().toString();

        if (TextUtils.isEmpty(regMail)) {


            Toast.makeText(this, "Please Enter your Email", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(regPassword)) {


            Toast.makeText(this, "Please Enter your Password", Toast.LENGTH_LONG).show();
        } else {
            loadingBar.setTitle("Creating new Account...");
            loadingBar.setMessage("Pleast wait");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();


//            mAuth.createUserWithEmailAndPassword(regMail, regPassword)
//                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//                                String currentUser = mAuth.getCurrentUser().getUid();
//                                RootRef.child("Users").child(currentUser).setValue("");
//
//                                Intent mainActivity = new Intent(RegisterAcativity.this, MainActivity.class);
//                                mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(mainActivity);
//
//
//                                Toast.makeText(RegisterAcativity.this, "Account Created Successfully", Toast.LENGTH_LONG).show();
//                                loadingBar.dismiss();
//                            } else {
//
//                                String messege = task.getException().toString();
//                                Toast.makeText(RegisterAcativity.this, "Error : " + messege, Toast.LENGTH_LONG).show();
//                                loadingBar.dismiss();
//                            }
//                        }
//                    });
        }


    }

    private void SendUsertoMainActivity() {


        Intent mainActivity = new Intent(RegisterAcativity.this, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
        finish();
    }

}
