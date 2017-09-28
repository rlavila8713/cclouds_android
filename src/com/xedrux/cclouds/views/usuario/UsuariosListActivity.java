package com.xedrux.cclouds.views.usuario;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import com.xedrux.cclouds.R;
import com.xedrux.cclouds.adapters.UsuariosArrayAdapter;
import com.xedrux.cclouds.constants.URLS;
import com.xedrux.cclouds.models.Usuario;
import com.xedrux.cclouds.utils.SingletonPattern;
import com.xedrux.cclouds.utils.Utils;
import com.xedrux.cclouds.utils.WebServiceHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by reinier on 03/09/2015.
 */
public class UsuariosListActivity extends ListActivity implements SearchView.OnQueryTextListener {
    private MenuItem contactSearch, contactSynchronize, mainOptions;
    private String serverAddress, username, password, error;

    private List<Usuario> usuariosList;
    private usuarioListTask usuarioListTask;
    private UsuariosArrayAdapter adapter;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usuarios_list);
        usuariosList = new ArrayList<Usuario>();
        adapter = new UsuariosArrayAdapter(this, usuariosList);
        setListAdapter(adapter);

        username = SingletonPattern.getActiveUser().getUsername();
        password = SingletonPattern.getActiveUser().getPassword();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.usuario_list_options, menu);
//        contactSearch = (MenuItem) menu.findItem(R.id.contactSearch);
        contactSynchronize = (MenuItem) menu.findItem(R.id.getAllUsers);
        mainOptions = (MenuItem) menu.findItem(R.id.mainOptions);

        MenuItem search = menu.findItem(R.id.usuarioSearch);
        mSearchView = (SearchView) search.getActionView();
        mSearchView.setQueryHint("Search...");
        mSearchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Usuario usuario = (Usuario) l.getAdapter().getItem(position);
        Intent intent = new Intent(UsuariosListActivity.this, UserProfile.class);

        Bundle bundle = new Bundle();
        bundle.putInt("idUser", usuario.getIdUser());
        bundle.putInt("idRol", usuario.getIdRol());
        bundle.putString("Address", usuario.getAddress());
        bundle.putInt("idParroquia", usuario.getIdParroquia());
        bundle.putString("username", usuario.getUsername());
        bundle.putString("userEmail", usuario.getUserEmail());
        bundle.putString("firstName", usuario.getFirstName());
        bundle.putString("lastName", usuario.getLastName());
        bundle.putChar("sex", usuario.getSex());
        bundle.putString("dateOdBirth", usuario.getDateOfBirth());
        bundle.putString("phoneNumber", usuario.getPhoneNumber());

        intent.putExtras(bundle);
        startActivityForResult(intent, RESULT_OK);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.getAllUsers):
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.userSynchronization));
               /* builder.setItems(getResources().getStringArray(R.array.contactsSynchronizationByValues),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int index) {
                                Intent intent = null;
                                Bundle bag = new Bundle();
                                switch (index) {
                                    case 1:
                                    case 2:
                                    case 3:
                                }
                            }
                        });*/
                builder.setMessage(R.string.userSynchronizationInformation);
                builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //contactSynchronize.setActionView(R.layout.actionbar_indeterminate_progress);
                        attemptToGetAllUsers();
                    }
                });
                builder.setIcon(R.drawable.cclouds_sync);
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        adapter.getFilter().filter(s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    private void attemptToGetAllUsers() {
        if (usuariosList != null) {
            mainOptions.setActionView(R.layout.actionbar_indeterminate_progress);
            usuarioListTask = new usuarioListTask();
            usuarioListTask.execute((Void) null);
        }
    }

    public class usuarioListTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            Utils.loadPreferences(getApplication());

            if (!SingletonPattern.getActiveUser().getPassword().equals(Utils.passwordValue) && Utils.usingStoredPass) {
                Utils.usingStoredPass = false;
            }

            String parametros = "?action=getallusers&LoginForm[username]=" + SingletonPattern.getActiveUser().getUsername()
                    + "&LoginForm[password]=" + SingletonPattern.getActiveUser().getPassword();

            String add = "/getallusers";
            JSONObject result;
            try {

                result = WebServiceHelper.getServerDataObject(parametros,
                        URLS.SERVER_ADDRESS + add);

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            boolean success = false;

            if (result != null && result.length() > 0) {
                try {
                    success = result.getBoolean("success");

                    JSONArray array = result.getJSONArray("data");

                    if (success) {
                        Usuario usuario;
                        usuariosList.clear();
                        int i = 0;
                        int length = array.length();

                        for (; i < length; i++) {
                            usuario = new Usuario();
                            usuario.setIdUser(array.getJSONObject(i).has("idUser")?array.getJSONObject(i).getInt("idUser"):null);
                            usuario.setIdRol(array.getJSONObject(i).has("idRol")?array.getJSONObject(i).getInt("idRol"):null);
                            usuario.setAddress(array.getJSONObject(i).has("Address")?array.getJSONObject(i).getString("Address"):null);
                            usuario.setIdParroquia(array.getJSONObject(i).has("idParroquia")?array.getJSONObject(i).getInt("idParroquia"):null);
                            usuario.setUsername(array.getJSONObject(i).has("username") ? array.getJSONObject(i).getString("username") : null);
                            usuario.setUserEmail(array.getJSONObject(i).has("userEmail") ? array.getJSONObject(i).getString("userEmail") : null);
                            usuario.setFirstName(array.getJSONObject(i).has("FirstName") ? array.getJSONObject(i).getString("FirstName") : null);
                            usuario.setLastName(array.getJSONObject(i).has("LastName") ? array.getJSONObject(i).getString("LastName") : null);
                            usuario.setSex(array.getJSONObject(i).has("Sex") ? array.getJSONObject(i).getString("Sex").charAt(0) : null);
                            usuario.setDateOfBirth(array.getJSONObject(i).has("DateBirth") ? array.getJSONObject(i).getString("DateBirth") : null);
                            usuario.setPhoneNumber(array.getJSONObject(i).has("phoneNumber")?array.getJSONObject(i).getString("phoneNumber"):null);

                            if (!usuariosList.contains(usuario)) {
                                usuariosList.add(usuario);
                            }
                            else
                            {
                                Toast t = Toast.makeText(getApplicationContext(), getResources().getString(R.string.noUserDataWasSynchronized), Toast.LENGTH_LONG);
                                t.show();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return success;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                setListAdapter(adapter);
                adapter.notifyDataSetChanged();
                mainOptions.setActionView(null);
            } else {
                Toast t = Toast.makeText(getApplicationContext(), getResources().getString(R.string.unexpectedError), Toast.LENGTH_LONG);
                t.show();
                mainOptions.setActionView(null);

            }
        }

        @Override
        protected void onCancelled() {
            usuarioListTask = null;
//            showProgress(false);
        }
    }
}
