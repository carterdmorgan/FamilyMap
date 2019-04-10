package carterdmorgan.com.familymap;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.net.HttpURLConnection;

import carterdmorgan.com.familymap.api.network.FamilyMapService;
import carterdmorgan.com.familymap.api.request.LoginUserRequest;
import carterdmorgan.com.familymap.api.request.RegisterUserRequest;
import carterdmorgan.com.familymap.api.result.UserResult;
import carterdmorgan.com.familymap.data.PersonHelper;
import carterdmorgan.com.familymap.data.UserDataStore;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

public class LoginFragment extends Fragment {
    private Button btnSignIn;
    private Button btnRegister;
    private EditText etServerHost;
    private EditText etServerPort;
    private EditText etUserName;
    private EditText etPassword;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private RadioGroup rgGender;
    private OnFragmentInteractionListener mListener;

    public LoginFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_login, container, false);

        btnSignIn = view.findViewById(R.id.sign_in_button);
        btnRegister = view.findViewById(R.id.register_button);
        etServerHost = view.findViewById(R.id.server_host_edit_text);
        etServerPort = view.findViewById(R.id.server_port_edit_text);
        etUserName = view.findViewById(R.id.user_name_edit_text);
        etPassword = view.findViewById(R.id.password_edit_text);
        etFirstName = view.findViewById(R.id.first_name_edit_text);
        etLastName = view.findViewById(R.id.last_name_edit_text);
        etEmail = view.findViewById(R.id.email_edit_text);
        rgGender = view.findViewById(R.id.gender_radio_group);

        btnRegister.setEnabled(false);
        btnSignIn.setEnabled(false);

        etServerPort.addTextChangedListener(activateButtonsWatcher());
        etServerHost.addTextChangedListener(activateButtonsWatcher());
        etUserName.addTextChangedListener(activateButtonsWatcher());
        etPassword.addTextChangedListener(activateButtonsWatcher());
        etFirstName.addTextChangedListener(activateButtonsWatcher());
        etLastName.addTextChangedListener(activateButtonsWatcher());
        etEmail.addTextChangedListener(activateButtonsWatcher());

        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1) {
                    btnRegister.setEnabled(true);
                }
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser();
            }
        });

        return view;
    }

    private TextWatcher activateButtonsWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String serverHost = etServerHost.getText().toString();
                String serverPort = etServerPort.getText().toString();
                String userName = etUserName.getText().toString();
                String password = etPassword.getText().toString();
                final String firstName = etFirstName.getText().toString();
                final String lastName = etLastName.getText().toString();
                String email = etEmail.getText().toString();
                int gender = rgGender.getCheckedRadioButtonId();

                if (!TextUtils.isEmpty(serverHost) &&
                        !TextUtils.isEmpty(serverPort) &&
                        !TextUtils.isEmpty(userName) &&
                        !TextUtils.isEmpty(password)) {
                    btnSignIn.setEnabled(true);
                } else {
                    btnSignIn.setEnabled(false);
                }

                if (!TextUtils.isEmpty(serverHost) &&
                        !TextUtils.isEmpty(serverPort) &&
                        !TextUtils.isEmpty(userName) &&
                        !TextUtils.isEmpty(password) &&
                        !TextUtils.isEmpty(firstName) &&
                        !TextUtils.isEmpty(lastName) &&
                        !TextUtils.isEmpty(email) &&
                        gender != -1) {
                    btnRegister.setEnabled(true);
                } else {
                    btnRegister.setEnabled(false);
                }
            }
        };
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onUserLoaded();
    }


    public void registerUser() {
        String serverHost = etServerHost.getText().toString();
        String serverPort = etServerPort.getText().toString();
        String userName = etUserName.getText().toString();
        String password = etPassword.getText().toString();
        final String firstName = etFirstName.getText().toString();
        final String lastName = etLastName.getText().toString();
        String email = etEmail.getText().toString();
        int gender = rgGender.getCheckedRadioButtonId();

        UserDataStore.getInstance().setServerHost(serverHost);
        UserDataStore.getInstance().setServerPort(serverPort);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(String.format("http://%s:%s", serverHost, serverPort))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final FamilyMapService fmService = retrofit.create(FamilyMapService.class);
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUserName(userName);
        request.setPassword(password);
        request.setEmail(email);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        if (gender == R.id.male_radio_button) {
            request.setGender(PersonHelper.GENDER_MARKER_MALE);
        } else {
            request.setGender(PersonHelper.GENDER_MARKER_FEMALE);
        }

        Call<UserResult> call = fmService.registerUser(request);
        call.enqueue(new Callback<UserResult>() {
            @Override
            public void onResponse(Call<UserResult> call, Response<UserResult> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    UserResult userResult = response.body();
                    UserDataStore.getInstance().setUserResult(userResult);
                    UserDataStore.getInstance().retrieveFamilyData(fmService, new UserDataStore.LoadFamilyDataListener() {
                        @Override
                        public void onSuccess() {
                            mListener.onUserLoaded();
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(getContext(), R.string.register_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    try {
                        Log.d(TAG, "onResponse: failed: " + response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), "Register failed.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResult> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), getString(R.string.register_failure_message), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void signInUser() {
        String serverHost = etServerHost.getText().toString();
        String serverPort = etServerPort.getText().toString();
        String userName = etUserName.getText().toString();
        String password = etPassword.getText().toString();

        UserDataStore.getInstance().setServerHost(serverHost);
        UserDataStore.getInstance().setServerPort(serverPort);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(String.format(getString(R.string.fm_server_format), serverHost, serverPort))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final FamilyMapService fmService = retrofit.create(FamilyMapService.class);
        LoginUserRequest request = new LoginUserRequest();
        request.setUserName(userName);
        request.setPassword(password);

        Call<UserResult> call = fmService.loginUser(request);
        call.enqueue(new Callback<UserResult>() {
            @Override
            public void onResponse(Call<UserResult> call, Response<UserResult> response) {
                if (response.code() == 200) {
                    UserResult userResult = response.body();
                    UserDataStore.getInstance().setUserResult(userResult);
                    UserDataStore.getInstance().retrieveFamilyData(fmService, new UserDataStore.LoadFamilyDataListener() {
                        @Override
                        public void onSuccess() {
                            mListener.onUserLoaded();
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(getContext(), R.string.sign_in_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    try {
                        Log.d(TAG, "onResponse: failed: " + response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), R.string.sign_in_failure_message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResult> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), R.string.sign_in_failure_message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
