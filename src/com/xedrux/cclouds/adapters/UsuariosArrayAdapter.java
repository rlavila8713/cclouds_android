package com.xedrux.cclouds.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import com.xedrux.cclouds.R;
import com.xedrux.cclouds.models.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by reinier on 03/09/2015.
 */
public class UsuariosArrayAdapter extends ArrayAdapter<Usuario> {
    private List<Usuario> usuariosList;
    private List<Usuario> usuariosListAux;
    private Activity context;
    private Filter usuarioFilterObject;
    private Usuario usuario;

    public UsuariosArrayAdapter(Activity context, List<Usuario> usuariosList) {
        super(context, R.layout.row_user, usuariosList);
        this.usuariosList = usuariosList;
        this.usuariosListAux = usuariosList;
        this.context = context;
        this.usuario = null;
    }

    @Override
    public Filter getFilter() {
        if (usuarioFilterObject == null) {
            usuarioFilterObject = new usuariosFilter();
        }
        return usuarioFilterObject;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Usuario usuario = null;
        if (view == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.row_user, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.firstName = (TextView) view.findViewById(R.id.firstName);
            viewHolder.phoneNumber = (TextView) view.findViewById(R.id.phoneNumber);
            viewHolder.userRol = (TextView) view.findViewById(R.id.userRol);


//            viewHolder.username = (TextView) view.findViewById(R.id.FirstName);
            view.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        usuario = usuariosList.get(position);
        holder.firstName.setText(usuario.getFirstName());
        holder.phoneNumber.setText(usuario.getPhoneNumber());
        holder.userRol.setText(usuario.getIdRol() == 1 ? "Administrator" : "Client");
        return view;

    }

    static class ViewHolder {
        protected TextView firstName;
        protected TextView username;
        protected TextView phoneNumber;
        protected TextView userRol;


    }

    class usuariosFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Usuario> mListAux = new ArrayList<Usuario>();

            if (constraint == null || constraint.length() == 0) {
                results.values = usuariosList;
                results.count = usuariosList.size();
            } else {
                for (Usuario usuario : usuariosListAux) {
                    if (usuario.getFirstName().toUpperCase()
                            .startsWith(constraint.toString().toUpperCase())) {
                        mListAux.add(usuario);
                    }
                }
                results.values = mListAux;
                results.count = mListAux.size();

                // for (int i = 0; i < mListAux.size(); i++) {
                // Log.e("", mListAux.get(i).getName() + " "
                // + mListAux.get(i).getDist());
                // }

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            if (filterResults.count == 0) {
                Log.e("count", "0");
                notifyDataSetChanged();
            } else {
                usuariosList = (List<Usuario>) filterResults.values;
                notifyDataSetChanged();
            }
        }
    }
}
