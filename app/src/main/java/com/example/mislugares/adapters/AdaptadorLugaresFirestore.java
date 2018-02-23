package com.example.mislugares.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mislugares.R;
import com.example.mislugares.models.Lugar;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.mislugares.adapters.AdaptadorLugares.personalizaVista;

/**
 * Created by vicch on 14/02/2018.
 */

public class AdaptadorLugaresFirestore extends RecyclerView.Adapter<AdaptadorLugares.ViewHolder> implements EventListener<QuerySnapshot>, AdaptadorLugaresInterface {

    public static final String TAG = "Mislugares";
    private Query query;
    private List<DocumentSnapshot> items;
    private ListenerRegistration registration;
    private LayoutInflater inflador;
    private View.OnClickListener onClickListener;

    public AdaptadorLugaresFirestore(Context contexto, Query query){
        items = new ArrayList<DocumentSnapshot>();
        //reference = ref;
        this.query = query;
        inflador = (LayoutInflater) contexto .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Log.d("TIPO", "Firestore SDK ");

    }

    @Override
    public AdaptadorLugares.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflador.inflate(R.layout.elemento_lista, null);
        v.setOnClickListener(onClickListener); return new AdaptadorLugares.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdaptadorLugares.ViewHolder holder, int posicion) {
        Lugar lugar = getItem(posicion);
        personalizaVista(holder, lugar);
    }

    public Lugar getItem(int pos) {
        return items.get(pos).toObject(Lugar.class);
    }

    public String getKey(int pos) {
        return items.get(pos).getId();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnItemClickListener(View.OnClickListener onClick){
        onClickListener = onClick;
    }

    public void startListening(){
        items = new ArrayList<DocumentSnapshot>();
        registration = query.addSnapshotListener(this);
    }

    public void stopListening(){
        registration.remove();
    }

    @Override
    public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "error al recibir evento", e);
            return;
        }
        for (DocumentChange dc : snapshots.getDocumentChanges()) {
            int pos = dc.getNewIndex();
            int oldPos = dc.getOldIndex();
            switch (dc.getType()) {
                case ADDED:
                    items.add(pos, dc.getDocument());
                    notifyItemInserted(pos);
                    break;
                case REMOVED:
                    items.remove(oldPos);
                    notifyItemRemoved(oldPos);
                    break;
                case MODIFIED:
                    items.remove(oldPos);
                    items.add(pos, dc.getDocument());
                    notifyItemRangeChanged(Math.min(pos, oldPos), Math.abs(pos - oldPos) + 1);
                    break;
                default:
                    Log.w(TAG, "Tipo de cambio desconocido", e);
            }
        }
    }

}
